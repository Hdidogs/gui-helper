package fr.hdi.gui.screen.objects;

import fr.hdi.gui.screen.CustomScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.BiPredicate;

public class SlotObject {
    private String id;
    private int index;
    private int objectStartX;
    private int objectStartY;
    private Identifier backgroundSprite;
    private boolean locked;
    private String group;
    private SlotClickHandler onClick;
    private BiPredicate<CustomScreenHandler, ItemStack> canInsert;
    private BiPredicate<CustomScreenHandler, PlayerEntity> canTakeItems;
    private int maxCount;
    private SlotTakeHandler onTake;
    private ContentChangedHandler onContentChanged;
    private CloseHandler onClosed;

    public SlotObject(String id, int index, int objectStartX, int objectStartY) {
        this(id, index, objectStartX, objectStartY, null);
    }

    public SlotObject(String id, int index, int objectStartX, int objectStartY, Identifier backgroundSprite) {
        this.id = id;
        this.index = index;
        this.objectStartX = objectStartX;
        this.objectStartY = objectStartY;
        this.backgroundSprite = backgroundSprite;
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public int getObjectStartX() {
        return objectStartX;
    }

    public int getObjectStartY() {
        return objectStartY;
    }

    public Identifier getBackgroundSprite() {
        return backgroundSprite;
    }

    public boolean hasBackgroundSprite() {
        return backgroundSprite != null;
    }

    public String getGroup() {
        return group;
    }

    public SlotObject setGroup(String group) {
        this.group = group == null || group.isEmpty() ? null : group;

        return this;
    }

    public boolean hasGroup() {
        return group != null;
    }

    public boolean isLocked() {
        return locked;
    }

    public SlotObject setLocked(boolean locked) {
        this.locked = locked;

        return this;
    }

    public SlotClickHandler getOnClick() {
        return onClick;
    }

    public SlotObject setOnClick(SlotClickHandler onClick) {
        this.onClick = onClick;

        return this;
    }

    public boolean hasOnClick() {
        return onClick != null;
    }

    public SlotObject setCanInsert(BiPredicate<CustomScreenHandler, ItemStack> canInsert) {
        this.canInsert = canInsert;

        return this;
    }

    public boolean canInsert(CustomScreenHandler handler, ItemStack stack) {
        return canInsert == null || canInsert.test(handler, stack);
    }

    public SlotObject setCanTakeItems(BiPredicate<CustomScreenHandler, PlayerEntity> canTakeItems) {
        this.canTakeItems = canTakeItems;

        return this;
    }

    public boolean canTakeItems(CustomScreenHandler handler, PlayerEntity player) {
        return canTakeItems == null || canTakeItems.test(handler, player);
    }

    public SlotTakeHandler getOnTake() {
        return onTake;
    }

    public SlotObject setOnTake(SlotTakeHandler onTake) {
        this.onTake = onTake;

        return this;
    }

    public boolean hasOnTake() {
        return onTake != null;
    }

    public ContentChangedHandler getOnContentChanged() {
        return onContentChanged;
    }

    public SlotObject setOnContentChanged(ContentChangedHandler onContentChanged) {
        this.onContentChanged = onContentChanged;

        return this;
    }

    public boolean hasOnContentChanged() {
        return onContentChanged != null;
    }

    public CloseHandler getOnClosed() {
        return onClosed;
    }

    public SlotObject setOnClosed(CloseHandler onClosed) {
        this.onClosed = onClosed;

        return this;
    }

    public boolean hasOnClosed() {
        return onClosed != null;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public boolean hasMaxCount() {
        return maxCount > 0;
    }

    public SlotObject setMaxCount(int maxCount) {
        this.maxCount = Math.max(0, maxCount);

        return this;
    }
}
