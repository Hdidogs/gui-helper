package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.ButtonObject;
import fr.hdi.gui.client.gui.objects.GuiObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TexturedButtonWidget extends PressableWidget {
    private ButtonObject guiObject;

    public TexturedButtonWidget(ButtonObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), object.getText().getText());
        this.guiObject = object;
    }


    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int dx = getX() + (isHovered() ? -1 : 0);
        int dy = getY() + (isHovered() ? -1 : 0);

        context.drawTexture(guiObject.getTexture().getTexture(), dx, dy, (float) guiObject.getTexture().getDrawStartX(), (float) guiObject.getTexture().getDrawStartY(), guiObject.getTexture().getTextureSizeX(), guiObject.getTexture().getDrawSizeY(), guiObject.getTexture().getTextureSizeX(), guiObject.getTexture().getTextureSizeY());

        //if (icon != null) {
        //    int ix = dx + (getWidth() - iconSize) / 2;
        //    int iy = dy + (getHeight() - iconSize) / 2;
        //    context.drawTexture(icon, ix, iy, iconSize, iconSize, 0.0F, 0.0F, iconSource, iconSource, iconSource, iconSource);
        //}

        if (guiObject.getText().getText() != null) {
            context.getMatrices().push();
            context.getMatrices().translate(dx + getWidth() / 2.0F, dy + getHeight() / 2.0F, 0.0F);
            context.getMatrices().scale(guiObject.getText().getTextSize(), guiObject.getText().getTextSize(), 1.0F);
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, guiObject.getText().getText(), 0, -4, guiObject.getText().getColor().getHexColor());
            context.getMatrices().pop();
        }
    }

    @Override
    public void onPress() {
        guiObject.getOnPress().run();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
