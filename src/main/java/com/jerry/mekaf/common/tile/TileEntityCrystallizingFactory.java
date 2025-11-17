package com.jerry.mekaf.common.tile;

import com.jerry.mekaf.common.tile.base.TileEntityMergedToItemFactory;
import com.jerry.mekaf.common.upgrade.MergedToItemUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ChemicalCrystallizerRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.ChemicalCrystallizerCachedRecipe;
import mekanism.api.recipes.inputs.BoxedChemicalInputHandler;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.ChemicalCrystallizerInputRecipeCache;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityCrystallizingFactory extends TileEntityMergedToItemFactory<ChemicalCrystallizerRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(RecipeError.NOT_ENOUGH_ENERGY);
    private static final long MAX_CHEMICAL = 10_000;

    @Nullable
    private IContentsListener recipeCacheSaveOnlyListener;

    public TileEntityCrystallizingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        recipeCacheSaveOnlyListener = null;
    }

    @Override
    protected void presetVariables() {
        super.presetVariables();
        // 在初始化所有储罐之前
        inputTank = new MergedChemicalTank[tier.processes];
        mergedInputHandlers = new BoxedChemicalInputHandler[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            inputTank[i] = MergedChemicalTank.create(
                    ChemicalTankBuilder.GAS.input(MAX_CHEMICAL * tier.processes, gas -> getRecipeType().getInputCache().containsInput(level, gas), recipeCacheLookupMonitors[i]),
                    ChemicalTankBuilder.INFUSION.input(MAX_CHEMICAL * tier.processes, infuseType -> getRecipeType().getInputCache().containsInput(level, infuseType), recipeCacheLookupMonitors[i]),
                    ChemicalTankBuilder.PIGMENT.input(MAX_CHEMICAL * tier.processes, pigment -> getRecipeType().getInputCache().containsInput(level, pigment), recipeCacheLookupMonitors[i]),
                    ChemicalTankBuilder.SLURRY.input(MAX_CHEMICAL * tier.processes, slurry -> getRecipeType().getInputCache().containsInput(level, slurry), recipeCacheLookupMonitors[i]));
            mergedInputHandlers[i] = new BoxedChemicalInputHandler(inputTank[i], RecipeError.NOT_ENOUGH_INPUT);
        }
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<ChemicalCrystallizerRecipe, ChemicalCrystallizerInputRecipeCache> getRecipeType() {
        return MekanismRecipeType.CRYSTALLIZING;
    }

    @Override
    public @Nullable ChemicalCrystallizerRecipe getRecipe(int cacheIndex) {
        return getRecipeType().getInputCache().findFirstRecipe(level, mergedInputHandlers[cacheIndex].getInput());
    }

    @Override
    public @NotNull CachedRecipe<ChemicalCrystallizerRecipe> createNewCachedRecipe(@NotNull ChemicalCrystallizerRecipe recipe, int cacheIndex) {
        return new ChemicalCrystallizerCachedRecipe(recipe, recheckAllRecipeErrors[cacheIndex], mergedInputHandlers[cacheIndex], itemOutputHandlers[cacheIndex])
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
        return new MergedToItemUpgradeData(redstone, getControlType(), getEnergyContainer(),
                progress, energySlot, inputChemicalTanks, outputItemSlots, isSorting(), getComponents());
    }
}
