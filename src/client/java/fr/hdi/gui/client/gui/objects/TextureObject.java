package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TextureRenderWidget;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;

public class TextureObject extends GuiObject {
    private boolean fitToSize;

    public TextureObject(String id, Texture texture, int objectStartX, int objectStartY) {
        this(id, texture, objectStartX, objectStartY, texture.getDrawSizeX(), texture.getDrawSizeY());
    }

    public TextureObject(String id, Texture texture, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        super(id, ObjectType.TEXTURE, texture, null, objectStartX, objectStartY, objectSizeX, objectSizeY);
    }

    public TextureObject fitToSize() {
        return fitToSize(true);
    }

    public TextureObject fitToSize(boolean fitToSize) {
        this.fitToSize = fitToSize;

        return this;
    }

    public boolean isFitToSize() {
        return fitToSize;
    }

    private float getFitScale() {
        return Math.min((float) getObjectSizeX() / getTexture().getDrawSizeX(), (float) getObjectSizeY() / getTexture().getDrawSizeY());
    }

    public int getDrawSizeX() {
        if (!fitToSize) return getObjectSizeX();

        return Math.max(1, Math.round(getTexture().getDrawSizeX() * getFitScale()));
    }

    public int getDrawSizeY() {
        if (!fitToSize) return getObjectSizeY();

        return Math.max(1, Math.round(getTexture().getDrawSizeY() * getFitScale()));
    }

    public int getDrawOffsetX() {
        return (getObjectSizeX() - getDrawSizeX()) / 2;
    }

    public int getDrawOffsetY() {
        return (getObjectSizeY() - getDrawSizeY()) / 2;
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new TextureRenderWidget(this, bgX, bgY));
    }
}
