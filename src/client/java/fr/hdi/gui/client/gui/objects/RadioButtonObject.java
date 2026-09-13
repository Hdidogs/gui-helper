package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TexturedRadioButtonWidget;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import fr.hdi.gui.utils.Textures;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.ArrayList;
import java.util.List;

public class RadioButtonObject extends GuiObject {
    private Texture selectedTexture;
    private List<RadioOption> options = new ArrayList<>();
    private String defaultValue;
    private String value;

    public RadioButtonObject(String id, int objectStartX, int objectStartY) {
        this(id, Textures.TEXTURE_RADIO_BUTTON_OFF, Textures.TEXTURE_RADIO_BUTTON_ON, objectStartX, objectStartY);
    }

    public RadioButtonObject(String id, Texture texture, Texture selectedTexture, int objectStartX, int objectStartY) {
        super(id, ObjectType.RADIO_BUTTON, texture, null, objectStartX, objectStartY, 0, 0);
        this.selectedTexture = selectedTexture;
    }

    public RadioButtonObject addOption(String value, int objectStartX, int objectStartY) {
        return addOption(value, objectStartX, objectStartY, null);
    }

    public RadioButtonObject addOption(String value, int objectStartX, int objectStartY, TextWithDetail label) {
        options.add(new RadioOption(value, objectStartX, objectStartY, label));

        if (this.value == null) setDefaultValue(value);

        return this;
    }

    public List<RadioOption> getOptions() {
        return options;
    }

    public Texture getSelectedTexture() {
        return selectedTexture;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RadioButtonObject setDefaultValue(String defaultValue) {
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
        this.value = data.getString(getId(), value);
    }

    @Override
    public void collectData(GuiData data) {
        if (value != null) data.set(getId(), value);
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new TexturedRadioButtonWidget(this, bgX, bgY));
    }

    public static class RadioOption {
        private String value;
        private int objectStartX;
        private int objectStartY;
        private TextWithDetail label;

        public RadioOption(String value, int objectStartX, int objectStartY, TextWithDetail label) {
            this.value = value;
            this.objectStartX = objectStartX;
            this.objectStartY = objectStartY;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public int getObjectStartX() {
            return objectStartX;
        }

        public int getObjectStartY() {
            return objectStartY;
        }

        public TextWithDetail getLabel() {
            return label;
        }

        public boolean hasLabel() {
            return label != null;
        }
    }
}
