package fr.hdi.gui.client.gui;

import fr.hdi.gui.client.gui.objects.BoxObject;
import fr.hdi.gui.client.gui.objects.GuiObject;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.Texture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Gui {
    private Identifier id;
    private GuiData data = new GuiData();
    private Text title;
    private List<GuiObject> objects = new ArrayList<>();
    private Texture background;
    private int sizeX;
    private int sizeY;
    private float sizeMultiplicator;
    private boolean shouldPause;
    private boolean shouldCloseOnEsc;
    private boolean blur;
    private boolean darkBackground;

    public Gui() {
        title = Text.literal("");
        blur = true;
        shouldPause = false;
        shouldCloseOnEsc = true;
        sizeMultiplicator = 1.0F;
    }

    public float getSizeMultiplicator() {
        return sizeMultiplicator;
    }

    public Gui setSizeMultiplicator(float sizeMultiplicator) {
        this.sizeMultiplicator = Math.max(0.1F, sizeMultiplicator);

        return this;
    }

    public List<GuiObject> getObjects() {
        return objects;
    }

    public GuiData getData() {
        return data;
    }

    public void setData(GuiData data) {
        this.data = data;
    }

    public GuiData collectValues() {
        GuiData values = new GuiData();

        collectValues(objects, values);

        return values;
    }

    private static void collectValues(List<GuiObject> objects, GuiData values) {
        for (GuiObject object : objects) {
            object.collectData(values);

            if (object instanceof BoxObject box) collectValues(box.getObjects(), values);
        }
    }

    public void resetState() {
        resetState(objects, data);
    }

    private static void resetState(List<GuiObject> objects, GuiData data) {
        for (GuiObject object : objects) {
            object.resetState();
            object.applyData(data);

            if (object instanceof BoxObject box) resetState(box.getObjects(), data);
        }
    }

    public GuiObject getObject(String id) {
        return findObject(objects, id);
    }

    private static GuiObject findObject(List<GuiObject> objects, String id) {
        for (GuiObject object : objects) {
            if (id.equals(object.getId())) return object;

            if (object instanceof BoxObject box) {
                GuiObject found = findObject(box.getObjects(), id);

                if (found != null) return found;
            }
        }

        return null;
    }

    public <T extends GuiObject> T getObject(String id, Class<T> type) {
        GuiObject object = getObject(id);

        return type.isInstance(object) ? type.cast(object) : null;
    }

    public Gui setSize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        return this;
    }

    public int getSizeX() {
        return sizeX > 0 ? sizeX : (hasBackground() ? background.getDrawSizeX() : 0);
    }

    public int getSizeY() {
        return sizeY > 0 ? sizeY : (hasBackground() ? background.getDrawSizeY() : 0);
    }

    public boolean hasBackground() {
        return background != null;
    }

    public Texture getBackground() {
        return background;
    }

    public Gui setBackground(Texture background) {
        this.background = background;
        return this;
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public Text getTitle() {
        return title;
    }

    public Gui setTitle(Text title) {
        this.title = title;

        return this;
    }

    public boolean shouldPause() {
        return shouldPause;
    }

    public Gui setShouldPause(boolean shouldPause) {
        this.shouldPause = shouldPause;

        return this;
    }

    public boolean shouldCloseOnEsc() {
        return shouldCloseOnEsc;
    }

    public Gui setShouldCloseOnEsc(boolean shouldCloseOnEsc) {
        this.shouldCloseOnEsc = shouldCloseOnEsc;

        return this;
    }

    public boolean isBlur() {
        return blur;
    }

    public Gui activeBlur() {
        return setBlur(true);
    }

    public Gui setBlur(boolean blur) {
        this.blur = blur;

        return this;
    }

    public Gui addObject(GuiObject object) {
        objects.add(object);

        validateIds();

        return this;
    }

    public void validateIds() {
        collectIds(objects, new HashSet<>());
    }

    private static void collectIds(List<GuiObject> objects, Set<String> ids) {
        for (GuiObject object : objects) {
            if (!ids.add(object.getId())) throw new IllegalStateException("Duplicate gui object id : " + object.getId());

            if (object instanceof BoxObject box) collectIds(box.getObjects(), ids);
        }
    }

    public boolean isDarkBackground() {
        return darkBackground;
    }

    public Gui setDarkBackground(boolean darkBackground) {
        this.darkBackground = darkBackground;

        return this;
    }
}
