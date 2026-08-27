package fr.hdi.gui.network;

import fr.hdi.gui.GuiHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenDebugScreenPayload() implements CustomPayload {
    public static final Id<OpenDebugScreenPayload> ID =
            new Id<>(Identifier.of(GuiHelper.MOD_ID, "open_screen"));

    public static final PacketCodec<PacketByteBuf, OpenDebugScreenPayload> CODEC = PacketCodec.unit(new OpenDebugScreenPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
