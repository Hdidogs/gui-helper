package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.ScrollBarObject;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ScrollBarWidget extends ClickableWidget {
    private ScrollBarObject guiObject;
    private int maxScrollY;
    private boolean dragging;

    public ScrollBarWidget(ScrollBarObject object, int boxX, int boxY) {
        super(boxX + getStartX(object), boxY + object.getObjectStartY(), getSizeX(object), getSizeY(object), Text.empty());
        this.guiObject = object;
    }

    private static int getSizeX(ScrollBarObject object) {
        return object.getObjectSizeX() > 0 ? object.getObjectSizeX() : object.getTexture().getDrawSizeX();
    }

    private static int getSizeY(ScrollBarObject object) {
        if (object.getObjectSizeY() > 0) return object.getObjectSizeY();

        return object.getOwner() != null ? object.getOwner().getObjectSizeY() : 0;
    }

    private static int getStartX(ScrollBarObject object) {
        if (!object.isAutoPlaced() || object.getOwner() == null) return object.getObjectStartX();

        return object.getOwner().getObjectSizeX() - getSizeX(object);
    }

    public double getScrollY() {
        return guiObject.getScrollY();
    }

    public void setScrollY(double scrollY) {
        guiObject.setScrollY(MathHelper.clamp(scrollY, 0.0D, maxScrollY));
    }

    public void setMaxScrollY(int maxScrollY) {
        this.maxScrollY = Math.max(0, maxScrollY);

        setScrollY(getScrollY());
    }

    public boolean isNeeded() {
        return maxScrollY > 0;
    }

    private int getSliderHeight() {
        return guiObject.getSliderTexture().getDrawSizeY();
    }

    private int getSliderY() {
        int track = getHeight() - getSliderHeight();

        return track <= 0 ? getY() : getY() + (int) (getScrollY() * track / maxScrollY);
    }

    private void slideTo(double mouseY) {
        int track = getHeight() - getSliderHeight();

        if (track <= 0) return;

        setScrollY((mouseY - getY() - getSliderHeight() / 2.0D) * maxScrollY / track);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!isNeeded()) return;

        UtilsWidgets.drawTexture(context, guiObject.getTexture(), getX(), getY(), getWidth(), getHeight());
        UtilsWidgets.drawTexture(context, guiObject.getSliderTexture(), getX(), getSliderY(), getWidth(), getSliderHeight());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isNeeded() || !isMouseOver(mouseX, mouseY)) return false;

        dragging = true;

        slideTo(mouseY);

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!dragging) return false;

        slideTo(mouseY);

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;

        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
