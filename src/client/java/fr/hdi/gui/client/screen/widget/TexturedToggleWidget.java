package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.ButtonObject;
import fr.hdi.gui.client.gui.objects.ToggleObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

public class TexturedToggleWidget extends PressableWidget {
    private ToggleObject guiObject;

    public TexturedToggleWidget(ToggleObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), object.getText() != null ? object.getText().getText() : Text.empty());
        this.guiObject = object;
    }

    public boolean getState() {
        return guiObject.getValue();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        UtilsWidgets.drawTexture(context, guiObject.getValue() ? guiObject.getOnTexture() : guiObject.getTexture(), getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void onPress() {
        guiObject.setValue(!guiObject.getValue());
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
