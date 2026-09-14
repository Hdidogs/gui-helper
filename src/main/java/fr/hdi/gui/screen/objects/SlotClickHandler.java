package fr.hdi.gui.screen.objects;

import fr.hdi.gui.screen.CustomScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;

@FunctionalInterface
public interface SlotClickHandler {
    void onClick(CustomScreenHandler handler, PlayerEntity player, int button, SlotActionType actionType);
}
