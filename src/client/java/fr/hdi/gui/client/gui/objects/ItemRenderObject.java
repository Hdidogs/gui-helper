package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.ItemRenderWidget;
import fr.hdi.gui.utils.GuiData;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemRenderObject extends GuiObject {
    public static final String COUNT_SUFFIX = ".count";

    private ItemStack defaultStack;
    private ItemStack stack;
    private float scale;
    private boolean showItemTooltip;

    public ItemRenderObject(String id, Item item, int objectStartX, int objectStartY, float scale) {
        this(id, new ItemStack(item), objectStartX, objectStartY, scale);
    }

    public ItemRenderObject(String id, ItemStack stack, int objectStartX, int objectStartY, float scale) {
        super(id, ObjectType.ITEM_RENDER, objectStartX, objectStartY, (int) (16 * scale), (int) (16 * scale));
        this.scale = scale;
        this.defaultStack = stack;
        this.stack = stack.copy();
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public Item getItem() {
        return stack.getItem();
    }

    public float getScale() {
        return scale;
    }

    public boolean isShowItemTooltip() {
        return showItemTooltip;
    }

    public ItemRenderObject setShowItemTooltip(boolean showItemTooltip) {
        this.showItemTooltip = showItemTooltip;

        return this;
    }

    @Override
    public void resetState() {
        this.stack = defaultStack.copy();
    }

    @Override
    public void applyData(GuiData data) {
        if (!data.has(getId())) return;

        Identifier item = Identifier.tryParse(data.getString(getId(), ""));

        if (item == null || !Registries.ITEM.containsId(item)) return;

        this.stack = new ItemStack(Registries.ITEM.get(item), Math.max(1, data.getInt(getId() + COUNT_SUFFIX, 1)));
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new ItemRenderWidget(this, bgX, bgY));
    }
}
