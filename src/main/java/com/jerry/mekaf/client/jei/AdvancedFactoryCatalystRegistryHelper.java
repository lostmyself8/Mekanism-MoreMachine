package com.jerry.mekaf.client.jei;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import mekanism.client.jei.CatalystRegistryHelper;
import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;

import mezz.jei.api.registration.IRecipeCatalystRegistration;

public class AdvancedFactoryCatalystRegistryHelper {

    private AdvancedFactoryCatalystRegistryHelper() {}

    public static void register(IRecipeCatalystRegistration registry) {
        for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
            CatalystRegistryHelper.register(registry, MekanismJEIRecipeType.OXIDIZING, AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.OXIDIZING));
            CatalystRegistryHelper.register(registry, MekanismJEIRecipeType.DISSOLUTION, AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.DISSOLVING));
            CatalystRegistryHelper.register(registry, MekanismJEIRecipeType.WASHING, AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.WASHING));
            CatalystRegistryHelper.register(registry, MekanismJEIRecipeType.CRYSTALLIZING, AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CRYSTALLIZING));
            CatalystRegistryHelper.register(registry, MekanismJEIRecipeType.REACTION, AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PRESSURISED_REACTING));
            CatalystRegistryHelper.register(registry, MekanismJEIRecipeType.CENTRIFUGING, AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CENTRIFUGING));
            CatalystRegistryHelper.register(registry, MekanismJEIRecipeType.NUTRITIONAL_LIQUIFICATION, AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.LIQUIFYING));
            CatalystRegistryHelper.register(registry, MekanismJEIRecipeType.PIGMENT_EXTRACTING, AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PIGMENT_EXTRACTING));
            CatalystRegistryHelper.register(registry, MekanismJEIRecipeType.PAINTING, AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PAINTING));
        }
    }
}
