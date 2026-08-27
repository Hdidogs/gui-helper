package fr.hdi.gui.gui;

import fr.hdi.gui.gui.objects.GuiObject;
import fr.hdi.gui.gui.utils.GuiAllign;
import fr.hdi.gui.gui.utils.Texture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Gui {
    private Text title;
    private List<GuiObject> objects = new ArrayList<>();
    private Texture background;
    private GuiAllign allign;
    private boolean isHandler;
    private boolean shouldPause;
    private boolean shouldCloseOnEsc;
    private boolean blur;
    private boolean darkBackground;

    public Gui() {
        title = Text.literal("");
        allign = GuiAllign.CENTER;
        isHandler = false;
        blur = true;
        shouldPause = false;
        shouldCloseOnEsc = true;
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

    public Text getTitle() {
        return title;
    }

    public boolean isHandler() {
        return isHandler;
    }

    public boolean shouldPause() {
        return shouldPause;
    }

    public boolean shouldCloseOnEsc() {
        return shouldCloseOnEsc;
    }

    public boolean isBlur() {
        return blur;
    }

    public Gui setBlur(boolean blur) {
        this.blur = blur;
        return this;
    }

    public boolean isDarkBackground() {
        return darkBackground;
    }

    public Gui setDarkBackground(boolean darkBackground) {
        this.darkBackground = darkBackground;

        return this;
    }
}
