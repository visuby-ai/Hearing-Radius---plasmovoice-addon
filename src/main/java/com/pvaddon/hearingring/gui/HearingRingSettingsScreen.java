package com.pvaddon.hearingring.gui;

import com.pvaddon.hearingring.config.PlasmoAddonSettings;
import com.pvaddon.hearingring.config.AddonConfig;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * In-game control panel for this addon: +/- buttons for hearing distance,
 * on/off toggles, and buttons that open the click-to-pick RGB color picker for
 * the hearing ring and the microphone ring separately.
 *
 * Keybinds are registered through Plasmo Voice's native Hotkeys API.
 *
 * All the settings here (distance, toggles, ring colors) are the exact same
 * entries shown on Plasmo Voice's own Add-ons settings page - this screen
 * is just a more visual way to reach them, plus the color picker.
 *
 * Open with /hearingring, /hearingdistance gui, or /voicering gui.
 */
public class HearingRingSettingsScreen extends Screen {
    private enum Binding { NONE, OPEN_GUI, INCREASE, DECREASE }
    private Binding listeningFor = Binding.NONE;
    private Button openKeyButton;
    private Button increaseKeyButton;
    private Button decreaseKeyButton;

    public HearingRingSettingsScreen() {
        super(Component.literal("Hearing Ring Settings"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 55;

        // --- hearing distance: -5 / -1 / +1 / +5 ---
        addRenderableWidget(Button.builder(Component.literal("-5"), btn -> stepDistance(-5))
                .bounds(centerX - 100, y, 40, 20).build());
        addRenderableWidget(Button.builder(Component.literal("-1"), btn -> stepDistance(-1))
                .bounds(centerX - 55, y, 40, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+1"), btn -> stepDistance(1))
                .bounds(centerX + 15, y, 40, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+5"), btn -> stepDistance(5))
                .bounds(centerX + 60, y, 40, 20).build());

        y += 34;

        boolean limitEnabled = PlasmoAddonSettings.HEARING_LIMIT_ENABLED != null
                && PlasmoAddonSettings.HEARING_LIMIT_ENABLED.value();
        addRenderableWidget(CycleButton.onOffBuilder(limitEnabled)
                .create(centerX - 100, y, 200, 20, Component.literal("Limit hearing distance"), (btn, value) -> {
                    if (PlasmoAddonSettings.HEARING_LIMIT_ENABLED != null) {
                        PlasmoAddonSettings.HEARING_LIMIT_ENABLED.set(value);
                    }
                }));

        y += 24;

        boolean ringEnabled = PlasmoAddonSettings.FLAT_RING_ENABLED != null
                && PlasmoAddonSettings.FLAT_RING_ENABLED.value();
        addRenderableWidget(CycleButton.onOffBuilder(ringEnabled)
                .create(centerX - 100, y, 200, 20, Component.literal("Flat ring indicator"), (btn, value) -> {
                    if (PlasmoAddonSettings.FLAT_RING_ENABLED != null) {
                        PlasmoAddonSettings.FLAT_RING_ENABLED.set(value);
                    }
                }));

        y += 30;

        // --- hearing ring color ---
        addRenderableWidget(Button.builder(Component.literal("Hearing ring color"), btn ->
                this.minecraft.setScreen(new ColorPickerScreen(this,
                        Component.literal("Hearing Ring Color"),
                        PlasmoAddonSettings.getRingColor(),
                        PlasmoAddonSettings::setRingColor)))
                .bounds(centerX - 100, y, 200, 20).build());

        y += 30;

        // --- microphone ring: override toggle + color ---
        boolean micOverride = PlasmoAddonSettings.MIC_RING_COLOR_OVERRIDE != null
                && PlasmoAddonSettings.MIC_RING_COLOR_OVERRIDE.value();
        addRenderableWidget(CycleButton.onOffBuilder(micOverride)
                .create(centerX - 100, y, 200, 20, Component.literal("Custom mic ring color"), (btn, value) -> {
                    if (PlasmoAddonSettings.MIC_RING_COLOR_OVERRIDE != null) {
                        PlasmoAddonSettings.MIC_RING_COLOR_OVERRIDE.set(value);
                    }
                }));

        y += 24;

        addRenderableWidget(Button.builder(Component.literal("Mic ring color"), btn ->
                this.minecraft.setScreen(new ColorPickerScreen(this,
                        Component.literal("Mic Ring Color"),
                        PlasmoAddonSettings.getMicRingColor(),
                        PlasmoAddonSettings::setMicRingColor)))
                .bounds(centerX - 100, y, 200, 20).build());

        y += 34;

        // --- click-to-bind controls ---
        openKeyButton = addRenderableWidget(Button.builder(keyLabel("Open GUI", AddonConfig.OPEN_GUI_KEY.get()), btn -> startBinding(Binding.OPEN_GUI))
                .bounds(centerX - 100, y, 200, 20).build());
        y += 24;
        increaseKeyButton = addRenderableWidget(Button.builder(keyLabel("Increase distance", AddonConfig.INCREASE_DISTANCE_KEY.get()), btn -> startBinding(Binding.INCREASE))
                .bounds(centerX - 100, y, 200, 20).build());
        y += 24;
        decreaseKeyButton = addRenderableWidget(Button.builder(keyLabel("Decrease distance", AddonConfig.DECREASE_DISTANCE_KEY.get()), btn -> startBinding(Binding.DECREASE))
                .bounds(centerX - 100, y, 200, 20).build());
        y += 30;

        addRenderableWidget(Button.builder(Component.literal("Done"), btn -> onClose())
                .bounds(centerX - 50, y, 100, 20).build());
    }

    private void startBinding(Binding binding) {
        listeningFor = binding;
        refreshKeyButtons();
    }

    private void refreshKeyButtons() {
        if (openKeyButton != null) openKeyButton.setMessage(listeningFor == Binding.OPEN_GUI ? Component.literal("Press a key...") : keyLabel("Open GUI", AddonConfig.OPEN_GUI_KEY.get()));
        if (increaseKeyButton != null) increaseKeyButton.setMessage(listeningFor == Binding.INCREASE ? Component.literal("Press a key...") : keyLabel("Increase distance", AddonConfig.INCREASE_DISTANCE_KEY.get()));
        if (decreaseKeyButton != null) decreaseKeyButton.setMessage(listeningFor == Binding.DECREASE ? Component.literal("Press a key...") : keyLabel("Decrease distance", AddonConfig.DECREASE_DISTANCE_KEY.get()));
    }

    private static Component keyLabel(String action, int key) {
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name == null) name = switch (key) {
            case GLFW.GLFW_KEY_UNKNOWN -> "None";
            case GLFW.GLFW_KEY_SPACE -> "Space";
            case GLFW.GLFW_KEY_LEFT_BRACKET -> "[";
            case GLFW.GLFW_KEY_RIGHT_BRACKET -> "]";
            case GLFW.GLFW_KEY_F1 -> "F1"; case GLFW.GLFW_KEY_F2 -> "F2"; case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4"; case GLFW.GLFW_KEY_F5 -> "F5"; case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7"; case GLFW.GLFW_KEY_F8 -> "F8"; case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10"; case GLFW.GLFW_KEY_F11 -> "F11"; case GLFW.GLFW_KEY_F12 -> "F12";
            default -> "Key " + key;
        };
        return Component.literal(action + ": " + name.toUpperCase());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningFor != Binding.NONE) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                listeningFor = Binding.NONE;
                refreshKeyButtons();
                return true;
            }
            switch (listeningFor) {
                case OPEN_GUI -> AddonConfig.OPEN_GUI_KEY.set(keyCode);
                case INCREASE -> AddonConfig.INCREASE_DISTANCE_KEY.set(keyCode);
                case DECREASE -> AddonConfig.DECREASE_DISTANCE_KEY.set(keyCode);
                default -> { }
            }
            listeningFor = Binding.NONE;
            refreshKeyButtons();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void stepDistance(int delta) {
        if (PlasmoAddonSettings.HEARING_DISTANCE == null) return;
        int current = PlasmoAddonSettings.HEARING_DISTANCE.value();
        int next = Math.max(1, Math.min(128, current + delta));
        PlasmoAddonSettings.HEARING_DISTANCE.set(next);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0x90101010);

        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        int distance = PlasmoAddonSettings.HEARING_DISTANCE != null
                ? PlasmoAddonSettings.HEARING_DISTANCE.value() : 48;
        graphics.drawCenteredString(this.font,
                Component.literal("Hearing distance: " + distance + " blocks"),
                this.width / 2, 40, 0xFFFF55);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
