package com.jerry.mekmm.api.recipes.cache;

import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriPredicate;

import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.*;

@NothingNullByDefault
public class ThreeInputCachedRecipe<INPUT_A, INPUT_B, INPUT_C, OUTPUT, RECIPE extends MekanismRecipe<?> & TriPredicate<INPUT_A, INPUT_B, INPUT_C>> extends CachedRecipe<RECIPE> {

    protected final IInputHandler<INPUT_A> primaryInputHandler;
    protected final IInputHandler<INPUT_B> secondaryInputHandler;
    protected final IInputHandler<INPUT_C> tertiaryInputHandler;
    protected final IOutputHandler<OUTPUT> outputHandler;
    protected final Predicate<INPUT_A> primaryEmptyCheck;
    protected final Predicate<INPUT_B> secondaryInputEmptyCheck;
    protected final Predicate<INPUT_C> tertiaryInputEmptyCheck;
    protected final Supplier<? extends InputIngredient<INPUT_A>> primaryInputSupplier;
    protected final Supplier<? extends InputIngredient<INPUT_B>> secondaryInputSupplier;
    protected final Supplier<? extends InputIngredient<INPUT_C>> tertiaryInputSupplier;
    protected final TriFunction<INPUT_A, INPUT_B, INPUT_C, OUTPUT> outputGetter;
    protected final Predicate<OUTPUT> outputEmptyCheck;
    protected final TriConsumer<INPUT_A, INPUT_B, INPUT_C> inputsSetter;
    protected final Consumer<OUTPUT> outputSetter;

    @Nullable
    protected INPUT_A primaryInput;
    @Nullable
    protected INPUT_B secondaryInput;
    @Nullable
    protected INPUT_C tertiaryInput;
    @Nullable
    protected OUTPUT output;

    /**
     * @param recipe           Recipe.
     * @param recheckAllErrors Returns {@code true} if processing should be continued even if an error is hit in order
     *                         to gather all the errors. It is recommended to not
     *                         do this every tick or if there is no one viewing recipes.
     */
    protected ThreeInputCachedRecipe(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<INPUT_A> primaryInputHandler, IInputHandler<INPUT_B> secondaryInputHandler, IInputHandler<INPUT_C> tertiaryInputHandler,
                                     IOutputHandler<OUTPUT> outputHandler, Supplier<? extends InputIngredient<INPUT_A>> primaryInputSupplier, Supplier<? extends InputIngredient<INPUT_B>> secondaryInputSupplier,
                                     Supplier<? extends InputIngredient<INPUT_C>> tertiaryInputSupplier, TriFunction<INPUT_A, INPUT_B, INPUT_C, OUTPUT> outputGetter, Predicate<INPUT_A> primaryEmptyCheck,
                                     Predicate<INPUT_B> secondaryInputEmptyCheck, Predicate<INPUT_C> tertiaryInputEmptyCheck, Predicate<OUTPUT> outputEmptyCheck) {
        super(recipe, recheckAllErrors);
        this.primaryInputHandler = Objects.requireNonNull(primaryInputHandler, "Primary input handler cannot be null.");
        this.secondaryInputHandler = Objects.requireNonNull(secondaryInputHandler, "Secondary input handler cannot be null.");
        this.tertiaryInputHandler = Objects.requireNonNull(tertiaryInputHandler, "Tertiary input handler cannot be null.");
        this.outputHandler = Objects.requireNonNull(outputHandler, "Output handler cannot be null.");
        this.primaryInputSupplier = Objects.requireNonNull(primaryInputSupplier, "Primary input ingredient supplier cannot be null.");
        this.secondaryInputSupplier = Objects.requireNonNull(secondaryInputSupplier, "Secondary input ingredient supplier cannot be null.");
        this.tertiaryInputSupplier = Objects.requireNonNull(tertiaryInputSupplier, "Tertiary input ingredient supplier cannot be null.");
        this.outputGetter = Objects.requireNonNull(outputGetter, "Output getter cannot be null.");
        this.primaryEmptyCheck = Objects.requireNonNull(primaryEmptyCheck, "Primary input empty check cannot be null.");
        this.secondaryInputEmptyCheck = Objects.requireNonNull(secondaryInputEmptyCheck, "Secondary input empty check cannot be null.");
        this.tertiaryInputEmptyCheck = Objects.requireNonNull(tertiaryInputEmptyCheck, "Tertiary input empty check cannot be null.");
        this.outputEmptyCheck = Objects.requireNonNull(outputEmptyCheck, "Output empty check cannot be null.");
        this.inputsSetter = (primary, secondary, tertiary) -> {
            this.primaryInput = primary;
            this.secondaryInput = secondary;
            this.tertiaryInput = tertiary;
        };
        this.outputSetter = output -> this.output = output;
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        threeInputCalculateOperationsThisTick(tracker, primaryInputHandler, primaryInputSupplier, secondaryInputHandler, secondaryInputSupplier, tertiaryInputHandler,
                tertiaryInputSupplier, inputsSetter, outputHandler, outputGetter, outputSetter, primaryEmptyCheck, secondaryInputEmptyCheck, tertiaryInputEmptyCheck);
    }

    @Override
    public boolean isInputValid() {
        INPUT_A primaryInput = primaryInputHandler.getInput();
        if (primaryEmptyCheck.test(primaryInput)) {
            return false;
        }
        INPUT_B secondaryInput = secondaryInputHandler.getInput();
        if (secondaryInputEmptyCheck.test(secondaryInput)) {
            return false;
        }
        INPUT_C tertiaryInput = tertiaryInputHandler.getInput();
        return !tertiaryInputEmptyCheck.test(tertiaryInput) && recipe.test(primaryInput, secondaryInput, tertiaryInput);
    }

    @Override
    protected void finishProcessing(int operations) {
        if (primaryInput != null && secondaryInput != null && tertiaryInput != null && output != null && !primaryEmptyCheck.test(primaryInput) &&
                !secondaryInputEmptyCheck.test(secondaryInput) && !tertiaryInputEmptyCheck.test(tertiaryInput) && !outputEmptyCheck.test(output)) {
            primaryInputHandler.use(primaryInput, operations);
            secondaryInputHandler.use(secondaryInput, operations);
            tertiaryInputHandler.use(tertiaryInput, operations);
            outputHandler.handleOutput(output, operations);
        }
    }

    private static <INPUT_A, INPUT_B, INPUT_C, OUTPUT> void threeInputCalculateOperationsThisTick(OperationTracker tracker, IInputHandler<INPUT_A> inputAHandler,
                                                                                                  Supplier<? extends InputIngredient<INPUT_A>> inputAIngredient, IInputHandler<INPUT_B> inputBHandler, Supplier<? extends InputIngredient<INPUT_B>> inputBIngredient,
                                                                                                  IInputHandler<INPUT_C> inputCHandler, Supplier<? extends InputIngredient<INPUT_C>> inputCIngredient, TriConsumer<INPUT_A, INPUT_B, INPUT_C> inputsSetter,
                                                                                                  IOutputHandler<OUTPUT> outputHandler, TriFunction<INPUT_A, INPUT_B, INPUT_C, OUTPUT> outputGetter, Consumer<OUTPUT> outputSetter,
                                                                                                  Predicate<INPUT_A> emptyCheckA, Predicate<INPUT_B> emptyCheckB, Predicate<INPUT_C> emptyCheckC) {
        if (tracker.shouldContinueChecking()) {
            INPUT_A inputA = inputAHandler.getRecipeInput(inputAIngredient.get());
            if (emptyCheckA.test(inputA)) {
                tracker.mismatchedRecipe();
            } else {
                INPUT_B inputB = inputBHandler.getRecipeInput(inputBIngredient.get());
                if (emptyCheckB.test(inputB)) {
                    tracker.mismatchedRecipe();
                } else {
                    INPUT_C inputC = inputCHandler.getRecipeInput(inputCIngredient.get());
                    if (emptyCheckC.test(inputC)) {
                        tracker.mismatchedRecipe();
                    } else {
                        inputsSetter.accept(inputA, inputB, inputC);
                        inputAHandler.calculateOperationsCanSupport(tracker, inputA);
                        if (tracker.shouldContinueChecking()) {
                            inputBHandler.calculateOperationsCanSupport(tracker, inputB);
                            if (tracker.shouldContinueChecking()) {
                                inputCHandler.calculateOperationsCanSupport(tracker, inputC);
                                if (tracker.shouldContinueChecking()) {
                                    OUTPUT output = outputGetter.apply(inputA, inputB, inputC);
                                    outputSetter.accept(output);
                                    outputHandler.calculateOperationsCanSupport(tracker, output);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static <RECIPE extends TripleItemToItemRecipe> ThreeInputCachedRecipe<ItemStack, ItemStack, ItemStack, ItemStack, RECIPE> TripleItemToItem(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<@NotNull ItemStack> primaryInputHandler,
                                                                                                                                                      IInputHandler<@NotNull ItemStack> secondaryInputHandler, IInputHandler<@NotNull ItemStack> tertiaryInputHandler,
                                                                                                                                                      IOutputHandler<@NotNull ItemStack> outputHandler) {
        return new ThreeInputCachedRecipe<>(recipe, recheckAllErrors, primaryInputHandler, secondaryInputHandler, tertiaryInputHandler, outputHandler, recipe::getFirstInput,
                recipe::getSecondInput, recipe::getSecondInput, recipe::getOutput, ConstantPredicates.ITEM_EMPTY, ConstantPredicates.ITEM_EMPTY, ConstantPredicates.ITEM_EMPTY,
                ConstantPredicates.ITEM_EMPTY);
    }
}
