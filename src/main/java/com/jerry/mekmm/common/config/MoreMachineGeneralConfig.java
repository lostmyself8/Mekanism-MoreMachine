package com.jerry.mekmm.common.config;

import com.jerry.mekmm.common.util.ValidatorUtils;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedConfigValue;
import mekanism.common.config.value.CachedFloatingLongValue;
import mekanism.common.config.value.CachedIntValue;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoreMachineGeneralConfig extends BaseMekanismConfig {

    private final ForgeConfigSpec configSpec;

    public final CachedConfigValue<List<? extends String>> itemReplicatorRecipe;
    public final CachedConfigValue<List<? extends String>> fluidReplicatorRecipe;

    public final CachedConfigValue<List<? extends String>> dimensionGasMappings;
    public final CachedIntValue gasCollectAmount;
    public final CachedFloatingLongValue wirelessChargingStationChargingRate;

    MoreMachineGeneralConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("General Config. This config is synced from server to client.").push("general");

        builder.comment("Custom Replicator Recipes").push("replicator_recipes");
        itemReplicatorRecipe = CachedConfigValue.wrap(this, builder.comment("The recipes added here will be added to the item replicator. Write using modid:registeredName#amount, # followed by the amount(not null or zero) of UU matter consumed. For example:[\"minecraft:stone#10\",\"mekanism:basic_bin#100\"]")
                .defineListAllowEmpty(Collections.singletonList("itemReplicatorRecipe"), ArrayList::new, e -> e instanceof String list && ValidatorUtils.validateList(list)));
        fluidReplicatorRecipe = CachedConfigValue.wrap(this, builder.comment("The recipes added here will be added to the fluid replicator. Write using modid:registeredName#amount, # followed by the amount(not null or zero) of UU matter consumed. For example:[\"minecraft:water#10\",\"mekanism:heavy_water#100\"]")
                .defineListAllowEmpty("fluidReplicatorRecipe", ArrayList::new, e -> e instanceof String list && ValidatorUtils.validateList(list)));
        builder.pop();

        gasCollectAmount = CachedIntValue.wrap(this, builder.comment("mB of Unstable Dimensional Gas collected by the Ambient Gas Collector.")
                .defineInRange("gasCollectAmount", 1, 1, FluidType.BUCKET_VOLUME));

        dimensionGasMappings = CachedConfigValue.wrap(this, builder.comment(
                "Dimension → Gas mappings for the Ambient Gas Collector.",
                "Format: \"dimensionId|gasId|amount\" (one entry per dimension)",
                "When the collector is placed in a matching dimension,",
                "it produces the configured gas instead of unstable_dimensional_gas.",
                "Example: [\"ad_astra:moon_orbit|mekanismgenerators:deuterium|10\",",
                "          \"ad_astra:mars_orbit|mekanismgenerators:tritium|8\"]",
                "Unlisted dimensions fall back to unstable_dimensional_gas.").defineListAllowEmpty("dimensionGasMappings", ArrayList::new, e -> e instanceof String));

        wirelessChargingStationChargingRate = CachedFloatingLongValue.define(this, builder, "Amount of Energy(joules) an item can receive per tick from a Wireless Charging Station.",
                "wirelessChargingStationChargingRate", FloatingLong.createConst(100_000L));

        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "general";
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
