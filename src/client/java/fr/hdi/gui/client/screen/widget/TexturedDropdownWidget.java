package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.DropdownObject;
import fr.hdi.gui.client.gui.objects.DropdownObject.DropdownOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class TexturedDropdownWidget extends ClickableWidget implements OverlayRenderer {
    private static final int OPEN_Z_OFFSET = 200;
    private static final int TEXT_PADDING_X = 4;
    private static final int ARROW_SIZE = 7;
    private static final int SCROLL_INDICATOR_SIZE_X = 2;
    private static final int SCROLL_INDICATOR_MIN_SIZE_Y = 6;

    private DropdownObject guiObject;
    private boolean open;

    public TexturedDropdownWidget(DropdownObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), Text.empty());
        this.guiObject = object;
    }

    private int getListY() {
        return getY() + getHeight();
    }

    private int getMaxScrollY() {
        return Math.max(0, guiObject.getContentSizeY() - guiObject.getListSizeY());
    }

    private double getScrollY() {
        return guiObject.getScrollY();
    }

    private void setScrollY(double scrollY) {
        guiObject.setScrollY(MathHelper.clamp(scrollY, 0.0D, getMaxScrollY()));
    }

    private void scrollToSelected() {
        int index = guiObject.getSelectedIndex();

        if (index < 0) return;

        int top = index * guiObject.getOptionSizeY();
        int bottom = top + guiObject.getOptionSizeY();

        if (top < getScrollY()) setScrollY(top);
        else if (bottom > getScrollY() + guiObject.getListSizeY()) setScrollY(bottom - guiObject.getListSizeY());
    }

    private int getOptionAt(double mouseX, double mouseY) {
        if (!open || mouseX < getX() || mouseX >= getX() + getWidth()) return -1;
        if (mouseY < getListY() || mouseY >= getListY() + guiObject.getListSizeY()) return -1;

        int index = (int) ((mouseY - getListY() + getScrollY()) / guiObject.getOptionSizeY());

        return index >= 0 && index < guiObject.getOptions().size() ? index : -1;
    }

    private void drawBorder(DrawContext context, int x, int y, int sizeX, int sizeY) {
        int color = guiObject.getBorderColor().getHexColor();

        context.fill(x, y, x + sizeX, y + 1, color);
        context.fill(x, y + sizeY - 1, x + sizeX, y + sizeY, color);
        context.fill(x, y + 1, x + 1, y + sizeY - 1, color);
        context.fill(x + sizeX - 1, y + 1, x + sizeX, y + sizeY - 1, color);
    }

    private void drawArrow(DrawContext context) {
        int color = guiObject.getBorderColor().getHexColor();
        int rows = ARROW_SIZE / 2 + 1;
        int x = getX() + getWidth() - TEXT_PADDING_X - ARROW_SIZE;
        int y = getY() + (getHeight() - rows) / 2;

        for (int row = 0; row < rows; row++) {
            int rowY = open ? y + rows - row - 1 : y + row;

            context.fill(x + row, rowY, x + ARROW_SIZE - row, rowY + 1, color);
        }
    }

    private void drawScrollIndicator(DrawContext context, int listY, int listSizeY) {
        int maxScrollY = getMaxScrollY();

        if (maxScrollY <= 0) return;

        int trackSizeY = listSizeY - 2;
        int indicatorSizeY = Math.max(SCROLL_INDICATOR_MIN_SIZE_Y, trackSizeY * listSizeY / guiObject.getContentSizeY());
        int indicatorX = getX() + getWidth() - 1 - SCROLL_INDICATOR_SIZE_X;
        int indicatorY = listY + 1 + (int) (getScrollY() * (trackSizeY - indicatorSizeY) / maxScrollY);

        context.fill(indicatorX, indicatorY, indicatorX + SCROLL_INDICATOR_SIZE_X, indicatorY + indicatorSizeY, guiObject.getBorderColor().getHexColor());
    }

    private void drawOptionText(DrawContext context, DropdownOption option, int y, int sizeY) {
        if (option == null || option.getLabel() == null) return;

        int textY = y + (sizeY - (int) (MinecraftClient.getInstance().textRenderer.fontHeight * option.getLabel().getTextSize())) / 2;

        UtilsWidgets.drawText(context, option.getLabel().getText(), getX() + TEXT_PADDING_X, textY, option.getLabel().getColor().getHexColor(), option.getLabel().getTextSize());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), guiObject.getBackgroundColor().getHexColor());
        drawBorder(context, getX(), getY(), getWidth(), getHeight());
        drawOptionText(context, guiObject.getSelectedOption(), getY(), getHeight());
        drawArrow(context);

    }

    @Override
    public boolean isCapturing() {
        return open && visible;
    }

    @Override
    public boolean mouseClickedOverlay(double mouseX, double mouseY, int button) {
        if (!isCapturing() || !isValidClickButton(button)) return false;

        int option = getOptionAt(mouseX, mouseY);

        if (option < 0) return false;

        guiObject.setValue(guiObject.getOptions().get(option).getValue());

        open = false;

        playDownSound(MinecraftClient.getInstance().getSoundManager());

        return true;
    }

    @Override
    public boolean mouseScrolledOverlay(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isCapturing() || getMaxScrollY() <= 0 || !isMouseOver(mouseX, mouseY)) return false;

        setScrollY(getScrollY() - verticalAmount * guiObject.getOptionSizeY());

        return true;
    }

    @Override
    public void closeOverlay() {
        open = false;
    }

    @Override
    public void renderOverlay(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!open || !visible) return;

        renderList(context, mouseX, mouseY);
    }

    private void renderList(DrawContext context, int mouseX, int mouseY) {
        int listY = getListY();
        int listSizeY = guiObject.getListSizeY();
        int optionSizeY = guiObject.getOptionSizeY();
        int hovered = getOptionAt(mouseX, mouseY);

        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 0.0F, OPEN_Z_OFFSET);

        context.fill(getX(), listY, getX() + getWidth(), listY + listSizeY, guiObject.getBackgroundColor().getHexColor());

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Vector4f start = matrix.transform(new Vector4f(getX(), listY, 0.0F, 1.0F));
        Vector4f end = matrix.transform(new Vector4f(getX() + getWidth(), listY + listSizeY, 0.0F, 1.0F));

        context.enableScissor((int) start.x, (int) start.y, (int) end.x, (int) end.y);
        context.getMatrices().push();
        context.getMatrices().translate(0.0F, (float) -getScrollY(), 0.0F);

        for (int index = 0; index < guiObject.getOptions().size(); index++) {
            int optionY = listY + index * optionSizeY;

            if (index == hovered) context.fill(getX(), optionY, getX() + getWidth(), optionY + optionSizeY, guiObject.getHoverColor().getHexColor());

            drawOptionText(context, guiObject.getOptions().get(index), optionY, optionSizeY);
        }

        context.getMatrices().pop();
        context.disableScissor();

        drawScrollIndicator(context, listY, listSizeY);
        drawBorder(context, getX(), listY, getWidth(), listSizeY);

        context.getMatrices().pop();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) return true;

        return open && mouseX >= getX() && mouseX < getX() + getWidth()
                && mouseY >= getListY() && mouseY < getListY() + guiObject.getListSizeY();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!open || getMaxScrollY() <= 0 || !isMouseOver(mouseX, mouseY)) return false;

        setScrollY(getScrollY() - verticalAmount * guiObject.getOptionSizeY());

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || !isValidClickButton(button)) return false;

        if (!super.isMouseOver(mouseX, mouseY)) {
            open = false;

            return false;
        }

        open = !open;

        if (open) scrollToSelected();

        playDownSound(MinecraftClient.getInstance().getSoundManager());

        return true;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
