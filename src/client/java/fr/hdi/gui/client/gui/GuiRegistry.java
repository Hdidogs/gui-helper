package fr.hdi.gui.client.gui;

import fr.hdi.gui.GuiHelper;
import fr.hdi.gui.client.screen.CustomScreen;
import fr.hdi.gui.utils.GuiData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class GuiRegistry {
    private static final Map<Identifier, Gui> GUIS = new HashMap<>();

    public static Gui register(Identifier id, Gui gui) {
        if (GUIS.containsKey(id)) throw new IllegalStateException("Gui already registered : " + id);

        gui.validateIds();
        gui.setId(id);
        GUIS.put(id, gui);

        return gui;
    }

    public static Gui get(Identifier id) {
        return GUIS.get(id);
    }

    public static boolean isRegistered(Identifier id) {
        return GUIS.containsKey(id);
    }

    public static void close() {
        MinecraftClient.getInstance().setScreen(null);
    }

    public static void open(Identifier id) {
        open(id, new GuiData());
    }

    public static void open(Identifier id, GuiData data) {
        Gui gui = get(id);

        if (gui == null) {
            GuiHelper.LOGGER.warn("No gui registered for {}", id);

            return;
        }

        gui.setData(data);

        MinecraftClient.getInstance().setScreen(new CustomScreen(gui));
    }
}
