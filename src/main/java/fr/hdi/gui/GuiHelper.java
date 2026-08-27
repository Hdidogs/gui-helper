package fr.hdi.gui;

import fr.hdi.gui.network.GuiMessages;
import fr.hdi.gui.network.OpenDebugScreenPayload;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiHelper implements ModInitializer {
	public static final String MOD_ID = "gui-helper";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean DEBUG = true;

	@Override
	public void onInitialize() {
		LOGGER.info("Gui Helper Framework Initialize !");

        GuiMessages.registerGlobalReceivers();

        if (DEBUG) {
            initDebugCommand();
            registerTestGui();
        }
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

    public void registerTestGui() {

    }

    public void initDebugCommand() {
        //CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
        //    dispatcher.register(CommandManager.literal("testhandlerGui").executes(context -> {
        //        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        //        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
        //                (syncId, inv, p) -> new LightsaberForgeScreenHandler(syncId, inv),
        //                Text.literal("Lightsaber Forge")
        //        ));

        //        return 1;
        //    }));
        //});

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("testGui").executes(context -> {
                ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                ServerPlayNetworking.send(player, new OpenDebugScreenPayload());

                return 1;
            }));
        });
    }
}
