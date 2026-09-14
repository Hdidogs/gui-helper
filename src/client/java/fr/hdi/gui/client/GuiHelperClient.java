package fr.hdi.gui.client;

import fr.hdi.gui.GuiHelper;
import fr.hdi.gui.client.gui.objects.*;
import fr.hdi.gui.client.gui.Gui;
import fr.hdi.gui.client.gui.GuiPages;
import fr.hdi.gui.client.gui.GuiRegistry;
import fr.hdi.gui.utils.*;
import fr.hdi.gui.network.GuiValuesPayload;
import fr.hdi.gui.network.CloseGuiPayload;
import fr.hdi.gui.network.OpenGuiPayload;
import fr.hdi.gui.screen.CustomScreenHandler;
import fr.hdi.gui.screen.GuiHandlerRegistry;
import fr.hdi.gui.screen.objects.SlotObject;
import fr.hdi.gui.client.screen.CustomHandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class GuiHelperClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(OpenGuiPayload.ID,
                (payload, context) -> context.client().execute(() -> GuiRegistry.open(payload.open().gui(), payload.open().data())));

        ClientPlayNetworking.registerGlobalReceiver(CloseGuiPayload.ID,
                (payload, context) -> context.client().execute(GuiRegistry::close));

        HandledScreens.register(CustomScreenHandler.TYPE, CustomHandledScreen::new);

        if (GuiHelper.DEBUG) {
            GuiRegistry.register(GuiHelper.id("debug_handler"), debugHandlerGui());

            Texture background_debug = new Texture(GuiHelper.id("textures/gui/background_debug.png"), 0, 0, 176, 166, 256, 256);
            Texture close_button = new Texture(GuiHelper.id("textures/gui/background_debug.png"), 0, 166, 13, 13, 256, 256);
            Texture text_field = new Texture(GuiHelper.id("textures/gui/background_debug.png"), 0, 179, 88, 13, 256, 256);

            Gui gui = new Gui().setBackground(background_debug).activeBlur().setSizeMultiplicator(1.5f);

            ButtonObject button = new ButtonObject("close", close_button, new TextWithDetail(Text.literal("X")), 158, 5, 13, 13, () -> {
                GuiHelper.LOGGER.info("name = {} / sound = {}", gui.getObject("name", TextFieldObject.class).getValue(), gui.getObject("sound", ToggleObject.class).getValue());
                this.close();
            });
            TextFieldObject textField = new TextFieldObject("name", text_field, new TextWithDetail(Text.literal("test")), 5, 147, 88, 13, 25);
            TextAreaObject textArea = new TextAreaObject("notes", text_field, null, 110, 60, 60, 50, 256);

            textArea.setLabel(new TextWithDetail(Text.translatable("gui-helper.debug.notes"), 0.5f));
            TextObject textObject = new TextObject("title", new TextWithDetail(Text.literal("textField::getValue"), ColorHelper.RED, 0.5f), 45, 61);
            ItemRenderObject itemRender = new ItemRenderObject("redstone", Items.REDSTONE, 40, 61, 0.5f).setShowItemTooltip(true);
            ToggleObject toggleObject = new ToggleObject("sound", Textures.TEXTURE_TOGGLE_OFF, Textures.TEXTURE_TOGGLE_ON, 145, 27, 25, 13);

            BoxObject box = new BoxObject("list", null, 5, 85, 100, 50);

            for (int index = 0; index < 8; index++) {
                box.addObject(new TextObject("line_" + index, new TextWithDetail(Text.literal("Line " + index), ColorHelper.WHITE, 0.5f), 2, 2 + index * 10));
            }

            BoxObject innerBox = new BoxObject("inner_list", null, 40, 4, 50, 30);

            for (int index = 0; index < 8; index++) {
                innerBox.addObject(new TextObject("inner_line_" + index, new TextWithDetail(Text.literal("Inner " + index), ColorHelper.RED, 0.5f), 2, 2 + index * 10));
            }

            box.addObject(innerBox);

            box.addObject(new DropdownObject("boxed_mode", 2, 90, 60, 12)
                    .addOption("one", new TextWithDetail(Text.literal("One"), 0.5f))
                    .addOption("two", new TextWithDetail(Text.literal("Two"), 0.5f))
                    .addOption("three", new TextWithDetail(Text.literal("Three"), 0.5f)));

            textField.setLabel(new TextWithDetail(Text.literal("Name"), 0.5f));
            toggleObject.setLabel(new TextWithDetail(Text.literal("Sound"), 0.5f));
            toggleObject.setTooltip(Text.literal("Sound"), Text.literal("Shows the redstone item when on"));
            itemRender.setShowWhen(toggleObject::getValue);

            DropdownObject dropdown = new DropdownObject("difficulty", 5, 25, 88, 13)
                    .addOption("peaceful", new TextWithDetail(Text.literal("Peaceful"), 0.6f))
                    .addOption("easy", new TextWithDetail(Text.literal("Easy"), 0.6f))
                    .addOption("normal", new TextWithDetail(Text.literal("Normal"), 0.6f))
                    .addOption("hard", new TextWithDetail(Text.literal("Hard"), 0.6f))
                    .addOption("hardcore", new TextWithDetail(Text.literal("Hardcore"), 0.6f))
                    .addOption("custom", new TextWithDetail(Text.literal("Custom"), 0.6f))
                    .setMaxVisibleOptions(3);

            dropdown.setLabel(new TextWithDetail(Text.literal("Difficulty"), 0.5f));

            gui.addObject(dropdown).addObject(button).addObject(textField).addObject(textArea).addObject(textObject).addObject(itemRender).addObject(toggleObject).addObject(box);

            gui.setBuilder(built -> {
                BoxObject list = built.getObject("list", BoxObject.class);
                int lines = built.getData().getInt("lines", 3);

                for (int index = 0; index < lines; index++) {
                    built.addObject(list, new TextObject("built_" + index, new TextWithDetail(Text.translatable("gui-helper.debug.built_line", index), ColorHelper.WHITE, 0.5f), 2, 100 + index * 10));
                }
            });

            GuiRegistry.register(GuiHelper.id("debug"), gui);
            GuiRegistry.register(GuiHelper.id("cartel"), testCartel());
        }
	}

    public Gui testCartel() {
        Texture background = new Texture(GuiHelper.id("textures/gui/test.png"), 0, 0, 162, 106, 256, 256);
        Texture uneButton = new Texture(GuiHelper.id("textures/gui/test.png"), 0, 106, 48, 82, 256, 256);
        Texture quotidienButton = new Texture(GuiHelper.id("textures/gui/test.png"), 48, 106, 24, 34, 256, 256);

        Texture bandeau = new Texture(GuiHelper.id("textures/gui/bandeau.png"), 0, 0, 48, 8, 48, 8);

        Texture img_1 = new Texture(GuiHelper.id("textures/gui/armor.png"), 0, 0, 289, 649, 289, 649);
        Texture img_2 = new Texture(GuiHelper.id("textures/gui/snow2.png"), 0, 0, 276, 512, 276, 512);

        Gui gui = new Gui().setBackground(background).activeBlur();
        ButtonObject une1 = new ButtonObject("une1", uneButton, null, 6, 14, 48, 82, this::close).setHoverAnimation(false);
        ButtonObject une2 = new ButtonObject("une2", uneButton, null, 56, 14, 48, 82, this::close).setHoverAnimation(false);

        ButtonObject quotidien1 = new ButtonObject("quotidien1", quotidienButton, null, 106, 26, 24, 34, this::close).setHoverAnimation(false);
        ButtonObject quotidien2 = new ButtonObject("quotidien2", quotidienButton, null, 132, 26, 24, 34, this::close).setHoverAnimation(false);
        ButtonObject quotidien3 = new ButtonObject("quotidien3", quotidienButton, null, 106, 62, 24, 34, this::close).setHoverAnimation(false);
        ButtonObject quotidien4 = new ButtonObject("quotidien4", quotidienButton, null, 132, 62, 24, 34, this::close).setHoverAnimation(false);

        TextObject legal = new TextObject("legal", new TextWithDetail(Text.literal("Les objets sont cosmétiques uniquement"), ColorHelper.WHITE, 0.3f), 4, 97, 154, 5, Align.CENTER, VerticalAlign.MIDDLE);
        TextObject une = new TextObject("une", new TextWithDetail(Text.literal("À la une"), ColorHelper.WHITE, 0.7f), 35, 6, 40, 8, Align.CENTER, VerticalAlign.MIDDLE);
        TextObject daily = new TextObject("daily", new TextWithDetail(Text.literal("Object Quotidien"), ColorHelper.WHITE, 0.5f), 111, 18, 40, 8, Align.CENTER, VerticalAlign.MIDDLE);
        TextObject time = new TextObject("time", new TextWithDetail(Text.literal("Temps restant : 18H"), ColorHelper.WHITE, 0.4f), 108, 5, 38, 4, Align.LEFT, VerticalAlign.MIDDLE);
        TextObject balance = new TextObject("balance", new TextWithDetail(Text.literal("999"), ColorHelper.CARTEL, 0.4f), 147, 5, 10, 4, Align.RIGHT, VerticalAlign.MIDDLE);

        TextureObject img1 = new TextureObject("img1", img_1, 12, 17, 34, 76);
        TextureObject img2 = new TextureObject("img2", img_2, 61, 17, 39, 76);

        TextureObject price_bandeau = new TextureObject("price_bandeau", bandeau, 8, 83, 44, 8);
        TextObject price = new TextObject("price", new TextWithDetail(Text.literal("2000"), ColorHelper.CARTEL, 0.4f), 8, 83, 44, 8, Align.CENTER, VerticalAlign.MIDDLE);

        return gui.addObject(une1).addObject(une2).addObject(quotidien1).addObject(quotidien2).addObject(quotidien3).addObject(quotidien4).addObject(legal).addObject(img1).addObject(img2).addObject(une).addObject(daily).addObject(balance).addObject(time).addObject(price_bandeau).addObject(price).setSizeMultiplicator(2);
    }

    public Gui debugHandlerGui() {
        Texture background = new Texture(GuiHelper.id("textures/gui/background_debug.png"), 0, 0, 176, 166, 256, 256);
        Texture buttonTexture = new Texture(GuiHelper.id("textures/gui/background_debug.png"), 0, 179, 88, 13, 256, 256).setNineSlice(3);

        List<String> toggleIds = List.of("alpha", "beta", "gamma");

        Gui gui = new Gui().setBackground(background).setDarkBackground(true).setFitToWindow(true).setFillRatio(0.8f);

        for (SlotObject slot : GuiHandlerRegistry.get(GuiHelper.id("debug_handler")).getSlots()) {
            gui.addObject(new TextureObject(slot.getId(), Textures.TEXTURE_SLOT, slot.getObjectStartX() - 1, slot.getObjectStartY() - 1)
                    .setShowWhen(() -> GuiPages.isActive(slot.getGroup())));
        }

        gui.addObject(new ButtonObject("page_input", buttonTexture, new TextWithDetail(Text.translatable("gui-helper.debug.page.input"), 0.7f), 60, 4, 44, 13, () -> GuiPages.set("input")));
        gui.addObject(new ButtonObject("page_trade", buttonTexture, new TextWithDetail(Text.translatable("gui-helper.debug.page.trade"), 0.7f), 106, 4, 44, 13, () -> GuiPages.set("trade")));

        for (int index = 0; index < toggleIds.size(); index++) {
            gui.addObject(new ToggleObject(toggleIds.get(index), Textures.TEXTURE_TOGGLE_OFF, Textures.TEXTURE_TOGGLE_ON, 15, 20 + index * 20, 25, 13)
                    .setLabel(new TextWithDetail(Text.literal(toggleIds.get(index)), 0.5f))
                    .setTooltip(Text.literal("Toggle " + toggleIds.get(index)), Text.literal("Sent to the server on Enter")));
        }

        gui.addObject(new RadioButtonObject("mode", 60, 20)
                .addOption("easy", 0, 0, new TextWithDetail(Text.literal("Easy"), 0.5f))
                .addOption("hard", 0, 16, new TextWithDetail(Text.literal("Hard"), 0.5f))
                .setTooltip(Text.literal("Pick one mode")));

        gui.addObject(new ButtonObject("enter", buttonTexture, new TextWithDetail(Text.translatable("gui-helper.debug.enter"), 0.7f), 60, 60, 88, 13, () -> {
            ClientPlayNetworking.send(new GuiValuesPayload(GuiHelper.id("debug_handler"), gui.collectValues()));
            this.close();
        }));

        return gui;
    }

    public void close() {
        MinecraftClient.getInstance().setScreen(null);
    }
}