package fr.hdi.gui.screen.utils;

import fr.hdi.gui.screen.objects.SlotObject;
import net.minecraft.inventory.Inventory;

@Deprecated(since = "1.2.0", forRemoval = true)
public class LockedSlot extends GuiSlot {
    public LockedSlot(Inventory inventory, int index, int x, int y) {
        super(null, new SlotObject("", index, x, y).setLocked(true), inventory);
    }
}
