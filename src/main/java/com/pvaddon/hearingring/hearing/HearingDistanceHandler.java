package com.pvaddon.hearingring.hearing;

import com.pvaddon.hearingring.config.PlasmoAddonSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import su.plo.voice.api.client.event.audio.source.AudioSourceWriteEvent;
import su.plo.voice.api.event.EventSubscribe;
import su.plo.voice.proto.data.audio.source.PlayerSourceInfo;

import java.util.UUID;

/**
 * Client-side hearing cap.
 *
 * Instead of repeatedly forcing OpenAL volume to zero (which Plasmo Voice
 * can overwrite on its next audio update), this cancels the audio-source
 * write event itself when the speaking player is outside our hearing range.
 */
public final class HearingDistanceHandler {

    public static final HearingDistanceHandler INSTANCE = new HearingDistanceHandler();

    private HearingDistanceHandler() {}

    @EventSubscribe
    public void onAudioSourceWrite(AudioSourceWriteEvent event) {
        if (PlasmoAddonSettings.HEARING_LIMIT_ENABLED == null
                || !PlasmoAddonSettings.HEARING_LIMIT_ENABLED.value()) {
            return;
        }

        if (!(event.getSource().getSourceInfo() instanceof PlayerSourceInfo info)) {
            return;
        }

        UUID speakerUuid = info.getPlayerInfo() != null
                ? info.getPlayerInfo().getPlayerId()
                : null;

        if (speakerUuid == null) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer local = mc.player;
        if (local == null) return;

        double maxDistance = PlasmoAddonSettings.HEARING_DISTANCE.value();
        double maxDistanceSq = maxDistance * maxDistance;

        if (local.level() != null) {
            var speaker = local.level().getPlayerByUUID(speakerUuid);
            if (speaker != null && local.distanceToSqr(speaker) > maxDistanceSq) {
                event.setCancelled(true);
            }
        }
    }
}
