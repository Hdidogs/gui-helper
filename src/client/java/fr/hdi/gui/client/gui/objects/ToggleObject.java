package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TexturedTextFieldWidget;
import fr.hdi.gui.client.screen.widget.TexturedToggleWidget;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.function.BooleanSupplier;

public class ToggleObject extends GuiObject {
    private Texture onTexture;
    private boolean defaultValue;
    private boolean value;

    public ToggleObject(String id, Texture offTexture, Texture onTexture, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        super(id, ObjectType.TOGGLE, offTexture, null, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.onTexture = onTexture;
    }

    public Texture getOnTexture() {
        return onTexture;
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new TexturedToggleWidget(this, bgX, bgY));
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public ToggleObject setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
        this.value = defaultValue;

        return this;
    }

    @Override
    public void resetState() {
        this.value = defaultValue;
    }

    @Override
    public void applyData(GuiData data) {
        this.value = data.getBoolean(getId(), value);
    }

    @Override
    public void collectData(GuiData data) {
        data.set(getId(), value);
    }

}
