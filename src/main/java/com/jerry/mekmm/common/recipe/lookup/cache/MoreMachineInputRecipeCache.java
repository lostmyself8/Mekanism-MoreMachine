package com.jerry.mekmm.common.recipe.lookup.cache;

import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.TripleInputRecipeCache;
import mekanism.common.recipe.lookup.cache.type.ItemInputCache;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriPredicate;

import java.util.function.Function;

public class MoreMachineInputRecipeCache {

    private MoreMachineInputRecipeCache() {}

    public static class TripleItem<RECIPE extends MekanismRecipe<?> & TriPredicate<ItemStack, ItemStack, ItemStack>>
                                  extends TripleInputRecipeCache<Item, ItemStack, ItemStackIngredient, Item, ItemStack, ItemStackIngredient, Item, ItemStack, ItemStackIngredient, RECIPE, ItemInputCache<RECIPE>, ItemInputCache<RECIPE>, ItemInputCache<RECIPE>> {

        public TripleItem(MekanismRecipeType<?, RECIPE, ?> recipeType, Function<RECIPE, ItemStackIngredient> inputAExtractor,
                          Function<RECIPE, ItemStackIngredient> inputBExtractor, Function<RECIPE, ItemStackIngredient> inputCExtractor) {
            super(recipeType, inputAExtractor, new ItemInputCache<>(), inputBExtractor, new ItemInputCache<>(), inputCExtractor, new ItemInputCache<>());
        }
    }

    public static class Pressing extends TripleItem<TripleItemToItemRecipe> {

        public Pressing(MekanismRecipeType<?, TripleItemToItemRecipe, ?> recipeType) {
            super(recipeType, TripleItemToItemRecipe::getFirstInput, TripleItemToItemRecipe::getSecondInput, TripleItemToItemRecipe::getThirdInput);
        }
    }
}
