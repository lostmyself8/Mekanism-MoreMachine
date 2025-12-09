package com.jerry.mekmm.common.config;

import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedLongValue;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tier.MidChemicalTankTier;

import java.util.Locale;

public class MoreMachineTierConfig extends BaseMekanismConfig {

    private static final String LARGE_CHEMICAL_TANK_CATEGORY = "large_chemical_tanks";
    private static final String MID_CHEMICAL_TANK_CATEGORY = "mid";
    private static final String MAX_CHEMICAL_TANK_CATEGORY = "max";

    private final ForgeConfigSpec configSpec;

    MoreMachineTierConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Tier Config. This config is synced from server to client.").push("tier");

        builder.comment("Large Chemical Tanks").push(LARGE_CHEMICAL_TANK_CATEGORY);
        addLargeChemicalTankCategory(builder);
        builder.pop();

        builder.pop();
        configSpec = builder.build();
    }

    private void addLargeChemicalTankCategory(ForgeConfigSpec.Builder builder) {
        builder.comment("Mid Chemical Tanks").push(MID_CHEMICAL_TANK_CATEGORY);
        for (MidChemicalTankTier tier : MoreMachineEnumUtils.MID_CHEMICAL_TANK_TIER) {
            String tierName = tier.getBaseTier().getSimpleName();
            CachedLongValue storageReference = CachedLongValue.wrap(this, builder.comment("Storage size of " + tierName + " mid chemical tanks in mB.")
                    .defineInRange(tierName.toLowerCase(Locale.ROOT) + "Storage", tier.getBaseStorage(), 1, Long.MAX_VALUE));
            CachedLongValue outputReference = CachedLongValue.wrap(this, builder.comment("Output rate of " + tierName + " mid chemical tanks in mB.")
                    .defineInRange(tierName.toLowerCase(Locale.ROOT) + "Output", tier.getBaseOutput(), 1, Long.MAX_VALUE));
            tier.setConfigReference(storageReference, outputReference);
        }
        builder.pop();
        builder.comment("Max Chemical Tanks").push(MAX_CHEMICAL_TANK_CATEGORY);
        for (MaxChemicalTankTier tier : MoreMachineEnumUtils.MAX_CHEMICAL_TANK_TIER) {
            String tierName = tier.getBaseTier().getSimpleName();
            CachedLongValue storageReference = CachedLongValue.wrap(this, builder.comment("Storage size of " + tierName + " max chemical tanks in mB.")
                    .defineInRange(tierName.toLowerCase(Locale.ROOT) + "Storage", tier.getBaseStorage(), 1, Long.MAX_VALUE));
            CachedLongValue outputReference = CachedLongValue.wrap(this, builder.comment("Output rate of " + tierName + " max chemical tanks in mB.")
                    .defineInRange(tierName.toLowerCase(Locale.ROOT) + "Output", tier.getBaseOutput(), 1, Long.MAX_VALUE));
            tier.setConfigReference(storageReference, outputReference);
        }
        builder.pop();
    }

    @Override
    public String getFileName() {
        return "tiers";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public ModConfig.Type getConfigType() {
        return ModConfig.Type.SERVER;
    }

    @Override
    public boolean addToContainer() {
        return false;
    }
}
