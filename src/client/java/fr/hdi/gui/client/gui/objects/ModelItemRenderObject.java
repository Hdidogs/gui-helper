package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.ModelItemRenderWidget;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ModelItemRenderObject extends GuiObject {
    public static final String COUNT_SUFFIX = ".count";

    private ItemStack defaultStack;
    private ItemStack stack;
    private ModelTransformationMode transformationMode = ModelTransformationMode.FIXED;
    private Direction.Axis spinAxis;
    private float spinSpeed;
    private float tiltX;
    private float tiltY;
    private float tiltZ;

    public ModelItemRenderObject(String id, Texture texture, ItemStack stack, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        this(id, texture, stack, objectStartX, objectStartY, objectSizeX, objectSizeY, Direction.Axis.Y, 1.0F);
    }

    public ModelItemRenderObject(String id, Texture texture, ItemStack stack, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, Direction.Axis spinAxis, float spinSpeed) {
        super(id, ObjectType.MODEL_ITEM_RENDER, texture, null, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.defaultStack = stack;
        this.stack = stack.copy();
        this.spinAxis = spinAxis;
        this.spinSpeed = spinSpeed;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ModelItemRenderObject setStack(ItemStack stack) {
        this.stack = stack;

        return this;
    }

    public ModelTransformationMode getTransformationMode() {
        return transformationMode;
    }

    public ModelItemRenderObject setTransformationMode(ModelTransformationMode transformationMode) {
        this.transformationMode = transformationMode;

        return this;
    }

    public Direction.Axis getSpinAxis() {
        return spinAxis;
    }

    public float getSpinSpeed() {
        return spinSpeed;
    }

    public ModelItemRenderObject setSpin(Direction.Axis spinAxis, float spinSpeed) {
        this.spinAxis = spinAxis;
        this.spinSpeed = spinSpeed;

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

    public ModelItemRenderObject setTilt(float tiltX, float tiltY, float tiltZ) {
        this.tiltX = tiltX;
        this.tiltY = tiltY;
        this.tiltZ = tiltZ;

        return this;
    }

    @Override
    public void resetState() {
        this.stack = defaultStack.copy();
    }

    @Override
    public void applyData(GuiData data) {
        if (!data.has(getId())) return;

        Identifier item = Identifier.tryParse(data.getString(getId(), ""));

        if (item == null || !Registries.ITEM.containsId(item)) return;

        this.stack = new ItemStack(Registries.ITEM.get(item), Math.max(1, data.getInt(getId() + COUNT_SUFFIX, 1)));
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new ModelItemRenderWidget(this, bgX, bgY));
    }
}
