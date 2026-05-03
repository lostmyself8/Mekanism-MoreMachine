package com.jerry.mekaf.common.tile.factory.base;

import com.jerry.mekaf.common.inventory.slot.AdvancedFactoryInputInventorySlot;

import mekanism.api.Action;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.lookup.monitor.FactoryRecipeCacheLookupMonitor;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.upgrade.MachineUpgradeData;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.ItemStackMap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.ToIntBiFunction;

public abstract class TileEntityItemToItemAdvancedFactory<RECIPE extends MekanismRecipe<?>> extends TileEntityAdvancedFactoryBase<RECIPE> {

    private IIProcessInfo[] processInfoSlots;

    protected final List<IInventorySlot> inputItemSlots;
    protected final List<IInventorySlot> outputItemSlots;

    protected TileEntityItemToItemAdvancedFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes);
        inputItemSlots = new ArrayList<>();
        outputItemSlots = new ArrayList<>();

        for (IIProcessInfo info : processInfoSlots) {
            inputItemSlots.add(info.inputSlot());
            outputItemSlots.add(info.outputSlot());
        }

        configComponent.setupItemIOConfig(inputItemSlots, outputItemSlots, energySlot, false);
        IInventorySlot extraSlot = getExtraSlot();
        if (extraSlot != null) {
            ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
            if (itemConfig != null) {
                itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, extraSlot));
            }
        }
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        itemInputHandlers = new IInputHandler[tier.processes];
        itemOutputHandlers = new IOutputHandler[tier.processes];
        processInfoSlots = new IIProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            FactoryRecipeCacheLookupMonitor<RECIPE> lookupMonitor = recipeCacheLookupMonitors[i];
            IContentsListener updateSortingAndUnpause = () -> {
                updateSortingListener.onContentsChanged();
                lookupMonitor.unpause();
            };
            OutputInventorySlot outputSlot = OutputInventorySlot.at(updateSortingAndUnpause, getXPos(i), 57);
            AdvancedFactoryInputInventorySlot inputSlot = AdvancedFactoryInputInventorySlot.create(this, i, outputSlot, recipeCacheLookupMonitors[i], getXPos(i), 13);
            int index = i;
            builder.addSlot(inputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT, index)));
            builder.addSlot(outputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index)));
            itemInputHandlers[i] = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT);
            itemOutputHandlers[i] = OutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
            processInfoSlots[i] = new IIProcessInfo(i, inputSlot, outputSlot);
        }
    }

    public boolean inputProducesOutput(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, boolean updateCache) {
        return outputSlot.isEmpty() || getRecipeForInput(process, fallbackInput, outputSlot, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected abstract boolean isCachedRecipeValid(@Nullable CachedRecipe<RECIPE> cached, @NotNull ItemStack stack);

    @Nullable
    protected RECIPE getRecipeForInput(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, boolean updateCache) {
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
    protected abstract RECIPE findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot);

    public abstract boolean isItemValidForSlot(@NotNull ItemStack stack);

    /**
     * Like isItemValidForSlot makes no assumptions about current stored types
     */
    public abstract boolean isValidInputItem(@NotNull ItemStack stack);

    protected abstract int getNeededInput(RECIPE recipe, ItemStack inputStack);

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider) {
        if (upgradeData instanceof MachineUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            getEnergyContainer().setEnergy(data.energyContainer.getEnergy());
            sorting = data.sorting;
            ContainerType.ITEM.copy(data.energySlot, energySlot);
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputSlots.size(); i++) {
                // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
                ContainerType.ITEM.copy(data.inputSlots.get(i), inputItemSlots.get(i));
            }
            for (int i = 0; i < data.outputSlots.size(); i++) {
                outputItemSlots.get(i).setStack(data.outputSlots.get(i).getStack());
            }
            readUpgradeComponents(provider, data.components);
        } else {
            super.parseUpgradeData(upgradeData, provider);
        }
    }

    // Methods relating to IComputerTile
    @ComputerMethod
    ItemStack getInput(int process) throws ComputerException {
        validateValidProcess(process);
        return processInfoSlots[process].inputSlot().getStack();
    }

    @ComputerMethod
    ItemStack getOutput(int process) throws ComputerException {
        validateValidProcess(process);
        return processInfoSlots[process].outputSlot().getStack();
    }
    // End methods IComputerTile

    @Override
    protected void sortInventoryOrTank() {
        Map<ItemStack, IIRecipeProcessInfo<RECIPE>> processes = ItemStackMap.createTypeAndTagMap();
        List<IIProcessInfo> emptyProcesses = new ArrayList<>();
        for (IIProcessInfo processInfo : processInfoSlots) {
            IInventorySlot inputSlot = processInfo.inputSlot();
            if (inputSlot.isEmpty()) {
                emptyProcesses.add(processInfo);
            } else {
                ItemStack inputStack = inputSlot.getStack();
                IIRecipeProcessInfo<RECIPE> recipeProcessInfo = processes.computeIfAbsent(inputStack, i -> new IIRecipeProcessInfo<>());
                recipeProcessInfo.processes.add(processInfo);
                recipeProcessInfo.totalCount += inputStack.getCount();
                if (recipeProcessInfo.lazyMinPerSlot == null && !CommonWorldTickHandler.flushTagAndRecipeCaches) {
                    // If we don't have a lazily initialized min per slot calculation set for it yet
                    // and our cache is not invalid/out of date due to a reload
                    CachedRecipe<RECIPE> cachedRecipe = getCachedRecipe(processInfo.process());
                    if (isCachedRecipeValid(cachedRecipe, inputStack)) {
                        recipeProcessInfo.item = inputStack;
                        recipeProcessInfo.recipe = cachedRecipe.getRecipe();
                        // And our current process has a cached recipe then set the lazily initialized per slot value
                        // Note: If something goes wrong, and we end up with zero as how much we need as an input
                        // we just bump the value up to one to make sure we properly handle it
                        recipeProcessInfo.lazyMinPerSlot = (info, factory) -> factory.getNeededInput(info.recipe, (ItemStack) info.item);
                    }
                }
            }
        }
        if (processes.isEmpty()) {
            // If all input slots are empty, just exit
            return;
        }
        for (Map.Entry<ItemStack, IIRecipeProcessInfo<RECIPE>> entry : processes.entrySet()) {
            IIRecipeProcessInfo<RECIPE> recipeProcessInfo = entry.getValue();
            if (recipeProcessInfo.lazyMinPerSlot == null) {
                recipeProcessInfo.item = entry.getKey();
                // If we don't have a lazy initializer for our minPerSlot setup, that means that there is
                // no valid cached recipe for any of the slots of this type currently, so we want to try and
                // get the recipe we will have for the first slot, once we end up with more items in the stack
                recipeProcessInfo.lazyMinPerSlot = (info, factory) -> {
                    // Note: We put all of this logic in the lazy init, so that we don't actually call any of this
                    // until it is needed. That way if we have no empty slots and all our input slots are filled
                    // we don't do any extra processing here, and can properly short circuit
                    ItemStack item = (ItemStack) info.item;
                    ItemStack largerInput = item.copyWithCount(Math.min(item.getMaxStackSize(), info.totalCount));
                    IIProcessInfo processInfo = info.processes.getFirst();
                    // Try getting a recipe for our input with a larger size, and update the cache if we find one
                    info.recipe = factory.getRecipeForInput(processInfo.process(), largerInput, processInfo.outputSlot(), true);
                    if (info.recipe != null) {
                        return factory.getNeededInput(info.recipe, largerInput);
                    }
                    return 1;
                };
            }
        }
        if (!emptyProcesses.isEmpty()) {
            // If we have any empty slots, we need to factor them in as valid slots for items to transferred to
            addEmptySlotsAsTargets(processes, emptyProcesses);
            // Note: Any remaining empty slots are "ignored" as we don't have any
            // spare items to distribute to them
        }
        // Distribute items among the slots
        distributeItems(processes);
    }

    private void addEmptySlotsAsTargets(Map<ItemStack, IIRecipeProcessInfo<RECIPE>> processes, List<IIProcessInfo> emptyProcesses) {
        for (Map.Entry<ItemStack, IIRecipeProcessInfo<RECIPE>> entry : processes.entrySet()) {
            IIRecipeProcessInfo<RECIPE> recipeProcessInfo = entry.getValue();
            int minPerSlot = recipeProcessInfo.getMinPerSlot(this);
            int maxSlots = recipeProcessInfo.totalCount / minPerSlot;
            if (maxSlots <= 1) {
                // If we don't have enough to even fill the input for a slot for a single recipe; skip
                continue;
            }
            // Otherwise, if we have at least enough items for two slots see how many we already have with items in them
            int processCount = recipeProcessInfo.processes.size();
            if (maxSlots <= processCount) {
                // If we don't have enough extra to fill another slot skip
                continue;
            }
            // Note: This is some arbitrary input stack one of the stacks contained
            ItemStack sourceStack = entry.getKey();
            int emptyToAdd = maxSlots - processCount;
            int added = 0;
            List<IIProcessInfo> toRemove = new ArrayList<>();
            for (IIProcessInfo emptyProcess : emptyProcesses) {
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

    private void distributeItems(Map<ItemStack, IIRecipeProcessInfo<RECIPE>> processes) {
        for (Map.Entry<ItemStack, IIRecipeProcessInfo<RECIPE>> entry : processes.entrySet()) {
            IIRecipeProcessInfo<RECIPE> recipeProcessInfo = entry.getValue();
            int processCount = recipeProcessInfo.processes.size();
            if (processCount == 1) {
                // If there is only one process with the item in it; short-circuit, no balancing is needed
                continue;
            }
            ItemStack item = entry.getKey();
            // Note: This isn't based on any limits the slot may have (but we currently don't have any reduced ones
            // here, so it doesn't matter)
            int maxStackSize = item.getMaxStackSize();
            int numberPerSlot = recipeProcessInfo.totalCount / processCount;
            if (numberPerSlot == maxStackSize) {
                // If all the slots are already maxed out; short-circuit, no balancing is needed
                continue;
            }
            int remainder = recipeProcessInfo.totalCount % processCount;
            int minPerSlot = recipeProcessInfo.getMinPerSlot(this);
            if (minPerSlot > 1) {
                int perSlotRemainder = numberPerSlot % minPerSlot;
                if (perSlotRemainder > 0) {
                    // Reduce the number we distribute per slot by what our excess
                    // is if we are trying to balance it by the size of the input
                    // required by the recipe
                    numberPerSlot -= perSlotRemainder;
                    // and then add how many items we removed to our remainder
                    remainder += perSlotRemainder * processCount;
                    // Note: After this processing the remainder is at most:
                    // processCount - 1 + processCount * (minPerSlot - 1) =
                    // processCount - 1 + processCount * minPerSlot - processCount =
                    // processCount * minPerSlot - 1
                    // Which means that reducing the remainder by minPerSlot for each
                    // slot while we still have a remainder, will make sure
                }
                if (numberPerSlot + minPerSlot > maxStackSize) {
                    // If adding how much we want per slot would cause the slot to overflow
                    // we reduce how much we set per slot to how much there is room for
                    // Note: we can do this safely because while our remainder may be
                    // processCount * minPerSlot - 1 (as shown above), if we are in
                    // this if statement, that means that we really have at most:
                    // processCount * maxStackSize - 1 items being distributed and
                    // have: processCount * numberPerSlot + remainder
                    // which means that our remainder is actually at most:
                    // processCount * (maxStackSize - numberPerSlot) - 1
                    // so we can safely set our per slot distribution to maxStackSize - numberPerSlot
                    minPerSlot = maxStackSize - numberPerSlot;
                }
            }
            for (int i = 0; i < processCount; i++) {
                IIProcessInfo processInfo = recipeProcessInfo.processes.get(i);
                AdvancedFactoryInputInventorySlot inputSlot = processInfo.inputSlot();
                int sizeForSlot = numberPerSlot;
                if (remainder > 0) {
                    // If we have a remainder, factor it into our slots
                    if (remainder > minPerSlot) {
                        // If our remainder is greater than how much we need to fill out the min amount for the slot
                        // based
                        // on the recipe then, to keep it distributed as evenly as possible, increase our size for the
                        // slot
                        // by how much we need, and decrease our remainder by that amount
                        sizeForSlot += minPerSlot;
                        remainder -= minPerSlot;
                    } else {
                        // Otherwise, add our entire remainder to the size for slot, and mark our remainder as fully
                        // used
                        sizeForSlot += remainder;
                        remainder = 0;
                    }
                }
                if (inputSlot.isEmpty()) {
                    // Note: sizeForSlot should never be zero here as we would not have added
                    // the empty slot to this item's distribution grouping if it would not
                    // end up getting any items; check it just in case though before creating
                    // a stack for the slot and setting it
                    if (sizeForSlot > 0) {
                        // Note: We use setStackUnchecked here, as there is a very small chance that
                        // the stack is not actually valid for the slot because of a reload causing
                        // recipes to change. If this is the case, then we want to properly not crash,
                        // but we would rather not add any extra overhead about revalidating the item
                        // each time as it can get somewhat expensive.
                        inputSlot.setStackUnchecked(item.copyWithCount(sizeForSlot));
                    }
                } else {
                    // Slot is not currently empty
                    if (sizeForSlot == 0) {
                        // If the amount of the item we want to set it to is zero (all got used by earlier stacks, which
                        // might
                        // happen if the recipe requires a stacked input (minPerSlot > 1)), then we need to set the slot
                        // to empty
                        inputSlot.setEmpty();
                    } else if (inputSlot.getCount() != sizeForSlot) {
                        // Otherwise, if our slot doesn't already contain the amount we want it to,
                        // we need to adjust how much is stored in it, and log an error if it changed
                        // by a different amount then we expected
                        // Note: We use setStackSize here rather than setStack to avoid an unnecessary stack copy call
                        // as copying item stacks can sometimes be rather expensive in a heavily modded environment
                        MekanismUtils.logMismatchedStackSize(sizeForSlot, inputSlot.setStackSize(sizeForSlot, Action.EXECUTE));
                    }
                }
            }
        }
    }

    public record IIProcessInfo(int process, @NotNull AdvancedFactoryInputInventorySlot inputSlot,
                                @NotNull IInventorySlot outputSlot) {}

    protected static class IIRecipeProcessInfo<RECIPE extends MekanismRecipe<?>> {

        private final List<IIProcessInfo> processes = new ArrayList<>();
        @Nullable
        private ToIntBiFunction<IIRecipeProcessInfo<RECIPE>, TileEntityItemToItemAdvancedFactory<RECIPE>> lazyMinPerSlot;
        private Object item;
        private RECIPE recipe;
        private int minPerSlot = 1;
        private int totalCount;

        public int getMinPerSlot(TileEntityItemToItemAdvancedFactory<RECIPE> factory) {
            if (lazyMinPerSlot != null) {
                // Get the value lazily
                minPerSlot = Math.max(1, lazyMinPerSlot.applyAsInt(this, factory));
                lazyMinPerSlot = null;
            }
            return minPerSlot;
        }
    }
}
