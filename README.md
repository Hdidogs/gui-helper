# Gui Helper

A Fabric 1.20.6 framework for building Minecraft GUIs from a single `Gui` object that holds every object and its data.

You describe a screen once, register it under an `Identifier`, and the server opens it by that id. Values come back the same way.

---

## Quick start

```java
// client — build and register
Gui shop = GuiRegistry.register(GuiHelper.id("shop"), new Gui()
        .setTitle(Text.literal("Shop"))
        .setBackground(new Texture(GuiHelper.id("textures/gui/shop.png"), 0, 0, 176, 166, 256, 256))
        .setDarkBackground(true));

shop.addObject(new TextObject("price", new TextWithDetail(Text.literal("0"), ColorHelper.WHITE, 0.5f),
        8, 20, 60, 10, Align.CENTER));

// server — open it
GuiNetwork.open(player, GuiHelper.id("shop"), new GuiData().set("price", 2000));
```

Every object takes a **String id** first. That id is the key for lookup, for server-sent data, and for values sent back — so it must be unique within a `Gui` (`addObject` throws otherwise).

---

## Objects

All extend `GuiObject`. Coordinates are relative to the GUI's top-left corner.

| Object | Purpose | Value type |
|---|---|---|
| `TextObject` | Text, with `Align` / `VerticalAlign` inside a box | — |
| `TextureObject` | A texture | — |
| `ItemRenderObject` | An `ItemStack` with count, optional vanilla item tooltip | — |
| `ButtonObject` | Clickable, `Runnable onPress` | — |
| `ToggleObject` | On/off, two textures | `boolean` |
| `RadioButtonObject` | One group, exclusive options | `String` |
| `DropdownObject` | Vector-drawn expanding list, scrolls past `maxVisibleOptions` | `String` |
| `TextFieldObject` | Text input | `String` |
| `BoxObject` | Container, clips and scrolls its children | — |
| `ScrollBarObject` | Bar owned by a `BoxObject` | — |

### Shared by every object

```java
object.setLabel(new TextWithDetail(Text.literal("Name"), 0.5f));   // drawn above, left-aligned
object.setTooltip(Text.literal("Line one"), Text.literal("Line two"));
object.setShowWhen(toggle::getValue);                              // re-evaluated every frame
```

### Reading values

```java
gui.getObject("sound", ToggleObject.class).getValue();
gui.collectValues();   // GuiData of every stateful object, boxes included
```

`getObject` recurses into boxes.

---

## State

Values live on the **object**, not the widget, which decides when they reset:

| Event | Result |
|---|---|
| Window resize | Preserved — widgets rebuild and re-read the object |
| Reopening the GUI | Reset to defaults |

A `Gui` is a singleton once registered, so `resetState()` runs from the `GuiRenderer` constructor — one per screen instance, not per `init()`. Set your own baseline with `setDefaultValue(...)`.

---

## Data flow

`GuiData` is a `String -> String` map with typed accessors, used in both directions.

```java
// server -> client, on open
GuiNetwork.open(player, id, new GuiData().set("price", 2000).set("sound", true));

// client -> server, on demand
ClientPlayNetworking.send(new GuiValuesPayload(id, gui.collectValues()));
```

An object picks up the entry matching **its own id** via `applyData`, and writes it back via `collectData`. `ItemRenderObject` also reads `<id>.count`.

Defaults are applied first, then server data — so an id absent from the payload leaves the default alone.

### Receiving values

```java
GuiValuesRegistry.register(GuiHelper.id("shop"), (player, values) -> {
    // runs on the server thread
});
```

The receiver rejects a payload unless the player has that GUI open (`GuiSessions`) and a listener is registered. The wire format is capped at 64 entries, 64-char keys, 256-char values.

**`GuiData` is untrusted.** The server cannot validate ids or types — `Gui` is client-only — so re-check everything against server state.

---

## Handler GUIs (inventories)

Slots are the sync protocol: index, order and count must match on both sides. So the **layout is common** (`src/main`) and the **look is client** (`src/client`), keyed by the same `Identifier`.

```java
// common — onInitialize, both sides
GuiHandlerRegistry.register(GuiHelper.id("forge"), new GuiHandler()
        .setTitle(Text.literal("Forge"))
        .addSlotGrid("input", 0, 8, 18, 3, 3)     // idPrefix, startIndex, x, y, columns, rows
        .addSlot(new SlotObject("output", 9, 120, 35))
        .setPlayerInventory(8, 84));

// client — a Gui under the same id
GuiRegistry.register(GuiHelper.id("forge"), buildForgeGui());

// server
GuiNetwork.openHandler(player, GuiHelper.id("forge"), data, inventory);
```

`getInventorySize()` is derived from the highest slot index, so size is never declared twice. To decorate slots, read their positions back rather than repeating them:

```java
for (SlotObject slot : GuiHandlerRegistry.get(id).getSlots()) {
    gui.addObject(new TextureObject(slot.getId(), Textures.TEXTURE_SLOT,
            slot.getObjectStartX() - 1, slot.getObjectStartY() - 1));
}
```

One `ExtendedScreenHandlerType<CustomScreenHandler, GuiOpenData>` carries the id and data in the vanilla open packet, so handlers can be registered at any time.

---

## Textures

```java
new Texture(id, drawStartX, drawStartY, drawSizeX, drawSizeY, textureSizeX, textureSizeY)
```

`drawStart` + `drawSize` select a region of a sheet; `textureSize` is the file's dimensions. The region stretches to whatever size the object declares.

**Nine-slice** keeps corners crisp at any size — it works on sub-regions, unlike vanilla's GUI sprite atlas:

```java
new Texture(...).setNineSlice(3);                 // all sides
new Texture(...).setNineSlice(6, 3, 6, 3);        // left, top, right, bottom
```

---

## Scaling

`gui.setSizeMultiplicator(2)` renders everything twice as big. Mouse coordinates are divided to match, and slot hit-testing follows. Tooltips stay at normal size at the real cursor.

---

## Overlays

A `DropdownObject`'s open list floats above everything and takes input first — clicks, wheel and ESC — so a widget underneath can't steal a click or show its tooltip. Implement `OverlayRenderer` for anything else that needs to escape draw order and clipping; `BoxWidget` forwards all five members with its scroll offset, so overlays work inside boxes.

---

## Debug

`GuiHelper.DEBUG` follows `FabricLoader.isDevelopmentEnvironment()`, so these exist under `runClient` and not in a built jar:

| Command | Opens |
|---|---|
| `/testGui [id]` | a registered `Gui` as a plain screen (default `debug`) |
| `/testHandler [id]` | a registered `GuiHandler` (default `debug_handler`, tab-completes) |

---

## Known limits

- A registered `Gui` is a shared instance — one screen at a time on the client, which is fine, but it is not re-entrant.
- `SlotObject.backgroundSprite` goes through `TexturedSlot`, so the identifier must be a sprite stitched into the **block atlas**, not a loose `textures/gui/*.png`.
- `GuiObject` still carries `texture` / `text` / `onPress` fields that most subclasses leave null.
