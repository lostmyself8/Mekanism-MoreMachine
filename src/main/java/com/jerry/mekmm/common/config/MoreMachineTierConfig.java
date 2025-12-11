package com.jerry.mekmm.common.config;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;
import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tier.MidChemicalTankTier;

import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.MekanismConfigTranslations.TierTranslations;
import mekanism.common.config.value.CachedLongValue;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Locale;

public class MoreMachineTierConfig extends BaseMekanismConfig {

    private final ModConfigSpec configSpec;

    MoreMachineTierConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        addMaxChemicalTankCategory(builder);
        addMidChemicalTankCategory(builder);

        configSpec = builder.build();
    }

    private void addMaxChemicalTankCategory(ModConfigSpec.Builder builder) {
        MoreMachineConfigTranslations.TIER_LARGE_CHEMICAL_TANK.applyToBuilder(builder).push("max_chemical_tanks");
        for (MaxChemicalTankTier tier : MoreMachineEnumUtils.MAX_CHEMICAL_TANK_TIERS) {
            TierTranslations translations = create(tier);
            String tierName = tier.getBaseTier().getSimpleName().toLowerCase(Locale.ROOT);
            CachedLongValue storageReference = CachedLongValue.wrap(this, translations.first().applyToBuilder(builder)
                    .defineInRange(tierName + "Capacity", tier.getBaseStorage(), 1, Long.MAX_VALUE));
            CachedLongValue outputReference = CachedLongValue.wrap(this, translations.second().applyToBuilder(builder)
                    .defineInRange(tierName + "Output", tier.getBaseOutput(), 1, Long.MAX_VALUE));
            tier.setConfigReference(storageReference, outputReference);
        }
        builder.pop();
    }

    private void addMidChemicalTankCategory(ModConfigSpec.Builder builder) {
        MoreMachineConfigTranslations.TIER_LARGE_CHEMICAL_TANK.applyToBuilder(builder).push("mid_chemical_tanks");
        for (MidChemicalTankTier tier : MoreMachineEnumUtils.MID_CHEMICAL_TANK_TIERS) {
            TierTranslations translations = create(tier);
            String tierName = tier.getBaseTier().getSimpleName().toLowerCase(Locale.ROOT);
            CachedLongValue storageReference = CachedLongValue.wrap(this, translations.first().applyToBuilder(builder)
                    .defineInRange(tierName + "Capacity", tier.getBaseStorage(), 1, Long.MAX_VALUE));
            CachedLongValue outputReference = CachedLongValue.wrap(this, translations.second().applyToBuilder(builder)
                    .defineInRange(tierName + "Output", tier.getBaseOutput(), 1, Long.MAX_VALUE));
            tier.setConfigReference(storageReference, outputReference);
        }
        builder.pop();
    }

    public static TierTranslations create(ILargeChemicalTankTier tier) {
        return TierTranslations.create(tier, tier.getType().toLowerCase(Locale.ROOT) + "_chemical_tank", name -> "Storage size of " + name + " " + tier.getType().toLowerCase(Locale.ROOT) + " chemical tanks in mB.",
                name -> "Output rate of " + name + " " + tier.getType().toLowerCase(Locale.ROOT) + " chemical tanks in mB.");
    }

    @Override
    public String getFileName() {
        return "tiers";
    }

    @Override
    public String getTranslation() {
        return "Tier Config";
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
