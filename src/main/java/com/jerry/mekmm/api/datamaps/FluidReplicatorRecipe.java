package com.jerry.mekmm.api.datamaps;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.common.tile.machine.TileEntityFluidReplicator;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import net.minecraft.resources.Identifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FluidReplicatorRecipe(int inputAmount, long UUAmount, int outputAmount) {

    private static final int FLUID_TANK_CAP = TileEntityFluidReplicator.MAX_FLUID;
    private static final long CHEMICAL_TANK_CAP = TileEntityFluidReplicator.MAX_GAS;

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, "fluid_replicator");

    private static final Codec<Integer> INPUT_AMOUNT_CODEC = Codec.intRange(1, FLUID_TANK_CAP / 2);
    private static final Codec<Long> UU_AMOUNT_CODEC = MoreMachineUtils.longRange(1L, CHEMICAL_TANK_CAP);
    private static final Codec<Integer> OUTPUT_AMOUNT_CODEC = Codec.intRange(1, FLUID_TANK_CAP);

    public static final Codec<FluidReplicatorRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
            INPUT_AMOUNT_CODEC.fieldOf(MoreMachineSerializationConstants.INPUT_AMOUNT).forGetter(FluidReplicatorRecipe::inputAmount),
            UU_AMOUNT_CODEC.fieldOf(MoreMachineSerializationConstants.UU_AMOUNT).forGetter(FluidReplicatorRecipe::UUAmount),
            OUTPUT_AMOUNT_CODEC.fieldOf(MoreMachineSerializationConstants.OUTPUT_AMOUNT).forGetter(FluidReplicatorRecipe::outputAmount)).apply(in, FluidReplicatorRecipe::new));

    public FluidReplicatorRecipe {
        if (inputAmount < 1 || inputAmount > FLUID_TANK_CAP / 2) {
            throw new IllegalArgumentException("Input amount must be between one and " + FLUID_TANK_CAP / 2 + " inclusive");
        } else if (UUAmount < 1 || UUAmount > CHEMICAL_TANK_CAP) {
            throw new IllegalArgumentException("UU Matter amount must be between one and " + CHEMICAL_TANK_CAP + " inclusive");
        } else if (outputAmount < 1 || outputAmount > FLUID_TANK_CAP) {
            throw new IllegalArgumentException("Output amount must be between one and " + FLUID_TANK_CAP + " inclusive");
        }
    }
}
