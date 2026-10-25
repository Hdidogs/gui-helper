package fr.hdi.gui.client.screen.widget;

public interface ViewportAware {
    void setViewport(double offsetY, int clipStartX, int clipStartY, int clipEndX, int clipEndY);

    boolean isOverSlot(double mouseX, double mouseY);
}
