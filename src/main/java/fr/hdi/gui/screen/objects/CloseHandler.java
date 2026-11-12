package fr.hdi.gui.screen.objects;

import fr.hdi.gui.screen.CustomScreenHandler;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface CloseHandler {
    void onClosed(CustomScreenHandler handler, PlayerEntity player);
}
