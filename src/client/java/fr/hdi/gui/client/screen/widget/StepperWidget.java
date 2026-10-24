package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.StepperObject;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class StepperWidget extends ClickableWidget {
    private StepperObject guiObject;

    public StepperWidget(StepperObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), Text.empty());
        this.guiObject = object;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.active = guiObject.isEnabled();

        UtilsWidgets.renderState(context, guiObject, () -> draw(context));
    }

    private void draw(DrawContext context) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        UtilsWidgets.drawTexture(context, guiObject.getTexture(), getX(), getY(), getWidth(), getHeight());
        drawSprite(context, guiObject.getLeftArrowTexture(), guiObject.getLeftArrowX(), guiObject.getLeftArrowY());
        drawSprite(context, guiObject.getRightArrowTexture(), guiObject.getRightArrowX(), guiObject.getRightArrowY());

        for (int index = 0; index < guiObject.getCount(); index++) {
            Texture tick = index + 1 == guiObject.getValue() && guiObject.getSelectedTickTexture() != null ? guiObject.getSelectedTickTexture() : guiObject.getTickTexture();

            drawSprite(context, tick, guiObject.getTickX(index), guiObject.getStripStartY());
        }
    }

    private void drawSprite(DrawContext context, Texture texture, int x, int y) {
        if (texture == null) return;

        UtilsWidgets.drawTexture(context, texture, getX() + x, getY() + y, texture.getDrawSizeX(), texture.getDrawSizeY());
    }

    private static boolean inside(Texture texture, double x, double y, int startX, int startY) {
        return texture != null && x >= startX && x < startX + texture.getDrawSizeX() && y >= startY && y < startY + texture.getDrawSizeY();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible || !isValidClickButton(button) || !isMouseOver(mouseX, mouseY)) return false;

        double x = mouseX - getX();
        double y = mouseY - getY();

        if (inside(guiObject.getLeftArrowTexture(), x, y, guiObject.getLeftArrowX(), guiObject.getLeftArrowY())) {
            guiObject.previous();
        } else if (inside(guiObject.getRightArrowTexture(), x, y, guiObject.getRightArrowX(), guiObject.getRightArrowY())) {
            guiObject.next();
        } else if (x >= guiObject.getStripStartX() && x < guiObject.getStripEndX() && y >= guiObject.getStripStartY() && y < guiObject.getStripStartY() + guiObject.getStripSizeY()) {
            int index = (int) ((x - guiObject.getStripStartX()) / guiObject.getSegmentSize());

            guiObject.change(Math.min(guiObject.getCount() - 1, index) + 1);
        } else {
            return false;
        }

        playDownSound(MinecraftClient.getInstance().getSoundManager());

        return true;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
