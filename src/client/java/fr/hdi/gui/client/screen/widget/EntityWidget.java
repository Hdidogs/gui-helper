package fr.hdi.gui.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.hdi.gui.client.gui.objects.EntityGuiObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityWidget extends ClickableWidget {
    private static final float FIT_MARGIN = 0.8F;
    private static final float MOUSE_SENSITIVITY = 40.0F;
    private static final float DEGREES = (float) Math.PI / 180.0F;

    private EntityGuiObject guiObject;

    public EntityWidget(EntityGuiObject object, int bgX, int bgY) {
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

        Entity entity = guiObject.getEntity();

        if (entity == null || entity.getHeight() <= 0.0F) return;

        float centerX = getX() + getWidth() / 2.0F;
        float centerY = getY() + getHeight() / 2.0F;
        float size = Math.min(getHeight() / entity.getHeight(), getWidth() / Math.max(entity.getWidth(), 0.1F)) * FIT_MARGIN * guiObject.getEntityScale();
        Vector3f offset = new Vector3f(0.0F, entity.getHeight() / 2.0F, 0.0F);
        float yaw = isMouseDriven() ? (float) Math.atan((centerX - mouseX) / MOUSE_SENSITIVITY) : 0.0F;
        float pitch = isMouseDriven() ? (float) Math.atan((centerY - mouseY) / MOUSE_SENSITIVITY) : 0.0F;
        Runnable draw = entity instanceof LivingEntity living
                ? () -> renderLiving(context, living, centerX, centerY, size, offset, yaw, pitch)
                : () -> renderEntity(context, entity, centerX, centerY, size, offset, yaw, pitch);

        UtilsWidgets.enableScissor(context, getX(), getY(), getX() + getWidth(), getY() + getHeight());

        if (guiObject.getRenderWrapper() != null) {
            guiObject.getRenderWrapper().accept(draw);
        } else {
            draw.run();
        }

        context.disableScissor();
    }

    private boolean isMouseDriven() {
        return guiObject.isControlByMouse() && !guiObject.hasEntityYaw();
    }

    private Quaternionf getSpinRotation() {
        if (guiObject.isControlByMouse() || guiObject.hasEntityYaw()) return new Quaternionf();

        return UtilsWidgets.spinRotation(guiObject.getSpinAxis(), guiObject.getSpinSpeed());
    }

    private Quaternionf getCameraRotation(float pitch) {
        if (isMouseDriven()) return new Quaternionf().rotateX(pitch * 20.0F * DEGREES);

        return new Quaternionf().rotationXYZ(guiObject.getTiltX() * DEGREES, guiObject.getTiltY() * DEGREES, guiObject.getTiltZ() * DEGREES);
    }

    private void renderLiving(DrawContext context, LivingEntity entity, float centerX, float centerY, float size, Vector3f offset, float yaw, float pitch) {
        float bodyYaw = entity.bodyYaw;
        float entityYaw = entity.getYaw();
        float entityPitch = entity.getPitch();
        float prevHeadYaw = entity.prevHeadYaw;
        float headYaw = entity.headYaw;
        Quaternionf camera = getCameraRotation(pitch);
        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI).mul(camera).mul(getSpinRotation());

        entity.bodyYaw = guiObject.hasEntityYaw() ? guiObject.getEntityYaw() : 180.0F + yaw * 20.0F;
        entity.setYaw(guiObject.hasEntityYaw() ? guiObject.getEntityYaw() : 180.0F + yaw * MOUSE_SENSITIVITY);
        entity.setPitch(-pitch * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();

        InventoryScreen.drawEntity(context, centerX, centerY, size, offset, rotation, camera, entity);

        entity.bodyYaw = bodyYaw;
        entity.setYaw(entityYaw);
        entity.setPitch(entityPitch);
        entity.prevHeadYaw = prevHeadYaw;
        entity.headYaw = headYaw;
    }

    private void renderEntity(DrawContext context, Entity entity, float centerX, float centerY, float size, Vector3f offset, float yaw, float pitch) {
        Quaternionf camera = getCameraRotation(pitch);
        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI).mul(camera).mul(getSpinRotation());

        if (guiObject.hasEntityYaw()) rotation.rotateY((180.0F - guiObject.getEntityYaw()) * DEGREES);
        else if (guiObject.isControlByMouse()) rotation.rotateY(-yaw * MOUSE_SENSITIVITY * DEGREES);

        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY, 50.0F);
        context.getMatrices().scale(size, size, -size);
        context.getMatrices().translate(offset.x(), offset.y(), offset.z());
        context.getMatrices().multiply(rotation);
        DiffuseLighting.method_34742();

        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();

        dispatcher.setRotation(camera.conjugate());
        dispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE));
        context.draw();
        dispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
