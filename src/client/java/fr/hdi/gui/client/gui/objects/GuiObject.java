package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectRegisterType;
import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.*;
import net.minecraft.text.Text;

public abstract class GuiObject {
    private ObjectType objectType;
    private ObjectRegisterType objectRegisterType;
    private Texture texture;
    private TextWithDetail text;
    private int objectStartX;
    private int objectStartY;
    private int objectSizeX;
    private int objectSizeY;
    private Runnable onPress;

    public GuiObject(ObjectType objectType, ObjectRegisterType objectRegisterType, Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, Runnable onPress) {
        this.objectType = objectType;
        this.objectRegisterType = objectRegisterType;
        this.texture = texture;
        this.text = text;
        this.objectStartX = objectStartX;
        this.objectStartY = objectStartY;
        this.objectSizeX = objectSizeX;
        this.objectSizeY = objectSizeY;
        this.onPress = onPress;
    }

    public <T extends Element & Drawable & Selectable> T register(int bgX, int bgY) {
        return null;
    }

    public ObjectRegisterType getObjectRegisterType() {
        return objectRegisterType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public Texture getTexture() {
        return texture;
    }

    public TextWithDetail getText() {
        return text;
    }

    public int getObjectStartX() {
        return objectStartX;
    }

    public int getObjectStartY() {
        return objectStartY;
    }

    public int getObjectSizeX() {
        return objectSizeX;
    }

    public int getObjectSizeY() {
        return objectSizeY;
    }

    public Runnable getOnPress() {
        return onPress;
    }
}
