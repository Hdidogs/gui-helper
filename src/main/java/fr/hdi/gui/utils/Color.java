package fr.hdi.gui.utils;

public class Color {
    private String name;
    private int color;

    public Color(String name, int color) {
        this.name = name;
        this.color = (color >>> 24) == 0 ? color | 0xFF000000 : color;
    }

    public Color withAlpha(float alpha) {
        int value = (int) (Math.max(0.0F, Math.min(1.0F, alpha)) * 255.0F);

        return new Color(name, (color & 0x00FFFFFF) | (value << 24));
    }

    public int getAlpha() {
        return color >>> 24;
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

