package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;

public class IntegerFieldObject extends NumberFieldObject {
    public IntegerFieldObject(String id, Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, int maxLength) {
        super(id, texture, text, objectStartX, objectStartY, objectSizeX, objectSizeY, maxLength, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public int getIntValue() {
        return (int) getNumber();
    }
}
