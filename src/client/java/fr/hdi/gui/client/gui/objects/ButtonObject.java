package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TexturedButtonWidget;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.function.BooleanSupplier;

public class ButtonObject extends GuiObject {
    private boolean hoverAnimation = true;
    private Runnable onPress;
    private BooleanSupplier enableWhen = () -> true;

    public ButtonObject(String id, Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, Runnable onPress) {
        super(id, ObjectType.BUTTON, texture, text, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.onPress = onPress;
    }

    public Runnable getOnPress() {
        return onPress;
    }

    public boolean isEnabled() {
        return enableWhen.getAsBoolean();
    }

    public ButtonObject setEnableWhen(BooleanSupplier enableWhen) {
        this.enableWhen = enableWhen;

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