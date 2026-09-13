package fr.hdi.gui.network;

import fr.hdi.gui.screen.CustomHandlerFactory;
import fr.hdi.gui.screen.CustomScreenHandler;
import fr.hdi.gui.utils.GuiData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class GuiNetwork {
    public static void open(ServerPlayerEntity player, Identifier gui) {
        open(player, gui, new GuiData());
    }

    public static void open(ServerPlayerEntity player, Identifier gui, GuiData data) {
        GuiSessions.opened(player, gui);

        ServerPlayNetworking.send(player, new OpenGuiPayload(new GuiOpenData(gui, data)));
    }

    public static void open(Iterable<ServerPlayerEntity> players, Identifier gui, GuiData data) {
        for (ServerPlayerEntity player : players) open(player, gui, data);
    }

    public static void close(ServerPlayerEntity player) {
        GuiSessions.closed(player);

        if (player.currentScreenHandler instanceof CustomScreenHandler) {
            player.closeHandledScreen();

            return;
        }

        ServerPlayNetworking.send(player, new CloseGuiPayload());
    }

    public static void openHandler(ServerPlayerEntity player, Identifier gui) {
        openHandler(player, gui, new GuiData(), null);
    }

    public static void openHandler(ServerPlayerEntity player, Identifier gui, GuiData data) {
        openHandler(player, gui, data, null);
    }

    public static void openHandler(ServerPlayerEntity player, Identifier gui, GuiData data, Inventory inventory) {
        GuiSessions.opened(player, gui);

        player.openHandledScreen(new CustomHandlerFactory(new GuiOpenData(gui, data), inventory));
    }
}
