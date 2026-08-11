package com.jerry.mekmm.api.datamaps;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.MoreMachineSerializationConstants;

import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SolarHeatFluid(int consumption, double generationModifier) {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "solar_heat_fluid");

    private static final Codec<Integer> CONSUMPTION_CODEC = Codec.intRange(1, Integer.MAX_VALUE);
    private static final Codec<Double> GENERATION_MODIFIER_CODEC = Codec.doubleRange(Double.MIN_VALUE, Double.MAX_VALUE);

    public static final Codec<SolarHeatFluid> CODEC = RecordCodecBuilder.create(in -> in.group(
            CONSUMPTION_CODEC.fieldOf(MoreMachineSerializationConstants.CONSUMPTION).forGetter(SolarHeatFluid::consumption),
            GENERATION_MODIFIER_CODEC.fieldOf(MoreMachineSerializationConstants.GENERATION_MODIFIER).forGetter(SolarHeatFluid::generationModifier)).apply(in, SolarHeatFluid::new));

    public SolarHeatFluid {
        if (consumption <= 0) {
            throw new IllegalArgumentException("Solar heat fluid consumption must be positive");
        } else if (!Double.isFinite(generationModifier) || generationModifier <= 0) {
            throw new IllegalArgumentException("Solar heat fluid generation modifier must be finite and positive");
        }
    }
}
