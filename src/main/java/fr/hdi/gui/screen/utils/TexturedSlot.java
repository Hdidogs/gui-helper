package fr.hdi.gui.screen.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class TexturedSlot extends Slot {
    private final Identifier texture;

    public TexturedSlot(Inventory inventory, int index, int x, int y, Identifier texture) {
        super(inventory, index, x, y);
        this.texture = texture;
    }

    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, this.texture);
    }
}
