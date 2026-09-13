package fr.hdi.gui;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.hdi.gui.network.GuiMessages;
import fr.hdi.gui.network.GuiNetwork;
import fr.hdi.gui.network.GuiValuesRegistry;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.screen.CustomScreenHandler;
import fr.hdi.gui.screen.GuiHandler;
import fr.hdi.gui.screen.GuiHandlerRegistry;
import net.minecraft.text.Text;
import fr.hdi.gui.screen.CustomScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiHelper implements ModInitializer {
	public static final String MOD_ID = "gui-helper";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

	@Override
	public void onInitialize() {
		LOGGER.info("Gui Helper Framework Initialize !");

        GuiMessages.registerGlobalReceivers();
        CustomScreenHandler.register();

        if (DEBUG) {
            initDebugCommand();
            registerTestGui();
        }
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

    public void registerTestGui() {
        GuiHandlerRegistry.register(id("debug_handler"), new GuiHandler()
                .setTitle(Text.literal("Debug Handler"))
                .addSlotGrid("input", 0, 111, 20, 3, 1)
                .setPlayerInventory(8, 83));

        GuiValuesRegistry.register(id("debug_handler"), (player, values) -> {
            player.sendMessage(Text.literal("Values from " + id("debug_handler")), false);

            values.getValues().forEach((key, value) -> player.sendMessage(Text.literal(" - " + key + " = " + value), false));
        });
    }

    public void initDebugCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("testGui")
                    .executes(context -> openScreen(context.getSource().getPlayerOrThrow(), "debug"))
                    .then(CommandManager.argument("gui", StringArgumentType.word())
                            .executes(context -> openScreen(context.getSource().getPlayerOrThrow(), StringArgumentType.getString(context, "gui")))));

            dispatcher.register(CommandManager.literal("testHandler")
                    .executes(context -> openHandler(context.getSource().getPlayerOrThrow(), "debug_handler"))
                    .then(CommandManager.argument("handler", StringArgumentType.word())
                            .suggests((context, builder) -> CommandSource.suggestMatching(GuiHandlerRegistry.getIds().stream().map(Identifier::getPath), builder))
                            .executes(context -> openHandler(context.getSource().getPlayerOrThrow(), StringArgumentType.getString(context, "handler")))));
        });
    }

    private int openScreen(ServerPlayerEntity player, String path) {
        GuiNetwork.open(player, id(path));

        return 1;
    }

    private int openHandler(ServerPlayerEntity player, String path) {
        Identifier gui = id(path);

        if (!GuiHandlerRegistry.isRegistered(gui)) {
            player.sendMessage(Text.literal("No gui handler registered for " + gui), false);

            return 0;
        }

        GuiNetwork.openHandler(player, gui, new GuiData().set("alpha", true).set("gamma", true));

        return 1;
    }
}
