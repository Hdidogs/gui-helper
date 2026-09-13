package fr.hdi.gui.network;

import fr.hdi.gui.GuiHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record CloseGuiPayload() implements CustomPayload {
    public static final Id<CloseGuiPayload> ID = new Id<>(GuiHelper.id("close_gui"));

    public static final PacketCodec<PacketByteBuf, CloseGuiPayload> CODEC = PacketCodec.unit(new CloseGuiPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
