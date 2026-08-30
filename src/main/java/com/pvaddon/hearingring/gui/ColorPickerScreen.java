package com.pvaddon.hearingring.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.function.IntConsumer;

/**
 * A tap/click RGB color picker: a saturation/value square, a hue bar, and a
 * hex text box for direct entry, with a live preview swatch. Changes are
 * applied immediately through {@link #onApply} so the ring color updates in
 * real time as you drag; "Cancel" restores whatever color you opened with.
 */
public class ColorPickerScreen extends Screen {

    private static final int SQUARE_SIZE = 150;
    private static final int HUE_BAR_WIDTH = 20;
    private static final int HUE_BAR_GAP = 16;
    private static final int CELL = 5;

    private final Screen parent;
    private final int initialColor;
    private final IntConsumer onApply;

    private float hue;
    private float sat;
    private float val;

    private int squareX;
    private int squareY;
    private int hueBarX;
    private int hueBarY;

    private boolean draggingSquare;
    private boolean draggingHue;

    private EditBox hexBox;
    private boolean updatingHexBox;

    public ColorPickerScreen(Screen parent, Component title, int initialColor, IntConsumer onApply) {
        super(title);
        this.parent = parent;
        this.initialColor = initialColor & 0xFFFFFF;
        this.onApply = onApply;

        float[] hsb = Color.RGBtoHSB(
                (this.initialColor >> 16) & 0xFF,
                (this.initialColor >> 8) & 0xFF,
                this.initialColor & 0xFF,
                null);
        this.hue = hsb[0];
        this.sat = hsb[1];
        this.val = hsb[2];
    }

    @Override
    protected void init() {
        squareX = this.width / 2 - (SQUARE_SIZE + HUE_BAR_GAP + HUE_BAR_WIDTH) / 2;
        squareY = 45;
        hueBarX = squareX + SQUARE_SIZE + HUE_BAR_GAP;
        hueBarY = squareY;

        hexBox = new EditBox(this.font, squareX + 30, squareY + SQUARE_SIZE + 22, 90, 18, Component.literal("Hex"));
        hexBox.setMaxLength(6);
        hexBox.setValue(String.format("%06X", currentColor()));
        hexBox.setResponder(text -> {
            if (updatingHexBox) return;
            String cleaned = text.replace("#", "").trim();
            if (cleaned.length() == 6 && cleaned.matches("[0-9a-fA-F]{6}")) {
                int rgb = Integer.parseInt(cleaned, 16);
                float[] hsb = Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);
                hue = hsb[0];
                sat = hsb[1];
                val = hsb[2];
                applyColor(false);
            }
        });
        addRenderableWidget(hexBox);
        setInitialFocus(hexBox);

        addRenderableWidget(Button.builder(Component.literal("\u0e1a\u0e31\u0e19\u0e17\u0e36\u0e01"), btn -> onClose())
                .bounds(squareX, squareY + SQUARE_SIZE + 50, 100, 20).build());

        addRenderableWidget(Button.builder(Component.literal("\u0e22\u0e01\u0e40\u0e25\u0e34\u0e01"), btn -> {
            onApply.accept(initialColor);
            this.minecraft.setScreen(parent);
        }).bounds(squareX + 110, squareY + SQUARE_SIZE + 50, 100, 20).build());
    }

    private int currentColor() {
        return Color.HSBtoRGB(hue, sat, val) & 0xFFFFFF;
    }

    private void applyColor(boolean updateHex) {
        int rgb = currentColor();
        onApply.accept(rgb);
        if (updateHex && hexBox != null) {
            updatingHexBox = true;
            hexBox.setValue(String.format("%06X", rgb));
            updatingHexBox = false;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Keep the world visible behind the picker (light dim only) so the
        // ring color change can be previewed live while dragging.
        graphics.fill(0, 0, this.width, this.height, 0x66000000);

        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        drawSquare(graphics);
        drawHueBar(graphics);
        drawPreview(graphics);

        graphics.drawString(this.font, "Hex:", squareX, squareY + SQUARE_SIZE + 27, 0xAAAAAA, false);
    }

    private void drawSquare(GuiGraphics graphics) {
        for (int px = 0; px < SQUARE_SIZE; px += CELL) {
            for (int py = 0; py < SQUARE_SIZE; py += CELL) {
                float s = px / (float) SQUARE_SIZE;
                float v = 1.0f - (py / (float) SQUARE_SIZE);
                int color = Color.HSBtoRGB(hue, s, v);
                graphics.fill(squareX + px, squareY + py,
                        squareX + Math.min(px + CELL, SQUARE_SIZE), squareY + Math.min(py + CELL, SQUARE_SIZE),
                        0xFF000000 | (color & 0xFFFFFF));
            }
        }
        drawBorder(graphics, squareX, squareY, SQUARE_SIZE, SQUARE_SIZE);

        int markerX = squareX + Math.round(sat * SQUARE_SIZE);
        int markerY = squareY + Math.round((1.0f - val) * SQUARE_SIZE);
        drawCross(graphics, markerX, markerY);
    }

    private void drawCross(GuiGraphics graphics, int cx, int cy) {
        int r = 4;
        graphics.fill(cx - r, cy - 1, cx + r, cy, 0xFF000000);
        graphics.fill(cx - 1, cy - r, cx, cy + r, 0xFF000000);
        graphics.fill(cx - r, cy, cx + r, cy + 1, 0xFFFFFFFF);
        graphics.fill(cx, cy - r, cx + 1, cy + r, 0xFFFFFFFF);
    }

    private void drawHueBar(GuiGraphics graphics) {
        for (int py = 0; py < SQUARE_SIZE; py++) {
            float h = py / (float) SQUARE_SIZE;
            int color = Color.HSBtoRGB(h, 1.0f, 1.0f);
            graphics.fill(hueBarX, hueBarY + py, hueBarX + HUE_BAR_WIDTH, hueBarY + py + 1,
                    0xFF000000 | (color & 0xFFFFFF));
        }
        drawBorder(graphics, hueBarX, hueBarY, HUE_BAR_WIDTH, SQUARE_SIZE);

        int markerY = hueBarY + Math.round(hue * SQUARE_SIZE);
        graphics.fill(hueBarX - 2, Math.max(hueBarY, markerY - 1), hueBarX + HUE_BAR_WIDTH + 2,
                Math.min(hueBarY + SQUARE_SIZE, markerY + 1), 0xFFFFFFFF);
    }

    private void drawPreview(GuiGraphics graphics) {
        int px = squareX + 130 + HUE_BAR_GAP;
        int py = squareY + SQUARE_SIZE + 22;
        graphics.fill(px, py, px + 40, py + 18, 0xFF000000 | currentColor());
        drawBorder(graphics, px, py, 40, 18);
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h) {
        graphics.fill(x - 1, y - 1, x + w + 1, y, 0xFFFFFFFF);
        graphics.fill(x - 1, y + h, x + w + 1, y + h + 1, 0xFFFFFFFF);
        graphics.fill(x - 1, y - 1, x, y + h + 1, 0xFFFFFFFF);
        graphics.fill(x + w, y - 1, x + w + 1, y + h + 1, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (mouseX >= squareX && mouseX < squareX + SQUARE_SIZE
                    && mouseY >= squareY && mouseY < squareY + SQUARE_SIZE) {
                draggingSquare = true;
                updateFromSquare(mouseX, mouseY);
                return true;
            }
            if (mouseX >= hueBarX && mouseX < hueBarX + HUE_BAR_WIDTH
                    && mouseY >= hueBarY && mouseY < hueBarY + SQUARE_SIZE) {
                draggingHue = true;
                updateFromHueBar(mouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingSquare) {
            updateFromSquare(mouseX, mouseY);
            return true;
        }
        if (draggingHue) {
            updateFromHueBar(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingSquare = false;
        draggingHue = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateFromSquare(double mouseX, double mouseY) {
        float s = (float) ((mouseX - squareX) / SQUARE_SIZE);
        float v = 1.0f - (float) ((mouseY - squareY) / SQUARE_SIZE);
        sat = Math.max(0f, Math.min(1f, s));
        val = Math.max(0f, Math.min(1f, v));
        applyColor(true);
    }

    private void updateFromHueBar(double mouseY) {
        float h = (float) ((mouseY - hueBarY) / SQUARE_SIZE);
        hue = Math.max(0f, Math.min(1f, h));
        applyColor(true);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
