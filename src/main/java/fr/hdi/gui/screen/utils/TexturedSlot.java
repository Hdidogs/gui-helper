package fr.hdi.gui.screen.utils;

import fr.hdi.gui.screen.objects.SlotObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@Deprecated(since = "1.2.0", forRemoval = true)
public class TexturedSlot extends GuiSlot {
    public TexturedSlot(Inventory inventory, int index, int x, int y, Identifier texture) {
        this(inventory, new SlotObject("", index, x, y, texture));
    }

    protected TexturedSlot(Inventory inventory, SlotObject object) {
        super(null, object, inventory);
    }
}
