package com.jerry.mekaf.common.tile.factory.base;

import com.jerry.mekaf.common.upgrade.ChemicalToItemUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.lookup.monitor.FactoryRecipeCacheLookupMonitor;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.ToIntBiFunction;

public abstract class TileEntityChemicalToItemFactory<RECIPE extends MekanismRecipe<?>> extends TileEntityAdvancedFactoryBase<RECIPE> {

    protected CIProcessInfo[] processInfoSlots;
    public OutputInventorySlot[] outputSlot;
    public IChemicalTank[] inputTank;

    public List<IChemicalTank> inputChemicalTanks;
    public List<IInventorySlot> outputItemSlots;

    protected TileEntityChemicalToItemFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes);
        inputChemicalTanks = new ArrayList<>();
        outputItemSlots = new ArrayList<>();

        processInfoSlots = new CIProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            processInfoSlots[i] = new CIProcessInfo(i, inputTank[i], outputSlot[i]);
        }

        for (CIProcessInfo info : processInfoSlots) {
            inputChemicalTanks.add(info.inputTank());
            outputItemSlots.add(info.outputSlot());
        }

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(false, true, outputItemSlots));
            IInventorySlot extraSlot = getExtraSlot();
            if (extraSlot != null) {
                itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, extraSlot));
            }
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }
        ConfigInfo chemicalConfig = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (chemicalConfig != null) {
            chemicalConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo(true, false, inputChemicalTanks));
        }
    }

    @Override
    protected void addTanks(MekContainerHelper<IChemicalTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        inputTank = new IChemicalTank[tier.processes];
        chemicalInputHandlers = new IInputHandler[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            int index = i;
            inputTank[i] = BasicChemicalTank.input(MAX_CHEMICAL * tier.processes, (stack, type) -> isValidInputChemical(stack),
                    stack -> isChemicalValidForTank(stack) && inputProducesOutput(index, stack, outputSlot[index], false), recipeCacheLookupMonitors[index]);
            builder.addContainer(inputTank[i]);
            chemicalInputHandlers[i] = InputHelper.getInputHandler(inputTank[i], RecipeError.NOT_ENOUGH_INPUT);
        }
    }

    @Override
    protected void addSlots(MekContainerHelper<IInventorySlot> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        outputSlot = new OutputInventorySlot[tier.processes];
        itemOutputHandlers = new IOutputHandler[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            FactoryRecipeCacheLookupMonitor<RECIPE> lookupMonitor = recipeCacheLookupMonitors[i];
            IContentsListener updateSortingAndUnpause = () -> {
                updateSortingListener.onContentsChanged();
                lookupMonitor.unpause();
            };
            int index = i;
            outputSlot[i] = OutputInventorySlot.at(updateSortingAndUnpause, getXPos(i), 70);
            builder.addContainer(outputSlot[i]).tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index)));
            itemOutputHandlers[i] = OutputHelper.getOutputHandler(outputSlot[i], RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        }
    }

    @Override
    public int UpperTankCount() {
        return 1;
    }

    @Override
    public int getTankCount() {
        return 1;
    }

    public boolean inputProducesOutput(int process, @NotNull ChemicalResource fallbackInput, @NotNull IInventorySlot outputSlot, boolean updateCache) {
        return outputSlot.isEmpty() || getRecipeForInput(process, fallbackInput, outputSlot, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected abstract boolean isCachedRecipeValid(@Nullable CachedRecipe<RECIPE> cached, @NotNull ChemicalResource stack);

    @Nullable
    protected RECIPE getRecipeForInput(int process, @NotNull ChemicalResource fallbackInput, @NotNull IInventorySlot outputSlot, boolean updateCache) {
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
        RECIPE foundRecipe = findRecipe(process, fallbackInput, outputSlot);
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
    protected abstract RECIPE findRecipe(int process, @NotNull ChemicalResource fallbackInput, @NotNull IInventorySlot outputSlot);

    public abstract boolean isChemicalValidForTank(@NotNull ChemicalResource stack);

    /**
     * Like isItemValidForSlot makes no assumptions about current stored types
     */
    public abstract boolean isValidInputChemical(@NotNull ChemicalResource stack);

    protected abstract int getNeededInput(RECIPE recipe, ChemicalResource inputStack);

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider, TransactionContext transaction) {
        if (upgradeData instanceof ChemicalToItemUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            energyContainer.copyContents(data.energyContainer, transaction);
            sorting = data.sorting;
            energySlot.copyContents(data.energySlot, transaction);
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputTanks.size(); i++) {
                // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
                inputChemicalTanks.get(i).copyContents(data.inputTanks.get(i), transaction);
            }
            for (int i = 0; i < data.outputSlots.size(); i++) {
                outputItemSlots.get(i).copyContents(data.outputSlots.get(i), transaction);
            }
            readUpgradeComponents(provider, data.components);
        } else {
            super.parseUpgradeData(upgradeData, provider, transaction);
        }
    }

    // Methods relating to IComputerTile
    @ComputerMethod
    ChemicalStack getInput(int process) throws ComputerException {
        validateValidProcess(process);
        IChemicalTank inputTank = processInfoSlots[process].inputTank();
        return inputTank.resource().toStack(inputTank.amountAsInt());
    }

    @ComputerMethod
    IChemicalTank getInputTank(int process) throws ComputerException {
        validateValidProcess(process);
        return inputTank[process];
    }

    @ComputerMethod
    ItemStack getOutput(int process) throws ComputerException {
        validateValidProcess(process);
        IInventorySlot outputSlot = processInfoSlots[process].outputSlot();
        return outputSlot.resource().toStack(outputSlot.amountAsInt());
    }
    // End methods IComputerTile

    protected void sortInventoryOrTank() {
        Map<ChemicalResource, CIRecipeProcessInfo<ChemicalResource, RECIPE>> processes = new HashMap<>();
        List<CIProcessInfo> emptyProcesses = new ArrayList<>();
        for (CIProcessInfo processInfo : processInfoSlots) {
            IChemicalTank inputTank = processInfo.inputTank();
            if (inputTank.isEmpty()) {
                emptyProcesses.add(processInfo);
            } else {
                ChemicalResource inputStack = inputTank.resource();
                CIRecipeProcessInfo<ChemicalResource, RECIPE> recipeProcessInfo = processes.computeIfAbsent(inputStack, CIRecipeProcessInfo::new);
                recipeProcessInfo.processes.add(processInfo);
                recipeProcessInfo.totalCount += inputTank.amountAsLong();
                if (recipeProcessInfo.lazyMinPerTank == null && !CommonWorldTickHandler.flushTagAndRecipeCaches) {
                    // If we don't have a lazily initialized min per slot calculation set for it yet
                    // and our cache is not invalid/out of date due to a reload
                    CachedRecipe<RECIPE> cachedRecipe = getCachedRecipe(processInfo.process());
                    if (isCachedRecipeValid(cachedRecipe, inputStack)) {
                        recipeProcessInfo.recipe = cachedRecipe.getRecipe();
                        // And our current process has a cached recipe then set the lazily initialized per slot value
                        // Note: If something goes wrong, and we end up with zero as how much we need as an input
                        // we just bump the value up to one to make sure we properly handle it
                        recipeProcessInfo.lazyMinPerTank = (info, factory) -> factory.getNeededInput(info.recipe, info.item);
                    }
                }
            }
        }
        if (processes.isEmpty()) {
            // If all input slots are empty, just exit
            return;
        }
        for (Map.Entry<ChemicalResource, CIRecipeProcessInfo<ChemicalResource, RECIPE>> entry : processes.entrySet()) {
            CIRecipeProcessInfo<ChemicalResource, RECIPE> recipeProcessInfo = entry.getValue();
            if (recipeProcessInfo.lazyMinPerTank == null) {
                // If we don't have a lazy initializer for our minPerTank setup, that means that there is
                // no valid cached recipe for any of the slots of this type currently, so we want to try and
                // get the recipe we will have for the first slot, once we end up with more items in the stack
                recipeProcessInfo.lazyMinPerTank = (info, factory) -> {
                    // Note: We put all of this logic in the lazy init, so that we don't actually call any of this
                    // until it is needed. That way if we have no empty slots and all our input slots are filled
                    // we don't do any extra processing here, and can properly short circuit
                    CIProcessInfo processInfo = info.processes.getFirst();
                    // Try getting a recipe for our input with a larger size, and update the cache if we find one
                    info.recipe = factory.getRecipeForInput(processInfo.process(), info.item, processInfo.outputSlot(), true);
                    if (info.recipe != null) {
                        return factory.getNeededInput(info.recipe, info.item);
                    }
                    return 1;
                };
            }
        }
        if (!emptyProcesses.isEmpty()) {
            // If we have any empty slots, we need to factor them in as valid slots for items to transferred to
            addEmptyTanksAsTargets(processes, emptyProcesses);
            // Note: Any remaining empty slots are "ignored" as we don't have any
            // spare items to distribute to them
        }
        // Distribute items among the slots
        distributeItems(processes);
    }

    protected void addEmptyTanksAsTargets(Map<ChemicalResource, CIRecipeProcessInfo<ChemicalResource, RECIPE>> processes, List<CIProcessInfo> emptyProcesses) {
        for (Map.Entry<ChemicalResource, CIRecipeProcessInfo<ChemicalResource, RECIPE>> entry : processes.entrySet()) {
            CIRecipeProcessInfo<ChemicalResource, RECIPE> recipeProcessInfo = entry.getValue();
            long minPerTank = recipeProcessInfo.getMinPerTank(this);
            long maxTanks = recipeProcessInfo.totalCount / minPerTank;
            if (maxTanks <= 1) {
                // If we don't have enough to even fill the input for a slot for a single recipe; skip
                continue;
            }
            // Otherwise, if we have at least enough items for two slots see how many we already have with items in them
            int processAmount = recipeProcessInfo.processes.size();
            if (maxTanks <= processAmount) {
                // If we don't have enough extra to fill another slot skip
                continue;
            }
            // Note: This is some arbitrary input stack one of the stacks contained
            ChemicalResource sourceStack = entry.getKey();
            long emptyToAdd = maxTanks - processAmount;
            int added = 0;
            List<CIProcessInfo> toRemove = new ArrayList<>();
            for (CIProcessInfo emptyProcess : emptyProcesses) {
                if (inputProducesOutput(emptyProcess.process(), sourceStack, emptyProcess.outputSlot(), true)) {
                    // If the input is valid for the stuff in the empty process' output slot
                    // then add our empty process to our recipeProcessInfo, and mark
                    // the empty process as accounted for
                    recipeProcessInfo.processes.add(emptyProcess);
                    toRemove.add(emptyProcess);
                    added++;
                    if (added >= emptyToAdd) {
                        // If we added as many as we could based on how much input we have; exit
                        break;
                    }
                }
            }
            emptyProcesses.removeAll(toRemove);
            if (emptyProcesses.isEmpty()) {
                // We accounted for all our empty processes, stop looking at inputs
                // for purposes of distributing empty slots among them
                break;
            }
        }
    }

    protected void distributeItems(Map<ChemicalResource, CIRecipeProcessInfo<ChemicalResource, RECIPE>> processes) {
        for (Map.Entry<ChemicalResource, CIRecipeProcessInfo<ChemicalResource, RECIPE>> entry : processes.entrySet()) {
            CIRecipeProcessInfo<ChemicalResource, RECIPE> recipeProcessInfo = entry.getValue();
            long processAmount = recipeProcessInfo.processes.size();
            if (processAmount == 1) {
                // If there is only one process with the item in it; short-circuit, no balancing is needed
                continue;
            }
            ChemicalResource item = entry.getKey();
            // Note: This isn't based on any limits the slot may have (but we currently don't have any reduced ones
            // here, so it doesn't matter)
            long maxAmount = MAX_CHEMICAL * tier.processes;
            long numberPerTank = recipeProcessInfo.totalCount / processAmount;
            if (numberPerTank == maxAmount) {
                // If all the slots are already maxed out; short-circuit, no balancing is needed
                continue;
            }
            long remainder = recipeProcessInfo.totalCount % processAmount;
            long minPerTank = recipeProcessInfo.getMinPerTank(this);
            if (minPerTank > 1) {
                long perSlotRemainder = numberPerTank % minPerTank;
                if (perSlotRemainder > 0) {
                    // Reduce the number we distribute per slot by what our excess
                    // is if we are trying to balance it by the size of the input
                    // required by the recipe
                    numberPerTank -= perSlotRemainder;
                    // and then add how many items we removed to our remainder
                    remainder += perSlotRemainder * processAmount;
                    // Note: After this processing the remainder is at most:
                    // processAmount - 1 + processAmount * (minPerTank - 1) =
                    // processAmount - 1 + processAmount * minPerTank - processAmount =
                    // processAmount * minPerTank - 1
                    // Which means that reducing the remainder by minPerTank for each
                    // slot while we still have a remainder, will make sure
                }
                if (numberPerTank + minPerTank > maxAmount) {
                    // If adding how much we want per slot would cause the slot to overflow
                    // we reduce how much we set per slot to how much there is room for
                    // Note: we can do this safely because while our remainder may be
                    // processAmount * minPerTank - 1 (as shown above), if we are in
                    // this if statement, that means that we really have at most:
                    // processAmount * maxStackSize - 1 items being distributed and
                    // have: processAmount * numberPerSlot + remainder
                    // which means that our remainder is actually at most:
                    // processAmount * (maxStackSize - numberPerSlot) - 1
                    // so we can safely set our per slot distribution to maxStackSize - numberPerSlot
                    minPerTank = maxAmount - numberPerTank;
                }
            }
            for (int i = 0; i < processAmount; i++) {
                CIProcessInfo processInfo = recipeProcessInfo.processes.get(i);
                IChemicalTank inputTank = processInfo.inputTank();
                long sizeForTank = numberPerTank;
                if (remainder > 0) {
                    // If we have a remainder, factor it into our slots
                    if (remainder > minPerTank) {
                        // If our remainder is greater than how much we need to fill out the min amount for the slot
                        // based
                        // on the recipe then, to keep it distributed as evenly as possible, increase our size for the
                        // slot
                        // by how much we need, and decrease our remainder by that amount
                        sizeForTank += minPerTank;
                        remainder -= minPerTank;
                    } else {
                        // Otherwise, add our entire remainder to the size for slot, and mark our remainder as fully
                        // used
                        sizeForTank += remainder;
                        remainder = 0;
                    }
                }
                inputTank.setContents(item, sizeForTank, null);
            }
        }
    }

    public record CIProcessInfo(int process, @NotNull IChemicalTank inputTank, @NotNull IInventorySlot outputSlot) {}

    protected static class CIRecipeProcessInfo<ITEM, RECIPE extends MekanismRecipe<?>> {

        private final List<CIProcessInfo> processes = new ArrayList<>();
        @Nullable
        private ToIntBiFunction<CIRecipeProcessInfo<ITEM, RECIPE>, TileEntityChemicalToItemFactory<RECIPE>> lazyMinPerTank;
        private final ITEM item;
        private RECIPE recipe;
        private long minPerTank = 1;
        private long totalCount;

        public CIRecipeProcessInfo(ITEM item) {
            this.item = item;
        }

        public long getMinPerTank(TileEntityChemicalToItemFactory<RECIPE> factory) {
            if (lazyMinPerTank != null) {
                // Get the value lazily
                minPerTank = Math.max(1, lazyMinPerTank.applyAsInt(this, factory));
                lazyMinPerTank = null;
            }
            return minPerTank;
        }
    }
}
