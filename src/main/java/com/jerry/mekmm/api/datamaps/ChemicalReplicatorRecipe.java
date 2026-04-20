package com.jerry.mekmm.api.datamaps;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.common.tile.machine.TileEntityChemicalReplicator;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ChemicalReplicatorRecipe(long inputAmount, long UUAmount, long outputAmount) {

    private static final long TANK_CAP = TileEntityChemicalReplicator.MAX_GAS;

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "chemical_replicator");

    private static final Codec<Long> INPUT_AMOUNT_CODEC = MoreMachineUtils.longRange(1L, TANK_CAP);
    private static final Codec<Long> UU_AMOUNT_CODEC = MoreMachineUtils.longRange(1L, TANK_CAP);
    private static final Codec<Long> OUTPUT_AMOUNT_CODEC = MoreMachineUtils.longRange(1L, TANK_CAP);

    public static final Codec<ChemicalReplicatorRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
            INPUT_AMOUNT_CODEC.fieldOf(MoreMachineSerializationConstants.INPUT_AMOUNT).forGetter(ChemicalReplicatorRecipe::inputAmount),
            UU_AMOUNT_CODEC.fieldOf(MoreMachineSerializationConstants.UU_AMOUNT).forGetter(ChemicalReplicatorRecipe::UUAmount),
            OUTPUT_AMOUNT_CODEC.fieldOf(MoreMachineSerializationConstants.OUTPUT_AMOUNT).forGetter(ChemicalReplicatorRecipe::outputAmount)).apply(in, ChemicalReplicatorRecipe::new));

    public ChemicalReplicatorRecipe {
        if (inputAmount < 1 || inputAmount > TANK_CAP) {
            throw new IllegalArgumentException("Input amount must be between one and " + TANK_CAP + " inclusive");
        } else if (UUAmount < 1 || UUAmount > TANK_CAP) {
            throw new IllegalArgumentException("UU Matter amount must be between one and " + TANK_CAP + " inclusive");
        } else if (outputAmount < 1 || outputAmount > TANK_CAP) {
            throw new IllegalArgumentException("Output amount must be between one and " + TANK_CAP + " inclusive");
        }
    }
}
