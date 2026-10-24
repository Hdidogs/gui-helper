package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;

public class DecimalFieldObject extends NumberFieldObject {
    public DecimalFieldObject(String id, Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, int maxLength) {
        super(id, texture, text, objectStartX, objectStartY, objectSizeX, objectSizeY, maxLength, true, -Double.MAX_VALUE, Double.MAX_VALUE);
    }
}
