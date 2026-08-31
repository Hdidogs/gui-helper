package fr.hdi.gui.utils;

import net.minecraft.text.Text;

public class TextWithDetail {
    private Text text;
    private Color color;
    private float textSize;

    public TextWithDetail(Text text) {
        this.text = text;
        this.color = ColorHelper.WHITE;
        this.textSize = 1;
    }

    public TextWithDetail(Text text, Color color, float textSize) {
        this.text = text;
        this.color = color;
        this.textSize = textSize;
    }

    public TextWithDetail(Text text, float textSize) {
        this.text = text;
        this.textSize = textSize;
        this.color = ColorHelper.WHITE;
    }

    public TextWithDetail(Text text, Color color) {
        this.text = text;
        this.color = color;
        this.textSize = 1;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public Text getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public float getTextSize() {
        return textSize;
    }
}
