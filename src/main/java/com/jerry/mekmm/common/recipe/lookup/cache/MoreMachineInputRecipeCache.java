package com.jerry.mekmm.common.recipe.lookup.cache;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.TripleInputRecipeCache;
import mekanism.common.recipe.lookup.cache.type.ItemInputCache;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriPredicate;

import java.util.function.Function;

public class MoreMachineInputRecipeCache {

    public static class TripleItem<RECIPE extends MekanismRecipe<?> & TriPredicate<ItemStack, ItemStack, ItemStack>> extends
                                  TripleInputRecipeCache<ItemStack, ItemStackIngredient, ItemStack, ItemStackIngredient, ItemStack, ItemStackIngredient, RECIPE, ItemInputCache<RECIPE>, ItemInputCache<RECIPE>, ItemInputCache<RECIPE>> {

        public TripleItem(MekanismRecipeType<?, RECIPE, ?> recipeType, Function<RECIPE, ItemStackIngredient> inputAExtractor,
                          Function<RECIPE, ItemStackIngredient> inputBExtractor, Function<RECIPE, ItemStackIngredient> inputCExtractor) {
            super(recipeType, inputAExtractor, new ItemInputCache<>(), inputBExtractor, new ItemInputCache<>(), inputCExtractor, new ItemInputCache<>());
        }
    }
}
