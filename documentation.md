# Gui Helper — Technical Documentation

Fabric 1.20.6 · Java 21 · `fr.hdi.gui`

Complete reference for building custom screens and inventory (handler) GUIs.

---

## Table of contents

1. [Core concepts](#1-core-concepts)
2. [Source set rules](#2-source-set-rules)
3. [`Gui` — the screen definition](#3-gui--the-screen-definition)
4. [`GuiObject` — shared API](#4-guiobject--shared-api)
5. [Object reference](#5-object-reference)
6. [Support types](#6-support-types)
7. [Registration and opening](#7-registration-and-opening)
8. [`GuiData` — server ⇄ client values](#8-guidata--server--client-values)
9. [Handler GUIs](#9-handler-guis)
10. [State and lifecycle](#10-state-and-lifecycle)
11. [Rendering internals](#11-rendering-internals)
12. [Extending the framework](#12-extending-the-framework)
13. [Cookbook](#13-cookbook)
14. [Pitfalls](#14-pitfalls)

---

## 1. Core concepts

Four rules explain almost everything:

**A GUI is one object.** A `Gui` holds a background, flags, and a flat list of `GuiObject`s. You build it once and register it.

**Everything is keyed by a String id.** An object's id is its lookup key (`gui.getObject(id)`), the key the server writes to (`applyData`), and the key it writes back (`collectData`). Ids must be unique within a `Gui` — `addObject` throws otherwise.

**A screen is keyed by an `Identifier`.** `gui-helper:shop`. The server opens a GUI by id; the client resolves it from its registry. Never a UUID — the server can't know a client-generated one.

**Coordinates are GUI-relative.** `(0, 0)` is the top-left of the GUI, not the screen. The framework centres the GUI and adds the offset.

### Pipeline

```
Gui (definition)  ──register──▶  GuiRegistry
                                      │
server: GuiNetwork.open(player, id, data)
                                      ▼
                        CustomScreen / CustomHandledScreen
                                      │
                                 GuiRenderer
                                      │
            object.register(x, y) ──▶ ClickableWidget ──▶ addDrawableChild
```

---

## 2. Source set rules

This project uses split source sets. Getting this wrong is the most common cause of a dedicated-server crash.

| Source set | Package | May contain |
|---|---|---|
| `src/client` | `fr.hdi.gui.client.*` | `Gui`, all `GuiObject`s, all widgets, screens, `GuiRegistry` |
| `src/main` | `fr.hdi.gui.*` | `GuiHandler`, `SlotObject`, registries, networking, `Texture`, `Color`, `GuiData` |

**`Gui` is client-only.** It holds `Texture`s and `Runnable` callbacks, which don't exist on a dedicated server. A slot layout, by contrast, must exist on **both** sides — which is why `GuiHandler` and `SlotObject` live in `src/main`.

| Register in | Entrypoint |
|---|---|
| `Gui` | `onInitializeClient()` |
| `GuiHandler` | `onInitialize()` — runs on **both** sides |
| `GuiValuesRegistry` listener | `onInitialize()` |

---

## 3. `Gui` — the screen definition

```java
Gui gui = new Gui()
        .setTitle(Text.literal("Shop"))
        .setBackground(new Texture(GuiHelper.id("textures/gui/shop.png"), 0, 0, 176, 166, 256, 256))
        .setDarkBackground(true)
        .setBlur(true)
        .setShouldPause(false)
        .setShouldCloseOnEsc(true)
        .setSizeMultiplicator(1.0f);
```

| Method | Default | Effect |
|---|---|---|
| `setTitle(Text)` | `""` | Screen title (narration; handler screens use `GuiHandler`'s) |
| `setBackground(Texture)` | none | Background, drawn at the GUI origin |
| `setSize(int, int)` | background draw size | Explicit size; needed only with no background |
| `setSizeMultiplicator(float)` | `1.0f` | Scales the whole GUI. Clamped to ≥ `0.1f` |
| `setDarkBackground(boolean)` | `false` | Vanilla darkening behind the GUI |
| `setBlur(boolean)` / `activeBlur()` | `true` | Background blur |
| `setShouldPause(boolean)` | `false` | Pause singleplayer |
| `setShouldCloseOnEsc(boolean)` | `true` | ESC closes the screen |

### Objects and values

```java
gui.addObject(object);                          // throws on duplicate id
gui.getObjects();                               // top-level list
gui.getObject("price");                         // recurses into boxes
gui.getObject("price", TextObject.class);       // typed, null if the type mismatches
gui.collectValues();                            // GuiData of every stateful object
gui.validateIds();                              // manual re-check
```

`getSizeX()` / `getSizeY()` return the explicit size if set, else the background's draw size, else `0`.

---

## 4. `GuiObject` — shared API

Every object type extends `GuiObject` and shares:

```java
object.setLabel(new TextWithDetail(Text.literal("Name"), 0.5f));
object.setTooltip(Text.literal("Line one"), Text.literal("Line two"));
object.setShowWhen(toggle::getValue);
```

| Method | Notes |
|---|---|
| `getId()` | Set in the constructor, always first parameter |
| `setLabel(TextWithDetail)` | Drawn one line **above** the object, left-aligned to it |
| `setTooltip(Text...)` | Varargs; multiple lines joined with `ScreenTexts.LINE_BREAK` |
| `setShowWhen(BooleanSupplier)` | Re-evaluated **every frame**; keep it cheap |
| `isShown()` | Evaluates the supplier |
| `getWidget()` | The live `ClickableWidget`, or `null` before `init()` |
| `getObjectStartX/Y()`, `getObjectSizeX/Y()` | Position and size |

`setShowWhen(false)` hides the widget **and** disables its input — it sets `widget.visible`, which `ClickableWidget.mouseClicked` checks.

### Data hooks

Override these in a custom object; all are no-ops by default.

| Hook | When | Purpose |
|---|---|---|
| `resetState()` | Once per screen opening | Restore defaults |
| `applyData(GuiData)` | Right after `resetState()` | Read server values |
| `collectData(GuiData)` | On `gui.collectValues()` | Write values to send back |

---

## 5. Object reference

### TextObject

```java
new TextObject("title", new TextWithDetail(Text.literal("Shop"), ColorHelper.WHITE, 0.7f), 8, 6)
new TextObject("price", text, 8, 20, 60, 10, Align.CENTER)
new TextObject("price", text, 8, 20, 60, 10, Align.CENTER, VerticalAlign.MIDDLE)
```

Aligns inside the declared box, accounting for `textSize` and measured width. Vertical centring uses an optical line height of **7px**, matching vanilla (`(height - 9) / 2 + 1`). With no size given, the widget measures itself and alignment is a no-op.

`applyData` replaces the text with the entry matching its id. No `collectData`.

### TextureObject

```java
new TextureObject("frame", texture, 8, 20)              // size from the texture region
new TextureObject("frame", texture, 8, 20, 64, 32)      // stretched (use nine-slice)
```

### ItemRenderObject

```java
new ItemRenderObject("reward", Items.DIAMOND, 40, 61, 0.5f)
new ItemRenderObject("reward", new ItemStack(Items.DIAMOND, 3), 40, 61, 0.5f)
        .setShowItemTooltip(true)
```

Draws the stack with its count overlay. `setShowItemTooltip(true)` shows the real vanilla item tooltip on hover without making the object clickable or focusable.

`applyData` reads the item id from its own key and the count from `<id>.count` (`ItemRenderObject.COUNT_SUFFIX`), ignoring unknown or malformed item ids. No `collectData`.

### ButtonObject

```java
new ButtonObject("close", texture, new TextWithDetail(Text.literal("X"), 0.7f), 158, 5, 13, 13, this::close)
        .setHoverAnimation(true)
        .setEnableWhen(toggle::getValue);
```

| Method | Default | Effect |
|---|---|---|
| `setHoverAnimation(boolean)` | `true` | 1px lift on hover |
| `setEnableWhen(BooleanSupplier)` | always on | Disabled ⇒ 50% opacity on texture **and** text, no hover lift, clicks blocked |

### ToggleObject

```java
new ToggleObject("sound", Textures.TEXTURE_TOGGLE_OFF, Textures.TEXTURE_TOGGLE_ON, 145, 27, 25, 13)
        .setDefaultValue(true);
```

Value: `boolean` via `getValue()` / `setValue()`. Full `applyData` / `collectData`.

### RadioButtonObject

One object is one **group** — exclusive selection with no cross-object messaging.

```java
new RadioButtonObject("mode", 60, 20)                                   // default textures
new RadioButtonObject("mode", offTexture, onTexture, 60, 20)
        .addOption("easy", 0, 0, new TextWithDetail(Text.literal("Easy"), 0.5f))
        .addOption("hard", 0, 16, new TextWithDetail(Text.literal("Hard"), 0.5f));
```

Option coordinates are relative to the group origin; labels draw to the right, vertically centred. The **first option added becomes the default**, so a group is never unselected. Clicks between options fall through. Value: `String`.

### DropdownObject

Vector-drawn — no texture.

```java
new DropdownObject("difficulty", 5, 25, 88, 13)
        .addOption("peaceful", new TextWithDetail(Text.literal("Peaceful"), 0.6f))
        .addOption("hard", new TextWithDetail(Text.literal("Hard"), 0.6f))
        .setMaxVisibleOptions(4)
        .setOptionSizeY(13)
        .setColors(background, border, hover);
```

| Default | Value |
|---|---|
| `DEFAULT_BACKGROUND` | `0x8B8B8B` |
| `DEFAULT_BORDER` | `0x373737` |
| `DEFAULT_HOVER` | `0x60FFFFFF` |
| `maxVisibleOptions` | `4` |
| `optionSizeY` | the object's height |

Past `maxVisibleOptions` the list scrolls with the wheel and draws a 2px indicator. Opening scrolls to the current selection. First option added is the default. Value: `String`.

The open list is an **overlay** — see [§11](#11-rendering-internals).

### TextFieldObject

```java
new TextFieldObject("name", texture, new TextWithDetail(Text.literal("Name")), 5, 147, 88, 13, 24)
        .setDefaultValue("player");
```

Last parameter is `maxLength`. The `TextWithDetail` is the widget **message**, not the content. Value: `String`.

### BoxObject

A container that clips and scrolls its children.

```java
BoxObject box = new BoxObject("list", backgroundTexture, 5, 85, 100, 50)
        .setScrollStep(9);

for (int i = 0; i < 20; i++) {
    box.addObject(new TextObject("line_" + i, text, 2, 2 + i * 10));
}
```

Child coordinates are relative to the **box** origin. Content height is measured from the children, and a scroll bar appears only when it overflows. Boxes nest — scroll offsets and clipping compose.

`getScrollBar()` lazily creates a default `ScrollBarObject` (`<boxId>_scroll_bar`); `setScrollBar(...)` replaces it.

### ScrollBarObject

Normally implicit. Customise it via `box.setScrollBar(...)`:

```java
new ScrollBarObject("bar")                                          // default textures, auto-placed
new ScrollBarObject("bar", barTexture, sliderTexture)
new ScrollBarObject("bar", barTexture, sliderTexture, x, y, sizeX, sizeY)
```

Auto-placed means the right edge of its owner, full height, width from the bar texture. The bar texture stretches; the slider keeps its texture height.

---

## 6. Support types

### Texture

```java
new Texture(identifier, drawStartX, drawStartY, drawSizeX, drawSizeY, textureSizeX, textureSizeY)
```

`drawStart` + `drawSize` select a region of a sheet; `textureSize` is the PNG's real dimensions. The region is stretched to whatever size the object declares.

**Nine-slice** — corners stay crisp at any size, and unlike vanilla's GUI sprite atlas it works on sub-regions of a shared sheet:

```java
new Texture(...).setNineSlice(3);                // all four sides
new Texture(...).setNineSlice(6, 3, 6, 3);       // left, top, right, bottom
```

Drawn smaller than `left + right` (or `top + bottom`), the edges and centre are skipped and only corners render.

`Textures` provides `TEXTURE_SLOT`, `TEXTURE_TOGGLE_ON/OFF`, `TEXTURE_RADIO_BUTTON_ON/OFF`, `TEXTURE_SCROLL_BAR`, `TEXTURE_SCROLL_SLIDER`.

### Color

ARGB. A value with no alpha bits is promoted to opaque, so `0xFFFFFF` works as written.

```java
new Color("gold", 0xBCA41C)
color.withAlpha(0.5f)      // new Color, faded
color.getHexColor()        // ARGB int for DrawContext
```

`ColorHelper` provides `WHITE`, `RED`, `GREEN`, `CARTEL`.

### TextWithDetail

Text plus colour and scale.

```java
new TextWithDetail(Text.literal("Hi"))
new TextWithDetail(Text.literal("Hi"), 0.5f)
new TextWithDetail(Text.literal("Hi"), ColorHelper.RED)
new TextWithDetail(Text.literal("Hi"), ColorHelper.RED, 0.5f)
```

Scale applies via the matrix stack. **Hitboxes are not scaled** except in `TextObject`, which measures itself.

### Align / VerticalAlign

`Align`: `LEFT`, `CENTER`, `RIGHT`. `VerticalAlign`: `TOP`, `MIDDLE`, `BOTTOM`.

---

## 7. Registration and opening

### Client

```java
GuiRegistry.register(GuiHelper.id("shop"), buildShopGui());   // returns the same Gui
GuiRegistry.get(id);
GuiRegistry.isRegistered(id);
GuiRegistry.open(id);                  // local, no server involved
GuiRegistry.open(id, data);
GuiRegistry.close();
```

`register` throws on a duplicate id and runs `validateIds()` on the tree.

### Server

```java
GuiNetwork.open(player, id);
GuiNetwork.open(player, id, data);
GuiNetwork.open(players, id, data);
GuiNetwork.close(player);
```

`close` picks the right mechanism: `closeHandledScreen()` for handler GUIs (keeping slot sync honest), a `CloseGuiPayload` otherwise. It also clears the `GuiSessions` entry.

An unknown id logs a warning client-side and opens nothing — the server cannot verify a `Gui` id, because that registry is client-only.

---

## 8. `GuiData` — server ⇄ client values

A `String -> String` map with typed accessors, used in both directions.

```java
new GuiData()
        .set("price", 2000)          // int
        .set("sound", true)          // boolean
        .set("scale", 0.5f)          // float
        .set("title", "Shop");       // String

data.getInt("price", 0);
data.getBoolean("sound", false);
data.getFloat("scale", 1.0f);
data.getString("title", "");
data.has("price");
```

Getters take a fallback and never throw — a malformed number returns the fallback.

### Server → client

```java
GuiNetwork.open(player, id, new GuiData().set("price", 2000).set("sound", true));
GuiNetwork.openHandler(player, id, data);
```

Each object reads the entry matching **its own id** in `applyData`. Defaults are applied first, so an absent id keeps the default.

### Client → server

```java
ClientPlayNetworking.send(new GuiValuesPayload(GuiHelper.id("shop"), gui.collectValues()));
```

`collectValues()` walks the whole tree, boxes included, and returns every stateful object's value.

### Receiving

```java
GuiValuesRegistry.register(GuiHelper.id("shop"), (player, values) -> {
    int price = values.getInt("price", 0);   // runs on the server thread
});
```

A payload is dropped unless the player has that GUI open (`GuiSessions`) **and** a listener is registered. Wire limits: `MAX_ENTRIES` 64, `MAX_KEY_LENGTH` 64, `MAX_VALUE_LENGTH` 256 — enforced by the codec, so an oversized packet never decodes.

> **`GuiData` is untrusted input.** The server cannot validate ids or types because `Gui` is client-only. A client can send any key with any value. Re-check everything against server state.

The session is cleared on disconnect, on `GuiNetwork.close`, and when another GUI opens — but **not** when the player closes the screen themselves, so a submit-then-close button isn't raced.

---

## 9. Handler GUIs

Slots are the sync protocol: index, order and count must match on both sides, because both construct the same `ScreenHandler`. So the layout is **common** and the look is **client**, sharing one `Identifier`.

### Layout — `src/main`, `onInitialize()`

```java
GuiHandlerRegistry.register(GuiHelper.id("forge"), new GuiHandler()
        .setTitle(Text.literal("Forge"))
        .addSlotGrid("input", 0, 8, 18, 3, 3)
        .addSlot(new SlotObject("output", 9, 120, 35))
        .setPlayerInventory(8, 84));
```

| Method | Parameters |
|---|---|
| `addSlot(SlotObject)` | one slot |
| `addSlotGrid(idPrefix, startIndex, x, y, columns, rows)` | grid, ids `<prefix>_<index>` |
| `setPlayerInventory(x, y)` | 3×9 main + hotbar at `y + HOTBAR_OFFSET_Y` |

Constants: `SLOT_SIZE` 18, `PLAYER_INVENTORY_COLUMNS` 9, `PLAYER_INVENTORY_ROWS` 3, `PLAYER_INVENTORY_SIZE` 36, `HOTBAR_OFFSET_Y` 58.

`SlotObject(id, index, x, y)` — `index` is into the **content inventory**, not the handler. `getInventorySize()` is derived from the highest index, so size is never declared twice.

### Look — `src/client`, same `Identifier`

```java
GuiRegistry.register(GuiHelper.id("forge"), buildForgeGui());
```

Read slot positions back rather than repeating them — this is the only way to guarantee the decoration can't drift from the real slots:

```java
for (SlotObject slot : GuiHandlerRegistry.get(GuiHelper.id("forge")).getSlots()) {
    gui.addObject(new TextureObject(slot.getId(), Textures.TEXTURE_SLOT,
            slot.getObjectStartX() - 1, slot.getObjectStartY() - 1));
}
```

The `- 1` matches vanilla: an 18×18 slot background around a 16×16 item.

### Opening

```java
GuiNetwork.openHandler(player, id);
GuiNetwork.openHandler(player, id, data);
GuiNetwork.openHandler(player, id, data, inventory);   // real inventory, server side
```

With no inventory the handler allocates a `SimpleInventory` of the derived size — that's also what the client does when it rebuilds.

### How it works

One `ExtendedScreenHandlerType<CustomScreenHandler, GuiOpenData>` is registered for the whole framework. Vanilla's `OpenScreenS2CPacket` carries only syncId, type and title, so the layout `Identifier` and the `GuiData` ride along as the extended data. Handlers can therefore be registered at any time without a registry entry each.

Handler slot indices are `0 .. contentSize-1` for content, then `contentSize .. +35` for the player inventory — which is exactly what `quickMove` uses for its `insertItem` ranges. `getContentSize()` is the **slot count**, not `getInventorySize()`; they differ if you map two slots to one inventory index.

`SlotObject`'s optional `backgroundSprite` goes through `TexturedSlot`, so the identifier must name a sprite stitched into the **block atlas** (e.g. `gui-helper:item/empty_slot` from `assets/gui-helper/textures/item/empty_slot.png`), not a loose `textures/gui/*.png`.

---

## 10. State and lifecycle

Values live on the **object**, not the widget. That decides when they survive:

| Event | Result |
|---|---|
| Window resize | **Preserved** — `init()` rebuilds widgets, which re-read the object |
| Reopening the GUI | **Reset** to defaults, then server data applied |

`resetState()` runs from the `GuiRenderer` constructor — once per screen instance, not per `init()`. The game calls `init()` for both opening and resizing, so it can't tell them apart; the constructor can.

Stateful: `ToggleObject` (boolean), `TextFieldObject`, `RadioButtonObject`, `DropdownObject` (String + scroll), `ScrollBarObject` (scroll), `ItemRenderObject` (stack).

A registered `Gui` is a **shared instance**. That's fine on a client with one screen at a time, but it is not re-entrant, and `setData` overwrites the previous opening's data.

---

## 11. Rendering internals

`GuiRenderer` owns the pipeline for both screen types:

| Method | Role |
|---|---|
| `init(w, h, Consumer<ClickableWidget>)` | Centring + the `register` loop |
| `syncVisibility()` | `showWhen` → `widget.visible` |
| `pushScale` / `popScale` | The `sizeMultiplicator` matrix |
| `scaled(double)` | Mouse-coordinate division |
| `drawBackground(context)` | Background blit |
| `renderOverlays` / `isOverlayCapturing` / `mouseClickedOverlay` / `mouseScrolledOverlay` / `closeOverlays` | Overlay pass |

### Scaling

`setSizeMultiplicator(2)` wraps rendering in a scale matrix; every mouse entry point divides by the same factor, so hit-testing, hover and slot picking stay correct. Tooltips draw after `render` at the raw cursor, unscaled.

### Overlays

An overlay both draws above everything **and** takes input above everything. Draw order alone isn't enough: a scissor is a hard clip, and `ParentElement.mouseClicked` walks `children()` in insertion order.

```java
public interface OverlayRenderer {
    boolean isCapturing();
    void renderOverlay(DrawContext context, int mouseX, int mouseY, float delta);
    boolean mouseClickedOverlay(double mouseX, double mouseY, int button);
    boolean mouseScrolledOverlay(double mouseX, double mouseY, double horizontal, double vertical);
    void closeOverlay();
}
```

- Screens call `mouseClickedOverlay` / `mouseScrolledOverlay` **before** `super`, so an open list wins over whatever is underneath.
- `clearTooltip()` runs while something is capturing, so a widget beneath can't surface its tooltip.
- ESC closes the overlay first, the screen second.
- `BoxWidget` implements all five as forwarding with `+ scrollY`, so overlays work at any nesting depth, and `renderOverlay` runs after `disableScissor()` so the list escapes the box's clip.

### Clipping

`DrawContext.enableScissor` takes raw framebuffer coordinates and **ignores the matrix stack**. Any scissor must be transformed first:

```java
Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
Vector4f start = matrix.transform(new Vector4f(getX(), getY(), 0.0F, 1.0F));
Vector4f end = matrix.transform(new Vector4f(getX() + width, getY() + height, 0.0F, 1.0F));
context.enableScissor((int) start.x, (int) start.y, (int) end.x, (int) end.y);
```

Vanilla's `ScissorStack` intersects with the enclosing rect, so nesting composes for free.

---

## 12. Extending the framework

### A new object type

1. Add a value to `ObjectType`.
2. Extend `GuiObject`; call one of the two constructors:

```java
GuiObject(String id, ObjectType type, int x, int y, int sizeX, int sizeY)
GuiObject(String id, ObjectType type, Texture texture, TextWithDetail text, int x, int y, int sizeX, int sizeY)
```

3. Implement `register(bgX, bgY)` and route through `cache(...)` — that's what wires up tooltips and the widget lookup:

```java
@Override
public ClickableWidget register(int bgX, int bgY) {
    return cache(new MyWidget(this, bgX, bgY));
}
```

4. Extend `ClickableWidget` (or `PressableWidget` / `TextFieldWidget`), positioning as `bgX + getObjectStartX()`.
5. If it has a value, override `resetState` / `applyData` / `collectData`.
6. If it draws outside its bounds, implement `OverlayRenderer`.

Decorative objects set `active = false` and override `isMouseOver` to return `false` — they still detect hover, because `ClickableWidget.render` sets `hovered` from bounds without consulting `active`.

### Drawing helpers

```java
UtilsWidgets.drawTexture(context, texture, x, y, sizeX, sizeY);
UtilsWidgets.drawTexture(context, texture, x, y, sizeX, sizeY, opacity);
UtilsWidgets.drawText(context, text, x, y, argb, scale);
UtilsWidgets.drawLabel(context, guiObject, x, y);
UtilsWidgets.drawItem(context, x, y, scale, item /* or ItemStack */);
```

`drawTexture` handles nine-slice, opacity (via `RenderSystem.setShaderColor`, restored afterwards) and null textures.

---

## 13. Cookbook

**Show an object conditionally**

```java
itemRender.setShowWhen(toggle::getValue);
```

**Disable a button until a choice is made**

```java
button.setEnableWhen(() -> !dropdown.getValue().equals("none"));
```

**Submit and close**

```java
// client
new ButtonObject("submit", texture, text, x, y, w, h, () ->
        ClientPlayNetworking.send(new GuiValuesPayload(id, gui.collectValues())));

// server
GuiValuesRegistry.register(id, (player, values) -> {
    if (!isAllowed(player, values)) return;
    apply(player, values);
    GuiNetwork.close(player);
});
```

**Server-driven shop entry**

```java
GuiNetwork.open(player, GuiHelper.id("shop"), new GuiData()
        .set("item", "minecraft:diamond_sword")
        .set("item.count", 1)
        .set("price", 2000));
```

**Scrollable list from live data**

```java
BoxObject box = new BoxObject("entries", null, 5, 20, 160, 100);

for (int i = 0; i < entries.size(); i++) {
    box.addObject(new TextObject("entry_" + i, new TextWithDetail(entries.get(i), 0.5f), 2, 2 + i * 10));
}
```

---

## 14. Pitfalls

| Symptom | Cause |
|---|---|
| Crash on a dedicated server | A `Gui`, `GuiObject` or widget referenced from `src/main` |
| Client can't rebuild a handler | `GuiHandler` registered in `onInitializeClient` instead of `onInitialize` |
| `Duplicate gui object id` | Two objects share an id — they must be unique per `Gui` |
| Values ignored server-side | No `GuiValuesRegistry` listener, or the player doesn't have that GUI open |
| Texture stretched oddly | Missing `setNineSlice(...)` for a resizable background |
| Text clipped in a box | `TextWithDetail` scale isn't applied to hitboxes outside `TextObject` |
| Slots misaligned at `sizeMultiplicator > 1` | Custom mouse handling that doesn't divide by the multiplier |
| Empty slot icon missing | `backgroundSprite` must be a block-atlas sprite, not `textures/gui/*.png` |
| Scroll resets | Expected on reopening; preserved on resize |

### Debug commands

Registered only when `GuiHelper.DEBUG` (`FabricLoader.isDevelopmentEnvironment()`) is true.

| Command | Opens |
|---|---|
| `/testGui [id]` | a registered `Gui` as a plain screen — default `debug` |
| `/testHandler [id]` | a registered `GuiHandler` — default `debug_handler`, tab-completes |
