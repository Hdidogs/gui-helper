package fr.hdi.gui.network;

import fr.hdi.gui.GuiHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record OpenGuiPayload(GuiOpenData open) implements CustomPayload {
    public static final Id<OpenGuiPayload> ID = new Id<>(GuiHelper.id("open_gui"));

    public static final PacketCodec<PacketByteBuf, OpenGuiPayload> CODEC =
            PacketCodec.tuple(GuiOpenData.PACKET_CODEC, OpenGuiPayload::open, OpenGuiPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
