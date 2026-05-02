package com.jerry.mekmm.common.attachments.component;

import mekanism.common.lib.transmitter.TransmissionType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

public record ConnectionConfig(BlockPos pos, Direction direction, TransmissionType type) {

    public static final Codec<ConnectionConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(ConnectionConfig::pos),
            Direction.CODEC.fieldOf("direction").forGetter(ConnectionConfig::direction),
            TransmissionType.CODEC.fieldOf("type").forGetter(ConnectionConfig::type)).apply(instance, ConnectionConfig::new));
    public static final StreamCodec<ByteBuf, ConnectionConfig> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConnectionConfig::pos, Direction.STREAM_CODEC, ConnectionConfig::direction,
            TransmissionType.STREAM_CODEC, ConnectionConfig::type, ConnectionConfig::new);

    /**
     * 保存到NBT
     */
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("pos", pos.asLong());
        tag.putByte("direction", (byte) direction.ordinal());
        tag.putByte("type", (byte) type.ordinal());
        return tag;
    }

    /**
     * 从NBT加载
     */
    public static ConnectionConfig fromNBT(CompoundTag tag) {
        BlockPos pos = BlockPos.of(tag.getLong("pos").orElse(0L));
        Direction dir = Direction.values()[Byte.toUnsignedInt(tag.getByte("direction").orElse((byte) 0)) % Direction.values().length];
        TransmissionType type = TransmissionType.values()[Byte.toUnsignedInt(tag.getByte("type").orElse((byte) 0)) % TransmissionType.values().length];
        return new ConnectionConfig(pos, dir, type);
    }
}
