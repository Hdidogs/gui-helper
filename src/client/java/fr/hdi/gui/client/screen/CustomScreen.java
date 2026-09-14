package fr.hdi.gui.client.screen;

import fr.hdi.gui.client.gui.Gui;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.gui.screen.Screen;

public class CustomScreen extends Screen {
    private Gui gui;
    private GuiRenderer renderer;

    public CustomScreen(Gui gui) {
        super(gui.getTitle());
        this.gui = gui;
        this.renderer = new GuiRenderer(gui);
    }

    public GuiRenderer getRenderer() {
        return renderer;
    }

    @Override
    protected void init() {
        super.init();

        renderer.init(this.width, this.height, this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderer.syncVisibility();
        renderer.pushScale(context);

        super.render(context, renderer.scaled(mouseX), renderer.scaled(mouseY), delta);

        if (renderer.isOverlayCapturing()) this.clearTooltip();

        renderer.renderOverlays(context, mouseX, mouseY, delta);

        renderer.popScale(context);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(renderer.scaled(mouseX), renderer.scaled(mouseY));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (renderer.mouseClickedOverlay(mouseX, mouseY, button)) return true;

        return super.mouseClicked(renderer.scaled(mouseX), renderer.scaled(mouseY), button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(renderer.scaled(mouseX), renderer.scaled(mouseY), button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(renderer.scaled(mouseX), renderer.scaled(mouseY), button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (renderer.mouseScrolledOverlay(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;

        return super.mouseScrolled(renderer.scaled(mouseX), renderer.scaled(mouseY), horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && renderer.isOverlayCapturing()) {
            renderer.closeOverlays();

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        assert this.client != null;

        if (this.client.world == null) {
            renderer.pushUnscaled(context);
            this.renderPanoramaBackground(context, delta);
            renderer.popScale(context);
        }

        if (gui.isBlur()) this.applyBlur(delta);

        if (gui.isDarkBackground()) {
            renderer.pushUnscaled(context);
            this.renderDarkening(context);
            renderer.popScale(context);
        }

        renderer.drawBackground(context);
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
