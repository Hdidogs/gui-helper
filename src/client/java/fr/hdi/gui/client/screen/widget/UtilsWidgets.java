package fr.hdi.gui.client.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class UtilsWidgets {
    public static void drawItem(DrawContext context, int x, int y, float scale, Item item) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0F);
        context.getMatrices().scale(scale, scale, scale);
        context.drawItem(new ItemStack(item), 0, 0);
        context.getMatrices().pop();
    }

    public static void drawText(DrawContext context, Text text, int textX, int textY, int color, float scale) {
        context.getMatrices().push();
        context.getMatrices().translate(textX, textY, 0.0F);
        context.getMatrices().scale(scale, scale, 1.0F);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, 0, 0, color);
        context.getMatrices().pop();
    }
}
