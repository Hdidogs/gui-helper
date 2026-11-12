package fr.hdi.gui.screen.objects;

import fr.hdi.gui.screen.CustomScreenHandler;
import net.minecraft.inventory.Inventory;

@FunctionalInterface
public interface ContentChangedHandler {
    void onContentChanged(CustomScreenHandler handler, Inventory inventory);
}
