package fr.hdi.gui.screen;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GuiHandlerRegistry {
    private static final Map<Identifier, GuiHandler> HANDLERS = new HashMap<>();

    public static GuiHandler register(Identifier id, GuiHandler handler) {
        if (HANDLERS.containsKey(id)) throw new IllegalStateException("Gui handler already registered : " + id);

        handler.setId(id);
        HANDLERS.put(id, handler);

        return handler;
    }

    public static Set<Identifier> getIds() {
        return HANDLERS.keySet();
    }

    public static GuiHandler get(Identifier id) {
        return HANDLERS.get(id);
    }

    public static boolean isRegistered(Identifier id) {
        return HANDLERS.containsKey(id);
    }
}
