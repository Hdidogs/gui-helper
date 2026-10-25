package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.GuiPages;
import fr.hdi.gui.client.gui.objects.SlotGridObject;
import fr.hdi.gui.client.screen.CustomHandledScreen;
import fr.hdi.gui.screen.GuiHandler;
import fr.hdi.gui.screen.utils.GuiSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SlotGridWidget extends ClickableWidget implements ViewportAware {
    private SlotGridObject guiObject;
    private List<GuiSlot> slots = new ArrayList<>();
    private double offsetY;
    private int clipStartX = Integer.MIN_VALUE;
    private int clipStartY = Integer.MIN_VALUE;
    private int clipEndX = Integer.MAX_VALUE;
    private int clipEndY = Integer.MAX_VALUE;

    public SlotGridWidget(SlotGridObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), Text.empty());
        this.guiObject = object;
        this.active = false;

        if (MinecraftClient.getInstance().player == null) return;

        for (Slot slot : MinecraftClient.getInstance().player.currentScreenHandler.slots) {
            if (slot instanceof GuiSlot guiSlot && object.contains(guiSlot.getObject())) slots.add(guiSlot);
        }
    }

    public void hideSlots() {
        for (GuiSlot slot : slots) slot.setClientShown(false);
    }

    @Override
    public void setViewport(double offsetY, int clipStartX, int clipStartY, int clipEndX, int clipEndY) {
        this.offsetY = offsetY;
        this.clipStartX = clipStartX;
        this.clipStartY = clipStartY;
        this.clipEndX = clipEndX;
        this.clipEndY = clipEndY;
    }

    private int getCellX(GuiSlot slot) {
        return getX() + guiObject.getRelativeX(slot.getObject());
    }

    private int getCellY(GuiSlot slot) {
        return getY() + guiObject.getRelativeY(slot.getObject());
    }

    private int getRealCellY(GuiSlot slot) {
        return (int) Math.round(getCellY(slot) - offsetY);
    }

    private boolean isCellShown(GuiSlot slot) {
        if (!GuiPages.isActive(slot.getObject().getGroup())) return false;

        int x = getCellX(slot);
        int y = getRealCellY(slot);

        return x >= clipStartX && y >= clipStartY && x + GuiHandler.SLOT_SIZE <= clipEndX && y + GuiHandler.SLOT_SIZE <= clipEndY;
    }

    @Override
    public boolean isOverSlot(double mouseX, double mouseY) {
        if (!this.visible) return false;

        for (GuiSlot slot : slots) {
            int x = getCellX(slot);
            int y = getCellY(slot);

            if (isCellShown(slot) && mouseX >= x && mouseX < x + GuiHandler.SLOT_SIZE && mouseY >= y && mouseY < y + GuiHandler.SLOT_SIZE) return true;
        }

        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        UtilsWidgets.renderState(context, guiObject, () -> draw(context));
    }

    private void draw(DrawContext context) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        int guiX = 0;
        int guiY = 0;

        if (MinecraftClient.getInstance().currentScreen instanceof CustomHandledScreen screen) {
            guiX = screen.getRenderer().getGuiX();
            guiY = screen.getRenderer().getGuiY();
        }

        for (GuiSlot slot : slots) {
            boolean shown = isCellShown(slot);
            int cellX = getCellX(slot);
            int cellY = getCellY(slot);

            slot.setClientShown(shown);
            slot.setPosition(cellX + 1 - guiX, getRealCellY(slot) + 1 - guiY);

            if (shown) UtilsWidgets.drawTexture(context, guiObject.getSlotTexture(), cellX, cellY, GuiHandler.SLOT_SIZE, GuiHandler.SLOT_SIZE);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
