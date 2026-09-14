package fr.hdi.gui.screen;

import fr.hdi.gui.GuiHelper;
import fr.hdi.gui.network.GuiOpenData;
import fr.hdi.gui.screen.objects.SlotObject;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.screen.utils.GuiSlot;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class CustomScreenHandler extends ScreenHandler {
    public static final String GROUP_KEY = "gui.group";

    public static ExtendedScreenHandlerType<CustomScreenHandler, GuiOpenData> TYPE;

    private GuiHandler guiHandler;
    private GuiData data;
    private Inventory inventory;
    private String activeGroup;

    public static void register() {
        TYPE = Registry.register(Registries.SCREEN_HANDLER, GuiHelper.id("handler"),
                new ExtendedScreenHandlerType<>(CustomScreenHandler::new, GuiOpenData.PACKET_CODEC));
    }

    public CustomScreenHandler(int syncId, PlayerInventory playerInventory, GuiOpenData open) {
        this(syncId, playerInventory, open, null);
    }

    public CustomScreenHandler(int syncId, PlayerInventory playerInventory, GuiOpenData open, Inventory inventory) {
        super(TYPE, syncId);

        this.guiHandler = GuiHandlerRegistry.get(open.gui());
        this.data = open.data();

        if (guiHandler == null) throw new IllegalStateException("No gui handler registered for " + open.gui());

        this.inventory = inventory != null ? inventory : new SimpleInventory(guiHandler.getInventorySize());
        this.activeGroup = normalizeGroup(data.getString(GROUP_KEY, guiHandler.getDefaultGroup()));

        for (SlotObject slot : guiHandler.getSlots()) addSlot(new GuiSlot(this, slot, this.inventory));

        if (guiHandler.hasPlayerInventory()) addPlayerInventory(playerInventory, guiHandler.getPlayerInventoryX(), guiHandler.getPlayerInventoryY());

        this.inventory.onOpen(playerInventory.player);
    }

    public GuiData getData() {
        return data;
    }

    public GuiHandler getGuiHandler() {
        return guiHandler;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getContentSize() {
        return guiHandler.getSlots().size();
    }

    public SlotObject getSlotObject(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= getContentSize()) return null;

        return guiHandler.getSlots().get(slotIndex);
    }

    public String getActiveGroup() {
        return activeGroup;
    }

    public void setActiveGroup(String group) {
        this.activeGroup = normalizeGroup(group);
    }

    public boolean isGroupActive(String group) {
        return group == null || group.equals(activeGroup);
    }

    private static String normalizeGroup(String group) {
        return group == null || group.isEmpty() ? null : group;
    }

    private void addPlayerInventory(PlayerInventory playerInventory, int x, int y) {
        for (int row = 0; row < GuiHandler.PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < GuiHandler.PLAYER_INVENTORY_COLUMNS; column++) {
                addSlot(new Slot(playerInventory, GuiHandler.PLAYER_INVENTORY_COLUMNS + column + row * GuiHandler.PLAYER_INVENTORY_COLUMNS,
                        x + column * GuiHandler.SLOT_SIZE, y + row * GuiHandler.SLOT_SIZE));
            }
        }

        for (int column = 0; column < GuiHandler.PLAYER_INVENTORY_COLUMNS; column++) {
            addSlot(new Slot(playerInventory, column, x + column * GuiHandler.SLOT_SIZE, y + GuiHandler.HOTBAR_OFFSET_Y));
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        SlotObject object = getSlotObject(slotIndex);

        if (object != null && !isGroupActive(object.getGroup())) return;

        if (object != null && object.hasOnClick()) {
            if (!player.getWorld().isClient) object.getOnClick().onClick(this, player, button, actionType);

            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasStack() || !slot.canTakeItems(player)) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getStack();
        ItemStack originalStack = slotStack.copy();
        int contentSize = getContentSize();

        if (slotIndex < contentSize) {
            if (!insertItem(slotStack, contentSize, this.slots.size(), true)) return ItemStack.EMPTY;
        } else if (!insertItem(slotStack, 0, contentSize, false)) {
            return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();

        return originalStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        inventory.onClose(player);
    }
}
