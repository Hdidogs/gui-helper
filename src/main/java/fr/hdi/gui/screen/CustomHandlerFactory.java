package fr.hdi.gui.screen;

import fr.hdi.gui.network.GuiOpenData;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CustomHandlerFactory implements ExtendedScreenHandlerFactory<GuiOpenData> {
    private GuiOpenData open;
    private Inventory inventory;

    public CustomHandlerFactory(GuiOpenData open) {
        this(open, null);
    }

    public CustomHandlerFactory(GuiOpenData open, Inventory inventory) {
        this.open = open;
        this.inventory = inventory;
    }

    @Override
    public GuiOpenData getScreenOpeningData(net.minecraft.server.network.ServerPlayerEntity player) {
        return open;
    }

    @Override
    public Text getDisplayName() {
        GuiHandler handler = GuiHandlerRegistry.get(open.gui());

        return handler != null ? handler.getTitle() : Text.empty();
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CustomScreenHandler(syncId, playerInventory, open, inventory);
    }
}
