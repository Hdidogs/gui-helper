package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectRegisterType;
import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TexturedButtonWidget;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

public class ButtonObject extends GuiObject {
    public ButtonObject(Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, Runnable onPress) {
        super(ObjectType.BUTTON, ObjectRegisterType.INIT, texture, text, objectStartX, objectStartY, objectSizeX, objectSizeY, onPress);
    }

    @Override
    public <T extends Element & Drawable & Selectable> T register(int bgX, int bgY) {
        return (T) new TexturedButtonWidget(this, bgX, bgY);
    }
}
