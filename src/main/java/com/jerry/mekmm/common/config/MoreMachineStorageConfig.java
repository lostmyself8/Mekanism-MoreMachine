package com.jerry.mekmm.common.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedLongValue;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MoreMachineStorageConfig extends BaseMekanismConfig {

    private final ModConfigSpec configSpec;

    public final CachedLongValue recycler;
    public final CachedLongValue plantingStation;
    public final CachedLongValue cnc_stamper;
    public final CachedLongValue cnc_lathe;
    public final CachedLongValue cnc_rollingMill;
    public final CachedLongValue itemReplicator;
    public final CachedLongValue fluidReplicator;
    public final CachedLongValue chemicalReplicator;
    public final CachedLongValue ambientGasCollector;
    public final CachedLongValue wirelessChargingStation;
    public final CachedLongValue wirelessTransmitterStation;

    public final CachedLongValue largeRotaryCondensentrator;
    public final CachedLongValue largeChemicalInfuser;
    public final CachedLongValue largeElectrolyticSeparator;
    public final CachedLongValue largeHeatGenerator;

    MoreMachineStorageConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // Normal Machine
        recycler = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_RECYCLER, "recycler", 20_000L, 1);
        plantingStation = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_PLANTING_STATION, "plantingStation", 80_000L, 1);
        cnc_stamper = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_CNC_STAMPER, "cnc_stamper", 20_000L, 1);
        cnc_lathe = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_CNC_LATHE, "cnc_lathe", 20_000L, 1);
        cnc_rollingMill = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_ROLLING_MILL, "cnc_rollingMill", 20_000L, 1);
        ambientGasCollector = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_AMBIENT_GAS_COLLECTOR, "ambientGasCollector", 40_000L, 1);
        wirelessChargingStation = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_WIRELESS_CHARGING_STATION, "wirelessChargingStation", 40_000_000L, 1);
        wirelessTransmitterStation = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_WIRELESS_TRANSMITTER_STATION, "wirelessTransmitterStation", 400_000_000L, 1);

        MoreMachineConfigTranslations.ENERGY_STORAGE_REPLICATOR.applyToBuilder(builder).push("replicator");
        itemReplicator = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_ITEM_REPLICATOR, "itemReplicator", 102_400_000L, 1);
        fluidReplicator = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_FLUID_REPLICATOR, "fluidReplicator", 102_400_000L, 1);
        chemicalReplicator = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_CHEMICAL_REPLICATOR, "chemicalReplicator", 102_400_000L, 1);
        builder.pop();

        // Large Machine
        largeRotaryCondensentrator = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_LARGE_ROTARY_CONDENSENTRATOR, "largeRotaryCondensentrator", 4_096_000L, 1);
        largeChemicalInfuser = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_LARGE_CHEMICAL_INFUSER, "largeChemicalInfuser", 4_096_000L, 1);
        largeElectrolyticSeparator = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_LARGE_ELECTROLYTIC_SEPARATOR, "largeElectrolyticSeparator", 4_096_000L, 1);
        largeHeatGenerator = CachedLongValue.definedMin(this, builder, MoreMachineConfigTranslations.ENERGY_STORAGE_LARGE_HEAT_GENERATOR, "largeHeatGenerator", 256_600_000L, 1);

        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "machine-storage";
    }

    @Override
    public String getTranslation() {
        return "Storage Config";
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
