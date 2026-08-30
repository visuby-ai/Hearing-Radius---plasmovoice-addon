package com.pvaddon.hearingring.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.lwjgl.glfw.GLFW;

/** Client-only settings kept outside Plasmo Voice's Add-ons page. */
public class AddonConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue RING_THICKNESS;
    public static final ForgeConfigSpec.IntValue RING_SEGMENTS;
    public static final ForgeConfigSpec.IntValue RING_COLOR_RED;
    public static final ForgeConfigSpec.IntValue RING_COLOR_GREEN;
    public static final ForgeConfigSpec.IntValue RING_COLOR_BLUE;
    public static final ForgeConfigSpec.IntValue MIC_RING_COLOR_RED;
    public static final ForgeConfigSpec.IntValue MIC_RING_COLOR_GREEN;
    public static final ForgeConfigSpec.IntValue MIC_RING_COLOR_BLUE;
    public static final ForgeConfigSpec.IntValue OPEN_GUI_KEY;
    public static final ForgeConfigSpec.IntValue INCREASE_DISTANCE_KEY;
    public static final ForgeConfigSpec.IntValue DECREASE_DISTANCE_KEY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("ring");
        RING_THICKNESS = builder.comment("Ring line thickness in blocks.")
                .defineInRange("thickness", 0.08, 0.01, 2.0);
        RING_SEGMENTS = builder.comment("Number of segments used to approximate the circle.")
                .defineInRange("segments", 96, 12, 256);
        builder.pop();

        builder.push("colors");
        RING_COLOR_RED = builder.defineInRange("hearing_red", 255, 0, 255);
        RING_COLOR_GREEN = builder.defineInRange("hearing_green", 102, 0, 255);
        RING_COLOR_BLUE = builder.defineInRange("hearing_blue", 204, 0, 255);
        MIC_RING_COLOR_RED = builder.defineInRange("mic_red", 85, 0, 255);
        MIC_RING_COLOR_GREEN = builder.defineInRange("mic_green", 255, 0, 255);
        MIC_RING_COLOR_BLUE = builder.defineInRange("mic_blue", 85, 0, 255);
        builder.pop();

        builder.push("keybinds");
        OPEN_GUI_KEY = builder.comment("GLFW key code. Click the key button in the Hearing Ring GUI to change it.")
                .defineInRange("open_gui", GLFW.GLFW_KEY_R, GLFW.GLFW_KEY_UNKNOWN, GLFW.GLFW_KEY_LAST);
        INCREASE_DISTANCE_KEY = builder.defineInRange("increase_distance", GLFW.GLFW_KEY_RIGHT_BRACKET, GLFW.GLFW_KEY_UNKNOWN, GLFW.GLFW_KEY_LAST);
        DECREASE_DISTANCE_KEY = builder.defineInRange("decrease_distance", GLFW.GLFW_KEY_LEFT_BRACKET, GLFW.GLFW_KEY_UNKNOWN, GLFW.GLFW_KEY_LAST);
        builder.pop();

        SPEC = builder.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC, "pvhearingring-client.toml");
    }
}
