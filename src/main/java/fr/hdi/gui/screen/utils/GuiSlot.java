package fr.hdi.gui.screen.utils;

import com.mojang.datafixers.util.Pair;
import fr.hdi.gui.screen.CustomScreenHandler;
import fr.hdi.gui.screen.objects.SlotObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class GuiSlot extends Slot {
    private final CustomScreenHandler handler;
    private final SlotObject object;
    private boolean clientShown = true;

    public GuiSlot(CustomScreenHandler handler, SlotObject object, Inventory inventory) {
        super(inventory, object.getIndex(), object.getObjectStartX(), object.getObjectStartY());
        this.handler = handler;
        this.object = object;
    }

    public SlotObject getObject() {
        return object;
    }

    public void setClientShown(boolean clientShown) {
        this.clientShown = clientShown;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private boolean isGroupActive() {
        return handler == null || handler.isGroupActive(object.getGroup());
    }

    @Override
    public boolean isEnabled() {
        return clientShown && isGroupActive();
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return !object.isLocked() && isGroupActive();
    }

    @Override
    public boolean canTakeItems(PlayerEntity player) {
        return !object.isLocked() && isGroupActive();
    }

    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        if (!object.hasBackgroundSprite()) return super.getBackgroundSprite();

        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, object.getBackgroundSprite());
    }
}
