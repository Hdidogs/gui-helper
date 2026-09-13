package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TexturedTextFieldWidget;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;

public class TextFieldObject extends GuiObject {
    private int maxLength;
    private String defaultValue = "";
    private String value = "";

    public TextFieldObject(String id, Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, int maxLength) {
        super(id, ObjectType.TEXT_FIELD, texture, text, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.maxLength = maxLength;
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new TexturedTextFieldWidget(MinecraftClient.getInstance().textRenderer, this, bgX, bgY, maxLength));
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TextFieldObject setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        this.value = defaultValue;

        return this;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public void resetState() {
        this.value = defaultValue;
    }

    @Override
    public void applyData(GuiData data) {
        this.value = data.getString(getId(), value);
    }

    @Override
    public void collectData(GuiData data) {
        data.set(getId(), value);
    }

}
