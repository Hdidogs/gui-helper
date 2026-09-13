package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.BoxObject;
import fr.hdi.gui.client.gui.objects.GuiObject;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class BoxWidget extends ContainerWidget implements OverlayRenderer {
    private BoxObject guiObject;
    private List<GuiObject> objects = new ArrayList<>();
    private List<ClickableWidget> widgets = new ArrayList<>();
    private ScrollBarWidget scrollBar;
    private int contentHeight;

    public BoxWidget(BoxObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), Text.empty());
        this.guiObject = object;

        for (GuiObject child : object.getObjects()) {
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
        UtilsWidgets.drawTexture(context, guiObject.getTexture(), getX(), getY(), getWidth(), getHeight());
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Vector4f start = matrix.transform(new Vector4f(getX(), getY(), 0.0F, 1.0F));
        Vector4f end = matrix.transform(new Vector4f(getX() + width, getY() + height, 0.0F, 1.0F));

        context.enableScissor((int) start.x, (int) start.y, (int) end.x, (int) end.y);
        context.getMatrices().push();
        context.getMatrices().translate(0.0F, (float) -getScrollY(), 0.0F);

        for (int index = 0; index < widgets.size(); index++) {
            ClickableWidget widget = widgets.get(index);
            widget.visible = objects.get(index).isShown();
            widget.render(context, mouseX, (int) (mouseY + getScrollY()), delta);
        }

        context.getMatrices().pop();
        context.disableScissor();

        scrollBar.render(context, mouseX, mouseY, delta);
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
        if (!isMouseOver(mouseX, mouseY)) return false;

        for (ClickableWidget widget : widgets) {
            if (widget.visible && widget.mouseScrolled(mouseX, mouseY + getScrollY(), horizontalAmount, verticalAmount)) return true;
        }

        if (getMaxScrollY() <= 0) return false;

        setScrollY(getScrollY() - verticalAmount * guiObject.getScrollStep());

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || !isMouseOver(mouseX, mouseY)) return false;

        if (scrollBar.mouseClicked(mouseX, mouseY, button)) {
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

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean handled = scrollBar.mouseReleased(mouseX, mouseY, button);

        for (ClickableWidget widget : widgets) {
            if (widget.visible && widget.mouseReleased(mouseX, mouseY + getScrollY(), button)) handled = true;
        }

        return handled;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scrollBar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;

        return getFocused() != null && getFocused().mouseDragged(mouseX, mouseY + getScrollY(), button, deltaX, deltaY);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
