package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.BoxObject;
import fr.hdi.gui.client.gui.objects.GuiObject;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BoxWidget extends ContainerWidget implements OverlayRenderer, ViewportAware {
    private BoxObject guiObject;
    private List<GuiObject> objects = new ArrayList<>();
    private List<ClickableWidget> widgets = new ArrayList<>();
    private ScrollBarWidget scrollBar;
    private int contentHeight;
    private double parentOffsetY;
    private int clipStartX = Integer.MIN_VALUE;
    private int clipStartY = Integer.MIN_VALUE;
    private int clipEndX = Integer.MAX_VALUE;
    private int clipEndY = Integer.MAX_VALUE;

    public BoxWidget(BoxObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), Text.empty());
        this.guiObject = object;

        for (GuiObject child : object.getObjects()) {
            child.resolveAnchor(getWidth(), getHeight());

            ClickableWidget widget = child.register(getX(), getY());

            if (widget == null) continue;

            objects.add(child);
            widgets.add(widget);
            contentHeight = Math.max(contentHeight, widget.getY() + widget.getHeight() - getY());
        }

        this.scrollBar = (ScrollBarWidget) object.getScrollBar().register(getX(), getY());
        this.scrollBar.setMaxScrollY(getMaxScrollY());
    }

    public int getMaxScrollY() {
        return Math.max(0, contentHeight - height);
    }

    public ScrollBarWidget getScrollBar() {
        return scrollBar;
    }

    public double getScrollY() {
        return scrollBar.getScrollY();
    }

    public void setScrollY(double scrollY) {
        scrollBar.setScrollY(scrollY);
    }

    @Override
    public void setViewport(double offsetY, int clipStartX, int clipStartY, int clipEndX, int clipEndY) {
        this.parentOffsetY = offsetY;
        this.clipStartX = clipStartX;
        this.clipStartY = clipStartY;
        this.clipEndX = clipEndX;
        this.clipEndY = clipEndY;
    }

    @Override
    public boolean isOverSlot(double mouseX, double mouseY) {
        if (!visible || !isMouseOver(mouseX, mouseY)) return false;

        for (ClickableWidget widget : widgets) {
            if (widget.visible && widget instanceof ViewportAware viewport && viewport.isOverSlot(mouseX, mouseY + getScrollY())) return true;
        }

        return false;
    }

    private void updateViewports() {
        int realY = (int) Math.round(getY() - parentOffsetY);
        double offsetY = parentOffsetY + getScrollY();
        int startX = Math.max(clipStartX, getX());
        int startY = Math.max(clipStartY, realY);
        int endX = Math.min(clipEndX, getX() + width);
        int endY = Math.min(clipEndY, realY + height);

        for (ClickableWidget widget : widgets) {
            if (widget instanceof ViewportAware viewport) viewport.setViewport(offsetY, startX, startY, endX, endY);
        }
    }

    @Override
    public List<? extends Element> children() {
        return widgets;
    }

    @Override
    public void setFocused(Element focused) {
        super.setFocused(focused);

        if (!(focused instanceof ClickableWidget widget)) return;

        int top = widget.getY() - getY();
        int bottom = top + widget.getHeight();

        if (top < getScrollY()) setScrollY(top);
        else if (bottom > getScrollY() + height) setScrollY(bottom - height);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.active = guiObject.isEnabled();

        if (!this.active) setFocused((Element) null);

        UtilsWidgets.renderState(context, guiObject, () -> draw(context, mouseX, mouseY, delta));
    }

    private void draw(DrawContext context, int mouseX, int mouseY, float delta) {
        UtilsWidgets.drawTexture(context, guiObject.getTexture(), getX(), getY(), getWidth(), getHeight());
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        updateViewports();

        UtilsWidgets.enableScissor(context, getX(), getY(), getX() + width, getY() + height);
        context.getMatrices().push();
        context.getMatrices().translate(0.0F, (float) -getScrollY(), 0.0F);

        for (int index = 0; index < widgets.size(); index++) {
            ClickableWidget widget = widgets.get(index);
            widget.visible = objects.get(index).isShown();
            widget.render(context, mouseX, (int) (mouseY + getScrollY()), delta);
        }

        context.getMatrices().pop();
        context.disableScissor();

        if (guiObject.hasScrollBar()) scrollBar.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean isCapturing() {
        for (ClickableWidget widget : widgets) {
            if (widget.visible && widget instanceof OverlayRenderer overlay && overlay.isCapturing()) return true;
        }

        return false;
    }

    @Override
    public boolean mouseClickedOverlay(double mouseX, double mouseY, int button) {
        if (!active) return false;

        for (ClickableWidget widget : widgets) {
            if (!widget.visible || !(widget instanceof OverlayRenderer overlay)) continue;

            if (overlay.mouseClickedOverlay(mouseX, mouseY + getScrollY(), button)) return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolledOverlay(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (ClickableWidget widget : widgets) {
            if (!widget.visible || !(widget instanceof OverlayRenderer overlay)) continue;

            if (overlay.mouseScrolledOverlay(mouseX, mouseY + getScrollY(), horizontalAmount, verticalAmount)) return true;
        }

        return false;
    }

    @Override
    public void closeOverlay() {
        for (ClickableWidget widget : widgets) {
            if (widget instanceof OverlayRenderer overlay) overlay.closeOverlay();
        }
    }

    @Override
    public void renderOverlay(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        context.getMatrices().translate(0.0F, (float) -getScrollY(), 0.0F);

        for (ClickableWidget widget : widgets) {
            if (widget.visible && widget instanceof OverlayRenderer overlay) overlay.renderOverlay(context, mouseX, (int) (mouseY + getScrollY()), delta);
        }

        context.getMatrices().pop();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!active || !isMouseOver(mouseX, mouseY)) return false;

        for (ClickableWidget widget : widgets) {
            if (widget.visible && widget.mouseScrolled(mouseX, mouseY + getScrollY(), horizontalAmount, verticalAmount)) return true;
        }

        if (getMaxScrollY() <= 0) return false;

        int step = guiObject.getScrollSnap() > 0 ? guiObject.getScrollSnap() : guiObject.getScrollStep();

        setScrollY(getScrollY() - verticalAmount * step);

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || !isMouseOver(mouseX, mouseY)) return false;

        if (guiObject.hasScrollBar() && scrollBar.mouseClicked(mouseX, mouseY, button)) {
            setFocused((Element) null);

            return true;
        }

        for (ClickableWidget widget : widgets) {
            if (widget.visible && widget.mouseClicked(mouseX, mouseY + getScrollY(), button)) {
                setFocused(widget);

                return true;
            }
        }

        setFocused((Element) null);

        return !isOverSlot(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean handled = guiObject.hasScrollBar() && scrollBar.mouseReleased(mouseX, mouseY, button);

        for (ClickableWidget widget : widgets) {
            if (widget.visible && widget.mouseReleased(mouseX, mouseY + getScrollY(), button)) handled = true;
        }

        return handled;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!active) return false;

        if (guiObject.hasScrollBar() && scrollBar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;

        return getFocused() != null && getFocused().mouseDragged(mouseX, mouseY + getScrollY(), button, deltaX, deltaY);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
