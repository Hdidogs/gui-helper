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
    private int borderLeft;
    private int borderTop;
    private int borderRight;
    private int borderBottom;

    public Texture(Identifier texture, int drawStartX, int drawStartY, int drawSizeX, int drawSizeY, int textureSizeX, int textureSizeY) {
        this.texture = texture;
        this.drawStartX = drawStartX;
        this.drawStartY = drawStartY;
        this.drawSizeX = drawSizeX;
        this.drawSizeY = drawSizeY;
        this.textureSizeX = textureSizeX;
        this.textureSizeY = textureSizeY;
    }

    public Texture setNineSlice(int border) {
        return setNineSlice(border, border, border, border);
    }

    public Texture setNineSlice(int borderLeft, int borderTop, int borderRight, int borderBottom) {
        this.borderLeft = borderLeft;
        this.borderTop = borderTop;
        this.borderRight = borderRight;
        this.borderBottom = borderBottom;

        return this;
    }

    public boolean isNineSlice() {
        return borderLeft > 0 || borderTop > 0 || borderRight > 0 || borderBottom > 0;
    }

    public int getBorderLeft() {
        return borderLeft;
    }

    public int getBorderTop() {
        return borderTop;
    }

    public int getBorderRight() {
        return borderRight;
    }

    public int getBorderBottom() {
        return borderBottom;
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
