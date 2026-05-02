package com.jerry.mekmm.api.recipes.cache;

import com.jerry.mekmm.api.recipes.RecyclerRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Base class to help implement handling of recipes with one input.
 */
@NothingNullByDefault
public class MoreMachineOneInputCachedRecipe<HOLDER, INPUT extends net.minecraft.core.TypedInstance<HOLDER>, OUTPUT, RECIPE extends MekanismRecipe<?> & Predicate<INPUT>> extends OneInputCachedRecipe<HOLDER, INPUT, OUTPUT, RECIPE> {

    /**
     * @param recipe           Recipe.
     * @param recheckAllErrors Returns {@code true} if processing should be continued even if an error is hit in order
     *                         to gather all the errors. It is recommended to not
     *                         do this every tick or if there is no one viewing recipes.
     * @param inputHandler     Input handler.
     * @param outputHandler    Output handler.
     * @param inputSupplier    Supplier of the recipe's input ingredient.
     * @param outputGetter     Gets the recipe's output when given the corresponding input.
     * @param inputEmptyCheck  Checks if the input is empty.
     * @param outputEmptyCheck Checks if the output is empty (indicating something went horribly wrong).
     */
    protected MoreMachineOneInputCachedRecipe(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<HOLDER, INPUT> inputHandler, IOutputHandler<OUTPUT> outputHandler, Supplier<? extends InputIngredient<HOLDER, INPUT>> inputSupplier, Function<INPUT, OUTPUT> outputGetter, Predicate<INPUT> inputEmptyCheck, Predicate<OUTPUT> outputEmptyCheck) {
        super(recipe, recheckAllErrors, inputHandler, outputHandler, inputSupplier, outputGetter, inputEmptyCheck, outputEmptyCheck);
    }

    /**
     * Base implementation for handling Recycler Recipes.
     *
     * @param recipe           Recipe.
     * @param recheckAllErrors Returns {@code true} if processing should be continued even if an error is hit in order
     *                         to gather all the errors. It is recommended to not
     *                         do this every tick or if there is no one viewing recipes.
     * @param inputHandler     Input handler.
     * @param outputHandler    Output handler.
     */
    public static MoreMachineOneInputCachedRecipe<Item, @NotNull ItemStack, RecyclerRecipe.@NotNull ChanceOutput, RecyclerRecipe> recycler(RecyclerRecipe recipe, BooleanSupplier recheckAllErrors,
                                                                                                                                           IInputHandler<Item, @NotNull ItemStack> inputHandler,
                                                                                                                                           IOutputHandler<RecyclerRecipe.@NotNull ChanceOutput> outputHandler) {
        return new MoreMachineOneInputCachedRecipe<>(recipe, recheckAllErrors, inputHandler, outputHandler, recipe::getInput, recipe::getOutput, ConstantPredicates.ITEM_EMPTY,
                ConstantPredicates.alwaysFalse());
    }
}
