package fr.hdi.gui.utils;

public enum Anchor {
    TOP_LEFT(0, 0),
    TOP_CENTER(1, 0),
    TOP_RIGHT(2, 0),
    MIDDLE_LEFT(0, 1),
    CENTER(1, 1),
    MIDDLE_RIGHT(2, 1),
    BOTTOM_LEFT(0, 2),
    BOTTOM_CENTER(1, 2),
    BOTTOM_RIGHT(2, 2);

    private final int horizontal;
    private final int vertical;

    Anchor(int horizontal, int vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public int resolveX(int containerSizeX, int sizeX, int offsetX) {
        return resolve(horizontal, containerSizeX, sizeX, offsetX);
    }

    public int resolveY(int containerSizeY, int sizeY, int offsetY) {
        return resolve(vertical, containerSizeY, sizeY, offsetY);
    }

    private static int resolve(int edge, int containerSize, int size, int offset) {
        if (edge == 1) return (containerSize - size) / 2 + offset;
        if (edge == 2) return containerSize - size - offset;

        return offset;
    }
}
