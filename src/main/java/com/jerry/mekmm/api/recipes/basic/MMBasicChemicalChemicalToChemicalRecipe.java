package com.jerry.mekmm.api.recipes.basic;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalStackTemplate;
import mekanism.api.recipes.ChemicalChemicalToChemicalRecipe;
import mekanism.api.recipes.basic.IBasicChemicalOutput;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;

import net.minecraft.core.TypedInstance;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NullMarked
public abstract class MMBasicChemicalChemicalToChemicalRecipe extends ChemicalChemicalToChemicalRecipe implements IBasicChemicalOutput {

    protected final ChemicalStackIngredient leftInput;
    protected final ChemicalStackIngredient rightInput;
    protected final ChemicalStackTemplate output;

    /**
     * @param leftInput  Left input.
     * @param rightInput Right input.
     * @param output     Output.
     *
     * @apiNote The order of the inputs does not matter.
     */
    public MMBasicChemicalChemicalToChemicalRecipe(ChemicalStackIngredient leftInput, ChemicalStackIngredient rightInput, ChemicalStackTemplate output) {
        this.leftInput = Objects.requireNonNull(leftInput, "Left input cannot be null.");
        this.rightInput = Objects.requireNonNull(rightInput, "Right input cannot be null.");
        this.output = Objects.requireNonNull(output, "Output cannot be null.");
    }

    @Override
    public boolean test(ChemicalStack input1, ChemicalStack input2) {
        return (leftInput.test(input1) && rightInput.test(input2)) || (rightInput.test(input1) && leftInput.test(input2));
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public ChemicalStackTemplate getOutput(TypedInstance<Chemical> input1, TypedInstance<Chemical> input2) {
        return output;
    }

    @Override
    public ChemicalStackIngredient getLeftInput() {
        return leftInput;
    }

    @Override
    public ChemicalStackIngredient getRightInput() {
        return rightInput;
    }

    @Override
    public List<ChemicalStackTemplate> getOutputDefinition() {
        return Collections.singletonList(output);
    }

    @Override
    public ChemicalStackTemplate getOutputRaw() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MMBasicChemicalChemicalToChemicalRecipe other = (MMBasicChemicalChemicalToChemicalRecipe) o;
        // Note: We don't need to compare the recipe type as that gets covered by the explicit class type check above
        return leftInput.equals(other.leftInput) && rightInput.equals(other.rightInput) && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftInput, rightInput, output);
    }
}
