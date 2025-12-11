package com.jerry.mekmm.common.config;

import com.jerry.mekmm.Mekmm;

import mekanism.common.config.IConfigTranslation;
import mekanism.common.config.TranslationPreset;

import net.minecraft.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MoreMachineConfigTranslations implements IConfigTranslation {

    GENERAL_ENERGY_CONVERSION("general.energy_conversion", "Energy Conversion Rate", "Settings for configuring Energy Conversions", "Edit Conversion Rates"),
    GENERAL_ENERGY_CONVERSION_MULTIPLIER("general.energy_conversion.conversion_multiplier", "Conversion Multiplier",
            "How much energy is produced per mB of Hydrogen, also affects Electrolytic Separator usage, Ethene burn rate and Gas-Burning Generator energy capacity."),

    GENERAL_REPLICATOR_RECIPES("general.replicator", "Replicator Recipes", "Custom Replicator Recipes"),
    ITEM_RECIPES("general.item_replicator.recipes", "Add Item Replicator Recipes", "The recipes added here will be added to the item replicator. Write using modId:registeredName#amount, # followed by the amount(not null or zero) of UU matter consumed. For example:[\"minecraft:stone#10\",\"mekanism:basic_bin#100\"]"),
    FLUID_RECIPES("general.fluid_replicator.recipes", "Add Fluid Replicator Recipes", "The recipes added here will be added to the fluid replicator. Write using modId:registeredName#amount, # followed by the amount(not null or zero) of UU matter consumed. For example:[\"minecraft:water#10\",\"mekanism:heavy_water#100\"]"),
    CHEMICAL_RECIPES("general.chemical_replicator.recipes", "Add Chemical Replicator Recipes", "The recipes added here will be added to the chemical replicator. Write using modId:registeredName#amount, # followed by the amount(not null or zero) of UU matter consumed. For example:[\"mekanism:oxygen#10\",\"mekanism:hydrogen#100\"]"),
    GENERAL_WIRELESS_TRANSMISSION_STATION_MAX_OUTPUT_RATE("general.wireless_transmission_station.max_transmission_rate", "Maximum Transmission Rate", "Wireless Transmission Station Max Output Rate"),
    ENERGY_RATE("general.energy.max_transmission_rate", "Energy Max Transmission Rate", "Set the maximum energy transmission rate during wireless transmission"),
    FLUIDS_RATE("general.fluids.max_transmission_rate", "Fluids Max Transmission Rate", "Set the maximum fluids transmission rate during wireless transmission"),
    CHEMICALS_RATE("general.chemicals.max_transmission_rate", "Chemicals Max Transmission Rate", "Set the maximum chemicals transmission rate during wireless transmission"),
    ITEMS_RATE("general.items.max_transmission_rate", "Items Max Transmission Rate", "Set the maximum items transmission rate during wireless transmission"),

    // Tier Config
    TIER_LARGE_CHEMICAL_TANK("tier.large_chemical_tank", "Large Chemical Tanks", "Settings for configuring Large Chemical Tanks", true),

    // Storage Config
    ENERGY_STORAGE_RECYCLER(TranslationPreset.ENERGY_STORAGE, "Recycler"),
    ENERGY_STORAGE_PLANTING_STATION(TranslationPreset.ENERGY_STORAGE, "Planting Station"),
    ENERGY_STORAGE_CNC_STAMPER(TranslationPreset.ENERGY_STORAGE, "CNC Stamper"),
    ENERGY_STORAGE_CNC_LATHE(TranslationPreset.ENERGY_STORAGE, "CNC Lathe"),
    ENERGY_STORAGE_ROLLING_MILL(TranslationPreset.ENERGY_STORAGE, "CNC Rolling Mill"),
    ENERGY_STORAGE_AMBIENT_GAS_COLLECTOR(TranslationPreset.ENERGY_STORAGE, "Ambient Gas Collector"),
    ENERGY_STORAGE_WIRELESS_CHARGING_STATION(TranslationPreset.ENERGY_STORAGE, "Wireless Charging Station"),
    ENERGY_STORAGE_WIRELESS_TRANSMITTER_STATION(TranslationPreset.ENERGY_STORAGE, "Wireless Transmitter Station"),
    ENERGY_STORAGE_REPLICATOR("storage.replicator", "Replicator", "Settings for configuring Replicator Energy Storage", true),
    ENERGY_STORAGE_ITEM_REPLICATOR(TranslationPreset.ENERGY_STORAGE, "Item Replicator"),
    ENERGY_STORAGE_FLUID_REPLICATOR(TranslationPreset.ENERGY_STORAGE, "Fluid Replicator"),
    ENERGY_STORAGE_CHEMICAL_REPLICATOR(TranslationPreset.ENERGY_STORAGE, "Chemical Replicator"),
    ENERGY_STORAGE_LARGE_ROTARY_CONDENSENTRATOR(TranslationPreset.ENERGY_STORAGE, "Large Rotary Condensentrator"),
    ENERGY_STORAGE_LARGE_CHEMICAL_INFUSER(TranslationPreset.ENERGY_STORAGE, "Large Chemical Infuser"),
    ENERGY_STORAGE_LARGE_ELECTROLYTIC_SEPARATOR(TranslationPreset.ENERGY_STORAGE, "Large Electrolytic Separator"),
    ENERGY_STORAGE_LARGE_HEAT_GENERATOR(TranslationPreset.ENERGY_STORAGE, "Large Heat Generator"),

    // Usage Config
    ENERGY_USAGE_RECYCLER(TranslationPreset.ENERGY_USAGE, "Recycler"),
    ENERGY_USAGE_PLANTING_STATION(TranslationPreset.ENERGY_USAGE, "Planting Station"),
    ENERGY_USAGE_CNC_STAMPER(TranslationPreset.ENERGY_USAGE, "CNC Stamper"),
    ENERGY_USAGE_CNC_LATHE(TranslationPreset.ENERGY_USAGE, "CNC Lathe"),
    ENERGY_USAGE_ROLLING_MILL(TranslationPreset.ENERGY_USAGE, "CNC Rolling Mill"),
    ENERGY_USAGE_AMBIENT_GAS_COLLECTOR(TranslationPreset.ENERGY_USAGE, "Ambient Gas Collector"),
    ENERGY_USAGE_WIRELESS_CHARGING_STATION(TranslationPreset.ENERGY_USAGE, "Wireless Charging Station"),
    ENERGY_USAGE_REPLICATOR("usage.replicator", "Replicator", "Settings for configuring Replicator Energy Usage", true),
    ENERGY_USAGE_ITEM_REPLICATOR(TranslationPreset.ENERGY_USAGE, "Item Replicator"),
    ENERGY_USAGE_FLUID_REPLICATOR(TranslationPreset.ENERGY_USAGE, "Fluid Replicator"),
    ENERGY_USAGE_CHEMICAL_REPLICATOR(TranslationPreset.ENERGY_USAGE, "Chemical Replicator"),
    ENERGY_USAGE_LARGE_ROTARY_CONDENSENTRATOR(TranslationPreset.ENERGY_USAGE, "Large Rotary Condensentrator"),
    ENERGY_USAGE_LARGE_CHEMICAL_INFUSER(TranslationPreset.ENERGY_USAGE, "Large Chemical Infuser"),

    // General Config
    GAS_COLLECT_AMOUNT("general.collect.amount", "Gas Collect Amount", "mB of Unstable Dimensional Gas collected by the Ambient Gas Collector."),
    WIRELESS_CHARGING_STATION_CHARGING_RATE("general.charging.rate", "Charge Rate", "Amount of Energy(joules) an item can receive per tick from a Wireless Charging Station."),

    // Generator Config
    SERVER_GENERATOR_LARGE_HEAT("server.generator.heat", "Large Heat Generator", "Settings for configuring Large Heat Generators", true),
    SERVER_GENERATOR_LARGE_HEAT_GENERATION("server.generator.heat.gen", "Energy Generation",
            "Amount of energy in Joules the Large Heat Generator produces per tick. largeHeatGeneration + largeHeatGenerationLava * lavaSides + largeHeatGenerationNether. " + "Note: lavaSides is how many sides are adjacent to lava, this includes the block itself if it is lava logged allowing for a max of 81 \"sides\"."),
    SERVER_GENERATOR_LARGE_HEAT_GEN_LAVA("server.generator.heat.gen.lava", "Submerged Energy Generation",
            "Multiplier of effectiveness of Lava that is adjacent to the Large Heat Generator."),
    SERVER_GENERATOR_LARGE_HEAT_GEN_NETHER("server.generator.heat.gen.nether", "Nether Energy Generation",
            "Add this amount of Joules to the energy produced by a large heat generator if it is in an 'ultrawarm' dimension, in vanilla this is just the Nether."),
    SERVER_GENERATOR_LARGE_HEAT_TANK_CAPACITY("server.generator.heat.tank_capacity", "Tank Capacity", "The capacity in mB of the fluid tank in the Large Heat Generator."),
    SERVER_GENERATOR_LARGE_HEAT_FLUID_RATE("server.generator.heat.fluid_rate", "Fluid Rate",
            "The amount of lava in mB that gets consumed to transfer largeHeatGeneration Joules to the Large Heat Generator."),

    SERVER_GENERATOR_LARGE_GAS("server.generator.gas", "Large Gas-Burning Generator", "Settings for configuring Large Gas-Burning Generators", true),
    SERVER_GENERATOR_LARGE_GAS_TANK_CAPACITY("server.generator.gas.tank_capacity", "Tank Capacity", "The capacity in mB of the chemical tank in the Large Gas-Burning Generator.");

    private final String key;
    private final String title;
    private final String tooltip;
    @Nullable
    private final String button;

    MoreMachineConfigTranslations(TranslationPreset preset, String type) {
        this(preset.path(type), preset.title(type), preset.tooltip(type));
    }

    MoreMachineConfigTranslations(TranslationPreset preset, String type, String tooltipSuffix) {
        this(preset.path(type), preset.title(type), preset.tooltip(type) + tooltipSuffix);
    }

    MoreMachineConfigTranslations(String path, String title, String tooltip) {
        this(path, title, tooltip, false);
    }

    MoreMachineConfigTranslations(String path, String title, String tooltip, boolean isSection) {
        this(path, title, tooltip, IConfigTranslation.getSectionTitle(title, isSection));
    }

    MoreMachineConfigTranslations(String path, String title, String tooltip, @Nullable String button) {
        this.key = Util.makeDescriptionId("configuration", Mekmm.rl(path));
        this.title = title;
        this.tooltip = tooltip;
        this.button = button;
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return key;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String tooltip() {
        return tooltip;
    }

    @Nullable
    @Override
    public String button() {
        return button;
    }
}
