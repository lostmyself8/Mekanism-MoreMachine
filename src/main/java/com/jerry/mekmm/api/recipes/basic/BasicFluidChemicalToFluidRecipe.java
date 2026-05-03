package com.jerry.mekmm.api.recipes.basic;

import com.jerry.mekmm.api.recipes.FluidChemicalToFluidRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NothingNullByDefault
public abstract class BasicFluidChemicalToFluidRecipe extends FluidChemicalToFluidRecipe {

    protected final FluidStackIngredient fluidInput;
    protected final ChemicalStackIngredient chemicalInput;
    protected final FluidStackTemplate output;

    /**
     * @param fluidInput    Fluid input.
     * @param chemicalInput Chemical input.
     * @param output        Output.
     */
    public BasicFluidChemicalToFluidRecipe(FluidStackIngredient fluidInput, ChemicalStackIngredient chemicalInput, FluidStackTemplate output) {
        this.fluidInput = Objects.requireNonNull(fluidInput, "Fluid input cannot be null.");
        this.chemicalInput = Objects.requireNonNull(chemicalInput, "Chemical input cannot be null.");
        Objects.requireNonNull(output, "Output cannot be null.");
        this.output = output;
    }

    @Override
    public boolean test(FluidStack fluidStack, ChemicalStack chemicalStack) {
        return fluidInput.test(fluidStack) && chemicalInput.test(chemicalStack);
    }

    @Override
    public FluidStackIngredient getFluidInput() {
        return fluidInput;
    }

    @Override
    public ChemicalStackIngredient getChemicalInput() {
        return chemicalInput;
    }

    @Override
    public List<FluidStack> getOutputDefinition() {
        return Collections.singletonList(output.create());
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public FluidStackTemplate getOutput(FluidStack fluidStack, ChemicalStack chemicalStack) {
        return output;
    }

    public FluidStackTemplate getOutputRaw() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicFluidChemicalToFluidRecipe other = (BasicFluidChemicalToFluidRecipe) o;
        return fluidInput.equals(other.fluidInput) && chemicalInput.equals(other.chemicalInput) && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluidInput, chemicalInput, output);
    }

    @Override
    public void logMissingTags() {
        getFluidInput().logMissingTags();
        getChemicalInput().logMissingTags();
    }
}
