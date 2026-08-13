package com.jerry.mekmm.common.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedIntValue;
import mekanism.common.config.value.CachedLongValue;

import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MoreMachineStartupConfig extends BaseMekanismConfig {

    private final ModConfigSpec configSpec;

    public final CachedLongValue wtsChemicalTankCapacity;
    public final CachedIntValue wtsFluidTankCapacity;

    MoreMachineStartupConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        MoreMachineConfigTranslations.STARTUP_WTS_TANK_CAPACITY.applyToBuilder(builder).push("wireless_transmission_station_tank_capacity");
        wtsChemicalTankCapacity = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.STARTUP_WTS_CHEMICAL_TANK_CAPACITY, "chemicalTankCapacity", 10_000L, 1L);
        wtsFluidTankCapacity = CachedIntValue.wrap(this, MoreMachineConfigTranslations.STARTUP_WTS_FLUID_TANK_CAPACITY.applyToBuilder(builder).defineInRange("fluidTankCapacity", 10_000, 1_000, Integer.MAX_VALUE));
        builder.pop();

        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "startup";
    }

    @Override
    public String getTranslation() {
        return "Startup Config";
    }

    @Override
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public Type getConfigType() {
        return Type.STARTUP;
    }
}
