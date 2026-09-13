package fr.hdi.gui.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.HashMap;
import java.util.Map;

public class GuiData {
    public static final int MAX_ENTRIES = 64;
    public static final int MAX_KEY_LENGTH = 64;
    public static final int MAX_VALUE_LENGTH = 256;

    public static final PacketCodec<ByteBuf, GuiData> PACKET_CODEC =
            PacketCodecs.<ByteBuf, String, String, Map<String, String>>map(HashMap::new,
                            PacketCodecs.string(MAX_KEY_LENGTH), PacketCodecs.string(MAX_VALUE_LENGTH), MAX_ENTRIES)
                    .xmap(GuiData::new, GuiData::getValues);

    private Map<String, String> values;

    public GuiData() {
        this(new HashMap<>());
    }

    public GuiData(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public boolean has(String id) {
        return values.containsKey(id);
    }

    public GuiData set(String id, String value) {
        values.put(id, value);

        return this;
    }

    public GuiData set(String id, int value) {
        return set(id, String.valueOf(value));
    }

    public GuiData set(String id, float value) {
        return set(id, String.valueOf(value));
    }

    public GuiData set(String id, boolean value) {
        return set(id, String.valueOf(value));
    }

    public String getString(String id, String fallback) {
        return values.getOrDefault(id, fallback);
    }

    public int getInt(String id, int fallback) {
        try {
            return has(id) ? Integer.parseInt(values.get(id)) : fallback;
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public float getFloat(String id, float fallback) {
        try {
            return has(id) ? Float.parseFloat(values.get(id)) : fallback;
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public boolean getBoolean(String id, boolean fallback) {
        return has(id) ? Boolean.parseBoolean(values.get(id)) : fallback;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
