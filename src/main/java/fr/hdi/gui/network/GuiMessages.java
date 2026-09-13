package fr.hdi.gui.network;

import fr.hdi.gui.GuiHelper;
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

        ServerPlayNetworking.registerGlobalReceiver(GuiValuesPayload.ID, (payload, context) ->
                receiveValues(context.player(), payload.gui(), payload.values()));

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
}
