package fr.hdi.gui.screen.objects;

import fr.hdi.gui.screen.CustomScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface SlotTakeHandler {
    void onTake(CustomScreenHandler handler, PlayerEntity player, ItemStack stack);
}
