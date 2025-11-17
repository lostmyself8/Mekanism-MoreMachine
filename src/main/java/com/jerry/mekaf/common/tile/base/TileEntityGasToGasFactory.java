package com.jerry.mekaf.common.tile.base;

import com.jerry.mekaf.common.upgrade.GasToGasUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntSupplier;

public abstract class TileEntityGasToGasFactory<RECIPE extends MekanismRecipe> extends TileEntityAdvancedFactoryBase<RECIPE> {

    private static final long MAX_CHEMICAL = 10_000;

    protected GasToGasProcessInfo[] processInfoSlots;
    IGasTank[] inputTank;
    IGasTank[] outputTank;

    public final List<IGasTank> inputGasTanks;
    public final List<IGasTank> outputGasTanks;

    protected TileEntityGasToGasFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, Set<CachedRecipe.OperationTracker.RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes);
        inputGasTanks = new ArrayList<>();
        outputGasTanks = new ArrayList<>();

        for (GasToGasProcessInfo info : processInfoSlots) {
            inputGasTanks.add(info.inputTank());
            outputGasTanks.add(info.outputTank());
        }

        configComponent.addSupported(TransmissionType.GAS);

        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.GAS);
        if (gasConfig != null) {
            gasConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo.GasSlotInfo(false, true, outputGasTanks));
        }

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
            // TODO:或许这玩意并不需要
            itemConfig.fill(DataType.ENERGY);
        }
    }

    @Override
    protected void addGasTanks(ChemicalTankHelper<Gas, GasStack, IGasTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        inputTank = new IGasTank[tier.processes];
        outputTank = new IGasTank[tier.processes];
        gasInputHandlers = new IInputHandler[tier.processes];
        gasOutputHandlers = new IOutputHandler[tier.processes];
        processInfoSlots = new GasToGasProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            int index = i;
            outputTank[i] = ChemicalTankBuilder.GAS.output(MAX_CHEMICAL * tier.processes, listener);
            inputTank[i] = ChemicalTankBuilder.GAS.create(MAX_CHEMICAL * tier.processes,
                    // 这个type似乎没什么用，就不增加isValidInputChemical的参数了
                    ChemicalTankHelper.radioactiveInputTankPredicate(() -> outputTank[index]), (stack, type) -> isValidInputChemical(stack.getStack(1)),
                    stack -> isChemicalValidForTank(stack.getStack(1)) && inputProducesOutput(index, stack.getStack(1), outputTank[index], false),
                    ChemicalAttributeValidator.ALWAYS_ALLOW, recipeCacheLookupMonitors[index]);
            builder.addTank(inputTank[i]);
            builder.addTank(outputTank[i]);
            gasInputHandlers[i] = InputHelper.getInputHandler(inputTank[i], CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT);
            gasOutputHandlers[i] = OutputHelper.getOutputHandler(outputTank[i], CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
            processInfoSlots[i] = new GasToGasProcessInfo(i, inputTank[i], outputTank[i]);
        }
    }

    public boolean inputProducesOutput(int process, @NotNull GasStack fallbackInput, @NotNull IGasTank outputTank, boolean updateCache) {
        return outputTank.isEmpty() || getRecipeForInput(process, fallbackInput, outputTank, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected abstract boolean isCachedRecipeValid(@Nullable CachedRecipe<RECIPE> cached, @NotNull GasStack stack);

    @Nullable
    protected RECIPE getRecipeForInput(int process, @NotNull GasStack fallbackInput, @NotNull IGasTank outputTank, boolean updateCache) {
        if (!CommonWorldTickHandler.flushTagAndRecipeCaches) {
            // If our recipe caches are valid, grab our cached recipe and see if it is still valid
            CachedRecipe<RECIPE> cached = getCachedRecipe(process);
            if (isCachedRecipeValid(cached, fallbackInput)) {
                // Our input matches the recipe we have cached for this slot
                return cached.getRecipe();
            }
        }
        // If there is no cached item input, or it doesn't match our fallback then it is an out of date cache, so we
        // ignore the fact that we have a cache
        RECIPE foundRecipe = findRecipe(process, fallbackInput, outputTank);
        if (foundRecipe == null) {
            // We could not find any valid recipe for the given item that matches the items in the current output slots
            return null;
        }
        if (updateCache) {
            // If we want to update the cache, then create a new cache with the recipe we found and update the cache
            recipeCacheLookupMonitors[process].updateCachedRecipe(foundRecipe);
        }
        return foundRecipe;
    }

    @Nullable
    protected abstract RECIPE findRecipe(int process, @NotNull GasStack fallbackInput, @NotNull IGasTank outputTanks);

    public abstract boolean isChemicalValidForTank(@NotNull GasStack stack);

    /**
     * Like isItemValidForSlot makes no assumptions about current stored types
     */
    public abstract boolean isValidInputChemical(@NotNull GasStack stack);

    protected abstract int getNeededInput(RECIPE recipe, GasStack inputStack);

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof GasToGasUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            getEnergyContainer().setEnergy(data.energyContainer.getEnergy());
            sorting = data.sorting;
            energySlot.deserializeNBT(data.energySlot.serializeNBT());
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputTanks.size(); i++) {
                // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
                inputGasTanks.get(i).deserializeNBT(data.inputTanks.get(i).serializeNBT());
            }
            for (int i = 0; i < data.outputTanks.size(); i++) {
                outputGasTanks.get(i).setStack(data.outputTanks.get(i).getStack());
            }
            for (ITileComponent component : getComponents()) {
                component.read(data.components);
            }
        } else {
            super.parseUpgradeData(upgradeData);
        }
    }

    @Override
    protected void sortInventoryOrTank() {}

    public record GasToGasProcessInfo(int process, @NotNull IGasTank inputTank, @NotNull IGasTank outputTank) {}

    public static class GasToGasRecipeProcessInfo {

        private final List<GasToGasProcessInfo> processes = new ArrayList<>();
        @Nullable
        private IntSupplier lazyMinPerSlot;
        private int minPerSlot = 1;
        private int totalCount;

        public int getMinPerSlot() {
            if (lazyMinPerSlot != null) {
                // Get the value lazily
                minPerSlot = lazyMinPerSlot.getAsInt();
                lazyMinPerSlot = null;
            }
            return minPerSlot;
        }
    }
}
