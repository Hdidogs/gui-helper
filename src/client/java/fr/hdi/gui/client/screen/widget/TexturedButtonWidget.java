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
import org.lwjgl.glfw.GLFW;

public class TexturedButtonWidget extends PressableWidget {
    private ButtonObject guiObject;
    private boolean holding;

    public TexturedButtonWidget(ButtonObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), object.getText() != null ? object.getText().getText() : Text.empty());
        this.guiObject = object;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.active = guiObject.isEnabled();

        UtilsWidgets.renderState(context, guiObject, () -> draw(context));

        tickHold(delta);
    }

    private void draw(DrawContext context) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        int offset = this.active && guiObject.hasHoverAnimation() && isHovered() ? -1 : 0;
        int dx = getX() + offset;
        int dy = getY() + offset;

        UtilsWidgets.drawTexture(context, guiObject.getTexture(), dx, dy, getWidth(), getHeight());

        if (guiObject.getIcon() != null) {
            int ix = dx + (getWidth() - guiObject.getIconSizeX()) / 2;
            int iy = dy + (getHeight() - guiObject.getIconSizeY()) / 2;

            UtilsWidgets.drawTexture(context, guiObject.getIcon(), ix, iy, guiObject.getIconSizeX(), guiObject.getIconSizeY());
        }

        if (guiObject.getText() != null && guiObject.getText().getText() != null) {
            int textColor = guiObject.getText().getColor().getHexColor();

            float scale = UtilsWidgets.fitScale(guiObject.getText().getText(), guiObject.getText().getTextSize(), guiObject.getTextSizeX(), guiObject.getTextSizeY());

            context.getMatrices().push();
            context.getMatrices().translate(dx + guiObject.getTextStartX() + guiObject.getTextSizeX() / 2.0F, dy + guiObject.getTextStartY() + guiObject.getTextSizeY() / 2.0F, 0.0F);
            context.getMatrices().scale(scale, scale, 1.0F);
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, guiObject.getText().getText(), 0, -4, textColor);
            context.getMatrices().pop();
        }
    }

    private void tickHold(float delta) {
        if (!holding) return;

        if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS || !this.active || !this.visible) {
            holding = false;

            return;
        }

        if (isHovered() && guiObject.getOnHold() != null) guiObject.getOnHold().accept(delta);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        holding = guiObject.getOnHold() != null;

        super.onClick(mouseX, mouseY);
    }

    @Override
    public void onPress() {
        if (guiObject.getOnPress() != null) guiObject.getOnPress().run();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
