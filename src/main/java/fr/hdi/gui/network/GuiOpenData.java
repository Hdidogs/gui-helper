package fr.hdi.gui.network;

import fr.hdi.gui.utils.GuiData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record GuiOpenData(Identifier gui, GuiData data) {
    public static final PacketCodec<ByteBuf, GuiOpenData> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, GuiOpenData::gui,
            GuiData.PACKET_CODEC, GuiOpenData::data,
            GuiOpenData::new);

    public GuiOpenData(Identifier gui) {
        this(gui, new GuiData());
    }
}
