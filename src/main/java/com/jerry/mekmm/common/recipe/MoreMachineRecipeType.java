package com.jerry.mekmm.common.recipe;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.api.recipes.StamperRecipe;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.DoubleItem;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.ItemChemical;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleItem;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;

public class MoreMachineRecipeType {

    public static RecipeTypeRegistryObject<RecyclerRecipe, SingleItem<RecyclerRecipe>> RECYCLING;

    public static RecipeTypeRegistryObject<PlantingRecipe, ItemChemical<Gas, GasStack, PlantingRecipe>> PLANTING;

    public static RecipeTypeRegistryObject<StamperRecipe, DoubleItem<StamperRecipe>> STAMPING;

    public static RecipeTypeRegistryObject<ItemStackToItemStackRecipe, SingleItem<ItemStackToItemStackRecipe>> LATHING;

    public static RecipeTypeRegistryObject<ItemStackToItemStackRecipe, SingleItem<ItemStackToItemStackRecipe>> ROLLING_MILL;
}
