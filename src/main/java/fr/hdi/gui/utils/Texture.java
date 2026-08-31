package fr.hdi.gui.utils;

import net.minecraft.util.Identifier;

public class Texture {
    private Identifier texture;
    private int drawStartX;
    private int drawStartY;
    private int drawSizeX;
    private int drawSizeY;
    private int textureSizeX;
    private int textureSizeY;

    public Texture(Identifier texture, int drawStartX, int drawStartY, int drawSizeX, int drawSizeY, int textureSizeX, int textureSizeY) {
        this.texture = texture;
        this.drawStartX = drawStartX;
        this.drawStartY = drawStartY;
        this.drawSizeX = drawSizeX;
        this.drawSizeY = drawSizeY;
        this.textureSizeX = textureSizeX;
        this.textureSizeY = textureSizeY;
    }

    public Identifier getTexture() {
        return texture;
    }

    public int getDrawStartX() {
        return drawStartX;
    }

    public int getDrawStartY() {
        return drawStartY;
    }

    public int getDrawSizeX() {
        return drawSizeX;
    }

    public int getDrawSizeY() {
        return drawSizeY;
    }

    public int getTextureSizeX() {
        return textureSizeX;
    }

    public int getTextureSizeY() {
        return textureSizeY;
    }
}
