package com.jerry.mekaf.common.tile;

import com.jerry.mekaf.common.tile.base.TileEntityItemToGasFactory;
import com.jerry.mekaf.common.upgrade.ItemToGasUpgradeData;

import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ItemStackToGasRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ItemRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleItem;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityOxidizingFactory extends TileEntityItemToGasFactory<ItemStackToGasRecipe> implements ItemRecipeLookupHandler<ItemStackToGasRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);

    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(RecipeError.NOT_ENOUGH_ENERGY);

    public TileEntityOxidizingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.GAS);
    }

    @Override
    @Contract("null, _ -> false")
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ItemStackToGasRecipe> cached, @NotNull ItemStack stack) {
        return cached != null && cached.getRecipe().getInput().testType(stack);
    }

    @Override
    protected @Nullable ItemStackToGasRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IGasTank outputTanks) {
        GasStack output = outputTanks.getStack();
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, recipe -> output.isTypeEqual(recipe.getOutput(fallbackInput)));
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipe(stack);
    }

    @Override
    protected int getNeededInput(ItemStackToGasRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<ItemStackToGasRecipe, SingleItem<ItemStackToGasRecipe>> getRecipeType() {
        return MekanismRecipeType.OXIDIZING;
    }

    @Override
    public @Nullable ItemStackToGasRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandlers[cacheIndex]);
    }

    @Override
    public @NotNull CachedRecipe<ItemStackToGasRecipe> createNewCachedRecipe(@NotNull ItemStackToGasRecipe recipe, int cacheIndex) {
        return OneInputCachedRecipe.itemToChemical(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], gasOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks);
    }

    @Override
    public @Nullable IUpgradeData getUpgradeData() {
        return new ItemToGasUpgradeData(redstone, getControlType(), getEnergyContainer(),
                progress, energySlot, inputItemSlots, outputGasTanks, isSorting(), getComponents());
    }
}
