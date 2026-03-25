package com.jerry.mekmm.common.config;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedFloatingLongValue;
import mekanism.common.config.value.CachedIntValue;
import mekanism.common.config.value.CachedLongValue;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.config.ModConfig;

public class MoreMachineGeneratorsConfig extends BaseMekanismConfig {

    private static final String LARGE_HEAT_CATEGORY = "large_heat_generator";
    private static final String LARGE_GAS_CATEGORY = "large_gas_generator";
    private static final String LARGE_WIND_CATEGORY = "large_wind_generator";

    private final ForgeConfigSpec configSpec;

    public final CachedFloatingLongValue largeHeatGeneration;
    public final CachedFloatingLongValue largeHeatGenerationLava;
    public final CachedFloatingLongValue largeHeatGenerationNether;
    public final CachedIntValue largeHeatTankCapacity;
    public final CachedIntValue largeHeatGenerationFluidRate;

    public final CachedLongValue lGBGTankCapacity;

    public final CachedFloatingLongValue largeWindGenerationMin;
    public final CachedFloatingLongValue largeWindGenerationMax;

    MoreMachineGeneratorsConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Mekanism Generators Config. This config is synced between server and client.").push("generators");

        builder.comment("Large Heat Generator Settings").push(LARGE_HEAT_CATEGORY);
        largeHeatGeneration = CachedFloatingLongValue.define(this, builder, "Amount of energy in Joules the Large Heat Generator produces per tick. largeHeatGeneration + largeHeatGenerationLava * lavaSides + largeHeatGenerationNether. Note: lavaSides is how many sides are adjacent to lava, this includes the block itself if it is lava logged allowing for a max of 81 \"sides\".",
                "largeHeatGeneration", FloatingLong.createConst(1_000L));
        largeHeatGenerationLava = CachedFloatingLongValue.define(this, builder, "Multiplier of effectiveness of Lava that is adjacent to the Large Heat Generator.",
                "largeHeatGenerationLava", FloatingLong.createConst(350L));
        largeHeatGenerationNether = CachedFloatingLongValue.define(this, builder, "Add this amount of Joules to the energy produced by a large heat generator if it is in an 'ultrawarm' dimension, in vanilla this is just the Nether.",
                "largeHeatGenerationNether", FloatingLong.createConst(750L));
        largeHeatTankCapacity = CachedIntValue.wrap(this, builder.comment("The capacity in mB of the fluid tank in the Large Heat Generator.")
                .defineInRange("tankCapacity", 240 * FluidType.BUCKET_VOLUME, 1, Integer.MAX_VALUE));
        largeHeatGenerationFluidRate = CachedIntValue.wrap(this, builder.comment("The amount of lava in mB that gets consumed to transfer largeHeatGeneration Joules to the Large Heat Generator.")
                .define("largeHeatGenerationFluidRate", 10, value -> value instanceof Integer i && i > 0 && i <= largeHeatTankCapacity.get()));
        builder.pop();

        builder.comment("Large Gas-Burning Generator Settings").push(LARGE_GAS_CATEGORY);
        lGBGTankCapacity = CachedLongValue.wrap(this, builder.comment("The capacity in mB of the gas tank in the Large Gas-Burning Generator.")
                .defineInRange("tankCapacity", 180L * FluidType.BUCKET_VOLUME, 1, Long.MAX_VALUE));
        builder.pop();

        builder.comment("Wind Generator Settings").push(LARGE_WIND_CATEGORY);
        largeWindGenerationMin = CachedFloatingLongValue.define(this, builder, "Minimum base generation value of the Large Wind Generator.",
                "largeWindGenerationMin", FloatingLong.createConst(2_250_000L));
        largeWindGenerationMax = CachedFloatingLongValue.define(this, builder, "Maximum base generation value of the Large Wind Generator.",
                "largeWindGenerationMax", FloatingLong.createConst(3_750_000L));
        builder.pop();

        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "generators";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public ModConfig.Type getConfigType() {
        return ModConfig.Type.SERVER;
    }
}
