package com.jerry.mekmm.api.recipes;

import com.jerry.mekmm.api.recipes.chemical.FluidStackChemicalToFluidStackRecipe;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public abstract class FluidStackGasToFluidStackRecipe extends FluidStackChemicalToFluidStackRecipe<Gas, GasStack, GasStackIngredient> {

    /**
     * @param id         Recipe name.
     * @param fluidInput Fluid input.
     * @param gasInput   Chemical input.
     * @param output     Output.
     */
    public FluidStackGasToFluidStackRecipe(ResourceLocation id, FluidStackIngredient fluidInput, GasStackIngredient gasInput, FluidStack output) {
        super(id, fluidInput, gasInput, output);
    }
}
