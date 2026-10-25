package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.api.recipes.basic.MMBasicItemStackChemicalToItemStackRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class ReplicatorIRecipeSingle extends MMBasicItemStackChemicalToItemStackRecipe {

    /**
     * @param chemicalInput Chemical input.
     * @param output        Output.
     */
    public ReplicatorIRecipeSingle(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStackTemplate output) {
        super(itemInput, chemicalInput, output);
    }

    @Override
    public RecipeSerializer<ReplicatorIRecipeSingle> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<ReplicatorIRecipeSingle> getType() {
        return null;
    }

    public String getGroup() {
        return "duplicator";
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(MoreMachineBlocks.REPLICATOR);
    }

    @Override
    public java.util.List<net.minecraft.world.item.crafting.display.RecipeDisplay> display() {
        return java.util.List.of(new mekanism.api.recipes.display.SimpleMachineRecipeDisplay(
                net.minecraft.world.item.crafting.display.SlotDisplay.Empty.INSTANCE,
                net.minecraft.world.item.crafting.display.SlotDisplay.Empty.INSTANCE,
                new net.minecraft.world.item.crafting.display.SlotDisplay.ItemStackSlotDisplay(net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(getToastSymbol()))));
    }
}
