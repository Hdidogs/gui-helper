package fr.hdi.gui.client.screen.widget;

import fr.hdi.gui.client.gui.objects.RadioButtonObject;
import fr.hdi.gui.client.gui.objects.RadioButtonObject.RadioOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class TexturedRadioButtonWidget extends ClickableWidget {
    private RadioButtonObject guiObject;

    public TexturedRadioButtonWidget(RadioButtonObject object, int bgX, int bgY) {
        super(bgX + object.getObjectStartX(), bgY + object.getObjectStartY(), getSizeX(object), getSizeY(object), Text.empty());
        this.guiObject = object;
    }

    private static int getSizeX(RadioButtonObject object) {
        int size = 0;

        for (RadioOption option : object.getOptions()) size = Math.max(size, option.getObjectStartX() + object.getTexture().getDrawSizeX());

        return size;
    }

    private static int getSizeY(RadioButtonObject object) {
        int size = 0;

        for (RadioOption option : object.getOptions()) size = Math.max(size, option.getObjectStartY() + object.getTexture().getDrawSizeY());

        return size;
    }

    private int getOptionX(RadioOption option) {
        return getX() + option.getObjectStartX();
    }

    private int getOptionY(RadioOption option) {
        return getY() + option.getObjectStartY();
    }

    private boolean isOverOption(RadioOption option, double mouseX, double mouseY) {
        int x = getOptionX(option);
        int y = getOptionY(option);

        return mouseX >= x && mouseX < x + guiObject.getTexture().getDrawSizeX() && mouseY >= y && mouseY < y + guiObject.getTexture().getDrawSizeY();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (guiObject.hasLabel()) UtilsWidgets.drawLabel(context, guiObject, getX(), getY());

        int sizeX = guiObject.getTexture().getDrawSizeX();
        int sizeY = guiObject.getTexture().getDrawSizeY();

        for (RadioOption option : guiObject.getOptions()) {
            boolean selected = option.getValue().equals(guiObject.getValue());

            UtilsWidgets.drawTexture(context, selected ? guiObject.getSelectedTexture() : guiObject.getTexture(), getOptionX(option), getOptionY(option), sizeX, sizeY);

            if (!option.hasLabel()) continue;

            int textY = getOptionY(option) + (sizeY - (int) (MinecraftClient.getInstance().textRenderer.fontHeight * option.getLabel().getTextSize())) / 2;

            UtilsWidgets.drawText(context, option.getLabel().getText(), getOptionX(option) + sizeX + 2, textY, option.getLabel().getColor().getHexColor(), option.getLabel().getTextSize());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || !isValidClickButton(button)) return false;

        for (RadioOption option : guiObject.getOptions()) {
            if (!isOverOption(option, mouseX, mouseY)) continue;

            guiObject.setValue(option.getValue());
            playDownSound(MinecraftClient.getInstance().getSoundManager());

            return true;
        }

        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
