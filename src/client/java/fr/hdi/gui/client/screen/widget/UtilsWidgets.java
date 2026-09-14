package fr.hdi.gui.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.hdi.gui.GuiHelper;
import fr.hdi.gui.client.gui.objects.GuiObject;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class UtilsWidgets {
    public static void enableScissor(DrawContext context, int startX, int startY, int endX, int endY) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Vector4f start = matrix.transform(new Vector4f(startX, startY, 0.0F, 1.0F));
        Vector4f end = matrix.transform(new Vector4f(endX, endY, 0.0F, 1.0F));

        context.enableScissor((int) start.x, (int) start.y, (int) end.x, (int) end.y);
    }

    public static void drawTexture(DrawContext context, Texture texture, int x, int y, int sizeX, int sizeY) {
        drawTexture(context, texture, x, y, sizeX, sizeY, 1.0F);
    }

    public static void drawTexture(DrawContext context, Texture texture, int x, int y, int sizeX, int sizeY, float opacity) {
        if (texture == null) return;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, MathHelper.clamp(opacity, 0.0F, 1.0F));

        if (texture.isNineSlice()) {
            drawNineSlice(context, texture, x, y, sizeX, sizeY);
        } else {
            drawRegion(context, texture, x, y, sizeX, sizeY, texture.getDrawStartX(), texture.getDrawStartY(), texture.getDrawSizeX(), texture.getDrawSizeY());
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    private static void drawRegion(DrawContext context, Texture texture, int x, int y, int sizeX, int sizeY, int u, int v, int regionSizeX, int regionSizeY) {
        if (sizeX <= 0 || sizeY <= 0 || regionSizeX <= 0 || regionSizeY <= 0) return;

        context.drawTexture(texture.getTexture(), x, y, sizeX, sizeY, (float) u, (float) v, regionSizeX, regionSizeY, texture.getTextureSizeX(), texture.getTextureSizeY());
    }

    private static void drawNineSlice(DrawContext context, Texture texture, int x, int y, int sizeX, int sizeY) {
        int u = texture.getDrawStartX();
        int v = texture.getDrawStartY();
        int textureSizeX = texture.getDrawSizeX();
        int textureSizeY = texture.getDrawSizeY();

        int left = texture.getBorderLeft();
        int top = texture.getBorderTop();
        int right = texture.getBorderRight();
        int bottom = texture.getBorderBottom();

        int centerSizeX = sizeX - left - right;
        int centerSizeY = sizeY - top - bottom;
        int centerRegionSizeX = textureSizeX - left - right;
        int centerRegionSizeY = textureSizeY - top - bottom;

        int rightX = x + sizeX - right;
        int bottomY = y + sizeY - bottom;
        int rightU = u + textureSizeX - right;
        int bottomV = v + textureSizeY - bottom;

        drawRegion(context, texture, x, y, left, top, u, v, left, top);
        drawRegion(context, texture, rightX, y, right, top, rightU, v, right, top);
        drawRegion(context, texture, x, bottomY, left, bottom, u, bottomV, left, bottom);
        drawRegion(context, texture, rightX, bottomY, right, bottom, rightU, bottomV, right, bottom);

        drawRegion(context, texture, x + left, y, centerSizeX, top, u + left, v, centerRegionSizeX, top);
        drawRegion(context, texture, x + left, bottomY, centerSizeX, bottom, u + left, bottomV, centerRegionSizeX, bottom);
        drawRegion(context, texture, x, y + top, left, centerSizeY, u, v + top, left, centerRegionSizeY);
        drawRegion(context, texture, rightX, y + top, right, centerSizeY, rightU, v + top, right, centerRegionSizeY);

        drawRegion(context, texture, x + left, y + top, centerSizeX, centerSizeY, u + left, v + top, centerRegionSizeX, centerRegionSizeY);
    }

    public static void drawLabel(DrawContext context, GuiObject object, int x, int y) {
        TextWithDetail label = object.getLabel();
        int height = (int) (MinecraftClient.getInstance().textRenderer.fontHeight * label.getTextSize());

        drawText(context, label.getText(), x, y - height - 1, label.getColor().getHexColor(), label.getTextSize());
    }

    public static void drawItem(DrawContext context, int x, int y, float scale, Item item) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0F);
        context.getMatrices().scale(scale, scale, scale);
        context.drawItem(new ItemStack(item), 0, 0);
        context.getMatrices().pop();
    }

    public static void drawItem(DrawContext context, int x, int y, float scale, ItemStack stack) {
        if (stack.isEmpty()) return;

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0F);
        context.getMatrices().scale(scale, scale, scale);
        context.drawItem(stack, 0, 0);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, stack, 0, 0);
        context.getMatrices().pop();
    }

    public static void drawText(DrawContext context, Text text, float textX, float textY, int color, float scale) {
        context.getMatrices().push();
        context.getMatrices().translate(textX, textY, 0.0F);
        context.getMatrices().scale(scale, scale, 1.0F);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, 0, 0, color);
        context.getMatrices().pop();
    }
}
