package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.TextObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class TextRenderWidget extends ClickableWidget {
    private static final float VISUAL_LINE_HEIGHT = 7.0F;
    private static final int AUTO_MAX_SIZE = 100;
    private static final int AUTO_MIN_SIZE = 30;
    private static final int AUTO_SIZE_STEP = 5;

    private TextObject guiObject;
    private Text layoutText;
    private List<OrderedText> layoutLines;
    private float layoutSize;

    public TextRenderWidget(TextObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(),
                object.getObjectSizeX() > 0 ? object.getObjectSizeX() : (int) Math.ceil(getTextWidth(object)),
                object.getObjectSizeY() > 0 ? object.getObjectSizeY() : (int) Math.ceil(getLineHeight(object)),
                object.getText().getText());
        this.guiObject = object;
        this.active = false;
    }

    private static float getTextWidth(TextObject object) {
        return MinecraftClient.getInstance().textRenderer.getWidth(object.getText().getText()) * object.getText().getTextSize();
    }

    private static float getLineHeight(TextObject object) {
        return MinecraftClient.getInstance().textRenderer.fontHeight * object.getText().getTextSize();
    }

    private static float getVisualHeight(TextObject object) {
        return VISUAL_LINE_HEIGHT * object.getText().getTextSize();
    }

    private boolean isAutoFit() {
        return guiObject.getText().isAutoSize() && guiObject.getObjectSizeX() > 0 && guiObject.getObjectSizeY() > 0;
    }

    private static float getBlockHeight(int lineCount, float size) {
        return ((lineCount - 1) * MinecraftClient.getInstance().textRenderer.fontHeight + VISUAL_LINE_HEIGHT) * size;
    }

    private boolean fits(TextRenderer renderer, List<OrderedText> lines, float size) {
        if (getBlockHeight(lines.size(), size) > getHeight()) return false;

        for (OrderedText line : lines) {
            if (renderer.getWidth(line) * size > getWidth()) return false;
        }

        return true;
    }

    private void updateLayout() {
        Text text = guiObject.getText().getText();

        if (text == layoutText && layoutLines != null) return;

        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        layoutText = text;

        for (int step = AUTO_MAX_SIZE; step >= AUTO_MIN_SIZE; step -= AUTO_SIZE_STEP) {
            float size = step / 100.0F;
            List<OrderedText> lines = renderer.wrapLines(text, Math.max(1, (int) (getWidth() / size)));

            if (fits(renderer, lines, size)) {
                layoutLines = lines;
                layoutSize = size;

                return;
            }
        }

        layoutSize = AUTO_MIN_SIZE / 100.0F;
        layoutLines = renderer.wrapLines(text, Math.max(1, (int) (getWidth() / layoutSize)));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        if (isAutoFit()) {
            renderAutoFit(context);

            return;
        }

        float textWidth = getTextWidth(guiObject);
        float textX = getAlignedX(textWidth);
        float visualHeight = getVisualHeight(guiObject);
        float textY = getAlignedY(visualHeight);

        UtilsWidgets.drawText(context, guiObject.getText().getText(), textX, textY, guiObject.getText().getColor().getHexColor(), guiObject.getText().getTextSize());
    }

    private void renderAutoFit(DrawContext context) {
        updateLayout();

        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        float lineHeight = renderer.fontHeight * layoutSize;
        float textY = getAlignedY(getBlockHeight(layoutLines.size(), layoutSize));
        int color = guiObject.getText().getColor().getHexColor();

        for (OrderedText line : layoutLines) {
            UtilsWidgets.drawText(context, line, getAlignedX(renderer.getWidth(line) * layoutSize), textY, color, layoutSize);
            textY += lineHeight;
        }
    }

    private float getAlignedX(float textWidth) {
        return switch (guiObject.getAlign()) {
            case LEFT -> getX();
            case CENTER -> getX() + (getWidth() - textWidth) / 2.0F;
            case RIGHT -> getX() + getWidth() - textWidth;
        };
    }

    private float getAlignedY(float textHeight) {
        return switch (guiObject.getVerticalAlign()) {
            case TOP -> getY();
            case MIDDLE -> getY() + (getHeight() - textHeight) / 2.0F;
            case BOTTOM -> getY() + getHeight() - textHeight;
        };
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
