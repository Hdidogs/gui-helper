package fr.hdi.gui.network;

import fr.hdi.gui.GuiHelper;
import fr.hdi.gui.screen.CustomScreenHandler;
import fr.hdi.gui.utils.GuiData;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class GuiMessages {
    public static void registerGlobalReceivers() {
        PayloadTypeRegistry.playS2C().register(OpenGuiPayload.ID, OpenGuiPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CloseGuiPayload.ID, CloseGuiPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(GuiValuesPayload.ID, GuiValuesPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(GuiPagePayload.ID, GuiPagePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(GuiValuesPayload.ID, (payload, context) ->
                receiveValues(context.player(), payload.gui(), payload.values()));

        ServerPlayNetworking.registerGlobalReceiver(GuiPagePayload.ID, (payload, context) ->
                receivePage(context.player(), payload.gui(), payload.group()));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> GuiSessions.closed(handler.player));
    }

    private static void receiveValues(ServerPlayerEntity player, Identifier gui, GuiData values) {
        if (!GuiSessions.hasOpen(player, gui)) {
            GuiHelper.LOGGER.warn("{} sent values for {} but has {} open", player.getName().getString(), gui, GuiSessions.getOpen(player));

            return;
        }

        BiConsumer<ServerPlayerEntity, GuiData> listener = GuiValuesRegistry.get(gui);

        if (listener == null) {
            GuiHelper.LOGGER.warn("{} sent values for {} but no listener is registered", player.getName().getString(), gui);

            return;
        }

        player.server.execute(() -> listener.accept(player, values));
    }

    private static void receivePage(ServerPlayerEntity player, Identifier gui, String group) {
        if (!GuiSessions.hasOpen(player, gui)) {
            GuiHelper.LOGGER.warn("{} sent a page for {} but has {} open", player.getName().getString(), gui, GuiSessions.getOpen(player));

            return;
        }

        player.server.execute(() -> {
            if (!(player.currentScreenHandler instanceof CustomScreenHandler handler) || !gui.equals(handler.getGuiHandler().getId())) {
                GuiHelper.LOGGER.warn("{} sent a page for {} but no matching handler is open", player.getName().getString(), gui);

                return;
            }

            if (!group.isEmpty() && !handler.getGuiHandler().hasGroup(group)) {
                GuiHelper.LOGGER.warn("{} sent an unknown page {} for {}", player.getName().getString(), group, gui);

                return;
            }

            handler.setActiveGroup(group);
        });
    }
}
