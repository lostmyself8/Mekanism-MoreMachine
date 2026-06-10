package com.jerry.mekmm.api.datamaps;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.MoreMachineSerializationConstants;

import net.minecraft.resources.Identifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SolarHeatFluid(double efficiency, double usage) {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, "solar_heat_fluid");

    private static final Codec<Double> EFFICIENCY_CODEC = Codec.doubleRange(0, 3);
    private static final Codec<Double> USAGE_CODEC = Codec.doubleRange(0, 1);

    public static final Codec<SolarHeatFluid> CODEC = RecordCodecBuilder.create(in -> in.group(
            EFFICIENCY_CODEC.fieldOf(MoreMachineSerializationConstants.EFFICIENCY).forGetter(SolarHeatFluid::efficiency),
            USAGE_CODEC.fieldOf(MoreMachineSerializationConstants.USAGE).forGetter(SolarHeatFluid::usage)).apply(in, SolarHeatFluid::new));

    public SolarHeatFluid {
        if (efficiency < 0 || efficiency > 3) {
            throw new IllegalArgumentException("Solar heat fluid efficiency must be between zero and three inclusive");
        } else if (usage < 0 || usage > 1) {
            throw new IllegalArgumentException("Solar heat fluid usage must be between zero and one inclusive");
        }
    }
}
