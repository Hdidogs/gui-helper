package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.ScrollBarWidget;
import fr.hdi.gui.utils.Texture;
import fr.hdi.gui.utils.Textures;
import net.minecraft.client.gui.widget.ClickableWidget;

public class ScrollBarObject extends GuiObject {
    private Texture sliderTexture;
    private GuiObject owner;
    private boolean autoPlaced;
    private double scrollY;

    public ScrollBarObject(String id) {
        this(id, Textures.TEXTURE_SCROLL_BAR, Textures.TEXTURE_SCROLL_SLIDER);
    }

    public ScrollBarObject(String id, Texture barTexture, Texture sliderTexture) {
        super(id, ObjectType.SCROLL_BAR, barTexture, null, 0, 0, 0, 0);
        this.sliderTexture = sliderTexture;
        this.autoPlaced = true;
    }

    public ScrollBarObject(String id, Texture barTexture, Texture sliderTexture, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        super(id, ObjectType.SCROLL_BAR, barTexture, null, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.sliderTexture = sliderTexture;
    }

    public Texture getSliderTexture() {
        return sliderTexture;
    }

    public GuiObject getOwner() {
        return owner;
    }

    public void setOwner(GuiObject owner) {
        this.owner = owner;
    }

    public double getScrollY() {
        return scrollY;
    }

    public void setScrollY(double scrollY) {
        this.scrollY = scrollY;
    }

    @Override
    public void resetState() {
        this.scrollY = 0.0D;
    }

    public boolean isAutoPlaced() {
        return autoPlaced;
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new ScrollBarWidget(this, bgX, bgY));
    }
}
