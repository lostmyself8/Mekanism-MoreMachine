package com.jerry.mekmm.api.recipes;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.vanilla_input.SingleFluidChemicalRecipeInput;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Base class for defining fluid chemical to chemical recipes.
 * <br>
 * Input: FluidStack
 * <br>
 * Input: Chemical
 * <br>
 * Output: ChemicalStack
 *
 * @apiNote Chemical Washers can process this recipe type.
 */
@NothingNullByDefault
public abstract class FluidChemicalToFluidRecipe extends MekanismRecipe<SingleFluidChemicalRecipeInput> implements BiPredicate<@NotNull FluidStack, ChemicalStack> {

    @Override
    public abstract boolean test(FluidStack fluidStack, ChemicalStack chemicalStack);

    @Override
    public boolean matches(SingleFluidChemicalRecipeInput input, Level level) {
        // Don't match incomplete recipes or ones that don't match
        return !isIncomplete() && test(input.fluid(), input.chemical());
    }

    /**
     * Gets the input fluid ingredient.
     */
    public abstract FluidStackIngredient getFluidInput();

    /**
     * Gets the input chemical ingredient.
     */
    public abstract ChemicalStackIngredient getChemicalInput();

    /**
     * For JEI, gets the output representations to display.
     *
     * @return Representation of the output, <strong>MUST NOT</strong> be modified.
     */
    public abstract List<FluidStack> getOutputDefinition();

    /**
     * Gets a new output based on the given inputs.
     *
     * @param fluidStack    Specific fluid input.
     * @param chemicalStack Specific chemical input.
     *
     * @return New output.
     *
     * @apiNote While Mekanism does not currently make use of the inputs, it is important to support it and pass the
     *          proper value in case any addons define input based
     *          outputs where things like NBT may be different.
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public abstract FluidStackTemplate getOutput(FluidStack fluidStack, ChemicalStack chemicalStack);

    @Override
    public boolean isIncomplete() {
        return getFluidInput().hasNoMatchingInstances() || getChemicalInput().hasNoMatchingInstances();
    }
}
