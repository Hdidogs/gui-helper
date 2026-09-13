package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.TextFieldObject;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class TexturedTextFieldWidget extends TextFieldWidget {
    private TextFieldObject guiObject;

    public TexturedTextFieldWidget(TextRenderer textRenderer, TextFieldObject guiObject, int bgX, int bgY, int maxLength) {
        super(textRenderer, bgX + guiObject.getObjectStartX(), bgY + guiObject.getObjectStartY(), guiObject.getObjectSizeX(), guiObject.getObjectSizeY(), guiObject.getText() != null ? guiObject.getText().getText() : Text.empty());
        this.guiObject = guiObject;
        this.setDrawsBackground(false);
        this.setMaxLength(maxLength);
        this.setText(guiObject.getValue());
        this.setChangedListener(guiObject::setValue);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        if (!this.isVisible()) return;
        UtilsWidgets.drawTexture(context, guiObject.getTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        super.renderWidget(context, mouseX, mouseY, delta);
    }
}
