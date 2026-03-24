package com.jerry.mekmm.common.config;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedFloatingLongValue;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;

public class MoreMachineStorageConfig extends BaseMekanismConfig {

    private final ForgeConfigSpec configSpec;

    public final CachedFloatingLongValue recycler;
    public final CachedFloatingLongValue plantingStation;
    public final CachedFloatingLongValue cnc_stamper;
    public final CachedFloatingLongValue cnc_lathe;
    public final CachedFloatingLongValue cnc_rollingMill;
    public final CachedFloatingLongValue itemReplicator;
    public final CachedFloatingLongValue fluidReplicator;
    public final CachedFloatingLongValue ambientGasCollector;
    public final CachedFloatingLongValue wirelessChargingStation;
    public final CachedFloatingLongValue largeRotaryCondensentrator;
    public final CachedFloatingLongValue largeChemicalInfuser;
    public final CachedFloatingLongValue largeElectrolyticSeparator;
    public final CachedFloatingLongValue largeAntiprotonicNucleosynthesizer;

    public final CachedFloatingLongValue largeHeatGenerator;
    public final CachedFloatingLongValue largeWindGenerator;

    MoreMachineStorageConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Machine Energy Storage Config. This config is synced from server to client.").push("storage");

        recycler = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "recycler", FloatingLong.createConst(20_000L));
        plantingStation = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "plantingStation", FloatingLong.createConst(80_000L));
        cnc_stamper = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "cnc_stamper", FloatingLong.createConst(20_000L));
        cnc_lathe = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "cnc_lathe", FloatingLong.createConst(20_000L));
        cnc_rollingMill = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "cnc_rollingMill", FloatingLong.createConst(20_000L));
        ambientGasCollector = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "ambientGasCollector", FloatingLong.createConst(40_000L));
        wirelessChargingStation = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "wirelessChargingStation", FloatingLong.createConst(10_000_000L));

        builder.comment("Settings for configuring Replicator Energy Storage").push("replicator");
        itemReplicator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "itemReplicator", FloatingLong.createConst(102_400_000L));
        fluidReplicator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "fluidReplicator", FloatingLong.createConst(102_400_000L));
        builder.pop();

        largeRotaryCondensentrator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).",
                "largeRotaryCondensentrator", FloatingLong.createConst(4_096_000L));
        largeChemicalInfuser = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).",
                "largeChemicalInfuser", FloatingLong.createConst(16_384_000L));
        largeElectrolyticSeparator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).",
                "largeElectrolyticSeparator", FloatingLong.createConst(32_768_000L));
        largeAntiprotonicNucleosynthesizer = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).",
                "largeAntiprotonicNucleosynthesizer", FloatingLong.createConst(64_000_000_000L));

        largeHeatGenerator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "largeHeatGenerator",
                FloatingLong.createConst(256_600_000L));
        largeWindGenerator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "largeWindGenerator",
                FloatingLong.createConst(256_600_000L));

        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "machine-storage";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public Type getConfigType() {
        return Type.SERVER;
    }
}
