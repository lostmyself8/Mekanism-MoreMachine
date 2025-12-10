package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineRecipeSerializers;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

@NothingNullByDefault
public class PlantingIRecipe extends PlantingRecipe {

    public PlantingIRecipe(ResourceLocation id, ItemStackIngredient itemInput, ChemicalStackIngredient.GasStackIngredient gasInput, ItemStack mainOutput, ItemStack secondaryOutput, double secondaryChance) {
        super(id, itemInput, gasInput, mainOutput, secondaryOutput, secondaryChance);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MoreMachineRecipeSerializers.PLANTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MoreMachineRecipeType.PLANTING.get();
    }

    @Override
    public String getGroup() {
        return MoreMachineBlocks.PLANTING_STATION.getName();
    }

    @Override
    public ItemStack getToastSymbol() {
        return MoreMachineBlocks.PLANTING_STATION.getItemStack();
    }
}
