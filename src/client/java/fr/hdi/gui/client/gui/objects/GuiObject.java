package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.utils.Anchor;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

import java.util.function.BooleanSupplier;

public abstract class GuiObject {
    private String id;
    private TextWithDetail label;
    private BooleanSupplier showWhen = () -> true;
    private BooleanSupplier disabledWhen = () -> false;
    private List<Text> tooltip;
    protected ClickableWidget widget;
    private ObjectType objectType;
    private Texture texture;
    private TextWithDetail text;
    private int objectStartX;
    private int objectStartY;
    private int objectSizeX;
    private int objectSizeY;
    private Anchor anchor = Anchor.TOP_LEFT;
    private int fillX = -1;
    private int fillY = -1;
    private boolean anchored;
    private int anchoredX;
    private int anchoredY;
    private int anchoredSizeX;
    private int anchoredSizeY;

    public GuiObject(String id, ObjectType objectType, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        this.id = id;
        this.objectType = objectType;
        this.objectStartX = objectStartX;
        this.objectStartY = objectStartY;
        this.objectSizeX = objectSizeX;
        this.objectSizeY = objectSizeY;
    }

    public GuiObject(String id, ObjectType objectType, Texture texture, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        this.id = id;
        this.objectType = objectType;
        this.texture = texture;
        this.text = text;
        this.objectStartX = objectStartX;
        this.objectStartY = objectStartY;
        this.objectSizeX = objectSizeX;
        this.objectSizeY = objectSizeY;
    }

    protected ClickableWidget cache(ClickableWidget widget) {
        this.widget = widget;

        if (hasTooltip()) widget.setTooltip(Tooltip.of(buildTooltipText()));

        return widget;
    }

    private Text buildTooltipText() {
        MutableText text = Text.empty();

        for (int index = 0; index < tooltip.size(); index++) {
            if (index > 0) text.append(ScreenTexts.LINE_BREAK);

            text.append(tooltip.get(index));
        }

        return text;
    }

    public void resetState() {
    }

    public void applyData(GuiData data) {
    }

    public void collectData(GuiData data) {
    }

    public abstract ClickableWidget register(int bgX, int bgY);

    public String getId() {
        return id;
    }

    public TextWithDetail getLabel() {
        return label;
    }

    public boolean hasLabel() {
        return label != null;
    }

    public GuiObject setLabel(TextWithDetail label) {
        this.label = label;

        return this;
    }

    public List<Text> getTooltip() {
        return tooltip;
    }

    public boolean hasTooltip() {
        return tooltip != null && !tooltip.isEmpty();
    }

    public GuiObject setTooltip(Text... lines) {
        this.tooltip = Arrays.asList(lines);

        return this;
    }

    public boolean isShown() {
        return showWhen.getAsBoolean();
    }

    public GuiObject setShowWhen(BooleanSupplier showWhen) {
        this.showWhen = showWhen;

        return this;
    }

    public boolean isDisabled() {
        return disabledWhen.getAsBoolean();
    }

    public boolean isEnabled() {
        return !isDisabled();
    }

    public GuiObject setDisabledWhen(BooleanSupplier disabledWhen) {
        this.disabledWhen = disabledWhen;

        return this;
    }

    public ClickableWidget getWidget() {
        return widget;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public Texture getTexture() {
        return texture;
    }

    public TextWithDetail getText() {
        return text;
    }

    public int getObjectStartX() {
        return anchored ? anchoredX : objectStartX;
    }

    public int getObjectStartY() {
        return anchored ? anchoredY : objectStartY;
    }

    public int getObjectSizeX() {
        return anchored ? anchoredSizeX : objectSizeX;
    }

    public int getObjectSizeY() {
        return anchored ? anchoredSizeY : objectSizeY;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public GuiObject setAnchor(Anchor anchor) {
        this.anchor = anchor == null ? Anchor.TOP_LEFT : anchor;

        return this;
    }

    public GuiObject setFillX(int endMargin) {
        this.fillX = Math.max(0, endMargin);

        return this;
    }

    public GuiObject setFillY(int endMargin) {
        this.fillY = Math.max(0, endMargin);

        return this;
    }

    public boolean isAnchored() {
        return anchored;
    }

    public int getAnchoredX() {
        return getObjectStartX();
    }

    public int getAnchoredY() {
        return getObjectStartY();
    }

    public void resolveAnchor(int containerSizeX, int containerSizeY) {
        this.anchoredSizeX = fillX >= 0 ? Math.max(0, containerSizeX - objectStartX - fillX) : objectSizeX;
        this.anchoredSizeY = fillY >= 0 ? Math.max(0, containerSizeY - objectStartY - fillY) : objectSizeY;
        this.anchoredX = anchor.resolveX(containerSizeX, anchoredSizeX, objectStartX);
        this.anchoredY = anchor.resolveY(containerSizeY, anchoredSizeY, objectStartY);
        this.anchored = true;
    }

}
