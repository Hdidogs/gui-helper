package fr.hdi.gui.client.screen;

import fr.hdi.gui.client.gui.Gui;
import fr.hdi.gui.client.gui.GuiRegistry;
import fr.hdi.gui.screen.CustomScreenHandler;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class CustomHandledScreen extends HandledScreen<CustomScreenHandler> {
    private Gui gui;
    private GuiRenderer renderer;

    public CustomHandledScreen(CustomScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        this.gui = GuiRegistry.get(handler.getGuiHandler().getId());
        this.gui.setData(handler.getData());
        this.renderer = new GuiRenderer(gui);
    }

    public GuiRenderer getRenderer() {
        return renderer;
    }

    @Override
    protected void init() {
        this.backgroundWidth = gui.getSizeX();
        this.backgroundHeight = gui.getSizeY();

        super.init();

        renderer.init(this.width, this.height, this::addDrawableChild);

        this.x = renderer.getGuiX();
        this.y = renderer.getGuiY();
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
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        renderer.drawBackground(context);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (gui.isBlur()) this.applyBlur(delta);
        if (gui.isDarkBackground()) this.renderDarkening(context);

        this.drawBackground(context, delta, mouseX, mouseY);
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
    public boolean shouldCloseOnEsc() {
        return gui.shouldCloseOnEsc();
    }

    @Override
    public boolean shouldPause() {
        return gui.shouldPause();
    }
}
