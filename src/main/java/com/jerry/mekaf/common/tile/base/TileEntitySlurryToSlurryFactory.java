package com.jerry.mekaf.common.tile.base;

import com.jerry.mekaf.common.upgrade.SlurryToSlurryUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
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
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo.SlurrySlotInfo;
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

public abstract class TileEntitySlurryToSlurryFactory<RECIPE extends MekanismRecipe> extends TileEntityAdvancedFactoryBase<RECIPE> {

    private static final long MAX_CHEMICAL = 10_000;

    protected SlurryToSlurryProcessInfo[] processInfoSlots;
    ISlurryTank[] inputTank;
    ISlurryTank[] outputTank;

    public final List<ISlurryTank> inputSlurryTanks;
    public final List<ISlurryTank> outputSlurryTanks;

    protected TileEntitySlurryToSlurryFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, Set<CachedRecipe.OperationTracker.RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes);
        inputSlurryTanks = new ArrayList<>();
        outputSlurryTanks = new ArrayList<>();

        for (SlurryToSlurryProcessInfo info : processInfoSlots) {
            inputSlurryTanks.add(info.inputTank());
            outputSlurryTanks.add(info.outputTank());
        }

        configComponent.addSupported(TransmissionType.SLURRY);

        ConfigInfo slurryConfig = configComponent.getConfig(TransmissionType.SLURRY);
        if (slurryConfig != null) {
            slurryConfig.addSlotInfo(DataType.OUTPUT, new SlurrySlotInfo(false, true, outputSlurryTanks));
            slurryConfig.fill(DataType.INPUT);
            slurryConfig.setDataType(DataType.OUTPUT, RelativeSide.RIGHT);
            slurryConfig.setEjecting(true);
        }

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }
    }

    @Override
    protected void addSlurryTanks(ChemicalTankHelper<Slurry, SlurryStack, ISlurryTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        inputTank = new ISlurryTank[tier.processes];
        outputTank = new ISlurryTank[tier.processes];
        slurryInputHandlers = new IInputHandler[tier.processes];
        slurryOutputHandlers = new IOutputHandler[tier.processes];
        processInfoSlots = new SlurryToSlurryProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            int index = i;
            outputTank[i] = ChemicalTankBuilder.SLURRY.output(MAX_CHEMICAL * tier.processes, listener);
            inputTank[i] = ChemicalTankBuilder.SLURRY.input(MAX_CHEMICAL * tier.processes, (stack) -> isValidInputChemical(stack.getStack(1)),
                    (stack) -> isChemicalValidForTank(stack.getStack(1)) && inputProducesOutput(index, stack.getStack(1), outputTank[index], false), recipeCacheLookupMonitors[index]);
            builder.addTank(inputTank[i]);
            builder.addTank(outputTank[i]);
            slurryInputHandlers[i] = InputHelper.getInputHandler(inputTank[i], CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT);
            slurryOutputHandlers[i] = OutputHelper.getOutputHandler(outputTank[i], CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
            processInfoSlots[i] = new SlurryToSlurryProcessInfo(i, inputTank[i], outputTank[i]);
        }
    }

    public boolean inputProducesOutput(int process, @NotNull SlurryStack fallbackInput, @NotNull ISlurryTank outputTank, boolean updateCache) {
        return outputTank.isEmpty() || getRecipeForInput(process, fallbackInput, outputTank, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected abstract boolean isCachedRecipeValid(@Nullable CachedRecipe<RECIPE> cached, @NotNull SlurryStack stack);

    @Nullable
    protected RECIPE getRecipeForInput(int process, @NotNull SlurryStack fallbackInput, @NotNull ISlurryTank outputTank, boolean updateCache) {
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
    protected abstract RECIPE findRecipe(int process, @NotNull SlurryStack fallbackInput, @NotNull ISlurryTank outputTanks);

    public abstract boolean isChemicalValidForTank(@NotNull SlurryStack stack);

    /**
     * Like isItemValidForSlot makes no assumptions about current stored types
     */
    public abstract boolean isValidInputChemical(@NotNull SlurryStack stack);

    protected abstract int getNeededInput(RECIPE recipe, SlurryStack inputStack);

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof SlurryToSlurryUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            getEnergyContainer().setEnergy(data.energyContainer.getEnergy());
            sorting = data.sorting;
            energySlot.deserializeNBT(data.energySlot.serializeNBT());
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputTanks.size(); i++) {
                // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
                inputSlurryTanks.get(i).deserializeNBT(data.inputTanks.get(i).serializeNBT());
            }
            for (int i = 0; i < data.outputTanks.size(); i++) {
                outputSlurryTanks.get(i).setStack(data.outputTanks.get(i).getStack());
            }
            for (ITileComponent component : getComponents()) {
                component.read(data.components);
            }
        } else {
            super.parseUpgradeData(upgradeData);
        }
    }

    public record SlurryToSlurryProcessInfo(int process, @NotNull ISlurryTank inputTank, @NotNull ISlurryTank outputTank) {}
}
