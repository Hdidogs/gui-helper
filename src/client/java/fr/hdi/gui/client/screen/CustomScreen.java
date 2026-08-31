package fr.hdi.gui.client.screen;

import fr.hdi.gui.client.gui.Gui;
import fr.hdi.gui.client.gui.objects.GuiObject;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class CustomScreen extends Screen {
    private Gui gui;
    private int bgX;
    private int bgY;

    public CustomScreen(Gui gui) {
        super(gui.getTitle());
        this.gui = gui;
    }

    @Override
    protected void init() {
        super.init();

        this.bgX = (this.width - gui.getBackground().getDrawSizeX()) / 2;
        this.bgY = (this.height - gui.getBackground().getDrawSizeY()) / 2;

        for (GuiObject object : gui.getObjectsInitRegisterType()) {
            this.addDrawable(object.register(this.bgX, this.bgY));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (gui.hasBackground()) context.drawTexture(gui.getBackground().getTexture(), this.bgX, this.bgY, gui.getBackground().getDrawStartX(), gui.getBackground().getDrawStartY(), gui.getBackground().getDrawSizeX(), gui.getBackground().getDrawSizeY(), gui.getBackground().getTextureSizeX(), gui.getBackground().getTextureSizeY());

        for (GuiObject object : gui.getObjectsRenderRegisterType()) {
            this.addDrawable(object.register(this.bgX, this.bgY));
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        assert this.client != null;
        if (this.client.world == null) {
            this.renderPanoramaBackground(context, delta);
        }

        if (gui.isBlur()) this.applyBlur(delta);
        if (gui.isDarkBackground()) this.renderDarkening(context);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return gui.shouldCloseOnEsc();
    }

    @Override
    public boolean shouldPause() {
        return gui.shouldPause();
    }
}
