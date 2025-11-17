package com.jerry.mekaf.client.jei;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IItemProvider;
import mekanism.client.jei.MekanismJEI;
import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

public class AdvancedFactoryCatalystRegistryHelper {

    private AdvancedFactoryCatalystRegistryHelper() {}

    public static void register(IRecipeCatalystRegistration registry, IBlockProvider mekanismBlock) {
        registerRecipeItem(registry, mekanismBlock, MekanismJEIRecipeType.findType(mekanismBlock.getRegistryName()));
    }

    public static void registerRecipeItem(IRecipeCatalystRegistration registry, IItemProvider mekanismItem, MekanismJEIRecipeType<?>... categories) {
        registerRecipeItem(registry, mekanismItem, MekanismJEI.recipeType(categories));
    }

    public static void registerRecipeItem(IRecipeCatalystRegistration registry, IItemProvider mekanismItem, RecipeType<?>... categories) {
        if (mekanismItem instanceof IBlockProvider mekanismBlock) {
            for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
                if (mekanismBlock == MekanismBlocks.CHEMICAL_OXIDIZER) {
                    registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.OXIDIZING).getItemStack(), categories);
                } else if (mekanismBlock == MekanismBlocks.CHEMICAL_INFUSER) {
                    registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CHEMICAL_INFUSING).getItemStack(), categories);
                } else if (mekanismBlock == MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER) {
                    registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.DISSOLVING).getItemStack(), categories);
                } else if (mekanismBlock == MekanismBlocks.CHEMICAL_WASHER) {
                    registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.WASHING).getItemStack(), categories);
                } else if (mekanismBlock == MekanismBlocks.CHEMICAL_CRYSTALLIZER) {
                    registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CRYSTALLIZING).getItemStack(), categories);
                } else if (mekanismBlock == MekanismBlocks.PRESSURIZED_REACTION_CHAMBER) {
                    registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PRESSURISED_REACTING).getItemStack(), categories);
                } else if (mekanismBlock == MekanismBlocks.ISOTOPIC_CENTRIFUGE) {
                    registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CENTRIFUGING).getItemStack(), categories);
                } else if (mekanismBlock == MekanismBlocks.NUTRITIONAL_LIQUIFIER) {
                    registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.LIQUIFYING).getItemStack(), categories);
                }
            }
        }
    }
}
