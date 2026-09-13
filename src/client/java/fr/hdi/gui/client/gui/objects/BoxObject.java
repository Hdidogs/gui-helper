package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.BoxWidget;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.ArrayList;
import java.util.List;

public class BoxObject extends GuiObject {
    private List<GuiObject> objects = new ArrayList<>();
    private int scrollStep = 9;
    private ScrollBarObject scrollBar;

    public BoxObject(String id, Texture texture, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        super(id, ObjectType.BOX, texture, null, objectStartX, objectStartY, objectSizeX, objectSizeY);
    }

    public BoxObject addObject(GuiObject object) {
        objects.add(object);

        return this;
    }

    public List<GuiObject> getObjects() {
        return objects;
    }

    public ScrollBarObject getScrollBar() {
        if (scrollBar == null) setScrollBar(new ScrollBarObject(getId() + "_scroll_bar"));

        return scrollBar;
    }

    public BoxObject setScrollBar(ScrollBarObject scrollBar) {
        this.scrollBar = scrollBar;
        this.scrollBar.setOwner(this);

        return this;
    }

    public int getScrollStep() {
        return scrollStep;
    }

    public BoxObject setScrollStep(int scrollStep) {
        this.scrollStep = scrollStep;

        return this;
    }

    @Override
    public void resetState() {
        getScrollBar().resetState();
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new BoxWidget(this, bgX, bgY));
    }
}
