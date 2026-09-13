package fr.hdi.gui.network;

import fr.hdi.gui.utils.GuiData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class GuiValuesRegistry {
    private static final Map<Identifier, BiConsumer<ServerPlayerEntity, GuiData>> LISTENERS = new HashMap<>();

    public static void register(Identifier gui, BiConsumer<ServerPlayerEntity, GuiData> listener) {
        if (LISTENERS.containsKey(gui)) throw new IllegalStateException("Gui values listener already registered : " + gui);

        LISTENERS.put(gui, listener);
    }

    public static BiConsumer<ServerPlayerEntity, GuiData> get(Identifier gui) {
        return LISTENERS.get(gui);
    }
}
