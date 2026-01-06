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
import java.util.function.ObjIntConsumer;

public record PacketGuiSetIntValue(GuiIntValue interaction, BlockPos pos, int value) implements IMekanismPacket {

    public static final Type<PacketGuiSetIntValue> TYPE = new Type<>(Mekmm.rl("set_int_value"));
    public static final StreamCodec<ByteBuf, PacketGuiSetIntValue> STREAM_CODEC = StreamCodec.composite(
            GuiIntValue.STREAM_CODEC, PacketGuiSetIntValue::interaction,
            BlockPos.STREAM_CODEC, PacketGuiSetIntValue::pos,
            ByteBufCodecs.VAR_INT, PacketGuiSetIntValue::value,
            PacketGuiSetIntValue::new);

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

    public enum GuiIntValue {

        SET_FLUIDS_RATE((tile, value) -> {
            if (tile instanceof TileEntityWirelessTransmissionStation transmissionStation) {
                transmissionStation.setFluidsRateFromPacket(value);
            }
        }),
        SET_ITEMS_RATE((tile, value) -> {
            if (tile instanceof TileEntityWirelessTransmissionStation transmissionStation) {
                transmissionStation.setItemsRateFromPacket(value);
            }
        });

        public static final IntFunction<GuiIntValue> BY_ID = ByIdMap.continuous(GuiIntValue::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, GuiIntValue> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GuiIntValue::ordinal);

        private final ObjIntConsumer<TileEntityMekanism> consumerForTile;

        GuiIntValue(ObjIntConsumer<TileEntityMekanism> consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, int value) {
            consumerForTile.accept(tile, value);
        }
    }
}
