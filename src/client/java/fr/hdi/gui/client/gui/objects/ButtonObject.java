package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TexturedButtonWidget;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.gui.widget.ClickableWidget;

public class ButtonObject extends GuiObject {
    private boolean hoverAnimation = true;
    private Runnable onPress;
    private int textStartX;
    private int textStartY;
    private int textSizeX;
    private int textSizeY;
    private FloatConsumer onHold;
    private Texture icon;
    private int iconSizeX;
    private int iconSizeY;

    public ButtonObject(String id, Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, Runnable onPress) {
        super(id, ObjectType.BUTTON, texture, text, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.onPress = onPress;
    }

    public ButtonObject setTextBox(int textStartX, int textStartY, int textSizeX, int textSizeY) {
        this.textStartX = textStartX;
        this.textStartY = textStartY;
        this.textSizeX = textSizeX;
        this.textSizeY = textSizeY;

        return this;
    }

    public boolean hasTextBox() {
        return textSizeX > 0 && textSizeY > 0;
    }

    public int getTextStartX() {
        return hasTextBox() ? textStartX : 0;
    }

    public int getTextStartY() {
        return hasTextBox() ? textStartY : 0;
    }

    public int getTextSizeX() {
        return hasTextBox() ? textSizeX : getObjectSizeX();
    }

    public int getTextSizeY() {
        return hasTextBox() ? textSizeY : getObjectSizeY();
    }

    public Runnable getOnPress() {
        return onPress;
    }

    public FloatConsumer getOnHold() {
        return onHold;
    }

    public ButtonObject setOnHold(FloatConsumer onHold) {
        this.onHold = onHold;

        return this;
    }

    public Texture getIcon() {
        return icon;
    }

    public int getIconSizeX() {
        return iconSizeX;
    }

    public int getIconSizeY() {
        return iconSizeY;
    }

    public ButtonObject setIcon(Texture icon, int iconSizeX, int iconSizeY) {
        this.icon = icon;
        this.iconSizeX = iconSizeX;
        this.iconSizeY = iconSizeY;

        return this;
    }

    public boolean hasHoverAnimation() {
        return hoverAnimation;
    }

    public ButtonObject setHoverAnimation(boolean hoverAnimation) {
        this.hoverAnimation = hoverAnimation;

        return this;
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new TexturedButtonWidget(this, bgX, bgY));
    }
}