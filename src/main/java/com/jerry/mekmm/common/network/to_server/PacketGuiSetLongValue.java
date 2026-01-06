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
import java.util.function.ObjLongConsumer;

public record PacketGuiSetLongValue(GuiLongValue interaction, BlockPos pos, long value) implements IMekanismPacket {

    public static final CustomPacketPayload.Type<PacketGuiSetLongValue> TYPE = new CustomPacketPayload.Type<>(Mekmm.rl("set_long_value"));
    public static final StreamCodec<ByteBuf, PacketGuiSetLongValue> STREAM_CODEC = StreamCodec.composite(
            GuiLongValue.STREAM_CODEC, PacketGuiSetLongValue::interaction,
            BlockPos.STREAM_CODEC, PacketGuiSetLongValue::pos,
            ByteBufCodecs.VAR_LONG, PacketGuiSetLongValue::value,
            PacketGuiSetLongValue::new);

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

    public enum GuiLongValue {

        SET_ENERGY_RATE((tile, value) -> {
            if (tile instanceof TileEntityWirelessTransmissionStation transmissionStation) {
                transmissionStation.setEnergyRateFromPacket(value);
            }
        }),
        SET_CHEMICALS_RATE((tile, value) -> {
            if (tile instanceof TileEntityWirelessTransmissionStation transmissionStation) {
                transmissionStation.setChemicalsRateFromPacket(value);
            }
        });

        public static final IntFunction<GuiLongValue> BY_ID = ByIdMap.continuous(GuiLongValue::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, GuiLongValue> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GuiLongValue::ordinal);

        private final ObjLongConsumer<TileEntityMekanism> consumerForTile;

        GuiLongValue(ObjLongConsumer<TileEntityMekanism> consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, long value) {
            consumerForTile.accept(tile, value);
        }
    }
}
