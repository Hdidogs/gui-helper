package fr.hdi.gui.client.screen;

import fr.hdi.gui.client.gui.Gui;
import fr.hdi.gui.client.gui.objects.GuiObject;
import fr.hdi.gui.client.screen.widget.OverlayRenderer;
import fr.hdi.gui.client.screen.widget.UtilsWidgets;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.function.Consumer;

public class GuiRenderer {
    private Gui gui;
    private int guiX;
    private int guiY;
    private float sizeMultiplicator;

    public GuiRenderer(Gui gui) {
        this.gui = gui;
        this.sizeMultiplicator = gui.getSizeMultiplicator();

        gui.build();
        gui.resetState();
    }

    public Gui getGui() {
        return gui;
    }

    public int getGuiX() {
        return guiX;
    }

    public int getGuiY() {
        return guiY;
    }

    public float getSizeMultiplicator() {
        return sizeMultiplicator;
    }

    public int scaled(double value) {
        return (int) (value / sizeMultiplicator);
    }

    private float computeSizeMultiplicator(int screenWidth, int screenHeight) {
        if (!gui.isFitToWindow()) return gui.getSizeMultiplicator();

        int sizeX = gui.getSizeX();
        int sizeY = gui.getSizeY();

        if (sizeX <= 0 || sizeY <= 0) return gui.getSizeMultiplicator();

        float fit = Math.min(screenWidth * gui.getFillRatio() / sizeX, screenHeight * gui.getFillRatio() / sizeY);

        return Math.max(0.1F, Math.min(fit, gui.getMaxSizeMultiplicator()));
    }

    public void init(int screenWidth, int screenHeight, Consumer<ClickableWidget> adder) {
        this.sizeMultiplicator = computeSizeMultiplicator(screenWidth, screenHeight);
        this.guiX = (int) (screenWidth / sizeMultiplicator - gui.getSizeX()) / 2;
        this.guiY = (int) (screenHeight / sizeMultiplicator - gui.getSizeY()) / 2;

        for (GuiObject object : gui.getObjects()) {
            adder.accept(object.register(this.guiX, this.guiY));
        }
    }

    public void syncVisibility() {
        for (GuiObject object : gui.getObjects()) {
            if (object.getWidget() != null) object.getWidget().visible = object.isShown();
        }
    }

    public void renderOverlays(DrawContext context, int mouseX, int mouseY, float delta) {
        for (GuiObject object : gui.getObjects()) {
            if (object.getWidget() instanceof OverlayRenderer overlay && object.getWidget().visible) overlay.renderOverlay(context, scaled(mouseX), scaled(mouseY), delta);
        }
    }

    public boolean isOverlayCapturing() {
        for (GuiObject object : gui.getObjects()) {
            if (object.getWidget() instanceof OverlayRenderer overlay && overlay.isCapturing()) return true;
        }

        return false;
    }

    public boolean mouseClickedOverlay(double mouseX, double mouseY, int button) {
        for (GuiObject object : gui.getObjects()) {
            if (!(object.getWidget() instanceof OverlayRenderer overlay) || !overlay.isCapturing()) continue;

            if (overlay.mouseClickedOverlay(scaled(mouseX), scaled(mouseY), button)) return true;
        }

        return false;
    }

    public boolean mouseScrolledOverlay(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (GuiObject object : gui.getObjects()) {
            if (!(object.getWidget() instanceof OverlayRenderer overlay) || !overlay.isCapturing()) continue;

            if (overlay.mouseScrolledOverlay(scaled(mouseX), scaled(mouseY), horizontalAmount, verticalAmount)) return true;
        }

        return false;
    }

    public void closeOverlays() {
        for (GuiObject object : gui.getObjects()) {
            if (object.getWidget() instanceof OverlayRenderer overlay) overlay.closeOverlay();
        }
    }

    public void pushScale(DrawContext context) {
        context.getMatrices().push();
        context.getMatrices().scale(sizeMultiplicator, sizeMultiplicator, 1.0F);
    }

    public void pushUnscaled(DrawContext context) {
        float inverse = 1.0F / sizeMultiplicator;

        context.getMatrices().push();
        context.getMatrices().scale(inverse, inverse, 1.0F);
    }

    public void popScale(DrawContext context) {
        context.getMatrices().pop();
    }

    public void drawBackground(DrawContext context) {
        if (gui.hasBackground()) UtilsWidgets.drawTexture(context, gui.getBackground(), guiX, guiY, gui.getSizeX(), gui.getSizeY());
    }
}
