package com.jerry.mekmm.common.network.to_server;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessTransmissionStation;

import mekanism.common.network.IMekanismPacket;
import mekanism.common.network.PacketUtils;
import mekanism.common.tile.base.TileEntityMekanism;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ByIdMap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;
import java.util.function.ObjDoubleConsumer;

public record PacketGuiSetDoubleValue(GuiDoubleValue interaction, BlockPos pos, double value) implements IMekanismPacket {

    public static final Type<PacketGuiSetDoubleValue> TYPE = new Type<>(Mekmm.rl("set_double_value"));
    public static final StreamCodec<ByteBuf, PacketGuiSetDoubleValue> STREAM_CODEC = StreamCodec.composite(
            GuiDoubleValue.STREAM_CODEC, PacketGuiSetDoubleValue::interaction,
            BlockPos.STREAM_CODEC, PacketGuiSetDoubleValue::pos,
            ByteBufCodecs.DOUBLE, PacketGuiSetDoubleValue::value,
            PacketGuiSetDoubleValue::new);

    @Override
    public void handle(IPayloadContext context) {
        if (PacketUtils.blockEntity(context, pos) instanceof TileEntityMekanism tile) {
            interaction.consume(tile, value);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum GuiDoubleValue {

        SET_HEAT_RATE((tile, extra) -> {
            if (tile instanceof TileEntityWirelessTransmissionStation transmissionStation) {
                transmissionStation.setHeatRateFromPacket(extra);
            }
        });

        public static final IntFunction<GuiDoubleValue> BY_ID = ByIdMap.continuous(GuiDoubleValue::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, GuiDoubleValue> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GuiDoubleValue::ordinal);

        private final ObjDoubleConsumer<TileEntityMekanism> consumerForTile;

        GuiDoubleValue(ObjDoubleConsumer<TileEntityMekanism> consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, double value) {
            consumerForTile.accept(tile, value);
        }
    }
}
