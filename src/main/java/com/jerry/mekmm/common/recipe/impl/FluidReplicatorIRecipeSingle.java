package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.api.recipes.FluidChemicalToFluidRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicFluidChemicalToFluidRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import org.jetbrains.annotations.NotNull;

@NothingNullByDefault
public class FluidReplicatorIRecipeSingle extends BasicFluidChemicalToFluidRecipe {

    /**
     * @param fluidInput    Fluid input.
     * @param chemicalInput Chemical input.
     * @param output        Output.
     */
    public FluidReplicatorIRecipeSingle(FluidStackIngredient fluidInput, ChemicalStackIngredient chemicalInput, FluidStackTemplate output) {
        super(fluidInput, chemicalInput, output);
    }

    @Override
    public RecipeSerializer<@NotNull FluidChemicalToFluidRecipe> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<@NotNull FluidChemicalToFluidRecipe> getType() {
        return null;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(MoreMachineBlocks.FLUID_REPLICATOR);
    }
}
