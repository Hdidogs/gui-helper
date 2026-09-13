package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TextRenderWidget;
import fr.hdi.gui.utils.Align;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.VerticalAlign;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class TextObject extends GuiObject {
    private Align align;
    private VerticalAlign verticalAlign;

    public TextObject(String id, TextWithDetail text, int objectStartX, int objectStartY) {
        this(id, text, objectStartX, objectStartY, 0, 0, Align.LEFT);
    }

    public TextObject(String id, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, Align align) {
        this(id, text, objectStartX, objectStartY, objectSizeX, objectSizeY, align, VerticalAlign.MIDDLE);
    }

    public TextObject(String id, TextWithDetail text, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY, Align align, VerticalAlign verticalAlign) {
        super(id, ObjectType.TEXT, null, text, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.align = align;
        this.verticalAlign = verticalAlign;
    }

    public Align getAlign() {
        return align;
    }

    public TextObject setAlign(Align align) {
        this.align = align;

        return this;
    }

    public VerticalAlign getVerticalAlign() {
        return verticalAlign;
    }

    public TextObject setVerticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign;

        return this;
    }

    @Override
    public void applyData(GuiData data) {
        if (data.has(getId())) getText().setText(Text.literal(data.getString(getId(), "")));
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new TextRenderWidget(this, bgX, bgY));
    }
}
