package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.ModelItemRenderObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.text.Text;
import org.joml.Quaternionf;

public class ModelItemRenderWidget extends ClickableWidget {
    private static final float FIT_MARGIN = 0.8F;
    private static final float DEGREES = (float) Math.PI / 180.0F;
    private static final float DEPTH = 150.0F;

    private ModelItemRenderObject guiObject;

    public ModelItemRenderWidget(ModelItemRenderObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), object.getObjectSizeX(), object.getObjectSizeY(), Text.empty());
        this.guiObject = object;
        this.active = false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        UtilsWidgets.drawTexture(context, guiObject.getTexture(), getX(), getY(), getWidth(), getHeight());

        if (guiObject.getStack().isEmpty()) return;

        float size = Math.min(getWidth(), getHeight()) * FIT_MARGIN;

        UtilsWidgets.enableScissor(context, getX(), getY(), getX() + getWidth(), getY() + getHeight());

        context.getMatrices().push();
        context.getMatrices().translate(getX() + getWidth() / 2.0F, getY() + getHeight() / 2.0F, DEPTH);
        context.getMatrices().scale(size, -size, size);
        context.getMatrices().multiply(new Quaternionf().rotationXYZ(guiObject.getTiltX() * DEGREES, guiObject.getTiltY() * DEGREES, guiObject.getTiltZ() * DEGREES));
        context.getMatrices().multiply(UtilsWidgets.spinRotation(guiObject.getSpinAxis(), guiObject.getSpinSpeed()));

        MinecraftClient client = MinecraftClient.getInstance();

        client.getItemRenderer().renderItem(guiObject.getStack(), guiObject.getTransformationMode(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV,
                context.getMatrices(), context.getVertexConsumers(), client.world, 0);

        context.draw();
        context.getMatrices().pop();
        context.disableScissor();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
