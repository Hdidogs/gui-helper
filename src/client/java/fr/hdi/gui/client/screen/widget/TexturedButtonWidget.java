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
    private static final float DISABLED_OPACITY = 0.5F;

    private ButtonObject guiObject;

    public TexturedButtonWidget(ButtonObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), object.getText() != null ? object.getText().getText() : Text.empty());
        this.guiObject = object;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.active = guiObject.isEnabled();

        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        int offset = this.active && guiObject.hasHoverAnimation() && isHovered() ? -1 : 0;
        int dx = getX() + offset;
        int dy = getY() + offset;

        UtilsWidgets.drawTexture(context, guiObject.getTexture(), dx, dy, getWidth(), getHeight(), this.active ? 1.0F : DISABLED_OPACITY);

        //if (icon != null) {
        //    int ix = dx + (getWidth() - iconSize) / 2;
        //    int iy = dy + (getHeight() - iconSize) / 2;
        //    context.drawTexture(icon, ix, iy, iconSize, iconSize, 0.0F, 0.0F, iconSource, iconSource, iconSource, iconSource);
        //}

        if (guiObject.getText() != null && guiObject.getText().getText() != null) {
            int textColor = this.active ? guiObject.getText().getColor().getHexColor()
                    : guiObject.getText().getColor().withAlpha(DISABLED_OPACITY).getHexColor();

            context.getMatrices().push();
            context.getMatrices().translate(dx + getWidth() / 2.0F, dy + getHeight() / 2.0F, 0.0F);
            context.getMatrices().scale(guiObject.getText().getTextSize(), guiObject.getText().getTextSize(), 1.0F);
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, guiObject.getText().getText(), 0, -4, textColor);
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
