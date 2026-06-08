package com.jerry.mekmm.api.recipes.cache;

import com.jerry.mekmm.api.recipes.RecyclerRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipeHelper;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.core.TypedInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Base class to help implement handling of recipes with one input.
 */
@NothingNullByDefault
public class MoreMachineOneInputCachedRecipe<HOLDER, INPUT extends TypedInstance<HOLDER>, OUTPUT, RECIPE extends MekanismRecipe<?> & Predicate<INPUT>> extends CachedRecipe<RECIPE> {

    private final IInputHandler<HOLDER, INPUT> inputHandler;
    private final IOutputHandler<OUTPUT> outputHandler;
    private final Supplier<? extends InputIngredient<HOLDER, INPUT>> inputSupplier;
    private final Function<INPUT, OUTPUT> outputGetter;
    private final Consumer<INPUT> inputSetter;
    private final Consumer<OUTPUT> outputSetter;
    @Nullable
    private INPUT input;
    @Nullable
    private OUTPUT output;

    /**
     * @param recipe           Recipe.
     * @param recheckAllErrors Returns {@code true} if processing should be continued even if an error is hit in order
     *                         to gather all the errors. It is recommended to not
     *                         do this every tick or if there is no one viewing recipes.
     * @param inputHandler     Input handler.
     * @param outputHandler    Output handler.
     * @param inputSupplier    Supplier of the recipe's input ingredient.
     * @param outputGetter     Gets the recipe's output when given the corresponding input.
     */
    protected MoreMachineOneInputCachedRecipe(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<HOLDER, INPUT> inputHandler, IOutputHandler<OUTPUT> outputHandler, Supplier<? extends InputIngredient<HOLDER, INPUT>> inputSupplier, Function<INPUT, OUTPUT> outputGetter) {
        super(recipe, recheckAllErrors);
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
        this.inputSupplier = inputSupplier;
        this.outputGetter = outputGetter;
        this.inputSetter = input -> this.input = input;
        this.outputSetter = output -> this.output = output;
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        CachedRecipeHelper.oneInputCalculateOperationsThisTick(tracker, inputHandler, inputSupplier, inputSetter, outputHandler, outputGetter, outputSetter);
    }

    @Override
    public boolean isInputValid() {
        INPUT input = inputHandler.getInput();
        return !inputHandler.isEmpty(input) && recipe.test(input);
    }

    @Override
    protected boolean finishProcessing(int operations, TransactionContext transaction) {
        return inputHandler.use(input, operations, transaction) &&
                outputHandler.handleOutput(output, operations, transaction);
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
        return new MoreMachineOneInputCachedRecipe<>(recipe, recheckAllErrors, inputHandler, outputHandler, recipe::getInput, recipe::getOutput);
    }
}
