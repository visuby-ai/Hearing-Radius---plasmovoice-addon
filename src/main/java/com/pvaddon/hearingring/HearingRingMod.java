package com.pvaddon.hearingring;

import com.pvaddon.hearingring.command.HearingRingCommands;
import com.pvaddon.hearingring.config.AddonConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import su.plo.voice.api.client.PlasmoVoiceClient;

/**
 * Forge entry point. The Plasmo Voice API requires Forge add-ons to be
 * explicitly submitted to the client AddonsLoader from the Forge mod
 * constructor. A META-INF/services entry alone is not enough.
 */
@Mod(HearingRingMod.MODID)
public class HearingRingMod {

    public static final String MODID = "pvhearingring";

    private final PVHearingRingAddon addon;

    public HearingRingMod() {
        // Forge config used by the ring renderer/commands.
        AddonConfig.register();

        // Forge client commands are registered on the Forge event bus.
        MinecraftForge.EVENT_BUS.register(HearingRingCommands.class);

        // IMPORTANT: Plasmo Voice 2.x Forge add-ons must be explicitly loaded.
        // This is what makes the add-on appear under V -> Settings -> Add-ons
        // and invokes PVHearingRingAddon.onAddonInitialize().
        addon = new PVHearingRingAddon();
        PlasmoVoiceClient.getAddonsLoader().load(addon);
    }
}

