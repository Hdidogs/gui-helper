package fr.hdi.gui.client;

import fr.hdi.gui.GuiHelper;
import fr.hdi.gui.client.screen.CustomScreen;
import fr.hdi.gui.gui.Gui;
import fr.hdi.gui.gui.utils.Texture;
import fr.hdi.gui.network.OpenDebugScreenPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class GuiHelperClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        if (GuiHelper.DEBUG) {
            Texture background_debug = new Texture(GuiHelper.id("textures/gui/background_debug.png"), 0, 0, 176, 166, 256, 256);

            Gui gui = new Gui().setBackground(background_debug);

            ClientPlayNetworking.registerGlobalReceiver(OpenDebugScreenPayload.ID,
                    (payload, context) -> context.client().execute(() -> context.client().setScreen(new CustomScreen(gui))));
        }
	}
}