package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.EntityWidget;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;

public class EntityGuiObject extends GuiObject {
    private Entity entity;
    private boolean controlByMouse;
    private float tiltX;
    private float tiltY;
    private float tiltZ;
    private Direction.Axis spinAxis = Direction.Axis.Y;
    private float spinSpeed;

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

    public float getTiltX() {
        return tiltX;
    }

    public float getTiltY() {
        return tiltY;
    }

    public float getTiltZ() {
        return tiltZ;
    }

    public EntityGuiObject setTilt(float tiltX, float tiltY, float tiltZ) {
        this.tiltX = tiltX;
        this.tiltY = tiltY;
        this.tiltZ = tiltZ;

        return this;
    }

    public EntityGuiObject setAngles(float angleX, float angleY, float angleZ) {
        return setTilt(angleX, angleY, angleZ);
    }

    public Direction.Axis getSpinAxis() {
        return spinAxis;
    }

    public float getSpinSpeed() {
        return spinSpeed;
    }

    public EntityGuiObject setSpin(Direction.Axis spinAxis, float spinSpeed) {
        this.spinAxis = spinAxis;
        this.spinSpeed = spinSpeed;

        return this;
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new EntityWidget(this, bgX, bgY));
    }
}
