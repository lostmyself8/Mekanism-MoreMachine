package com.jerry.mekmm.api.datamaps;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.common.tile.machine.TileEntityReplicator;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ItemReplicatorRecipe(long UUAmount) {

    private static final long TANK_CAP = TileEntityReplicator.MAX_GAS;

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "item_replicator");

    private static final Codec<Long> UU_AMOUNT_CODEC = MoreMachineUtils.longRange(1L, TANK_CAP);

    // 如果第一种编码/解码方式失败，会自动尝试第二种方式
    public static final Codec<ItemReplicatorRecipe> CODEC = Codec.withAlternative(RecordCodecBuilder.create(in -> in.group(
            UU_AMOUNT_CODEC.fieldOf(MoreMachineSerializationConstants.UU_AMOUNT).forGetter(ItemReplicatorRecipe::UUAmount)).apply(in, ItemReplicatorRecipe::new)), UU_AMOUNT_CODEC.xmap(ItemReplicatorRecipe::new, ItemReplicatorRecipe::UUAmount));

    public ItemReplicatorRecipe {
        if (UUAmount < 1 || UUAmount > TANK_CAP) {
            throw new IllegalArgumentException("UU Matter amount must be between one and " + TANK_CAP + " inclusive");
        }
    }
}
