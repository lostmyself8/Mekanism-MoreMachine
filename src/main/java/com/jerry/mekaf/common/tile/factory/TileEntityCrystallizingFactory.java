package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.tile.factory.base.TileEntityChemicalToItemFactory;
import com.jerry.mekaf.common.upgrade.ChemicalToItemUpgradeData;

import mekanism.api.chemical.ChemicalResource;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ChemicalCrystallizerRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tile.component.TileComponentEjector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.transfer.item.ItemResource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityCrystallizingFactory extends TileEntityChemicalToItemFactory<ChemicalCrystallizerRecipe> implements ChemicalRecipeLookupHandler<ChemicalCrystallizerRecipe> {

    protected static final TriPredicate<ChemicalCrystallizerRecipe, ChemicalResource, ItemResource> OUTPUT_CHECK = (recipe, input, output) -> output.isEmpty() || output.matches(recipe.getOutput(input));
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(RecipeError.NOT_ENOUGH_ENERGY);

    public TileEntityCrystallizingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ChemicalCrystallizerRecipe> cached, @NotNull ChemicalResource stack) {
        return cached != null && cached.getRecipe().getInput().testType(stack);
    }

    @Override
    protected @Nullable ChemicalCrystallizerRecipe findRecipe(int process, @NotNull ChemicalResource fallbackInput, @NotNull IInventorySlot outputSlot) {
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, outputSlot.resource(), OUTPUT_CHECK);
    }

    @Override
    public boolean isChemicalValidForTank(@NotNull ChemicalResource stack) {
        return containsRecipe(stack);
    }

    @Override
    public boolean isValidInputChemical(@NotNull ChemicalResource stack) {
        return containsRecipe(stack);
    }

    @Override
    protected int getNeededInput(ChemicalCrystallizerRecipe recipe, ChemicalResource inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, ChemicalCrystallizerRecipe, InputRecipeCache.SingleChemical<ChemicalCrystallizerRecipe>> getRecipeType() {
        return MekanismRecipeType.CRYSTALLIZING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<ChemicalCrystallizerRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.CRYSTALLIZING;
    }

    @Override
    public @Nullable ChemicalCrystallizerRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(chemicalInputHandlers[cacheIndex]);
    }

    @Override
    public @NotNull CachedRecipe<ChemicalCrystallizerRecipe> createNewCachedRecipe(@NotNull ChemicalCrystallizerRecipe recipe, int cacheIndex) {
        return new OneInputCachedRecipe<>(recipe, recheckAllRecipeErrors[cacheIndex], chemicalInputHandlers[cacheIndex], itemOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(this::canFunction)
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks)
                .setBaselineMaxOperations(this::getOperationsPerTick);
    }

    @Override
    public @Nullable ChemicalToItemUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new ChemicalToItemUpgradeData(provider, redstone, getControlType(), energyContainer,
                progress, energySlot, inputChemicalTanks, outputItemSlots, isSorting(), getComponents(), problemPath());
    }
}
