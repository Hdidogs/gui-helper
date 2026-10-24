package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;

import java.math.BigDecimal;

public abstract class NumberFieldObject extends TextFieldObject {
    private boolean decimal;
    private double min;
    private double max;

    protected NumberFieldObject(String id, Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, int maxLength, boolean decimal, double min, double max) {
        super(id, texture, text, objectStartX, objectStartY, objectSizeX, objectSizeY, maxLength);
        this.decimal = decimal;
        this.min = min;
        this.max = max;
    }

    public NumberFieldObject setRange(double min, double max) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);

        return this;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean isDecimal() {
        return decimal;
    }

    private static BigDecimal parse(String text) {
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private BigDecimal clamp(BigDecimal value) {
        BigDecimal low = BigDecimal.valueOf(min);
        BigDecimal high = BigDecimal.valueOf(max);

        return value.compareTo(low) < 0 ? low : value.compareTo(high) > 0 ? high : value;
    }

    private String format(BigDecimal value) {
        return decimal ? value.stripTrailingZeros().toPlainString() : value.toBigInteger().toString();
    }

    @Override
    public boolean accepts(String text) {
        if (text.isEmpty()) return true;
        if (!text.matches(decimal ? "-?\\d*\\.?\\d*" : "-?\\d*")) return false;
        if (text.startsWith("-") && min >= 0) return false;

        BigDecimal value = parse(text);

        if (value == null) return true;

        double number = value.doubleValue();

        return !(number > max && number > 0) && !(number < min && number < 0);
    }

    @Override
    public String normalizeInput(String text) {
        return decimal ? text.replace(',', '.') : text;
    }

    @Override
    public void commit() {
        BigDecimal value = parse(getValue());

        setValue(value == null ? "" : format(clamp(value)));
    }

    public double getNumber() {
        BigDecimal value = parse(getValue());

        return clamp(value == null ? BigDecimal.ZERO : value).doubleValue();
    }

    @Override
    public void applyData(GuiData data) {
        super.applyData(data);

        commit();
    }

    @Override
    public void collectData(GuiData data) {
        commit();

        super.collectData(data);
    }
}
