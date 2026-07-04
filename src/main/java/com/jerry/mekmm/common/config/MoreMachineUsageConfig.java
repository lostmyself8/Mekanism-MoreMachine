package com.jerry.mekmm.common.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedLongValue;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MoreMachineUsageConfig extends BaseMekanismConfig {

    private final ModConfigSpec configSpec;

    public final CachedLongValue recycler;
    public final CachedLongValue plantingStation;
    public final CachedLongValue cnc_stamper;
    public final CachedLongValue presser;
    public final CachedLongValue cnc_lathe;
    public final CachedLongValue cnc_rollingMill;
    public final CachedLongValue itemReplicator;
    public final CachedLongValue fluidReplicator;
    public final CachedLongValue chemicalReplicator;
    public final CachedLongValue ambientGasCollector;
    public final CachedLongValue largeRotaryCondensentrator;
    public final CachedLongValue largeChemicalInfuser;
    public final CachedLongValue largeAntiprotonicNucleosynthesizer;
    public final CachedLongValue largePigmentMixer;

    MoreMachineUsageConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        recycler = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_RECYCLER, "recycler", 50L);
        plantingStation = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_PLANTING_STATION, "plantingStation", 50L);
        cnc_stamper = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_CNC_STAMPER, "cnc_stamper", 50L);
        presser = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_PRESSER, "presser", 50L);
        cnc_lathe = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_CNC_LATHE, "cnc_lathe", 50L);
        cnc_rollingMill = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_ROLLING_MILL, "cnc_rollingMill", 50L);
        ambientGasCollector = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_AMBIENT_GAS_COLLECTOR, "ambientGasCollector", 100L);

        MoreMachineConfigTranslations.ENERGY_USAGE_REPLICATOR.applyToBuilder(builder).push("replicator");
        itemReplicator = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_ITEM_REPLICATOR, "itemReplicator", 102_400L);
        fluidReplicator = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_FLUID_REPLICATOR, "fluidReplicator", 102_400L);
        chemicalReplicator = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_CHEMICAL_REPLICATOR, "chemicalReplicator", 102_400L);
        builder.pop();

        largeRotaryCondensentrator = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_LARGE_ROTARY_CONDENSENTRATOR, "largeRotaryCondensentrator", 50L);
        largeChemicalInfuser = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_LARGE_CHEMICAL_INFUSER, "largeChemicalInfuser", 100L);
        largeAntiprotonicNucleosynthesizer = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER, "largeAntiprotonicNucleosynthesizer", 100_000L);
        largePigmentMixer = CachedLongValue.definePositive(this, builder, MoreMachineConfigTranslations.ENERGY_USAGE_LARGE_PIGMENT_MIXER, "largePigmentMixer", 100L);

        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "machine-usage";
    }

    @Override
    public String getTranslation() {
        return "Usage Config";
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
