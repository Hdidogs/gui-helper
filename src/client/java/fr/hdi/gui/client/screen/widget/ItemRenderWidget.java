package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.ItemRenderObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class ItemRenderWidget extends ClickableWidget {
    private ItemRenderObject guiObject;

    public ItemRenderWidget(ItemRenderObject object, int bgX, int bgY) {
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

        UtilsWidgets.drawItem(context, getX(), getY(), guiObject.getScale(), guiObject.getStack());

        if (guiObject.isShowItemTooltip() && isHovered()) renderItemTooltip();
    }

    private void renderItemTooltip() {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen screen = client.currentScreen;

        if (screen == null || guiObject.getStack().isEmpty()) return;

        screen.setTooltip(Screen.getTooltipFromItem(client, guiObject.getStack()).stream().map(Text::asOrderedText).toList());
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
