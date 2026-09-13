package fr.hdi.gui.network;

import fr.hdi.gui.GuiHelper;
import fr.hdi.gui.utils.GuiData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GuiValuesPayload(Identifier gui, GuiData values) implements CustomPayload {
    public static final Id<GuiValuesPayload> ID = new Id<>(GuiHelper.id("gui_values"));

    public static final PacketCodec<PacketByteBuf, GuiValuesPayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, GuiValuesPayload::gui,
            GuiData.PACKET_CODEC, GuiValuesPayload::values,
            GuiValuesPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
