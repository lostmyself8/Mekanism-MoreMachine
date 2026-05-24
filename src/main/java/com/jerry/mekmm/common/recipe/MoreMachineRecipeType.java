package com.jerry.mekmm.common.recipe;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;
import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache.TripleItem;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.DoubleItem;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.ItemChemical;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleItem;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;

import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class MoreMachineRecipeType {

    public static RecipeTypeRegistryObject<SingleRecipeInput, RecyclerRecipe, SingleItem<RecyclerRecipe>> RECYCLING;

    public static RecipeTypeRegistryObject<SingleItemChemicalRecipeInput, PlantingRecipe, ItemChemical<PlantingRecipe>> PLANTING_STATION;

    public static RecipeTypeRegistryObject<RecipeInput, StamperRecipe, DoubleItem<StamperRecipe>> STAMPING;

    public static RecipeTypeRegistryObject<SingleRecipeInput, ItemStackToItemStackRecipe, SingleItem<ItemStackToItemStackRecipe>> LATHING;

    public static RecipeTypeRegistryObject<SingleRecipeInput, ItemStackToItemStackRecipe, SingleItem<ItemStackToItemStackRecipe>> ROLLING_MILL;

    public static RecipeTypeRegistryObject<RecipeInput, TripleItemToItemRecipe, TripleItem<TripleItemToItemRecipe>> PRESSING;
}
