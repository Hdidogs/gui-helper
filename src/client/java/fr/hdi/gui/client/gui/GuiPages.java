package fr.hdi.gui.client.gui;

import fr.hdi.gui.client.screen.CustomHandledScreen;
import fr.hdi.gui.network.GuiPagePayload;
import fr.hdi.gui.screen.CustomScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class GuiPages {
    public static CustomScreenHandler getHandler() {
        if (MinecraftClient.getInstance().currentScreen instanceof CustomHandledScreen screen) return screen.getScreenHandler();

        return null;
    }

    public static String get() {
        CustomScreenHandler handler = getHandler();

        return handler == null ? null : handler.getActiveGroup();
    }

    public static boolean isActive(String group) {
        return group == null || group.equals(get());
    }

    public static void set(String group) {
        CustomScreenHandler handler = getHandler();

        if (handler == null || handler.getGuiHandler().getId() == null) return;
        if (group != null && !handler.getGuiHandler().hasGroup(group)) throw new IllegalArgumentException("Unknown slot group : " + group);

        handler.setActiveGroup(group);

        ClientPlayNetworking.send(new GuiPagePayload(handler.getGuiHandler().getId(), group == null ? "" : group));
    }
}
