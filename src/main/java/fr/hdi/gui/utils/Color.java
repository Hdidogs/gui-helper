package fr.hdi.gui.utils;

public class Color {
    private String name;
    private int color;

    public Color(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getHexColor() {
        return color;
    }

    public float[] getRGBColor() {
        float r = (float)((color & 0xFF0000) >> 16) / 255;
        float g = (float)((color & 0xFF00) >> 8) / 255;
        float b = (float)(color & 0xFF) / 255;

        return new float[] {r, g, b};
    }
}

