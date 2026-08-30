package com.pvaddon.hearingring.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.pvaddon.hearingring.config.PlasmoAddonSettings;
import com.pvaddon.hearingring.gui.HearingRingSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * /hearingdistance <blocks>   - set how far you want to hear other players
 * /hearingdistance toggle     - enable/disable the custom hearing cap
 * /hearingdistance gui        - open the settings panel (distance +/- and color pickers)
 * /voicering <0xRRGGBB>       - set the flat ring color
 * /voicering toggle           - switch the temporary microphone ring on/off
 * /voicering gui              - open the settings panel (same as above)
 * /hearingring                - shortcut that opens the settings panel directly
 */
public class HearingRingCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("hearingring")
                .executes(ctx -> {
                    Minecraft.getInstance().setScreen(new HearingRingSettingsScreen());
                    return 1;
                }));

        event.getDispatcher().register(Commands.literal("hearingdistance")
                .then(Commands.literal("gui").executes(ctx -> {
                    Minecraft.getInstance().setScreen(new HearingRingSettingsScreen());
                    return 1;
                }))
                .executes(ctx -> {
                    boolean enabled = PlasmoAddonSettings.HEARING_LIMIT_ENABLED != null
                            && PlasmoAddonSettings.HEARING_LIMIT_ENABLED.value();
                    int distance = PlasmoAddonSettings.HEARING_DISTANCE != null
                            ? PlasmoAddonSettings.HEARING_DISTANCE.value() : 48;
                    ctx.getSource().sendSuccess(() -> Component.literal(
                            "Hearing distance: " + distance + " blocks | Limit: " + (enabled ? "ON" : "OFF")), false);
                    return 1;
                })
                .then(Commands.literal("toggle").executes(ctx -> {
                    boolean newValue = !PlasmoAddonSettings.HEARING_LIMIT_ENABLED.value();
                    PlasmoAddonSettings.HEARING_LIMIT_ENABLED.set(newValue);
                    ctx.getSource().sendSuccess(() -> Component.literal(
                            "Custom hearing distance: " + (newValue ? "ON" : "OFF")), false);
                    return 1;
                }))
                .then(Commands.argument("blocks", IntegerArgumentType.integer(1, 128))
                        .executes(ctx -> {
                            int value = IntegerArgumentType.getInteger(ctx, "blocks");
                            PlasmoAddonSettings.HEARING_DISTANCE.set(value);
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "Hearing distance set to " + value + " blocks"), false);
                            return 1;
                        })));

        event.getDispatcher().register(Commands.literal("voicering")
                .then(Commands.literal("gui").executes(ctx -> {
                    Minecraft.getInstance().setScreen(new HearingRingSettingsScreen());
                    return 1;
                }))
                .then(Commands.literal("toggle").executes(ctx -> {
                    boolean current = PlasmoAddonSettings.FLAT_RING_ENABLED != null
                            && PlasmoAddonSettings.FLAT_RING_ENABLED.value();
                    boolean newValue = !current;
                    if (PlasmoAddonSettings.FLAT_RING_ENABLED != null) {
                        PlasmoAddonSettings.FLAT_RING_ENABLED.set(newValue);
                    }
                    ctx.getSource().sendSuccess(() -> Component.literal(
                            "Flat ring indicator: " + (newValue ? "ON" : "OFF (using Plasmo Voice default)")), false);
                    return 1;
                }))
                .then(Commands.literal("color")
                        .then(Commands.argument("hexRGB", IntegerArgumentType.integer(0x000000, 0xFFFFFF))
                                .executes(ctx -> {
                                    int value = IntegerArgumentType.getInteger(ctx, "hexRGB");
                                    PlasmoAddonSettings.setRingColor(value);
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            String.format("Ring color set to #%06X", value)), false);
                                    return 1;
                                })))
                .then(Commands.literal("alpha")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 255))
                                .executes(ctx -> {
                                    int value = IntegerArgumentType.getInteger(ctx, "value");
                                    if (PlasmoAddonSettings.RING_ALPHA != null) {
                                        PlasmoAddonSettings.RING_ALPHA.set(value);
                                    }
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            "Ring alpha set to " + value), false);
                                    return 1;
                                }))));
    }
}
