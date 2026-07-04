package com.jerry.mekmm.common.recipe;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;
import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;

import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class MoreMachineRecipeType {

    public static RecipeTypeRegistryObject<SingleRecipeInput, RecyclerRecipe, InputRecipeCache.SingleItem<RecyclerRecipe>> RECYCLING;

    public static RecipeTypeRegistryObject<SingleItemChemicalRecipeInput, PlantingRecipe, InputRecipeCache.ItemChemical<PlantingRecipe>> PLANTING_STATION;

    public static RecipeTypeRegistryObject<RecipeInput, StamperRecipe, InputRecipeCache.DoubleItem<StamperRecipe>> STAMPING;

    public static RecipeTypeRegistryObject<SingleRecipeInput, ItemStackToItemStackRecipe, InputRecipeCache.SingleItem<ItemStackToItemStackRecipe>> LATHING;

    public static RecipeTypeRegistryObject<SingleRecipeInput, ItemStackToItemStackRecipe, InputRecipeCache.SingleItem<ItemStackToItemStackRecipe>> ROLLING_MILL;

    public static RecipeTypeRegistryObject<RecipeInput, TripleItemToItemRecipe, MoreMachineInputRecipeCache.TripleItem<TripleItemToItemRecipe>> PRESSING;
}
