package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.TextFieldObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class TexturedTextFieldWidget extends TextFieldWidget {
    private static final int SUFFIX_COLOR = 0xE0E0E0;

    private TextFieldObject guiObject;

    public TexturedTextFieldWidget(TextRenderer textRenderer, TextFieldObject guiObject, int bgX, int bgY, int maxLength) {
        super(textRenderer, bgX + guiObject.getObjectStartX() + guiObject.getTextStartX(), bgY + guiObject.getObjectStartY() + guiObject.getTextStartY(),
                guiObject.getTextSizeX(), guiObject.getTextSizeY(), guiObject.getText() != null ? guiObject.getText().getText() : Text.empty());
        this.guiObject = guiObject;
        this.setDrawsBackground(false);
        this.setMaxLength(maxLength);
        this.setText(guiObject.getValue());
        this.setChangedListener(guiObject::setValue);
        this.setTextPredicate(guiObject::accepts);
    }

    @Override
    public void write(String text) {
        super.write(guiObject.normalizeInput(text));
    }

    @Override
    public void setFocused(boolean focused) {
        if (!focused && isFocused()) {
            guiObject.commit();

            if (!getText().equals(guiObject.getValue())) setText(guiObject.getValue());
        }

        super.setFocused(focused);
    }

    private int getSuffixWidth() {
        return guiObject.getSuffix() == null ? 0 : MinecraftClient.getInstance().textRenderer.getWidth(guiObject.getSuffix());
    }

    private void drawSuffix(DrawContext context) {
        if (guiObject.getSuffix() == null || getText().isEmpty()) return;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int x = getX() + Math.min(textRenderer.getWidth(getText()), getInnerWidth());

        context.drawTextWithShadow(textRenderer, guiObject.getSuffix(), x, getY(), SUFFIX_COLOR);
    }

    private float getTextScale() {
        float maxScale = guiObject.getText() != null ? guiObject.getText().getTextSize() : 1.0F;

        return UtilsWidgets.fitScale(null, maxScale, 0, guiObject.getTextSizeY());
    }

    private int getTextureX() {
        return getX() - guiObject.getTextStartX();
    }

    private int getTextureY() {
        return getY() - guiObject.getTextStartY();
    }

    @Override
    public int getInnerWidth() {
        return (int) (super.getInnerWidth() / getTextScale()) - getSuffixWidth();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(getX() + (mouseX - getX()) / getTextScale(), mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.isVisible()
                && mouseX >= getTextureX() && mouseX < getTextureX() + guiObject.getObjectSizeX()
                && mouseY >= getTextureY() && mouseY < getTextureY() + guiObject.getObjectSizeY();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.active = guiObject.isEnabled();

        if (!this.active && isFocused()) setFocused(false);

        UtilsWidgets.renderState(context, guiObject, () -> draw(context, mouseX, mouseY, delta));
    }

    private void draw(DrawContext context, int mouseX, int mouseY, float delta) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getTextureX(), getTextureY());

        if (!this.isVisible()) return;

        UtilsWidgets.drawTexture(context, guiObject.getTexture(), getTextureX(), getTextureY(), guiObject.getObjectSizeX(), guiObject.getObjectSizeY());

        float scale = getTextScale();

        context.getMatrices().push();
        context.getMatrices().translate(getX(), getY(), 0.0F);
        context.getMatrices().scale(scale, scale, 1.0F);
        context.getMatrices().translate(-getX(), -getY(), 0.0F);

        super.renderWidget(context, mouseX, mouseY, delta);
        drawSuffix(context);

        context.getMatrices().pop();
    }
}
