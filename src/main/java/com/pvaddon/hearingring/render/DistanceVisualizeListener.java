package com.pvaddon.hearingring.render;

import com.pvaddon.hearingring.config.PlasmoAddonSettings;
import su.plo.voice.api.client.event.render.VoiceDistanceRenderEvent;
import su.plo.voice.api.event.EventSubscribe;

/**
 * Prevents Plasmo Voice's default green dome from being drawn.
 * The replacement ring is drawn independently using the configured
 * hearing distance, not the speaker's microphone/broadcast distance.
 */
public class DistanceVisualizeListener {

    private final FlatRingRenderer renderer = new FlatRingRenderer();

    @EventSubscribe
    public void onDistanceRender(VoiceDistanceRenderEvent event) {
        // Replace Plasmo Voice's temporary green dome with the same temporary
        // visualization, but as a thin flat circle. The renderer keeps the
        // same lifetime/fade behavior as PV's own visualization.
        boolean ringEnabled = PlasmoAddonSettings.FLAT_RING_ENABLED != null
                && PlasmoAddonSettings.FLAT_RING_ENABLED.value();
        if (ringEnabled) {
            renderer.showMicrophoneRing(event.getRadius(), event.getColor());
            event.setCancelled(true);
        }
    }

    public FlatRingRenderer getRenderer() {
        return renderer;
    }
}
