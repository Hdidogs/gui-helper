package fr.hdi.gui.client;

import fr.hdi.gui.GuiHelper;
import fr.hdi.gui.client.gui.objects.ButtonObject;
import fr.hdi.gui.client.screen.CustomScreen;
import fr.hdi.gui.client.gui.Gui;
import fr.hdi.gui.utils.TextWithDetail;
import fr.hdi.gui.utils.Texture;
import fr.hdi.gui.network.OpenDebugScreenPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class GuiHelperClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        if (GuiHelper.DEBUG) {
            Texture background_debug = new Texture(GuiHelper.id("textures/gui/background_debug.png"), 0, 0, 176, 166, 256, 256);
            Texture close_button = new Texture(GuiHelper.id("textures/gui/background_debug.png"), 0, 166, 13, 13, 256, 256);

            ButtonObject button = new ButtonObject(close_button, new TextWithDetail(Text.literal("X")), 158, 5, 13, 13, this::close);


            Gui gui = new Gui().setBackground(background_debug).addObject(button).activeBlur();

            ClientPlayNetworking.registerGlobalReceiver(OpenDebugScreenPayload.ID,
                    (payload, context) -> context.client().execute(() -> context.client().setScreen(new CustomScreen(gui))));
        }
	}

    public void close() {
        MinecraftClient.getInstance().setScreen(null);
    }
}