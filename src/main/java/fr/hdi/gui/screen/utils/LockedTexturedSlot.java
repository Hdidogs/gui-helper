package fr.hdi.gui.screen.utils;

import fr.hdi.gui.screen.objects.SlotObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@Deprecated(since = "1.2.0", forRemoval = true)
@SuppressWarnings("removal")
public class LockedTexturedSlot extends TexturedSlot {
    public LockedTexturedSlot(Inventory inventory, int index, int x, int y, Identifier texture) {
        super(inventory, new SlotObject("", index, x, y, texture).setLocked(true));
    }
}
