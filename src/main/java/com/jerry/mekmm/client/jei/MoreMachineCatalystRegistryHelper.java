package com.jerry.mekmm.client.jei;

import com.jerry.mekmm.common.block.attribute.AttributeMoreMachineFactoryType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IItemProvider;
import mekanism.client.jei.MekanismJEI;
import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

public class MoreMachineCatalystRegistryHelper {

    private MoreMachineCatalystRegistryHelper() {}

    public static void register(IRecipeCatalystRegistration registry, IBlockProvider mekanismBlock, MekanismJEIRecipeType<?>... additionalCategories) {
        MekanismJEIRecipeType<?>[] categories = new MekanismJEIRecipeType<?>[additionalCategories.length + 1];
        categories[0] = MekanismJEIRecipeType.findType(mekanismBlock.getRegistryName());
        System.arraycopy(additionalCategories, 0, categories, 1, additionalCategories.length);
        registerRecipeItem(registry, mekanismBlock, categories);
    }

    public static void registerRecipeItem(IRecipeCatalystRegistration registry, IItemProvider mekanismItem, MekanismJEIRecipeType<?>... categories) {
        registerRecipeItem(registry, mekanismItem, MekanismJEI.recipeType(categories));
    }

    public static void registerRecipeItem(IRecipeCatalystRegistration registry, IItemProvider mekanismItem, RecipeType<?>... categories) {
        registry.addRecipeCatalyst(mekanismItem.getItemStack(), categories);
        if (mekanismItem instanceof IBlockProvider mekanismBlock) {
            Attribute.ifPresent(mekanismBlock.getBlock(), AttributeMoreMachineFactoryType.class, attr -> {
                for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
                    registry.addRecipeCatalyst(MoreMachineBlocks.getMoreMachineFactory(tier, attr.getMoreMachineFactoryType()).getItemStack(), categories);
                }
            });
        }
    }
}
