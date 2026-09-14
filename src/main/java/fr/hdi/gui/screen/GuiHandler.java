package fr.hdi.gui.screen;

import fr.hdi.gui.screen.objects.SlotObject;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    private String defaultGroup;
    private int lastAddedIndex = -1;

    public GuiHandler() {
        title = Text.empty();
    }

    public GuiHandler addSlot(SlotObject slot) {
        lastAddedIndex = slots.size();
        slots.add(slot);

        return this;
    }

    public GuiHandler addSlot(SlotObject slot, String group) {
        return addSlot(slot.setGroup(group));
    }

    public GuiHandler addSlotGrid(String idPrefix, int startIndex, int objectStartX, int objectStartY, int columns, int rows) {
        int first = slots.size();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int index = startIndex + column + row * columns;

                slots.add(new SlotObject(idPrefix + "_" + index, index, objectStartX + column * SLOT_SIZE, objectStartY + row * SLOT_SIZE));
            }
        }

        lastAddedIndex = first;

        return this;
    }

    public GuiHandler addSlotGrid(String idPrefix, int startIndex, int objectStartX, int objectStartY, int columns, int rows, String group) {
        return addSlotGrid(idPrefix, startIndex, objectStartX, objectStartY, columns, rows).group(group);
    }

    public GuiHandler group(String group) {
        if (lastAddedIndex < 0) throw new IllegalStateException("group() must follow addSlot or addSlotGrid");

        for (int index = lastAddedIndex; index < slots.size(); index++) slots.get(index).setGroup(group);

        return this;
    }

    public GuiHandler setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup == null || defaultGroup.isEmpty() ? null : defaultGroup;

        return this;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public Set<String> getGroups() {
        Set<String> groups = new LinkedHashSet<>();

        for (SlotObject slot : slots) {
            if (slot.hasGroup()) groups.add(slot.getGroup());
        }

        return groups;
    }

    public boolean hasGroup(String group) {
        if (group == null) return true;

        for (SlotObject slot : slots) {
            if (group.equals(slot.getGroup())) return true;
        }

        return false;
    }

    public List<SlotObject> getSlots(String group) {
        List<SlotObject> found = new ArrayList<>();

        for (SlotObject slot : slots) {
            if (slot.getGroup() == null ? group == null : slot.getGroup().equals(group)) found.add(slot);
        }

        return found;
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
