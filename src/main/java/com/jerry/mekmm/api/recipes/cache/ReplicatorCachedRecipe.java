package com.jerry.mekmm.api.recipes.cache;

import com.jerry.mekmm.api.recipes.basic.BasicFluidChemicalToFluidRecipe;
import com.jerry.mekmm.api.recipes.basic.MMBasicChemicalChemicalToChemicalRecipe;
import com.jerry.mekmm.api.recipes.basic.MMBasicItemStackChemicalToItemStackRecipe;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalStackTemplate;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipeHelper;
import mekanism.api.recipes.ingredients.InputIngredient;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.core.TypedInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.function.*;

@NullMarked
public class ReplicatorCachedRecipe<HOLDER, INPUT extends TypedInstance<HOLDER>, OUTPUT, RECIPE extends MekanismRecipe<?> & BiPredicate<INPUT, ChemicalStack>> extends CachedRecipe<RECIPE> {

    private final IInputHandler<HOLDER, INPUT> inputHandler;
    private final IInputHandler<Chemical, ChemicalStack> secondaryInputHandler;
    private final IOutputHandler<OUTPUT> outputHandler;
    private final Predicate<INPUT> inputEmptyCheck;
    private final Predicate<ChemicalStack> secondaryInputEmptyCheck;
    private final Supplier<? extends InputIngredient<HOLDER, INPUT>> inputSupplier;
    private final Supplier<? extends InputIngredient<Chemical, ChemicalStack>> secondaryInputSupplier;
    private final BiFunction<INPUT, ChemicalStack, OUTPUT> outputGetter;
    private final Predicate<OUTPUT> outputEmptyCheck;
    private final BiConsumer<INPUT, ChemicalStack> inputsSetter;
    private final Consumer<OUTPUT> outputSetter;

    // Note: Our inputs and outputs shouldn't be null in places they are actually used, but we mark them as nullable, so
    // we don't have to initialize them
    @Nullable
    private INPUT input;
    @Nullable
    private ChemicalStack secondaryInput;
    @Nullable
    private OUTPUT output;

    /**
     * @param recipe           Recipe.
     * @param recheckAllErrors Returns {@code true} if processing should be continued even if an error is hit in order
     *                         to gather all the errors. It is recommended to not
     *                         do this every tick or if there is no one viewing recipes.
     */
    private ReplicatorCachedRecipe(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<HOLDER, INPUT> inputHandler, IInputHandler<Chemical, ChemicalStack> secondaryInputHandler,
                                   IOutputHandler<OUTPUT> outputHandler, Supplier<InputIngredient<HOLDER, INPUT>> inputSupplier, Supplier<InputIngredient<Chemical, ChemicalStack>> secondaryInputSupplier,
                                   BiFunction<INPUT, ChemicalStack, OUTPUT> outputGetter, Predicate<INPUT> inputEmptyCheck, Predicate<ChemicalStack> secondaryInputEmptyCheck,
                                   Predicate<OUTPUT> outputEmptyCheck) {
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

    public static <RECIPE extends MMBasicItemStackChemicalToItemStackRecipe> ReplicatorCachedRecipe<Item, ItemStack, ItemStackTemplate, RECIPE> createItemReplicator(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<Item, @NotNull ItemStack> itemInputHandler,
                                                                                                                                                                     IInputHandler<Chemical, @NotNull ChemicalStack> chemicalInputHandler, IOutputHandler<@NotNull ItemStackTemplate> outputHandler) {
        return new ReplicatorCachedRecipe<>(recipe, recheckAllErrors, itemInputHandler, chemicalInputHandler, outputHandler, recipe::getItemInput, recipe::getChemicalInput,
                recipe::getOutput, ItemStack::isEmpty, ChemicalStack::isEmpty, Objects::isNull);
    }

    public static <RECIPE extends BasicFluidChemicalToFluidRecipe> ReplicatorCachedRecipe<Fluid, FluidStack, FluidStackTemplate, RECIPE> createFluidReplicator(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<Fluid, @NotNull FluidStack> fluidInputHandler,
                                                                                                                                                               IInputHandler<Chemical, @NotNull ChemicalStack> chemicalInputHandler, IOutputHandler<@NotNull FluidStackTemplate> outputHandler) {
        return new ReplicatorCachedRecipe<>(recipe, recheckAllErrors, fluidInputHandler, chemicalInputHandler, outputHandler, recipe::getFluidInput, recipe::getChemicalInput,
                recipe::getOutput, FluidStack::isEmpty, ChemicalStack::isEmpty, Objects::isNull);
    }

    public static <RECIPE extends MMBasicChemicalChemicalToChemicalRecipe> ReplicatorCachedRecipe<Chemical, ChemicalStack, ChemicalStackTemplate, RECIPE> createChemicalReplicator(RECIPE recipe, BooleanSupplier recheckAllErrors, IInputHandler<Chemical, @NotNull ChemicalStack> firstInputHandler,
                                                                                                                                                                                   IInputHandler<Chemical, @NotNull ChemicalStack> secondaryInputHandler, IOutputHandler<@NotNull ChemicalStackTemplate> outputHandler) {
        return new ReplicatorCachedRecipe<>(recipe, recheckAllErrors, firstInputHandler, secondaryInputHandler, outputHandler, recipe::getLeftInput, recipe::getRightInput,
                recipe::getOutput, ChemicalStack::isEmpty, ChemicalStack::isEmpty, Objects::isNull);
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        CachedRecipeHelper.twoInputCalculateOperationsThisTick(tracker, inputHandler, inputSupplier, secondaryInputHandler, secondaryInputSupplier, inputsSetter,
                outputHandler, outputGetter, outputSetter);
    }

    @Override
    public boolean isInputValid() {
        INPUT input = inputHandler.getInput();
        if (inputEmptyCheck.test(input)) {
            return false;
        }
        ChemicalStack secondaryInput = secondaryInputHandler.getInput();
        return !secondaryInputEmptyCheck.test(secondaryInput) && recipe.test(input, secondaryInput);
    }

    @Override
    protected boolean finishProcessing(int operations, TransactionContext transaction) {
        // Validate something didn't go horribly wrong
        if (input != null && secondaryInput != null && output != null && !inputEmptyCheck.test(input) && !secondaryInputEmptyCheck.test(secondaryInput) &&
                !outputEmptyCheck.test(output)) {
            // inputHandler.use(input, operations);
            return secondaryInputHandler.use(secondaryInput, operations, transaction) &&
                    outputHandler.handleOutput(output, operations, transaction);
        }
        return false;
    }
}
