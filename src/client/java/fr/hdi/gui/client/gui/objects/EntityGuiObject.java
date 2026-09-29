package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.EntityWidget;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.Entity;

public class EntityGuiObject extends GuiObject {
    private Entity entity;
    private boolean controlByMouse;
    private float angleX;
    private float angleY;
    private float angleZ;

    public EntityGuiObject(String id, Texture texture, Entity entity, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        super(id, ObjectType.ENTITY, texture, null, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntityGuiObject setEntity(Entity entity) {
        this.entity = entity;

        return this;
    }

    public boolean isControlByMouse() {
        return controlByMouse;
    }

    public EntityGuiObject canControlByMouse(boolean controlByMouse) {
        this.controlByMouse = controlByMouse;

        return this;
    }

    public float getAngleX() {
        return angleX;
    }

    public float getAngleY() {
        return angleY;
    }

    public float getAngleZ() {
        return angleZ;
    }

    public EntityGuiObject setAngles(float angleX, float angleY, float angleZ) {
        this.angleX = angleX;
        this.angleY = angleY;
        this.angleZ = angleZ;

        return this;
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new EntityWidget(this, bgX, bgY));
    }
}
