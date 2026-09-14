package fr.hdi.gui.network;

import fr.hdi.gui.GuiHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GuiPagePayload(Identifier gui, String group) implements CustomPayload {
    public static final int MAX_GROUP_LENGTH = 64;

    public static final Id<GuiPagePayload> ID = new Id<>(GuiHelper.id("gui_page"));

    public static final PacketCodec<PacketByteBuf, GuiPagePayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, GuiPagePayload::gui,
            PacketCodecs.string(MAX_GROUP_LENGTH), GuiPagePayload::group,
            GuiPagePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
