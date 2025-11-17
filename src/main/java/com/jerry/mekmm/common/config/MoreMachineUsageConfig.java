package com.jerry.mekmm.common.config;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedFloatingLongValue;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class MoreMachineUsageConfig extends BaseMekanismConfig {

    private final ForgeConfigSpec configSpec;

    public final CachedFloatingLongValue recycler;
    public final CachedFloatingLongValue plantingStation;
    public final CachedFloatingLongValue cnc_stamper;
    public final CachedFloatingLongValue cnc_lathe;
    public final CachedFloatingLongValue cnc_rollingMill;
    public final CachedFloatingLongValue itemReplicator;
    public final CachedFloatingLongValue fluidReplicator;
    public final CachedFloatingLongValue ambientGasCollector;
    // public final CachedFloatingLongValue largeRotaryCondensentrator;

    MoreMachineUsageConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Machine Energy Usage Config. This config is synced from server to client.").push("usage");

        //
        recycler = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "recycler", FloatingLong.createConst(50L));
        plantingStation = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "plantingStation", FloatingLong.createConst(200L));
        cnc_stamper = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "cnc_stamper", FloatingLong.createConst(50L));
        cnc_lathe = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "cnc_lathe", FloatingLong.createConst(50L));
        cnc_rollingMill = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "cnc_rollingMill", FloatingLong.createConst(50L));
        ambientGasCollector = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "ambientGasCollector", FloatingLong.createConst(100L));

        builder.comment("Settings for configuring Replicator Energy Usage").push("replicator");
        itemReplicator = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "itemReplicator", FloatingLong.createConst(102_400L));
        fluidReplicator = CachedFloatingLongValue.define(this, builder, "Energy per operation tick (Joules).", "fluidReplicator", FloatingLong.createConst(102_400L));
        builder.pop();

        // Large Machine
        // largeRotaryCondensentrator = CachedFloatingLongValue.define(this, builder, "Energy per operation tick
        // (Joules).", "largeRotaryCondensentrator", FloatingLong.createConst(500_000L));
        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "machine-usage";
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
