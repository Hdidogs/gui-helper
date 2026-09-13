package fr.hdi.gui.client.screen.widget;

import net.minecraft.client.gui.DrawContext;

public interface OverlayRenderer {
    boolean isCapturing();

    void renderOverlay(DrawContext context, int mouseX, int mouseY, float delta);

    boolean mouseClickedOverlay(double mouseX, double mouseY, int button);

    boolean mouseScrolledOverlay(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);

    void closeOverlay();
}
