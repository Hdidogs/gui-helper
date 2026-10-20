package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.StepperWidget;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.function.IntConsumer;

public class StepperObject extends GuiObject {
    private Texture leftArrowTexture;
    private Texture rightArrowTexture;
    private Texture tickTexture;
    private Texture selectedTickTexture;
    private int count;
    private int leftArrowX;
    private int leftArrowY;
    private int rightArrowX;
    private int rightArrowY;
    private int stripStartX;
    private int stripStartY;
    private int stripEndX;
    private int stripSizeY;
    private int defaultValue = 1;
    private int value = 1;
    private IntConsumer onChanged;

    public StepperObject(String id, Texture trackTexture, Texture leftArrowTexture, Texture rightArrowTexture, Texture tickTexture, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, int count) {
        super(id, ObjectType.STEPPER, trackTexture, null, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.leftArrowTexture = leftArrowTexture;
        this.rightArrowTexture = rightArrowTexture;
        this.tickTexture = tickTexture;
        this.count = Math.max(1, count);
    }

    public StepperObject setArrowBoxes(int leftArrowX, int leftArrowY, int rightArrowX, int rightArrowY) {
        this.leftArrowX = leftArrowX;
        this.leftArrowY = leftArrowY;
        this.rightArrowX = rightArrowX;
        this.rightArrowY = rightArrowY;

        return this;
    }

    public StepperObject setTickStrip(int stripStartX, int stripStartY, int stripEndX, int stripSizeY) {
        this.stripStartX = stripStartX;
        this.stripStartY = stripStartY;
        this.stripEndX = stripEndX;
        this.stripSizeY = stripSizeY;

        return this;
    }

    public StepperObject setSelectedTickTexture(Texture selectedTickTexture) {
        this.selectedTickTexture = selectedTickTexture;

        return this;
    }

    public StepperObject setDefaultValue(int defaultValue) {
        this.defaultValue = clamp(defaultValue);
        this.value = this.defaultValue;

        return this;
    }

    public StepperObject setOnChanged(IntConsumer onChanged) {
        this.onChanged = onChanged;

        return this;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = clamp(value);
    }

    public void change(int value) {
        int previous = this.value;

        setValue(value);

        if (this.value != previous && onChanged != null) onChanged.accept(this.value);
    }

    public void previous() {
        change(value <= 1 ? count : value - 1);
    }

    public void next() {
        change(value >= count ? 1 : value + 1);
    }

    private int clamp(int value) {
        return Math.max(1, Math.min(count, value));
    }

    public float getSegmentSize() {
        return (stripEndX - stripStartX) / (float) count;
    }

    public int getTickX(int index) {
        float segment = getSegmentSize();

        return Math.round(stripStartX + segment * index + segment / 2.0F - 0.5F);
    }

    public Texture getLeftArrowTexture() {
        return leftArrowTexture;
    }

    public Texture getRightArrowTexture() {
        return rightArrowTexture;
    }

    public Texture getTickTexture() {
        return tickTexture;
    }

    public Texture getSelectedTickTexture() {
        return selectedTickTexture;
    }

    public int getCount() {
        return count;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public int getLeftArrowX() {
        return leftArrowX;
    }

    public int getLeftArrowY() {
        return leftArrowY;
    }

    public int getRightArrowX() {
        return rightArrowX;
    }

    public int getRightArrowY() {
        return rightArrowY;
    }

    public int getStripStartX() {
        return stripStartX;
    }

    public int getStripStartY() {
        return stripStartY;
    }

    public int getStripEndX() {
        return stripEndX;
    }

    public int getStripSizeY() {
        return stripSizeY;
    }

    @Override
    public void resetState() {
        this.value = defaultValue;
    }

    @Override
    public void applyData(GuiData data) {
        this.value = clamp(data.getInt(getId(), value));
    }

    @Override
    public void collectData(GuiData data) {
        data.set(getId(), value);
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new StepperWidget(this, bgX, bgY));
    }
}
