package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TextureRenderWidget;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;

public class TextureObject extends GuiObject {
    public TextureObject(String id, Texture texture, int objectStartX, int objectStartY) {
        this(id, texture, objectStartX, objectStartY, texture.getDrawSizeX(), texture.getDrawSizeY());
    }

    public TextureObject(String id, Texture texture, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        super(id, ObjectType.TEXTURE, texture, null, objectStartX, objectStartY, objectSizeX, objectSizeY);
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new TextureRenderWidget(this, bgX, bgY));
    }
}
