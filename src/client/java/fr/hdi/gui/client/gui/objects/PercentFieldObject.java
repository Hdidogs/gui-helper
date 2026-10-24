package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;

public class PercentFieldObject extends DecimalFieldObject {
    private static final double MIN_PERCENT = 0.0D;
    private static final double MAX_PERCENT = 100.0D;
    private static final String SUFFIX = " %";

    public PercentFieldObject(String id, Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, int maxLength) {
        super(id, texture, text, objectStartX, objectStartY, objectSizeX, objectSizeY, maxLength);
        setRange(MIN_PERCENT, MAX_PERCENT);
    }

    @Override
    public NumberFieldObject setRange(double min, double max) {
        return super.setRange(Math.max(MIN_PERCENT, min), Math.min(MAX_PERCENT, max));
    }

    @Override
    public String getSuffix() {
        return SUFFIX;
    }
}
