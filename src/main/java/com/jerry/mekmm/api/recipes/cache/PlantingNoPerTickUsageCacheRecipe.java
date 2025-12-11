package com.jerry.mekmm.api.recipes.cache;

import com.jerry.mekmm.api.recipes.PlantingRecipe;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.SawmillRecipe.ChanceOutput;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.function.*;

public class PlantingNoPerTickUsageCacheRecipe<INPUT_A, INPUT_B, OUTPUT, RECIPE extends MekanismRecipe<?> & BiPredicate<INPUT_A, INPUT_B>> extends MoreMachineTwoInputCachedRecipe<INPUT_A, INPUT_B, OUTPUT, RECIPE> {

    /**
     * @param recipe                   Recipe.
     * @param recheckAllErrors         Returns {@code true} if processing should be continued even if an error is hit in
     *                                 order to gather all the errors. It is recommended
     *                                 to not do this every tick or if there is no one viewing recipes.
     * @param inputHandler             Main input handler.
     * @param secondaryInputHandler    Secondary input handler.
     * @param outputHandler            Output handler.
     * @param inputSupplier            Supplier of the recipe's input ingredient.
     * @param secondaryInputSupplier   Supplier of the recipe's secondary input ingredient.
     * @param outputGetter             Gets the recipe's output when given the corresponding inputs.
     * @param inputEmptyCheck          Checks if the primary input is empty.
     * @param secondaryInputEmptyCheck Checks if the secondary input is empty.
     * @param outputEmptyCheck         Checks if the output is empty (indicating something went horribly wrong).
     */
    protected PlantingNoPerTickUsageCacheRecipe(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<INPUT_A> inputHandler, IInputHandler<INPUT_B> secondaryInputHandler, IOutputHandler<OUTPUT> outputHandler, Supplier<InputIngredient<INPUT_A>> inputSupplier, Supplier<InputIngredient<INPUT_B>> secondaryInputSupplier, BiFunction<INPUT_A, INPUT_B, OUTPUT> outputGetter, Predicate<INPUT_A> inputEmptyCheck, Predicate<INPUT_B> secondaryInputEmptyCheck, Predicate<OUTPUT> outputEmptyCheck) {
        super(recipe, recheckAllErrors, inputHandler, secondaryInputHandler, outputHandler, inputSupplier, secondaryInputSupplier, outputGetter, inputEmptyCheck, secondaryInputEmptyCheck, outputEmptyCheck);
    }

    public static PlantingNoPerTickUsageCacheRecipe<ItemStack, ChemicalStack, ChanceOutput, PlantingRecipe> planting(PlantingRecipe recipe, BooleanSupplier recheckAllErrors,
                                                                                                                     IInputHandler<@NotNull ItemStack> itemInputHandler,
                                                                                                                     ILongInputHandler<@NotNull ChemicalStack> chemicalInputHandler,
                                                                                                                     IOutputHandler<ChanceOutput> outputHandler) {
        return new PlantingNoPerTickUsageCacheRecipe<>(recipe, recheckAllErrors, itemInputHandler, chemicalInputHandler, outputHandler, recipe::getItemInput, recipe::getChemicalInput, recipe::getOutput,
                ConstantPredicates.ITEM_EMPTY, ConstantPredicates.CHEMICAL_EMPTY, ConstantPredicates.alwaysFalse());
    }

    @Override
    protected void finishProcessing(int operations) {
        if (input != null && secondaryInput != null && output != null && !inputEmptyCheck.test(input) && !secondaryInputEmptyCheck.test(secondaryInput) &&
                !outputEmptyCheck.test(output)) {
            secondaryInputHandler.use(secondaryInput, operations);
            outputHandler.handleOutput(output, operations);
        }
    }
}
