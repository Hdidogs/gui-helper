package fr.hdi.gui.screen.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class LockedTexturedSlot extends TexturedSlot {
    public LockedTexturedSlot(Inventory inventory, int index, int x, int y, Identifier texture) {
        super(inventory, index, x, y, texture);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeItems(PlayerEntity player) {
        return false;
    }
}
