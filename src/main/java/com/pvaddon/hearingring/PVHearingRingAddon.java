package com.pvaddon.hearingring;

import com.pvaddon.hearingring.hearing.HearingDistanceHandler;
import com.pvaddon.hearingring.input.KeyBindHandler;
import com.pvaddon.hearingring.config.PlasmoAddonSettings;
import com.pvaddon.hearingring.render.DistanceVisualizeListener;
import net.minecraftforge.common.MinecraftForge;
import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.AddonLoaderScope;
import su.plo.voice.api.addon.InjectPlasmoVoice;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.client.PlasmoVoiceClient;

/**
 * Discovered by Plasmo Voice's addon loader via
 * META-INF/services/su.plo.voice.api.addon.AddonInitializer.
 *
 * Plasmo Voice instantiates this class, injects {@link #voiceClient}
 * via the @InjectPlasmoVoice field, then calls onAddonInitialize().
 */
@Addon(
        id = "pvhearingring",
        name = "Hearing Radius & Flat Ring",
        version = "1.7.0",
        authors = {"you"},
        scope = AddonLoaderScope.CLIENT
)
public class PVHearingRingAddon implements AddonInitializer {

    @InjectPlasmoVoice
    public PlasmoVoiceClient voiceClient;

    private DistanceVisualizeListener distanceVisualizeListener;
    private KeyBindHandler keyBindHandler;

    @Override
    public void onAddonInitialize() {
        // Put hearing-distance controls in Plasmo Voice -> Settings -> Add-ons.
        PlasmoAddonSettings.register(voiceClient.getAddonConfig(this));

        // Native Plasmo Voice hotkeys: open GUI / increase / decrease radius.
        keyBindHandler = new KeyBindHandler();
        keyBindHandler.register();

        // --- feature 1: independent client-side hearing distance ---
        voiceClient.getEventBus().register(this, HearingDistanceHandler.INSTANCE);

        // --- feature 2: flat ring instead of the dome distance indicator ---
        distanceVisualizeListener = new DistanceVisualizeListener();
        voiceClient.getEventBus().register(this, distanceVisualizeListener);
        MinecraftForge.EVENT_BUS.register(distanceVisualizeListener.getRenderer());
    }

    @Override
    public void onAddonShutdown() {
        if (keyBindHandler != null) {
            keyBindHandler.unregister();
        }
        if (voiceClient != null) {
            voiceClient.getEventBus().unregister(this);
        }
        if (distanceVisualizeListener != null) {
            MinecraftForge.EVENT_BUS.unregister(distanceVisualizeListener.getRenderer());
        }
    }
}
