# PV Hearing Radius & Flat Ring

Created with AI assistance. You are free to modify and adapt this mod.

Forge 1.20.1 / Plasmo Voice 2.1.13 client addon

## Features

### 1. Plasmo Voice microphone ring

- Keeps Plasmo Voice's microphone distance indicator enabled.
- Changes its appearance from the default green dome to a **thin, flat circular ring**.
- Uses the actual microphone distance reported by Plasmo Voice.
- Behaves like the original indicator: it appears when Plasmo Voice shows the microphone range, then fades out.
- Use `/voicering toggle` to enable or disable the flat microphone ring.

### 2. Hearing ring and addon settings

All addon settings are available under:

`Plasmo Voice → Settings → Add-ons`

Available settings:

- `Hearing distance` — 1 to 128 blocks
- `Limit hearing distance`
- `Flat ring indicator`
- `Ring opacity`
- Hearing ring color — separate Red, Green, and Blue sliders
- `Custom mic ring color`
- Microphone ring color — separate Red, Green, and Blue sliders

The colors use separate RGB sliders because Plasmo Voice's built-in settings page only supports sliders, toggles, and dropdowns. If you prefer a visual color picker, use the addon's in-game GUI described below. Both menus read and save the same settings.

### 3. Commands

- `/hearingdistance` — Shows the current hearing distance.
- `/hearingdistance <1-128>` — Sets the hearing distance.
- `/hearingdistance toggle` — Enables or disables the hearing-distance limit.
- `/voicering toggle` — Enables or disables the flat microphone ring.
- `/voicering color <0xRRGGBB>` — Sets the hearing ring color.
- `/voicering alpha <0-255>` — Sets the ring opacity.
- `/hearingring`, `/hearingdistance gui`, or `/voicering gui` — Opens the in-game settings GUI.

### 4. In-game settings GUI

Open the GUI with any of the commands above. Every option in this menu uses the same settings as the Plasmo Voice Add-ons page.

- Use `-5`, `-1`, `+1`, and `+5` to adjust hearing distance instantly.
- Toggle `Limit hearing distance` and `Flat ring indicator`.
- **Hearing ring color** opens a visual color picker for the hearing-distance ring. It includes a saturation/value square, hue slider, and hex input field.
- Toggle **Custom mic ring color** to use your own microphone-ring color instead of Plasmo Voice's default color.
- **Mic ring color** opens the same color picker for the microphone ring.
- Drag on the color square or hue bar to update the color in real time. The preview is visible immediately in the game world behind the menu.
- **Cancel** restores the color that was active before opening the picker. **Save** keeps the selected color.
- **Increase distance** and **Decrease distance** let you set keybinds directly in the GUI. Left-click and press a key to assign it, right-click to clear it, or press `Esc` to cancel without making changes.

### 5. Keybinds

Keybinds can be configured in two places, and both use the same settings:

- Directly in the addon's GUI.
- `Options → Controls → Key Binds → PV Hearing Radius & Flat Ring`

No keys are bound by default.

- `Increase hearing distance` and `Decrease hearing distance` change the distance by 1 block.
- Hold `Shift` while using either keybind to change the distance by 5 blocks.

## Keybinds in 1.7.0

Keybinds can also be set directly from:

`Plasmo Voice → Settings → Add-ons`

Available keybinds:

- Open GUI key
- Increase distance key
- Decrease distance key

These keybind settings are checked every client tick and are used to open the GUI or adjust the hearing distance. They are fully functional, not display-only options.