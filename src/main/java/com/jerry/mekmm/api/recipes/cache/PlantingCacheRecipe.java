package com.jerry.mekmm.api.recipes.cache;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.PlantingRecipe.PlantingStationRecipeOutput;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToItemStackCachedRecipe.ChemicalUsageMultiplier;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.LongConsumer;
import java.util.function.Predicate;

@NothingNullByDefault
public class PlantingCacheRecipe extends CachedRecipe<PlantingRecipe> {

    private final Predicate<PlantingStationRecipeOutput> outputEmptyCheck;
    private final IOutputHandler<@NotNull PlantingStationRecipeOutput> outputHandler;
    private final IInputHandler<@NotNull ItemStack> itemInputHandler;
    private final ILongInputHandler<GasStack> gasInputHandler;
    private final ChemicalUsageMultiplier gasUsage;
    private final LongConsumer gasUsedSoFarChanged;
    private long gasUsageMultiplier;
    private long gasUsedSoFar;

    private ItemStack recipeItem = ItemStack.EMPTY;
    // Note: Shouldn't be null in places it is actually used, but we mark it as nullable, so we don't have to initialize
    // it
    @Nullable
    private GasStack recipeGas;
    @Nullable
    private PlantingStationRecipeOutput output;

    /**
     * @param recipe              Recipe.
     * @param recheckAllErrors    Returns {@code true} if processing should be continued even if an error is hit in
     *                            order to gather all the errors. It is recommended
     *                            to not do this every tick or if there is no one viewing recipes.
     * @param itemInputHandler    Item input handler.
     * @param gasInputHandler     Gas input handler.
     * @param gasUsage            Gas usage multiplier.
     * @param gasUsedSoFarChanged Called when the number gas usage so far changes.
     * @param outputHandler       Output handler.
     */
    public PlantingCacheRecipe(PlantingRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<@NotNull ItemStack> itemInputHandler,
                               ILongInputHandler<GasStack> gasInputHandler, ChemicalUsageMultiplier gasUsage, LongConsumer gasUsedSoFarChanged,
                               IOutputHandler<@NotNull PlantingStationRecipeOutput> outputHandler, Predicate<PlantingStationRecipeOutput> outputEmptyCheck) {
        super(recipe, recheckAllErrors);
        this.itemInputHandler = Objects.requireNonNull(itemInputHandler, "Item input handler cannot be null.");
        this.gasInputHandler = Objects.requireNonNull(gasInputHandler, "Gas input handler cannot be null.");
        this.gasUsage = Objects.requireNonNull(gasUsage, "Gas usage cannot be null.");
        this.gasUsedSoFarChanged = Objects.requireNonNull(gasUsedSoFarChanged, "Gas used so far changed handler cannot be null.");
        this.outputHandler = Objects.requireNonNull(outputHandler, "Output handler cannot be null.");
        this.outputEmptyCheck = Objects.requireNonNull(outputEmptyCheck, "Output empty check cannot be null.");
    }

    /**
     * Sets the amount of gas that have been used so far. This is used to allow {@link CachedRecipe} holders to persist
     * and load recipe progress.
     *
     * @param gasUsedSoFar Amount of gas that has been used so far.
     */
    public void loadSavedUsageSoFar(long gasUsedSoFar) {
        if (gasUsedSoFar > 0) {
            this.gasUsedSoFar = gasUsedSoFar;
        }
    }

    @Override
    protected void setupVariableValues() {
        gasUsageMultiplier = Math.max(gasUsage.getToUse(gasUsedSoFar, getOperatingTicks()), 0);
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        if (tracker.shouldContinueChecking()) {
            recipeItem = itemInputHandler.getRecipeInput(recipe.getItemInput());
            // Test to make sure we can even perform a single operation. This is akin to !recipe.test(inputItem)
            if (recipeItem.isEmpty()) {
                // No input, we don't know if the recipe matches or not so treat it as not matching
                tracker.mismatchedRecipe();
            } else {
                // Now check the gas input
                recipeGas = gasInputHandler.getRecipeInput(recipe.getGasInput());
                // Test to make sure we can even perform a single operation. This is akin to !recipe.test(inputGas)
                if (recipeGas.isEmpty()) {
                    // TODO: Allow processing when secondary gas is empty if the usage multiplier is zero?
                    // Note: we don't force reset based on secondary per tick usages
                    tracker.updateOperations(0);
                    if (!tracker.shouldContinueChecking()) {
                        // If we shouldn't continue checking exit, otherwise see if there is an error with the item
                        // though due to not having a gas we won't be able to check if there is errors with the output
                        return;
                    }
                }
                // Calculate the current max based on the item input
                itemInputHandler.calculateOperationsCanSupport(tracker, recipeItem);
                if (!recipeGas.isEmpty() && tracker.shouldContinueChecking()) {
                    // Calculate the current max based on the gas input, and the given usage amount
                    gasInputHandler.calculateOperationsCanSupport(tracker, recipeGas, gasUsageMultiplier);
                    if (tracker.shouldContinueChecking()) {
                        output = recipe.getOutput(recipeItem, recipeGas);
                        // Calculate the max based on the space in the output
                        outputHandler.calculateOperationsCanSupport(tracker, output);
                    }
                }
            }
        }
    }

    @Override
    public boolean isInputValid() {
        ItemStack itemInput = itemInputHandler.getInput();
        if (!itemInput.isEmpty()) {
            GasStack gasStack = gasInputHandler.getInput();
            // Ensure that we check that we have enough for that the recipe matches *and* also that we have enough for
            // how much we need to use
            if (!gasStack.isEmpty() && recipe.test(itemInput, gasStack)) {
                GasStack recipeGas = gasInputHandler.getRecipeInput(recipe.getGasInput());
                return !recipeGas.isEmpty() && gasStack.getAmount() >= recipeGas.getAmount();
            }
        }
        return false;
    }

    @Override
    protected void useResources(int operations) {
        super.useResources(operations);
        if (gasUsageMultiplier <= 0) {
            // We don't need to use the gas
            return;
        } else if (recipeGas == null || recipeGas.isEmpty()) {
            // Something went wrong, this if should never really be true if we are in useResources
            return;
        }
        // Note: We should have enough because of the getOperationsThisTick call to reduce it based on amounts
        long toUse = operations * gasUsageMultiplier;
        gasInputHandler.use(recipeGas, toUse);
        gasUsedSoFar += toUse;
        gasUsedSoFarChanged.accept(gasUsedSoFar);
    }

    @Override
    protected void resetCache() {
        super.resetCache();
        gasUsedSoFar = 0;
        gasUsedSoFarChanged.accept(gasUsedSoFar);
    }

    @Override
    protected void finishProcessing(int operations) {
        // Validate something didn't go horribly wrong
        if (recipeGas != null && output != null && !recipeItem.isEmpty() && !recipeGas.isEmpty() && !outputEmptyCheck.test(output)) {
            itemInputHandler.use(recipeItem, operations);
            if (gasUsageMultiplier > 0) {
                gasInputHandler.use(recipeGas, operations * gasUsageMultiplier);
            }
            outputHandler.handleOutput(output, operations);
        }
    }

    public static PlantingCacheRecipe create(PlantingRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<@NotNull ItemStack> itemInputHandler,
                                             ILongInputHandler<@NotNull GasStack> gasInputHandler, ChemicalUsageMultiplier chemicalUsage,
                                             LongConsumer chemicalUsedSoFarChanged, IOutputHandler<PlantingStationRecipeOutput> outputHandler) {
        return new PlantingCacheRecipe(recipe, recheckAllErrors, itemInputHandler, gasInputHandler, chemicalUsage, chemicalUsedSoFarChanged, outputHandler, ConstantPredicates.alwaysFalse());
    }
}
