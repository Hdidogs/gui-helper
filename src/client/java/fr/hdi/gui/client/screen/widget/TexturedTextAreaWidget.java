package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.TextAreaObject;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.text.Text;

public class TexturedTextAreaWidget extends EditBoxWidget {
    private TextAreaObject guiObject;

    public TexturedTextAreaWidget(TextRenderer textRenderer, TextAreaObject guiObject, int bgX, int bgY, int maxLength) {
        super(textRenderer, bgX + guiObject.getObjectStartX(), bgY + guiObject.getObjectStartY(), guiObject.getObjectSizeX(), guiObject.getObjectSizeY(),
                guiObject.getText() != null ? guiObject.getText().getText() : Text.empty(), Text.empty());
        this.guiObject = guiObject;
        this.setMaxLength(maxLength);
        this.setText(guiObject.getValue());
        this.setChangeListener(guiObject::setValue);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.active = guiObject.isEnabled();

        if (!this.active && isFocused()) setFocused(false);

        UtilsWidgets.renderState(context, guiObject, () -> draw(context, mouseX, mouseY, delta));
    }

    private void draw(DrawContext context, int mouseX, int mouseY, float delta) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        if (!this.visible) return;

        UtilsWidgets.drawTexture(context, guiObject.getTexture(), getX(), getY(), getWidth(), getHeight());
        UtilsWidgets.enableScissor(context, getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1);

        context.getMatrices().push();
        context.getMatrices().translate(0.0D, -getScrollY(), 0.0D);

        this.renderContents(context, mouseX, mouseY, delta);

        context.getMatrices().pop();
        context.disableScissor();

        this.renderOverlay(context);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.active && super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.active && super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.active && super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.visible || !this.active || !isWithinBounds(mouseX, mouseY) || !overflows()) return false;

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
