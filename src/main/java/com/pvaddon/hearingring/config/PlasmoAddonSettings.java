package com.pvaddon.hearingring.config;

import su.plo.config.entry.BooleanConfigEntry;
import su.plo.config.entry.IntConfigEntry;
import su.plo.slib.api.chat.component.McTextComponent;


/** Settings exposed directly in Plasmo Voice -> Settings -> Add-ons. */
public final class PlasmoAddonSettings {
    private PlasmoAddonSettings() {}

    public static IntConfigEntry HEARING_DISTANCE;
    public static BooleanConfigEntry HEARING_LIMIT_ENABLED;
    public static BooleanConfigEntry FLAT_RING_ENABLED;
    public static IntConfigEntry RING_ALPHA;
    public static BooleanConfigEntry MIC_RING_COLOR_OVERRIDE;



    public static void register(su.plo.voice.api.client.config.addon.AddonConfig config) {
        HEARING_DISTANCE = config.addIntSlider("hearing_distance",
                McTextComponent.literal("Hearing distance"),
                McTextComponent.literal("Maximum distance at which you can hear other players."),
                "blocks", 48, 1, 128);

        HEARING_LIMIT_ENABLED = config.addToggle("hearing_distance_enabled",
                McTextComponent.literal("Limit hearing distance"),
                McTextComponent.literal("Ignore voice beyond your hearing distance."), true);

        FLAT_RING_ENABLED = config.addToggle("flat_ring_enabled",
                McTextComponent.literal("Flat ring indicator"),
                McTextComponent.literal("Replace the dome indicator with a flat ring."), true);

        RING_ALPHA = config.addIntSlider("ring_alpha",
                McTextComponent.literal("Ring opacity"),
                McTextComponent.literal("0 = invisible, 255 = solid."), "", 200, 0, 255);

        MIC_RING_COLOR_OVERRIDE = config.addToggle("mic_ring_color_override",
                McTextComponent.literal("Custom mic ring color"),
                McTextComponent.literal("Use the custom color for the microphone ring."), false);
    }

    public static int getRingColor() {
        return (AddonConfig.RING_COLOR_RED.get() << 16) | (AddonConfig.RING_COLOR_GREEN.get() << 8) | AddonConfig.RING_COLOR_BLUE.get();
    }
    public static void setRingColor(int rgb) {
        AddonConfig.RING_COLOR_RED.set((rgb >> 16) & 0xFF);
        AddonConfig.RING_COLOR_GREEN.set((rgb >> 8) & 0xFF);
        AddonConfig.RING_COLOR_BLUE.set(rgb & 0xFF);
    }
    public static int getMicRingColor() {
        return (AddonConfig.MIC_RING_COLOR_RED.get() << 16) | (AddonConfig.MIC_RING_COLOR_GREEN.get() << 8) | AddonConfig.MIC_RING_COLOR_BLUE.get();
    }
    public static void setMicRingColor(int rgb) {
        AddonConfig.MIC_RING_COLOR_RED.set((rgb >> 16) & 0xFF);
        AddonConfig.MIC_RING_COLOR_GREEN.set((rgb >> 8) & 0xFF);
        AddonConfig.MIC_RING_COLOR_BLUE.set(rgb & 0xFF);
    }

}
