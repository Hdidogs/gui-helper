package fr.hdi.gui.screen;

import fr.hdi.gui.screen.objects.SlotObject;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class GuiHandler {
    public static final int SLOT_SIZE = 18;
    public static final int PLAYER_INVENTORY_COLUMNS = 9;
    public static final int PLAYER_INVENTORY_ROWS = 3;
    public static final int PLAYER_INVENTORY_SIZE = PLAYER_INVENTORY_COLUMNS * PLAYER_INVENTORY_ROWS + PLAYER_INVENTORY_COLUMNS;
    public static final int HOTBAR_OFFSET_Y = 58;

    private Identifier id;
    private Text title;
    private List<SlotObject> slots = new ArrayList<>();
    private boolean playerInventory;
    private int playerInventoryX;
    private int playerInventoryY;

    public GuiHandler() {
        title = Text.empty();
    }

    public GuiHandler addSlot(SlotObject slot) {
        slots.add(slot);

        return this;
    }

    public GuiHandler addSlotGrid(String idPrefix, int startIndex, int objectStartX, int objectStartY, int columns, int rows) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int index = startIndex + column + row * columns;

                addSlot(new SlotObject(idPrefix + "_" + index, index, objectStartX + column * SLOT_SIZE, objectStartY + row * SLOT_SIZE));
            }
        }

        return this;
    }

    public GuiHandler setPlayerInventory(int objectStartX, int objectStartY) {
        this.playerInventory = true;
        this.playerInventoryX = objectStartX;
        this.playerInventoryY = objectStartY;

        return this;
    }

    public List<SlotObject> getSlots() {
        return slots;
    }

    public SlotObject getSlot(String id) {
        return slots.stream().filter(slot -> slot.getId().equals(id)).findFirst().orElse(null);
    }

    public int getInventorySize() {
        int size = 0;

        for (SlotObject slot : slots) size = Math.max(size, slot.getIndex() + 1);

        return size;
    }

    public boolean hasPlayerInventory() {
        return playerInventory;
    }

    public int getPlayerInventoryX() {
        return playerInventoryX;
    }

    public int getPlayerInventoryY() {
        return playerInventoryY;
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public Text getTitle() {
        return title;
    }

    public GuiHandler setTitle(Text title) {
        this.title = title;

        return this;
    }
}
