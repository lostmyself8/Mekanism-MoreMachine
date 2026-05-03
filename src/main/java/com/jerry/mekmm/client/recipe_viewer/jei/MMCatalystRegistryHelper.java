package com.jerry.mekmm.client.recipe_viewer.jei;

import com.jerry.mekmm.common.block.attribute.MoreMachineAttributeFactoryType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.client.recipe_viewer.jei.MekanismJEI;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.tier.FactoryTier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

import java.util.List;

public class MMCatalystRegistryHelper {

    private MMCatalystRegistryHelper() {}

    /**
     * 与Mekanism的CatalystRegistryHelper中的register方法功能是一致的，只是多了一个需否需要基础机器的参数，
     * 平常使用可以直接填true，对于“ENERGY_CONVERSION”或“CHEMICAL_CONVERSION”
     * 可能得填写false。
     *
     * @param needOrdinary 是否需要注册最基础的机器
     */
    public static void register(IRecipeCatalystRegistration registry, boolean needOrdinary, IRecipeViewerRecipeType<?>... categories) {
        for (IRecipeViewerRecipeType<?> category : categories) {
            register(registry, MekanismJEI.genericRecipeType(category), category.workstations(), needOrdinary);
        }
    }

    public static void register(IRecipeCatalystRegistration registry, IRecipeType<?> recipeType, List<ItemLike> workstations, boolean needOrdinary) {
        for (ItemLike workstation : workstations) {
            Item item = workstation.asItem();
            if (needOrdinary) {
                registry.addCraftingStation(recipeType, item);
            }
            if (item instanceof BlockItem blockItem) {
                MoreMachineAttributeFactoryType factoryType = Attribute.get(blockItem.getBlock(), MoreMachineAttributeFactoryType.class);
                if (factoryType != null) {
                    for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
                        registry.addCraftingStation(recipeType, MoreMachineBlocks.getMoreMachineFactory(tier, factoryType.getMoreMachineFactoryType()));
                    }
                }
            }
        }
    }
}
