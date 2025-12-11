package com.jerry.mekmm.api.recipes.cache;

import com.jerry.mekmm.api.recipes.PlantingRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.recipes.SawmillRecipe.ChanceOutput;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToObjectCachedRecipe.ChemicalUsageMultiplier;
import mekanism.api.recipes.cache.TwoInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.*;

/**
 * Base class to help implement handling of item chemical to object recipes. Unlike
 * {@link TwoInputCachedRecipe#itemChemicalToItem} this variant has constant
 * chemical usage.
 *
 * @since 10.7.0
 */
@NothingNullByDefault
public class PlantingCachedRecipe extends CachedRecipe<PlantingRecipe> {

    protected final Predicate<ChanceOutput> outputEmptyCheck;
    protected final IOutputHandler<@NotNull ChanceOutput> outputHandler;
    protected final IInputHandler<@NotNull ItemStack> itemInputHandler;
    protected final ILongInputHandler<ChemicalStack> chemicalInputHandler;
    protected final ChemicalUsageMultiplier chemicalUsage;
    protected final LongConsumer chemicalUsedSoFarChanged;
    protected long chemicalUsageMultiplier;
    protected long chemicalUsedSoFar;

    protected ItemStack recipeItem = ItemStack.EMPTY;
    // Note: Shouldn't be null in places it is actually used, but we mark it as nullable, so we don't have to initialize
    // it
    @Nullable
    protected ChemicalStack recipeChemical;
    @Nullable
    protected ChanceOutput output;

    /**
     * @param recipe                   Recipe.
     * @param recheckAllErrors         Returns {@code true} if processing should be continued even if an error is hit in
     *                                 order to gather all the errors. It is recommended
     *                                 to not do this every tick or if there is no one viewing recipes.
     * @param itemInputHandler         Item input handler.
     * @param chemicalInputHandler     Chemical input handler.
     * @param chemicalUsage            Chemical usage multiplier.
     * @param chemicalUsedSoFarChanged Called when the number chemical usage so far changes.
     * @param outputHandler            Output handler.
     */
    public PlantingCachedRecipe(PlantingRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<@NotNull ItemStack> itemInputHandler,
                                ILongInputHandler<ChemicalStack> chemicalInputHandler, ChemicalUsageMultiplier chemicalUsage, LongConsumer chemicalUsedSoFarChanged,
                                IOutputHandler<@NotNull ChanceOutput> outputHandler, Predicate<ChanceOutput> outputEmptyCheck) {
        super(recipe, recheckAllErrors);
        this.itemInputHandler = Objects.requireNonNull(itemInputHandler, "Item input handler cannot be null.");
        this.chemicalInputHandler = Objects.requireNonNull(chemicalInputHandler, "Chemical input handler cannot be null.");
        this.chemicalUsage = Objects.requireNonNull(chemicalUsage, "Chemical usage cannot be null.");
        this.chemicalUsedSoFarChanged = Objects.requireNonNull(chemicalUsedSoFarChanged, "Chemical used so far changed handler cannot be null.");
        this.outputHandler = Objects.requireNonNull(outputHandler, "Output handler cannot be null.");
        this.outputEmptyCheck = Objects.requireNonNull(outputEmptyCheck, "Output empty check cannot be null.");
    }

    /**
     * Sets the amount of chemical that have been used so far. This is used to allow {@link CachedRecipe} holders to
     * persist and load recipe progress.
     *
     * @param chemicalUsedSoFar Amount of chemical that has been used so far.
     */
    public void loadSavedUsageSoFar(long chemicalUsedSoFar) {
        if (chemicalUsedSoFar > 0) {
            this.chemicalUsedSoFar = chemicalUsedSoFar;
        }
    }

    @Override
    protected void setupVariableValues() {
        chemicalUsageMultiplier = Math.max(chemicalUsage.getToUse(chemicalUsedSoFar, getOperatingTicks()), 0);
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
                // Now check the chemical input
                recipeChemical = chemicalInputHandler.getRecipeInput(recipe.getChemicalInput());
                // Test to make sure we can even perform a single operation. This is akin to !recipe.test(inputChemical)
                if (recipeChemical.isEmpty()) {
                    // TODO: Allow processing when secondary chemical is empty if the usage multiplier is zero?
                    // Note: we don't force reset based on secondary per tick usages
                    tracker.updateOperations(0);
                    if (!tracker.shouldContinueChecking()) {
                        // If we shouldn't continue checking exit, otherwise see if there is an error with the item
                        // though due to not having a chemical we won't be able to check if there is errors with the
                        // output
                        return;
                    }
                }
                // Calculate the current max based on the item input
                itemInputHandler.calculateOperationsCanSupport(tracker, recipeItem);
                if (!recipeChemical.isEmpty() && tracker.shouldContinueChecking()) {
                    // Calculate the current max based on the chemical input, and the given usage amount
                    chemicalInputHandler.calculateOperationsCanSupport(tracker, recipeChemical, chemicalUsageMultiplier);
                    if (tracker.shouldContinueChecking()) {
                        output = recipe.getOutput(recipeItem, recipeChemical);
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
            ChemicalStack chemicalStack = chemicalInputHandler.getInput();
            // Ensure that we check that we have enough for that the recipe matches *and* also that we have enough for
            // how much we need to use
            if (!chemicalStack.isEmpty() && recipe.test(itemInput, chemicalStack)) {
                ChemicalStack recipeChemical = chemicalInputHandler.getRecipeInput(recipe.getChemicalInput());
                return !recipeChemical.isEmpty() && chemicalStack.getAmount() >= recipeChemical.getAmount();
            }
        }
        return false;
    }

    @Override
    protected void useResources(int operations) {
        super.useResources(operations);
        if (chemicalUsageMultiplier <= 0) {
            // We don't need to use the chemical
            return;
        } else if (recipeChemical == null || recipeChemical.isEmpty()) {
            // Something went wrong, this if should never really be true if we are in useResources
            return;
        }
        // Note: We should have enough because of the getOperationsThisTick call to reduce it based on amounts
        long toUse = operations * chemicalUsageMultiplier;
        chemicalInputHandler.use(recipeChemical, toUse);
        chemicalUsedSoFar += toUse;
        chemicalUsedSoFarChanged.accept(chemicalUsedSoFar);
    }

    @Override
    protected void resetCache() {
        super.resetCache();
        chemicalUsedSoFar = 0;
        chemicalUsedSoFarChanged.accept(chemicalUsedSoFar);
    }

    @Override
    protected void finishProcessing(int operations) {
        if (recipeChemical != null && output != null && !recipeItem.isEmpty() && !recipeChemical.isEmpty() && !outputEmptyCheck.test(output)) {
            if (chemicalUsageMultiplier > 0) {
                chemicalInputHandler.use(recipeChemical, operations * chemicalUsageMultiplier);
            }
            outputHandler.handleOutput(output, operations);
        }
    }

    public static PlantingCachedRecipe planting(PlantingRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<@NotNull ItemStack> itemInputHandler,
                                                ILongInputHandler<@NotNull ChemicalStack> chemicalInputHandler, ChemicalUsageMultiplier chemicalUsage,
                                                LongConsumer chemicalUsedSoFarChanged, IOutputHandler<ChanceOutput> outputHandler) {
        return new PlantingCachedRecipe(recipe, recheckAllErrors, itemInputHandler, chemicalInputHandler, chemicalUsage,
                chemicalUsedSoFarChanged, outputHandler, ConstantPredicates.alwaysFalse());
    }
}
