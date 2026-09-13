package fr.hdi.gui.network;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiSessions {
    private static final Map<UUID, Identifier> OPEN = new HashMap<>();

    public static void opened(ServerPlayerEntity player, Identifier gui) {
        OPEN.put(player.getUuid(), gui);
    }

    public static void closed(ServerPlayerEntity player) {
        OPEN.remove(player.getUuid());
    }

    public static Identifier getOpen(ServerPlayerEntity player) {
        return OPEN.get(player.getUuid());
    }

    public static boolean hasOpen(ServerPlayerEntity player, Identifier gui) {
        return gui.equals(OPEN.get(player.getUuid()));
    }
}
