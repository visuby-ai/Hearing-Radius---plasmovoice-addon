package com.pvaddon.hearingring.input;

import com.pvaddon.hearingring.config.AddonConfig;
import com.pvaddon.hearingring.config.PlasmoAddonSettings;
import com.pvaddon.hearingring.gui.HearingRingSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

/** Runtime key handling. Key codes are changed by clicking the buttons in the addon's GUI. */
public final class KeyBindHandler {
    private boolean openWasDown, increaseWasDown, decreaseWasDown;
    public void register() { MinecraftForge.EVENT_BUS.register(this); }
    public void unregister() { MinecraftForge.EVENT_BUS.unregister(this); }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase != ClientTickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.getWindow() == null || mc.screen != null) {
            openWasDown = increaseWasDown = decreaseWasDown = false;
            return;
        }
        long window = mc.getWindow().getWindow();
        boolean openDown = isDown(window, AddonConfig.OPEN_GUI_KEY.get());
        boolean increaseDown = isDown(window, AddonConfig.INCREASE_DISTANCE_KEY.get());
        boolean decreaseDown = isDown(window, AddonConfig.DECREASE_DISTANCE_KEY.get());
        if (openDown && !openWasDown) mc.setScreen(new HearingRingSettingsScreen());
        if (increaseDown && !increaseWasDown) adjust(1);
        if (decreaseDown && !decreaseWasDown) adjust(-1);
        openWasDown = openDown; increaseWasDown = increaseDown; decreaseWasDown = decreaseDown;
    }

    private static boolean isDown(long window, int key) {
        return key != GLFW.GLFW_KEY_UNKNOWN && GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }
    private static void adjust(int delta) {
        if (PlasmoAddonSettings.HEARING_DISTANCE == null) return;
        PlasmoAddonSettings.HEARING_DISTANCE.set(Math.max(1, Math.min(128, PlasmoAddonSettings.HEARING_DISTANCE.value() + delta)));
    }
}
