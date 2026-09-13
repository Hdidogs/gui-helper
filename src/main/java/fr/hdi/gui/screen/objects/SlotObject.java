package fr.hdi.gui.screen.objects;

import net.minecraft.util.Identifier;

public class SlotObject {
    private String id;
    private int index;
    private int objectStartX;
    private int objectStartY;
    private Identifier backgroundSprite;

    public SlotObject(String id, int index, int objectStartX, int objectStartY) {
        this(id, index, objectStartX, objectStartY, null);
    }

    public SlotObject(String id, int index, int objectStartX, int objectStartY, Identifier backgroundSprite) {
        this.id = id;
        this.index = index;
        this.objectStartX = objectStartX;
        this.objectStartY = objectStartY;
        this.backgroundSprite = backgroundSprite;
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public int getObjectStartX() {
        return objectStartX;
    }

    public int getObjectStartY() {
        return objectStartY;
    }

    public Identifier getBackgroundSprite() {
        return backgroundSprite;
    }

    public boolean hasBackgroundSprite() {
        return backgroundSprite != null;
    }
}
