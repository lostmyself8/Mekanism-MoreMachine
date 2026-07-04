package com.jerry.mekmm.api.recipes.cache;

import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.core.TypedInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.function.*;

@NullMarked
public class ThreeInputCachedRecipe<HOLDER_A, INPUT_A extends TypedInstance<HOLDER_A>, HOLDER_B, INPUT_B extends TypedInstance<HOLDER_B>, HOLDER_C, INPUT_C extends TypedInstance<HOLDER_C>, OUTPUT, RECIPE extends MekanismRecipe<?> & TriPredicate<INPUT_A, INPUT_B, INPUT_C>> extends CachedRecipe<RECIPE> {

    protected final IInputHandler<HOLDER_A, INPUT_A> primaryInputHandler;
    protected final IInputHandler<HOLDER_B, INPUT_B> secondaryInputHandler;
    protected final IInputHandler<HOLDER_C, INPUT_C> tertiaryInputHandler;
    protected final IOutputHandler<OUTPUT> outputHandler;
    protected final Supplier<? extends InputIngredient<HOLDER_A, INPUT_A>> primaryInputSupplier;
    protected final Supplier<? extends InputIngredient<HOLDER_B, INPUT_B>> secondaryInputSupplier;
    protected final Supplier<? extends InputIngredient<HOLDER_C, INPUT_C>> tertiaryInputSupplier;
    protected final TriFunction<INPUT_A, INPUT_B, INPUT_C, OUTPUT> outputGetter;
    protected final Predicate<INPUT_A> primaryEmptyCheck;
    protected final Predicate<INPUT_B> secondaryInputEmptyCheck;
    protected final Predicate<INPUT_C> tertiaryInputEmptyCheck;
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

    protected ThreeInputCachedRecipe(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<HOLDER_A, INPUT_A> primaryInputHandler,
                                     IInputHandler<HOLDER_B, INPUT_B> secondaryInputHandler, IInputHandler<HOLDER_C, INPUT_C> tertiaryInputHandler, IOutputHandler<OUTPUT> outputHandler,
                                     Supplier<? extends InputIngredient<HOLDER_A, INPUT_A>> primaryInputSupplier, Supplier<? extends InputIngredient<HOLDER_B, INPUT_B>> secondaryInputSupplier,
                                     Supplier<? extends InputIngredient<HOLDER_C, INPUT_C>> tertiaryInputSupplier, TriFunction<INPUT_A, INPUT_B, INPUT_C, OUTPUT> outputGetter,
                                     Predicate<INPUT_A> primaryEmptyCheck, Predicate<INPUT_B> secondaryInputEmptyCheck, Predicate<INPUT_C> tertiaryInputEmptyCheck, Predicate<OUTPUT> outputEmptyCheck) {
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
        if (tracker.shouldContinueChecking()) {
            INPUT_A inputA = primaryInputHandler.getRecipeInput(primaryInputSupplier.get());
            if (primaryEmptyCheck.test(inputA)) {
                tracker.mismatchedRecipe();
                return;
            }
            INPUT_B inputB = secondaryInputHandler.getRecipeInput(secondaryInputSupplier.get());
            if (secondaryInputEmptyCheck.test(inputB)) {
                tracker.mismatchedRecipe();
                return;
            }
            INPUT_C inputC = tertiaryInputHandler.getRecipeInput(tertiaryInputSupplier.get());
            if (tertiaryInputEmptyCheck.test(inputC)) {
                tracker.mismatchedRecipe();
                return;
            }
            inputsSetter.accept(inputA, inputB, inputC);
            primaryInputHandler.calculateOperationsCanSupport(tracker, inputA);
            if (tracker.shouldContinueChecking()) {
                secondaryInputHandler.calculateOperationsCanSupport(tracker, inputB);
                if (tracker.shouldContinueChecking()) {
                    tertiaryInputHandler.calculateOperationsCanSupport(tracker, inputC);
                    if (tracker.shouldContinueChecking()) {
                        OUTPUT output = outputGetter.apply(inputA, inputB, inputC);
                        outputSetter.accept(output);
                        outputHandler.calculateOperationsCanSupport(tracker, output);
                    }
                }
            }
        }
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
    protected boolean finishProcessing(int operations, TransactionContext transaction) {
        if (primaryInput != null && secondaryInput != null && tertiaryInput != null && output != null && !primaryEmptyCheck.test(primaryInput) &&
                !secondaryInputEmptyCheck.test(secondaryInput) && !tertiaryInputEmptyCheck.test(tertiaryInput) && !outputEmptyCheck.test(output)) {
            return primaryInputHandler.use(primaryInput, operations, transaction) &&
                    secondaryInputHandler.use(secondaryInput, operations, transaction) &&
                    tertiaryInputHandler.use(tertiaryInput, operations, transaction) &&
                    outputHandler.handleOutput(output, operations, transaction);
        }
        return false;
    }

    public static <RECIPE extends TripleItemToItemRecipe> ThreeInputCachedRecipe<Item, ItemStack, Item, ItemStack, Item, ItemStack, ItemStackTemplate, RECIPE> tripleItemToItem(
                                                                                                                                                                                RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<Item, ItemStack> primaryInputHandler,
                                                                                                                                                                                IInputHandler<Item, ItemStack> secondaryInputHandler, IInputHandler<Item, ItemStack> tertiaryInputHandler,
                                                                                                                                                                                IOutputHandler<ItemStackTemplate> outputHandler) {
        return new ThreeInputCachedRecipe<>(recipe, recheckAllErrors, primaryInputHandler, secondaryInputHandler, tertiaryInputHandler, outputHandler,
                recipe::getFirstInput, recipe::getSecondInput, recipe::getThirdInput, recipe::getOutput, ItemStack::isEmpty, ItemStack::isEmpty, ItemStack::isEmpty, Objects::isNull);
    }
}
