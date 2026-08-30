package com.pvaddon.hearingring.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.pvaddon.hearingring.config.AddonConfig;
import com.pvaddon.hearingring.config.PlasmoAddonSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

/**
 * Draws two independent flat circles:
 *  1) the Plasmo Voice microphone/broadcast radius, shown temporarily exactly
 *     when PV asks to visualize it (but as a flat circle instead of a dome);
 *  2) the user's hearing-distance radius, shown temporarily when the
 *     hearing distance or Limit toggle changes, then faded away.
 */
public class FlatRingRenderer {

    private volatile int microphoneRadius = 0;
    private volatile int microphoneColor = 0xFF55FF55;
    private volatile long microphoneShownAt = 0L;
    private volatile long microphoneExpiresAt = 0L;

    private volatile int hearingRadius = 0;
    private volatile long hearingShownAt = 0L;
    private volatile long hearingExpiresAt = 0L;
    private int lastHearingDistance = -1;
    private boolean lastHearingEnabled = false;
    private boolean hearingStateInitialized = false;

    public void showMicrophoneRing(int radius, int color) {
        microphoneRadius = Math.max(0, radius);
        // If the user set a custom mic ring color, use it instead of Plasmo Voice's own color.
        boolean micOverride = PlasmoAddonSettings.MIC_RING_COLOR_OVERRIDE != null
                && PlasmoAddonSettings.MIC_RING_COLOR_OVERRIDE.value();
        microphoneColor = micOverride
                ? (PlasmoAddonSettings.getMicRingColor() & 0x00FFFFFF)
                : (color & 0x00FFFFFF);
        microphoneShownAt = System.currentTimeMillis();
        // Match Plasmo Voice's built-in visualization lifetime: full for about
        // 2 seconds, then fade for roughly 15 seconds.
        microphoneExpiresAt = microphoneShownAt + 17_000L;
    }

    /**
     * The hearing-distance indicator is intentionally temporary.  It is shown
     * whenever the hearing distance or its Limit toggle changes, then fades
     * away just like the microphone-radius indicator.
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (PlasmoAddonSettings.HEARING_DISTANCE == null
                || PlasmoAddonSettings.HEARING_LIMIT_ENABLED == null) return;

        int distance = PlasmoAddonSettings.HEARING_DISTANCE.value();
        boolean enabled = PlasmoAddonSettings.HEARING_LIMIT_ENABLED.value();

        if (!hearingStateInitialized) {
            lastHearingDistance = distance;
            lastHearingEnabled = enabled;
            hearingStateInitialized = true;
            return;
        }

        if (distance != lastHearingDistance || enabled != lastHearingEnabled) {
            lastHearingDistance = distance;
            lastHearingEnabled = enabled;

            if (enabled) {
                showHearingRing(distance);
            } else {
                hearingRadius = 0;
                hearingShownAt = 0L;
                hearingExpiresAt = 0L;
            }
        }
    }

    public void showHearingRing(int radius) {
        hearingRadius = Math.max(0, Math.min(128, radius));
        hearingShownAt = System.currentTimeMillis();
        hearingExpiresAt = hearingShownAt + 17_000L;
    }

    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        Vec3 camPos = event.getCamera().getPosition();
        Vec3 playerPos = player.getPosition(event.getPartialTick());

        double relX = playerPos.x - camPos.x;
        double relY = playerPos.y - camPos.y;
        double relZ = playerPos.z - camPos.z;

        long now = System.currentTimeMillis();

        boolean ringEnabled = PlasmoAddonSettings.FLAT_RING_ENABLED != null
                && PlasmoAddonSettings.FLAT_RING_ENABLED.value();
        int ringAlpha = PlasmoAddonSettings.RING_ALPHA != null
                ? PlasmoAddonSettings.RING_ALPHA.value() : 200;
        boolean micOverride = PlasmoAddonSettings.MIC_RING_COLOR_OVERRIDE != null
                && PlasmoAddonSettings.MIC_RING_COLOR_OVERRIDE.value();

        // Temporary microphone ring. It follows Plasmo Voice's own visualizer
        // timing instead of being tied to the hearing-distance setting.
        if (ringEnabled && microphoneRadius > 0 && now < microphoneExpiresAt) {
            float alpha = microphoneAlpha(now);
            if (micOverride) {
                alpha *= (ringAlpha / 255f);
            }
            if (alpha > 0.001f) {
                drawRing(event.getPoseStack(), relX, relY + 0.05, relZ,
                        microphoneRadius, microphoneColor, alpha);
            }
        }

        // Temporary hearing-distance ring. It is shown when the user changes
        // the hearing distance or toggles the limit, then fades away.
        if (hearingRadius > 0 && now < hearingExpiresAt) {
            float alpha = fadeAlpha(now - hearingShownAt);
            if (alpha > 0.001f) {
                int ringColor = AddonConfig.RING_COLOR_RED != null
                        ? PlasmoAddonSettings.getRingColor() : 0xFF66CC;
                drawRing(event.getPoseStack(), relX, relY + 0.06, relZ,
                        hearingRadius, ringColor, alpha * (ringAlpha / 255f));
            }
        }
    }

    private float microphoneAlpha(long now) {
        long age = now - microphoneShownAt;
        return fadeAlpha(age);
    }

    private float fadeAlpha(long age) {
        if (age <= 2_000L) return 1.0f;
        if (age >= 17_000L) return 0.0f;
        return 1.0f - ((age - 2_000L) / 15_000f);
    }

    private void drawRing(PoseStack poseStack, double originX, double originY,
                          double originZ, double radius, int rgb, float alpha) {
        int segments = AddonConfig.RING_SEGMENTS.get();
        double halfThickness = AddonConfig.RING_THICKNESS.get() / 2.0;
        double outerRadius = radius + halfThickness;
        double innerRadius = Math.max(0.01, radius - halfThickness);

        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);

            float outerX = (float) (originX + outerRadius * cos);
            float outerZ = (float) (originZ + outerRadius * sin);
            float innerX = (float) (originX + innerRadius * cos);
            float innerZ = (float) (originZ + innerRadius * sin);

            buffer.vertex(matrix, outerX, (float) originY, outerZ)
                    .color(r, g, b, alpha).endVertex();
            buffer.vertex(matrix, innerX, (float) originY, innerZ)
                    .color(r, g, b, alpha).endVertex();
        }

        tesselator.end();
        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
