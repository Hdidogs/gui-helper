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
| `setSizeMultiplicator(float)` | `1.0f` | Fixed scale for the whole GUI. Clamped to ≥ `0.1f` |
| `setFitToWindow(boolean)` | `false` | Derive the scale from the window instead — see below |
| `setFillRatio(float)` | `1.0f` | Fraction of the window fit mode targets. Clamped to `0.1f .. 1.0f` |
| `setMaxSizeMultiplicator(float)` | `4.0f` | Upper clamp for the derived scale |
| `setBuilder(Consumer<Gui>)` | none | Rebuild objects once per opening — see [§10](#10-state-and-lifecycle) |
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

### Fit to window

```java
gui.setFitToWindow(true).setFillRatio(0.8f).setMaxSizeMultiplicator(3.0f);
```

`GuiRenderer.init(w, h, adder)` then computes

```
min(w * fillRatio / getSizeX(), h * fillRatio / getSizeY())
```

clamped to `0.1f .. maxSizeMultiplicator`. The result lives on the **`GuiRenderer`**, not on the `Gui` — a shared `Gui` instance is never mutated by fit mode, and `getSizeMultiplicator()` keeps returning whatever you set.

`init()` runs on opening **and** on every window resize, so the GUI re-fits as the window changes. It needs a real size: with no background and no `setSize(...)`, `getSizeX()` is `0` and fit mode falls back to the fixed multiplier.

Fit mode makes a **non-integer** multiplier the normal case, including values below `1.0f` when the GUI is larger than the window. Both screen types already divide every mouse entry point by the multiplier and place slots in the same scaled space, so picking follows rendering — see [§11](#11-rendering-internals).

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

### TextAreaObject

Multiline text, backed by vanilla's `EditBoxWidget`.

```java
new TextAreaObject("notes", texture, null, 110, 60, 60, 50, 256)
        .setDefaultValue("");
```

Same constructor shape and the same value contract as `TextFieldObject` — `String`, `setDefaultValue(String)`, `resetState` / `applyData` / `collectData`, last parameter `maxLength`. Text wraps at the widget width and the wheel scrolls it once the content overflows.

Two things it does **not** inherit from vanilla:

- `ScrollableWidget.renderWidget` scissors with raw coordinates. `TexturedTextAreaWidget` re-implements it through `UtilsWidgets.enableScissor`, so the clip is transformed by the matrix stack and composes with an enclosing `BoxObject` and with the size multiplier.
- `ScrollableWidget.mouseScrolled` consumes **every** scroll while visible, without a bounds check — which would swallow the wheel for the whole box it sits in. The override requires `isWithinBounds` and `overflows()` first, so scrolling outside it falls through to the parent box.

The background is the `Texture` you pass; the vanilla text-field box is never drawn.

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
| `group(String)` | assigns a group to the slots the **previous** `addSlot` / `addSlotGrid` added |
| `setDefaultGroup(String)` | the group active when the GUI opens |
| `setPlayerInventory(x, y)` | 3×9 main + hotbar at `y + HOTBAR_OFFSET_Y` |

Constants: `SLOT_SIZE` 18, `PLAYER_INVENTORY_COLUMNS` 9, `PLAYER_INVENTORY_ROWS` 3, `PLAYER_INVENTORY_SIZE` 36, `HOTBAR_OFFSET_Y` 58.

`SlotObject(id, index, x, y)` — `index` is into the **content inventory**, not the handler. `getInventorySize()` is derived from the highest index, so size is never declared twice.

### Locked slots and click callbacks

Two independent fluent options on `SlotObject`:

| Method | Effect |
|---|---|
| `setLocked(boolean)` | the slot rejects every item movement — insert, take, shift-click, number-key swap, `Q` throw, double-click merge |
| `setOnClick(SlotClickHandler)` | server-side callback; the slot's default click handling is skipped |

```java
@FunctionalInterface
public interface SlotClickHandler {
    void onClick(CustomScreenHandler handler, PlayerEntity player, int button, SlotActionType actionType);
}
```

The callback receives the `CustomScreenHandler` because `SlotObject`s are registered once statically — per-opening context comes from `handler.getData()`, never from a field on the object.

Shop example — a row of locked, clickable offers:

```java
GuiHandlerRegistry.register(GuiHelper.id("shop"), new GuiHandler()
        .setTitle(Text.literal("Shop"))
        .addSlot(new SlotObject("offer_0", 0, 8, 18)
                .setLocked(true)
                .setOnClick((handler, player, button, actionType) -> {
                    int price = handler.getData().getInt("price_0");

                    buy(player, handler.getInventory().getStack(0), price);
                }))
        .setPlayerInventory(8, 84));
```

```java
SimpleInventory offers = new SimpleInventory(1);
offers.setStack(0, new ItemStack(Items.DIAMOND, 4));

GuiNetwork.openHandler(player, GuiHelper.id("shop"), new GuiData().set("price_0", 64), offers);
```

What a locked slot **displays** comes from the inventory passed to `GuiNetwork.openHandler(player, id, data, inventory)` — locking only blocks movement, it does not fill the slot. With no inventory the handler allocates an empty `SimpleInventory` and the slots render empty.

Locked slots still count in `getContentSize()` and `getInventorySize()`, so handler indices and the `quickMove` insert ranges never shift.

### Slot groups — pages inside one handler

A tab editor wants 15 inventory slots on one tab and 36 trade slots on another, without reopening the screen. Slots are the sync protocol, so **every** slot stays registered with a stable index for the whole lifetime of the handler; a group is only activated or deactivated.

```java
GuiHandlerRegistry.register(GuiHelper.id("editor"), new GuiHandler()
        .setTitle(Text.translatable("mymod.editor.title"))
        .addSlotGrid("inv", 0, 8, 18, 5, 3).group("inventory")
        .addSlotGrid("trade", 15, 8, 18, 9, 4).group("trade")
        .setDefaultGroup("inventory")
        .setPlayerInventory(8, 140));
```

`group(String)` reads best right after the call that added the slots; `addSlot(slot, group)` and `addSlotGrid(..., group)` are the same thing in one call, and `SlotObject.setGroup(String)` / `getGroup()` are there for slots you build yourself. A slot with **no** group (`null`, the default) is always active — chrome that belongs to every page.

| Method | Where | Effect |
|---|---|---|
| `SlotObject.setGroup(String)` / `getGroup()` | `src/main` | the slot's group; `null` = always active |
| `GuiHandler.group(String)` | `src/main` | applies a group to the last-added slots |
| `GuiHandler.setDefaultGroup(String)` | `src/main` | group active on opening |
| `GuiHandler.getGroups()` / `hasGroup(String)` / `getSlots(String)` | `src/main` | introspection |
| `CustomScreenHandler.getActiveGroup()` / `setActiveGroup(String)` | `src/main` | the live page |
| `CustomScreenHandler.isGroupActive(String)` | `src/main` | `group == null || group.equals(active)` |

The active group is seeded from the `GuiData` passed to `openHandler` under the key `CustomScreenHandler.GROUP_KEY` (`"gui.group"`), falling back to `setDefaultGroup(...)`. Because the same `GuiOpenData` rides the extended handler type to the client, both sides start on the same page:

```java
GuiNetwork.openHandler(player, GuiHelper.id("editor"),
        new GuiData().set(CustomScreenHandler.GROUP_KEY, "trade"));
```

**Rendering and picking are free.** In 1.20.6 `Slot.isEnabled()` gates all three of `HandledScreen`'s uses: `drawSlot`, the `focusedSlot` assignment in `render`, and `getSlotAt` — which is what `mouseClicked` picks with. `GuiSlot.isEnabled()` returns `handler.isGroupActive(object.getGroup())`, so an inactive slot stops drawing *and* stops being clickable with no client-side code of ours. Nothing is needed in `CustomHandledScreen`.

The **decoration** is yours, though: the `TextureObject` you drew behind a slot is an ordinary `GuiObject` and keeps rendering unless you say otherwise.

```java
for (SlotObject slot : GuiHandlerRegistry.get(GuiHelper.id("editor")).getSlots()) {
    gui.addObject(new TextureObject(slot.getId(), Textures.TEXTURE_SLOT,
                    slot.getObjectStartX() - 1, slot.getObjectStartY() - 1)
            .setShowWhen(() -> GuiPages.isActive(slot.getGroup())));
}
```

**Never trust the client.** `GuiSlot` returns `false` from `canInsert` and `canTakeItems` for an inactive group, exactly as it does for a locked slot — the two compose, and a locked slot in an inactive group is refused by either test alone. That covers `SWAP` (number keys), `THROW` (`Q`) and `PICKUP_ALL` (double-click merge), which all route through `canTakeItems` on the source. `quickMove` is the one hole: `insertItem` consults `canInsert` on every destination, so destinations are covered, but `quickMove` never consults `canTakeItems` on its source — so it tests the source slot itself and returns `ItemStack.EMPTY`. `onSlotClick` additionally drops any click on a slot whose group is inactive, before the `SlotClickHandler` and before `super`.

### Switching page

Switching is **C2S**, because the server is what enforces the gate. It never closes or rebuilds the screen — mouse focus, scroll offsets and text-field state all survive.

```java
new ButtonObject("tab_trade", texture, text, 60, 4, 44, 13, () -> GuiPages.set("trade"));
```

| `GuiPages` — `src/client` | Effect |
|---|---|
| `set(String group)` | applies the group to the open handler, then sends `GuiPagePayload` |
| `get()` | the active group of the open handler, or `null` |
| `isActive(String group)` | `null`-safe test for `setShowWhen` |
| `getHandler()` | the open `CustomScreenHandler`, or `null` |

`GuiPages` reads the handler off `MinecraftClient.currentScreen`, so it holds no state of its own and cannot drift. Outside a `CustomHandledScreen` every call is a no-op returning `null`.

`GuiPagePayload(Identifier gui, String group)` is validated exactly like `GuiValuesPayload`, plus two checks of its own:

| Check | Failure |
|---|---|
| `PacketCodecs.string(MAX_GROUP_LENGTH)` — 64 chars | decode fails |
| `GuiSessions.hasOpen(player, gui)` | warn, drop |
| `player.currentScreenHandler` is a `CustomScreenHandler` for that `Identifier` | warn, drop |
| `guiHandler.hasGroup(group)` | warn, drop |

The empty string means "no group active"; `setActiveGroup` normalises it and `null` to the same thing.

The flow is client-driven only. A server-side `setActiveGroup(...)` changes what the server enforces but does **not** reach the client — to open a GUI on a given page, seed `GROUP_KEY` in the `GuiData` instead.

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

`onSlotClick` routes an index in `0 .. contentSize-1` to its `SlotObject`: a click on an inactive group is dropped, then, with a click handler, it fires server-side only (`!player.getWorld().isClient`) and default handling is skipped.

Every content slot is one class, `GuiSlot`, reading locking, grouping and the background sprite straight off its `SlotObject` — so the options compose instead of multiplying into a subclass per combination. `LockedSlot`, `TexturedSlot` and `LockedTexturedSlot` remain as deprecated wrappers over `GuiSlot` for source compatibility; they build a detached `SlotObject` and pass no handler, so they are ungrouped and always enabled. Nothing in the framework constructs them any more. `canInsert` / `canTakeItems` return `false` when the slot is locked **or** its group is inactive, which covers `SWAP` (number keys), `THROW` (`Q`) and `PICKUP_ALL` (double-click merge) for free. `quickMove` is the exception: vanilla checks `canInsert` on the destination but never `canTakeItems` on the source, so it guards the source slot explicitly.

Handler slot indices are `0 .. contentSize-1` for content, then `contentSize .. +35` for the player inventory — which is exactly what `quickMove` uses for its `insertItem` ranges. `getContentSize()` is the **slot count**, not `getInventorySize()`; they differ if you map two slots to one inventory index.

`SlotObject`'s optional `backgroundSprite` goes through `GuiSlot.getBackgroundSprite()`, so the identifier must name a sprite stitched into the **block atlas** (e.g. `gui-helper:item/empty_slot` from `assets/gui-helper/textures/item/empty_slot.png`), not a loose `textures/gui/*.png`.

---

## 10. State and lifecycle

Values live on the **object**, not the widget. That decides when they survive:

| Event | Result |
|---|---|
| Window resize | **Preserved** — `init()` rebuilds widgets, which re-read the object |
| Reopening the GUI | **Reset** to defaults, then server data applied |

`resetState()` runs from the `GuiRenderer` constructor — once per screen instance, not per `init()`. The game calls `init()` for both opening and resizing, so it can't tell them apart; the constructor can.

Stateful: `ToggleObject` (boolean), `TextFieldObject`, `TextAreaObject`, `RadioButtonObject`, `DropdownObject` (String + scroll), `ScrollBarObject` (scroll), `ItemRenderObject` (stack).

A registered `Gui` is a **shared instance**. That's fine on a client with one screen at a time, but it is not re-entrant, and `setData` overwrites the previous opening's data.

### Rebuilding content at runtime

A dialogue tree or a quest list is data-driven and changes while the screen is open, but a registered `Gui` is built once at `onInitializeClient`. `setBuilder` is the seam:

```java
gui.setBuilder(built -> {
    BoxObject list = built.getObject("entries", BoxObject.class);

    for (int index = 0; index < built.getData().getInt("count", 0); index++) {
        built.addObject(list, new TextObject("entry_" + index, text(index), 2, 2 + index * 10));
    }
});
```

| Method | Effect |
|---|---|
| `setBuilder(Consumer<Gui>)` | the builder; `null` disables it |
| `hasBuilder()` / `getBuilder()` | introspection |
| `build()` | runs it — called for you, once per screen opening |
| `addObject(BoxObject, GuiObject)` | adds into a box **and** tracks it for the next rebuild |

`GuiRenderer`'s constructor calls `build()`, then `resetState()`, then `applyData()` — so objects the builder added take part in the normal data pipeline like any statically declared one, and the builder itself can already read `gui.getData()` (both `GuiRegistry.open` and `CustomHandledScreen` call `setData` before constructing the renderer).

Each run **removes exactly what the previous run added** and nothing else. Objects added through `addObject(...)` while the builder is running are recorded with the list that owns them — the `Gui`'s own list, or a `BoxObject`'s — and removed from it on the next run. Objects you declared statically are never touched. `validateIds()` re-runs afterwards, so a builder that emits a duplicate id throws at build time rather than rendering twice.

**What a builder means for a shared `Gui`.** The shared instance from [above](#10-state-and-lifecycle) is the reason `build()` is destructive rather than additive: opening the same `Gui` twice runs the builder twice against the *same* object list, so the second run must undo the first or the list grows every time. The consequences follow from that:

- Build from the **arguments** — `built.getData()` and your own live state — never by appending to what is already there.
- A stale `GuiObject` from a previous opening still holds the widget it cached; it is dropped with the object and never re-registered.
- Two screens on one `Gui` at once would fight over the object list. As with `setData`, that isn't supported, and a client only shows one screen at a time.
- No builder means no tracking and no removal: a `Gui` with no builder behaves exactly as before.

---

## 11. Rendering internals

`GuiRenderer` owns the pipeline for both screen types:

| Method | Role |
|---|---|
| `init(w, h, Consumer<ClickableWidget>)` | Fit computation, centring, and the `register` loop |
| `syncVisibility()` | `showWhen` → `widget.visible` |
| `pushScale` / `popScale` | The size-multiplier matrix |
| `pushUnscaled` | Cancels it for a full-screen pass |
| `getSizeMultiplicator()` | The live multiplier — derived in fit mode |
| `scaled(double)` | Mouse-coordinate division |
| `drawBackground(context)` | Background blit |
| `renderOverlays` / `isOverlayCapturing` / `mouseClickedOverlay` / `mouseScrolledOverlay` / `closeOverlays` | Overlay pass |

### Scaling

The multiplier lives on the `GuiRenderer` — either the `Gui`'s fixed `sizeMultiplicator`, or the value fit mode derives from the window in `init()` ([§3](#3-gui--the-screen-definition)). `pushScale` wraps rendering in a scale matrix; every mouse entry point divides by the same factor through `scaled(...)`, so hit-testing, hover and slot picking stay correct. Tooltips draw after `render` at the raw cursor, unscaled.

`HandledScreen` positions **and** hit-tests slots from `this.x` / `this.y` plus the raw slot coordinates, with no matrix applied. That works here because `CustomHandledScreen` puts both in the *same* scaled space: `this.x` / `this.y` are assigned from `renderer.getGuiX()` / `getGuiY()`, which are computed in scaled units, and every mouse coordinate reaching `super` has been divided by the multiplier. Rendering runs inside the scale matrix over the same numbers, so slot geometry and slot picking cannot drift apart — at a non-integer multiplier the two agree to within vanilla's own ±1 px tolerance in `isPointWithinBounds`, which is measured in scaled units and so widens to ±`multiplier` screen pixels. Slot pitch is 18 against a 16 px slot, so even at the `4.0f` clamp the tolerance zones of neighbouring slots meet without overlapping and `getSlotAt` stays unambiguous.

`pushUnscaled` is the escape hatch for the passes that are genuinely full-screen. `renderDarkening` and `renderPanoramaBackground` fill `0, 0, width, height` in raw screen coordinates; under the scale matrix a multiplier below `1.0f` would leave the edges of the screen uncovered, which fit mode makes reachable. Both are wrapped in `pushUnscaled` / `popScale`, cancelling the scale for exactly that draw. `applyBlur` is a post-processing pass and ignores the matrix stack entirely.

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
7. If it clips, scissor through `UtilsWidgets.enableScissor(context, startX, startY, endX, endY)` — never `DrawContext.enableScissor`, which takes raw framebuffer coordinates ([§11](#11-rendering-internals)).

Subclassing a vanilla widget, check what it does with the matrix stack and with bounds before trusting it. `ScrollableWidget` fails both: it scissors raw, and its `mouseScrolled` consumes every scroll while visible without testing `isWithinBounds`. `TexturedTextAreaWidget` overrides both — see [§5](#5-object-reference).

Decorative objects set `active = false` and override `isMouseOver` to return `false` — they still detect hover, because `ClickableWidget.render` sets `hovered` from bounds without consulting `active`.

### Drawing helpers

```java
UtilsWidgets.enableScissor(context, startX, startY, endX, endY);
UtilsWidgets.drawTexture(context, texture, x, y, sizeX, sizeY);
UtilsWidgets.drawTexture(context, texture, x, y, sizeX, sizeY, opacity);
UtilsWidgets.drawText(context, text, x, y, argb, scale);
UtilsWidgets.drawLabel(context, guiObject, x, y);
UtilsWidgets.drawItem(context, x, y, scale, item /* or ItemStack */);
```

`drawTexture` handles nine-slice, opacity (via `RenderSystem.setShaderColor`, restored afterwards) and null textures. `enableScissor` transforms the rect through the current matrix before handing it to `DrawContext`, so it composes with the size multiplier and with any enclosing `BoxObject`.

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

**…that changes while the screen is open**

```java
gui.addObject(new BoxObject("entries", null, 5, 20, 160, 100));

gui.setBuilder(built -> {
    BoxObject box = built.getObject("entries", BoxObject.class);

    for (int i = 0; i < entries.size(); i++) {
        built.addObject(box, new TextObject("entry_" + i, new TextWithDetail(entries.get(i), 0.5f), 2, 2 + i * 10));
    }
});
```

**Tabs in one handler GUI**

```java
// src/main
GuiHandlerRegistry.register(id, new GuiHandler()
        .addSlotGrid("inv", 0, 8, 18, 5, 3).group("inventory")
        .addSlotGrid("trade", 15, 8, 18, 9, 4).group("trade")
        .setDefaultGroup("inventory")
        .setPlayerInventory(8, 140));

// src/client
gui.addObject(new ButtonObject("tab_inventory", texture, inventoryText, 8, 4, 44, 13, () -> GuiPages.set("inventory")));
gui.addObject(new ButtonObject("tab_trade", texture, tradeText, 54, 4, 44, 13, () -> GuiPages.set("trade")));

tradeOnlyLabel.setShowWhen(() -> GuiPages.isActive("trade"));
```

**Fill the window whatever its size**

```java
gui.setFitToWindow(true).setFillRatio(0.8f).setMaxSizeMultiplicator(3.0f);
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
| Slots misaligned under `setFitToWindow(true)` | Same cause, now the normal case — the derived multiplier is rarely `1.0f` or even an integer. `this.x` / `this.y` and every mouse coordinate must be in the same scaled space |
| Shift-click drains a slot in an inactive group | `quickMove` never consults `canTakeItems` on the source — it must return `ItemStack.EMPTY` when the source slot's group is inactive |
| An inactive group still shows its slot background | `isEnabled()` hides the slot, not your `TextureObject` — gate the decoration with `setShowWhen(() -> GuiPages.isActive(group))` |
| Objects pile up each time a GUI opens | A builder that appends instead of rebuilding, or objects added to a box directly rather than via `gui.addObject(box, object)` so they aren't tracked |
| Empty slot icon missing | `backgroundSprite` must be a block-atlas sprite, not `textures/gui/*.png` |
| Shift-click drains a locked slot | Same hole, same fix — the source guard covers locked and grouped slots alike |
| Scroll resets | Expected on reopening; preserved on resize |

### Debug commands

Registered only when `GuiHelper.DEBUG` (`FabricLoader.isDevelopmentEnvironment()`) is true.

| Command | Opens |
|---|---|
| `/testGui [id]` | a registered `Gui` as a plain screen — default `debug` |
| `/testHandler [id]` | a registered `GuiHandler` — default `debug_handler`, tab-completes |
