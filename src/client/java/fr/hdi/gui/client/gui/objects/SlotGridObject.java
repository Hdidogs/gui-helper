package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.SlotGridWidget;
import fr.hdi.gui.screen.GuiHandler;
import fr.hdi.gui.screen.GuiHandlerRegistry;
import fr.hdi.gui.screen.objects.SlotObject;
import fr.hdi.gui.utils.Texture;
import fr.hdi.gui.utils.Textures;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SlotGridObject extends GuiObject {
    private List<SlotObject> slots;
    private int minX;
    private int minY;
    private Texture slotTexture = Textures.TEXTURE_SLOT;

    public SlotGridObject(String id, Identifier handlerId, int objectStartX, int objectStartY) {
        this(id, findSlots(id, handlerId), objectStartX, objectStartY);
    }

    private SlotGridObject(String id, List<SlotObject> slots, int objectStartX, int objectStartY) {
        super(id, ObjectType.SLOT_GRID, objectStartX, objectStartY, getSpan(slots, true), getSpan(slots, false));
        this.slots = slots;
        this.minX = slots.stream().mapToInt(SlotObject::getObjectStartX).min().orElse(0);
        this.minY = slots.stream().mapToInt(SlotObject::getObjectStartY).min().orElse(0);
    }

    private static List<SlotObject> findSlots(String id, Identifier handlerId) {
        GuiHandler handler = GuiHandlerRegistry.get(handlerId);

        if (handler == null) throw new IllegalArgumentException("No gui handler registered for " + handlerId);

        List<SlotObject> found = new ArrayList<>();

        for (SlotObject slot : handler.getSlots()) {
            if (isInGrid(id, slot.getId())) found.add(slot);
        }

        if (found.isEmpty()) throw new IllegalArgumentException("No slot grid " + id + " in " + handlerId);

        return found;
    }

    private static boolean isInGrid(String id, String slotId) {
        String prefix = id + "_";

        return slotId.startsWith(prefix) && slotId.length() > prefix.length() && slotId.substring(prefix.length()).chars().allMatch(Character::isDigit);
    }

    private static int getSpan(List<SlotObject> slots, boolean horizontal) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (SlotObject slot : slots) {
            int value = horizontal ? slot.getObjectStartX() : slot.getObjectStartY();

            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        return max - min + GuiHandler.SLOT_SIZE;
    }

    public List<SlotObject> getSlots() {
        return slots;
    }

    public boolean contains(SlotObject slot) {
        return slots.contains(slot);
    }

    public int getRelativeX(SlotObject slot) {
        return slot.getObjectStartX() - minX;
    }

    public int getRelativeY(SlotObject slot) {
        return slot.getObjectStartY() - minY;
    }

    public Texture getSlotTexture() {
        return slotTexture;
    }

    public SlotGridObject setSlotTexture(Texture slotTexture) {
        this.slotTexture = slotTexture;

        return this;
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new SlotGridWidget(this, bgX, bgY));
    }
}
