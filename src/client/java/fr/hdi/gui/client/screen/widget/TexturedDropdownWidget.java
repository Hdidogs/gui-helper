package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.DropdownObject;
import fr.hdi.gui.client.gui.objects.DropdownObject.DropdownOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class TexturedDropdownWidget extends ClickableWidget implements OverlayRenderer {
    private static final int OPEN_Z_OFFSET = 200;
    private static final int TEXT_PADDING_X = 4;
    private static final int ARROW_SIZE = 7;
    private static final int SCROLL_INDICATOR_SIZE_X = 2;
    private static final int SCROLL_INDICATOR_MIN_SIZE_Y = 6;
    private static final int SCROLL_HIT_SIZE_X = 6;
    private static final int PLACEHOLDER_COLOR = 0x808080;
    private static final int CURSOR_SIZE_X = 6;

    private DropdownObject guiObject;
    private TextFieldWidget searchField;
    private boolean open;
    private boolean draggingScroll;

    public TexturedDropdownWidget(DropdownObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), Text.empty());
        this.guiObject = object;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        this.searchField = new TextFieldWidget(textRenderer, getX() + TEXT_PADDING_X, getY(), Math.max(0, getWidth() - TEXT_PADDING_X * 2), textRenderer.fontHeight, Text.empty());
        this.searchField.setDrawsBackground(false);
        this.searchField.setChangedListener(this::onSearchChanged);
    }

    private void onSearchChanged(String search) {
        guiObject.setSearch(search);
        setScrollY(0.0D);
    }

    private int getSearchY() {
        return getY() + getHeight();
    }

    private int getListY() {
        return getSearchY() + guiObject.getSearchSizeY();
    }

    private int getOverlayEndY() {
        return getListY() + guiObject.getListSizeY();
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

    private void openList() {
        open = true;

        scrollToSelected();

        if (guiObject.isSearchable()) searchField.setFocused(true);
    }

    private void closeList() {
        open = false;
        draggingScroll = false;

        searchField.setFocused(false);

        if (!searchField.getText().isEmpty()) searchField.setText("");
    }

    private void select(DropdownOption option) {
        guiObject.setValue(option.getValue());

        closeList();

        playDownSound(MinecraftClient.getInstance().getSoundManager());
    }

    private boolean isInsideX(double mouseX) {
        return mouseX >= getX() && mouseX < getX() + getWidth();
    }

    private boolean isOverSearch(double mouseX, double mouseY) {
        return open && guiObject.isSearchable() && isInsideX(mouseX) && mouseY >= getSearchY() && mouseY < getListY();
    }

    private boolean isOverScrollTrack(double mouseX, double mouseY) {
        return open && getMaxScrollY() > 0 && mouseX >= getX() + getWidth() - SCROLL_HIT_SIZE_X && mouseX < getX() + getWidth()
                && mouseY >= getListY() && mouseY < getOverlayEndY();
    }

    private int getOptionAt(double mouseX, double mouseY) {
        if (!open || !isInsideX(mouseX)) return -1;
        if (mouseY < getListY() || mouseY >= getOverlayEndY()) return -1;

        int index = (int) ((mouseY - getListY() + getScrollY()) / guiObject.getOptionSizeY());

        return index >= 0 && index < guiObject.getFilteredOptions().size() ? index : -1;
    }

    private int getIndicatorSizeY(int trackSizeY, int listSizeY) {
        return Math.max(SCROLL_INDICATOR_MIN_SIZE_Y, trackSizeY * listSizeY / guiObject.getContentSizeY());
    }

    private void slideTo(double mouseY) {
        int maxScrollY = getMaxScrollY();
        int listSizeY = guiObject.getListSizeY();
        int trackSizeY = listSizeY - 2;
        int indicatorSizeY = getIndicatorSizeY(trackSizeY, listSizeY);
        int range = trackSizeY - indicatorSizeY;

        if (maxScrollY <= 0 || range <= 0) return;

        setScrollY((mouseY - getListY() - 1 - indicatorSizeY / 2.0D) * maxScrollY / range);
    }

    private void tickScrollDrag(int mouseY) {
        if (!draggingScroll) return;

        if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS) {
            draggingScroll = false;

            return;
        }

        slideTo(mouseY);
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
        int indicatorSizeY = getIndicatorSizeY(trackSizeY, listSizeY);
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
        this.active = guiObject.isEnabled();

        if (!this.active && open) closeList();

        UtilsWidgets.renderState(context, guiObject, () -> {
            if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), guiObject.getBackgroundColor().getHexColor());
            drawBorder(context, getX(), getY(), getWidth(), getHeight());
            drawOptionText(context, guiObject.getSelectedOption(), getY(), getHeight());
            drawArrow(context);
        });
    }

    @Override
    public boolean isCapturing() {
        return open && visible && active;
    }

    @Override
    public boolean mouseClickedOverlay(double mouseX, double mouseY, int button) {
        if (!isCapturing() || !isValidClickButton(button)) return false;

        if (isOverScrollTrack(mouseX, mouseY)) {
            draggingScroll = true;

            slideTo(mouseY);

            return true;
        }

        if (isOverSearch(mouseX, mouseY)) {
            searchField.mouseClicked(mouseX, mouseY, button);

            return true;
        }

        int option = getOptionAt(mouseX, mouseY);

        if (option < 0) return false;

        select(guiObject.getFilteredOptions().get(option));

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
        closeList();
    }

    @Override
    public void renderOverlay(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!open || !visible) return;

        tickScrollDrag(mouseY);
        renderList(context, mouseX, mouseY, delta);
    }

    private void renderSearch(DrawContext context, int mouseX, int mouseY, float delta) {
        int searchY = getSearchY();
        int searchSizeY = guiObject.getSearchSizeY();

        searchField.setX(getX() + TEXT_PADDING_X);
        searchField.setY(searchY + (searchSizeY - searchField.getHeight()) / 2);
        searchField.setWidth(Math.max(0, getWidth() - TEXT_PADDING_X * 2));
        searchField.render(context, mouseX, mouseY, delta);

        if (guiObject.getSearchPlaceholder() != null && searchField.getText().isEmpty()) {
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, guiObject.getSearchPlaceholder(), searchField.getX() + CURSOR_SIZE_X, searchField.getY(), PLACEHOLDER_COLOR);
        }

        if (guiObject.getListSizeY() > 0) context.fill(getX(), getListY() - 1, getX() + getWidth(), getListY(), guiObject.getBorderColor().getHexColor());
    }

    private void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        int overlayY = getSearchY();
        int overlaySizeY = getOverlayEndY() - overlayY;
        int listY = getListY();
        int listSizeY = guiObject.getListSizeY();
        int optionSizeY = guiObject.getOptionSizeY();
        int hovered = draggingScroll || isOverScrollTrack(mouseX, mouseY) ? -1 : getOptionAt(mouseX, mouseY);
        List<DropdownOption> options = guiObject.getFilteredOptions();

        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 0.0F, OPEN_Z_OFFSET);

        context.fill(getX(), overlayY, getX() + getWidth(), overlayY + overlaySizeY, guiObject.getBackgroundColor().getHexColor());

        if (guiObject.isSearchable()) renderSearch(context, mouseX, mouseY, delta);

        if (listSizeY > 0) {
            Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
            Vector4f start = matrix.transform(new Vector4f(getX(), listY, 0.0F, 1.0F));
            Vector4f end = matrix.transform(new Vector4f(getX() + getWidth(), listY + listSizeY, 0.0F, 1.0F));

            context.enableScissor((int) start.x, (int) start.y, (int) end.x, (int) end.y);
            context.getMatrices().push();
            context.getMatrices().translate(0.0F, (float) -getScrollY(), 0.0F);

            for (int index = 0; index < options.size(); index++) {
                int optionY = listY + index * optionSizeY;

                if (index == hovered) context.fill(getX(), optionY, getX() + getWidth(), optionY + optionSizeY, guiObject.getHoverColor().getHexColor());

                drawOptionText(context, options.get(index), optionY, optionSizeY);
            }

            context.getMatrices().pop();
            context.disableScissor();

            drawScrollIndicator(context, listY, listSizeY);
        }

        if (overlaySizeY > 0) drawBorder(context, getX(), overlayY, getWidth(), overlaySizeY);

        context.getMatrices().pop();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) return true;

        return open && isInsideX(mouseX) && mouseY >= getSearchY() && mouseY < getOverlayEndY();
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
            if (open) closeList();

            return false;
        }

        if (open) closeList();
        else openList();

        playDownSound(MinecraftClient.getInstance().getSoundManager());

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!open || !active || !guiObject.isSearchable() || keyCode == GLFW.GLFW_KEY_ESCAPE) return false;

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            List<DropdownOption> options = guiObject.getFilteredOptions();

            if (!options.isEmpty()) select(options.get(0));

            return true;
        }

        searchField.keyPressed(keyCode, scanCode, modifiers);

        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!open || !active || !guiObject.isSearchable()) return false;

        searchField.charTyped(chr, modifiers);

        return true;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
