package com.jerry.datagen.common.recipe.builder;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.recipe.upgrade.MekanismShapedRecipe;
import mekanism.common.registration.impl.BlockRegistryObject;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingRecipe.CraftingBookInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

@NothingNullByDefault
public class MoreMachineDataShapedRecipeBuilder extends ExtendedShapedRecipeBuilder {

    private MoreMachineDataShapedRecipeBuilder(Holder<Item> result, int count) {
        super(result, count);
    }

    public static MoreMachineDataShapedRecipeBuilder shapedRecipe(BlockRegistryObject<?, ?> result) {
        return shapedRecipe(result, 1);
    }

    public static MoreMachineDataShapedRecipeBuilder shapedRecipe(BlockRegistryObject<?, ?> result, int count) {
        return shapedRecipe(result.getItemHolder(), count);
    }

    public static MoreMachineDataShapedRecipeBuilder shapedRecipe(Holder<Item> result) {
        return shapedRecipe(result, 1);
    }

    public static MoreMachineDataShapedRecipeBuilder shapedRecipe(Holder<Item> result, int count) {
        return new MoreMachineDataShapedRecipeBuilder(result, count);
    }

    @Override
    protected Recipe<?> wrapRecipe(Recipe.CommonInfo commonInfo, CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        return new MekanismShapedRecipe(commonInfo, bookInfo, pattern, result);
    }
}
