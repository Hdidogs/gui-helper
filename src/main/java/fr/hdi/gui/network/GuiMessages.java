package fr.hdi.gui.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class GuiMessages {
    public static void registerGlobalReceivers() {
        PayloadTypeRegistry.playS2C().register(OpenDebugScreenPayload.ID, OpenDebugScreenPayload.CODEC);
    }
}
