package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.TextObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;

public class TextRenderWidget extends ClickableWidget {
    private static final float VISUAL_LINE_HEIGHT = 7.0F;

    private TextObject guiObject;

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

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        float textWidth = getTextWidth(guiObject);
        float textX = switch (guiObject.getAlign()) {
            case LEFT -> getX();
            case CENTER -> getX() + (getWidth() - textWidth) / 2.0F;
            case RIGHT -> getX() + getWidth() - textWidth;
        };
        float visualHeight = getVisualHeight(guiObject);
        float textY = switch (guiObject.getVerticalAlign()) {
            case TOP -> getY();
            case MIDDLE -> getY() + (getHeight() - visualHeight) / 2.0F;
            case BOTTOM -> getY() + getHeight() - visualHeight;
        };

        UtilsWidgets.drawText(context, guiObject.getText().getText(), textX, textY, guiObject.getText().getColor().getHexColor(), guiObject.getText().getTextSize());
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
