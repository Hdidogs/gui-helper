package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.GuiObject;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class TexturedTextFieldWidget extends TextFieldWidget {
    private GuiObject guiObject;

    public TexturedTextFieldWidget(TextRenderer textRenderer, GuiObject guiObject) {
        super(textRenderer, guiObject.getObjectStartX(), guiObject.getObjectStartY(), guiObject.getObjectSizeX(), guiObject.getObjectSizeY(), guiObject.getText().getText());
        this.guiObject = guiObject;
        this.setDrawsBackground(false);
        this.setMaxLength(24);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) return;
        context.drawTexture(guiObject.getTexture().getTexture(), this.getX(), this.getY(), (float) guiObject.getTexture().getDrawStartX(), (float) guiObject.getTexture().getDrawStartY(), guiObject.getTexture().getTextureSizeX(), guiObject.getTexture().getDrawSizeY(), guiObject.getTexture().getTextureSizeX(), guiObject.getTexture().getTextureSizeY());
        super.renderWidget(context, mouseX, mouseY, delta);
    }
}
