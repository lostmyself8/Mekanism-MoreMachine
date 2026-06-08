package com.jerry.mekmm.api.recipes.cache;

import com.jerry.mekmm.api.recipes.StamperRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.*;

// 可能之后会拓展为额外输入不消化的通用CachedRecipe，因此参数不使用“mold”
@NothingNullByDefault
public class StamperCachedRecipe extends CachedRecipe<StamperRecipe> {

    private final IInputHandler<Item, ItemStack> inputHandler;
    private final IInputHandler<Item, ItemStack> secondaryInputHandler;
    private final IOutputHandler<ItemStackTemplate> outputHandler;
    private final Predicate<ItemStack> inputEmptyCheck;
    private final Predicate<ItemStack> secondaryInputEmptyCheck;
    private final Supplier<? extends InputIngredient<Item, ItemStack>> inputSupplier;
    private final Supplier<? extends InputIngredient<Item, ItemStack>> secondaryInputSupplier;
    private final BiFunction<ItemStack, ItemStack, ItemStackTemplate> outputGetter;
    private final Predicate<ItemStackTemplate> outputEmptyCheck;
    private final BiConsumer<ItemStack, ItemStack> inputsSetter;
    private final Consumer<ItemStackTemplate> outputSetter;

    // Note: Our inputs and outputs shouldn't be null in places they are actually used, but we mark them as nullable, so
    // we don't have to initialize them
    @Nullable
    private ItemStack input;
    @Nullable
    private ItemStack secondaryInput;
    @Nullable
    private ItemStackTemplate output;

    /**
     * @param recipe           Recipe.
     * @param recheckAllErrors Returns {@code true} if processing should be continued even if an error is hit in order
     *                         to gather all the errors. It is recommended to not
     *                         do this every tick or if there is no one viewing recipes.
     */
    protected StamperCachedRecipe(StamperRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<Item, ItemStack> inputHandler, IInputHandler<Item, ItemStack> secondaryInputHandler,
                                  IOutputHandler<ItemStackTemplate> outputHandler, Supplier<InputIngredient<Item, ItemStack>> inputSupplier, Supplier<InputIngredient<Item, ItemStack>> secondaryInputSupplier,
                                  BiFunction<ItemStack, ItemStack, ItemStackTemplate> outputGetter, Predicate<ItemStack> inputEmptyCheck, Predicate<ItemStack> secondaryInputEmptyCheck,
                                  Predicate<ItemStackTemplate> outputEmptyCheck) {
        super(recipe, recheckAllErrors);
        this.inputHandler = Objects.requireNonNull(inputHandler, "Input handler cannot be null.");
        this.secondaryInputHandler = Objects.requireNonNull(secondaryInputHandler, "Secondary input handler cannot be null.");
        this.outputHandler = Objects.requireNonNull(outputHandler, "Output handler cannot be null.");
        this.inputSupplier = Objects.requireNonNull(inputSupplier, "Input ingredient supplier cannot be null.");
        this.secondaryInputSupplier = Objects.requireNonNull(secondaryInputSupplier, "Secondary input ingredient supplier cannot be null.");
        this.outputGetter = Objects.requireNonNull(outputGetter, "Output getter cannot be null.");
        this.inputEmptyCheck = Objects.requireNonNull(inputEmptyCheck, "Input empty check cannot be null.");
        this.secondaryInputEmptyCheck = Objects.requireNonNull(secondaryInputEmptyCheck, "Secondary input empty check cannot be null.");
        this.outputEmptyCheck = Objects.requireNonNull(outputEmptyCheck, "Output empty check cannot be null.");
        this.inputsSetter = (input, secondary) -> {
            this.input = input;
            this.secondaryInput = secondary;
        };
        this.outputSetter = output -> this.output = output;
    }

    /**
     * Base implementation for handling Combiner Recipes.
     *
     * @param recipe            Recipe.
     * @param recheckAllErrors  Returns {@code true} if processing should be continued even if an error is hit in order
     *                          to gather all the errors. It is recommended to not
     *                          do this every tick or if there is no one viewing recipes.
     * @param inputHandler      Main input handler.
     * @param extraInputHandler Secondary/Extra input handler.
     * @param outputHandler     Output handler.
     */
    public static StamperCachedRecipe createCache(StamperRecipe recipe,
                                                  BooleanSupplier recheckAllErrors, IInputHandler<Item, @NotNull ItemStack> inputHandler, IInputHandler<Item, @NotNull ItemStack> extraInputHandler,
                                                  IOutputHandler<@NotNull ItemStackTemplate> outputHandler) {
        return new StamperCachedRecipe(recipe, recheckAllErrors, inputHandler, extraInputHandler, outputHandler, recipe::getInput, recipe::getMold,
                recipe::getOutput, ItemStack::isEmpty, ItemStack::isEmpty, Objects::isNull);
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        if (tracker.shouldContinueChecking()) {
            ItemStack inputA = inputHandler.getRecipeInput(inputSupplier.get());
            // Test to make sure we can even perform a single operation. This is akin to !recipe.test(inputA)
            if (inputEmptyCheck.test(inputA)) {
                // No input, we don't know if the recipe matches or not so treat it as not matching
                tracker.mismatchedRecipe();
            } else {
                ItemStack inputB = secondaryInputHandler.getRecipeInput(secondaryInputSupplier.get());
                // Test to make sure we can even perform a single operation. This is akin to !recipe.test(inputB)
                if (secondaryInputEmptyCheck.test(inputB)) {
                    // No input, we don't know if the recipe matches or not so treat it as not matching
                    tracker.mismatchedRecipe();
                } else {
                    inputsSetter.accept(inputA, inputB);
                    // Calculate the current max based on the primary input
                    inputHandler.calculateOperationsCanSupport(tracker, inputA);
                    if (tracker.shouldContinueChecking()) {
                        // Calculate the current max based on the secondary input
                        // 正常流程应该运行moldHandler.calculateOperationsCanSupport(tracker, inputB)
                        // 但这里不需要，因为模具类的输入可能始终为1，我们并不需要通过模具数量来决定并行数量
                        if (tracker.shouldContinueChecking()) {
                            ItemStackTemplate output = outputGetter.apply(inputA, inputB);
                            outputSetter.accept(output);
                            // Calculate the max based on the space in the output
                            outputHandler.calculateOperationsCanSupport(tracker, output);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isInputValid() {
        ItemStack input = inputHandler.getInput();
        if (inputEmptyCheck.test(input)) {
            return false;
        }
        ItemStack secondaryInput = secondaryInputHandler.getInput();
        return !secondaryInputEmptyCheck.test(secondaryInput) && recipe.test(input, secondaryInput);
    }

    @Override
    protected boolean finishProcessing(int operations, TransactionContext transaction) {
        if (input != null && secondaryInput != null && output != null && !inputEmptyCheck.test(input) && !secondaryInputEmptyCheck.test(secondaryInput) &&
                !outputEmptyCheck.test(output)) {
            boolean usedInput = inputHandler.use(input, operations, transaction);
            // secondaryInputHandler.use(secondaryInput, operations);
            return usedInput && outputHandler.handleOutput(output, operations, transaction);
        }
        return false;
    }
}
