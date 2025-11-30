package com.jerry.mekmm.common.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedIntValue;
import mekanism.common.config.value.CachedLongValue;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.fluids.FluidType;

public class MoreMachineGeneratorsConfig extends BaseMekanismConfig {

    private final ModConfigSpec configSpec;

    public final CachedLongValue largeHeatGeneration;
    public final CachedLongValue largeHeatGenerationLava;
    public final CachedLongValue largeHeatGenerationNether;
    public final CachedIntValue largeHeatTankCapacity;
    public final CachedIntValue largeHeatGenerationFluidRate;

    public final CachedLongValue LGBGTankCapacity;

    MoreMachineGeneratorsConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        MoreMachineConfigTranslations.SERVER_GENERATOR_LARGE_HEAT.applyToBuilder(builder).push("large_heat_generator");
        largeHeatGeneration = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.SERVER_GENERATOR_LARGE_HEAT_GENERATION,
                "largeHeatGeneration", 1_000L);
        largeHeatGenerationLava = CachedLongValue.define(this, builder, MoreMachineConfigTranslations.SERVER_GENERATOR_LARGE_HEAT_GEN_LAVA,
                "largeHeatGenerationLava", 350L, 0, Long.MAX_VALUE / 81);
        largeHeatGenerationNether = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.SERVER_GENERATOR_LARGE_HEAT_GEN_NETHER,
                "largeHeatGenerationNether", 750L);
        largeHeatTankCapacity = CachedIntValue.wrap(this, MoreMachineConfigTranslations.SERVER_GENERATOR_LARGE_HEAT_TANK_CAPACITY.applyToBuilder(builder)
                .defineInRange("tankCapacity", 240 * FluidType.BUCKET_VOLUME, 1, Integer.MAX_VALUE));
        largeHeatGenerationFluidRate = CachedIntValue.wrap(this, MoreMachineConfigTranslations.SERVER_GENERATOR_LARGE_HEAT_FLUID_RATE.applyToBuilder(builder)
                .define("largeHeatGenerationFluidRate", 10, value -> value instanceof Integer i && i > 0 && i <= largeHeatTankCapacity.getOrDefault() / 100));
        builder.pop();

        MoreMachineConfigTranslations.SERVER_GENERATOR_LARGE_GAS.applyToBuilder(builder).push("gas_generator");
        LGBGTankCapacity = CachedLongValue.wrap(this, MoreMachineConfigTranslations.SERVER_GENERATOR_LARGE_GAS_TANK_CAPACITY.applyToBuilder(builder)
                .defineInRange("tankCapacity", 180L * FluidType.BUCKET_VOLUME, 1, Long.MAX_VALUE));
        builder.pop();

        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "generators";
    }

    @Override
    public String getTranslation() {
        return "Generators Config";
    }

    @Override
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public ModConfig.Type getConfigType() {
        return ModConfig.Type.SERVER;
    }
}
