package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.api.recipes.basic.MMBasicChemicalChemicalToChemicalRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.chemical.ChemicalStackTemplate;
import mekanism.api.recipes.ChemicalChemicalToChemicalRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ChemicalReplicatorIRecipeSingle extends MMBasicChemicalChemicalToChemicalRecipe {

    /**
     * @param leftInput  Left input.
     * @param rightInput Right input.
     * @param output     Output.
     * @apiNote The order of the inputs does not matter.
     */
    public ChemicalReplicatorIRecipeSingle(ChemicalStackIngredient leftInput, ChemicalStackIngredient rightInput, ChemicalStackTemplate output) {
        super(leftInput, rightInput, output);
    }

    @Override
    public RecipeSerializer<@NotNull ChemicalChemicalToChemicalRecipe> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<@NotNull ChemicalChemicalToChemicalRecipe> getType() {
        return null;
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(MoreMachineBlocks.FLUID_REPLICATOR);
    }

    @Override
    public java.util.List<net.minecraft.world.item.crafting.display.RecipeDisplay> display() {
        return java.util.List.of(new mekanism.api.recipes.display.SimpleMachineRecipeDisplay(
                net.minecraft.world.item.crafting.display.SlotDisplay.Empty.INSTANCE,
                net.minecraft.world.item.crafting.display.SlotDisplay.Empty.INSTANCE,
                new net.minecraft.world.item.crafting.display.SlotDisplay.ItemStackSlotDisplay(net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(getToastSymbol()))));
    }
}
