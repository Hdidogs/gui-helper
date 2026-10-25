package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.BoxWidget;
import fr.hdi.gui.screen.GuiHandler;
import fr.hdi.gui.utils.Texture;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.ArrayList;
import java.util.List;

public class BoxObject extends GuiObject {
    private List<GuiObject> objects = new ArrayList<>();
    private int scrollStep = 9;
    private ScrollBarObject scrollBar;
    private boolean scrollBarVisible = true;
    private int scrollSnap = -1;

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
        if (this.scrollBar != null && this.scrollBar.isStartAtEnd()) scrollBar.setStartAtEnd(true);

        this.scrollBar = scrollBar;
        this.scrollBar.setOwner(this);

        return this;
    }

    public BoxObject withoutScrollBar() {
        return withoutScrollBar(true);
    }

    public BoxObject withoutScrollBar(boolean withoutScrollBar) {
        this.scrollBarVisible = !withoutScrollBar;

        return this;
    }

    public boolean hasScrollBar() {
        return scrollBarVisible;
    }

    public BoxObject setStartAtEnd(boolean startAtEnd) {
        getScrollBar().setStartAtEnd(startAtEnd);

        return this;
    }

    public int getScrollStep() {
        return scrollStep;
    }

    public BoxObject setScrollStep(int scrollStep) {
        this.scrollStep = scrollStep;

        return this;
    }

    public int getScrollSnap() {
        if (scrollSnap >= 0) return scrollSnap;

        return objects.stream().anyMatch(object -> object instanceof SlotGridObject) ? GuiHandler.SLOT_SIZE : 0;
    }

    public BoxObject setScrollSnap(int scrollSnap) {
        this.scrollSnap = Math.max(0, scrollSnap);

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
