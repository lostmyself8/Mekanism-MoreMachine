package com.jerry.mekaf.common.tile.factory.base;

import com.jerry.mekaf.common.inventory.slot.AdvancedFactoryInputInventorySlot;
import com.jerry.mekaf.common.upgrade.ItemToChemicalUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.BasicChemicalTank;
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
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.ToIntBiFunction;

public abstract class TileEntityItemToChemicalFactory<RECIPE extends MekanismRecipe<?>> extends TileEntityAdvancedFactoryBase<RECIPE> {

    protected ICProcessInfo[] processInfoSlots;
    public IChemicalTank[] outputTank;
    AdvancedFactoryInputInventorySlot[] inputSlot;

    protected final List<IInventorySlot> inputItemSlots;
    public final List<IChemicalTank> outputChemicalTanks;

    protected TileEntityItemToChemicalFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes);
        inputItemSlots = new ArrayList<>();
        outputChemicalTanks = new ArrayList<>();

        // 初始化COProcessInfo
        processInfoSlots = new ICProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            processInfoSlots[i] = new ICProcessInfo(i, inputSlot[i], outputTank[i]);
        }

        for (ICProcessInfo info : processInfoSlots) {
            inputItemSlots.add(info.inputSlot());
            outputChemicalTanks.add(info.outputTank());
        }

        ConfigInfo chemicalConfig = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (chemicalConfig != null) {
            chemicalConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo(false, true, outputChemicalTanks));
        }

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, false, inputItemSlots));
            IInventorySlot extraSlot = getExtraSlot();
            if (extraSlot != null) {
                itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, extraSlot));
            }
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }
    }

    @Override
    protected void addTanks(MekContainerHelper<IChemicalTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        outputTank = new IChemicalTank[tier.processes];
        chemicalOutputHandlers = new IOutputHandler[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            FactoryRecipeCacheLookupMonitor<RECIPE> lookupMonitor = recipeCacheLookupMonitors[i];
            IContentsListener updateSortingAndUnpause = () -> {
                updateSortingListener.onContentsChanged();
                lookupMonitor.unpause();
            };
            outputTank[i] = BasicChemicalTank.output(MAX_CHEMICAL * tier.processes, updateSortingAndUnpause);
            builder.addContainer(outputTank[i]);
            chemicalOutputHandlers[i] = OutputHelper.getOutputHandler(outputTank[i], RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        }
    }

    @Override
    protected void addSlots(MekContainerHelper<IInventorySlot> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        inputSlot = new AdvancedFactoryInputInventorySlot[tier.processes];
        itemInputHandlers = new IInputHandler[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            inputSlot[i] = AdvancedFactoryInputInventorySlot.create(this, i, outputTank[i], recipeCacheLookupMonitors[i], getXPos(i), 13);
            int index = i;
            builder.addContainer(inputSlot[i]).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT, index)));
            itemInputHandlers[i] = InputHelper.getInputHandler(inputSlot[i], RecipeError.NOT_ENOUGH_INPUT);
        }
    }

    @Override
    public int getTankCount() {
        return 1;
    }

    public boolean inputProducesOutput(int process, @NotNull ItemResource fallbackInput, @NotNull IChemicalTank outputTank, boolean updateCache) {
        return outputTank.isEmpty() || getRecipeForInput(process, fallbackInput, outputTank, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected abstract boolean isCachedRecipeValid(@Nullable CachedRecipe<RECIPE> cached, @NotNull ItemResource stack);

    @Nullable
    protected RECIPE getRecipeForInput(int process, @NotNull ItemResource fallbackInput, @NotNull IChemicalTank outputTank, boolean updateCache) {
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
    protected abstract RECIPE findRecipe(int process, @NotNull ItemResource fallbackInput, @NotNull IChemicalTank outputSlot);

    public abstract boolean isItemValidForSlot(@NotNull ItemResource stack);

    /**
     * Like isItemValidForSlot makes no assumptions about current stored types
     */
    public abstract boolean isValidInputItem(@NotNull ItemResource stack);

    protected abstract int getNeededInput(RECIPE recipe, ItemResource inputStack);

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider, TransactionContext transaction) {
        if (upgradeData instanceof ItemToChemicalUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            energyContainer.copyContents(data.energyContainer, transaction);
            sorting = data.sorting;
            energySlot.copyContents(data.energySlot, transaction);
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputSlots.size(); i++) {
                // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
                inputItemSlots.get(i).copyContents(data.inputSlots.get(i), transaction);
            }
            for (int i = 0; i < data.outputTanks.size(); i++) {
                outputChemicalTanks.get(i).copyContents(data.outputTanks.get(i), transaction);
            }
            readUpgradeComponents(provider, data.components);
        } else {
            super.parseUpgradeData(upgradeData, provider, transaction);
        }
    }

    // Methods relating to IComputerTile
    @ComputerMethod
    ItemStack getInput(int process) throws ComputerException {
        validateValidProcess(process);
        IInventorySlot inputSlot = processInfoSlots[process].inputSlot();
        return inputSlot.resource().toStack(inputSlot.amountAsInt());
    }

    @ComputerMethod
    ChemicalStack getOutput(int process) throws ComputerException {
        validateValidProcess(process);
        IChemicalTank outputTank = processInfoSlots[process].outputTank();
        return outputTank.resource().toStack(outputTank.amountAsInt());
    }

    @ComputerMethod
    IChemicalTank getOutputTank(int process) throws ComputerException {
        validateValidProcess(process);
        return outputTank[process];
    }
    // End methods IComputerTile

    protected void sortInventoryOrTank() {
        Map<ItemResource, ICRecipeProcessInfo<ItemResource, RECIPE>> processes = new HashMap<>();
        List<ICProcessInfo> emptyProcesses = new ArrayList<>();
        for (ICProcessInfo processInfo : processInfoSlots) {
            IInventorySlot inputSlot = processInfo.inputSlot();
            if (inputSlot.isEmpty()) {
                emptyProcesses.add(processInfo);
            } else {
                ItemResource inputStack = inputSlot.resource();
                ICRecipeProcessInfo<ItemResource, RECIPE> recipeProcessInfo = processes.computeIfAbsent(inputStack, ICRecipeProcessInfo::new);
                recipeProcessInfo.processes.add(processInfo);
                recipeProcessInfo.totalCount += inputSlot.amountAsLong();
                if (recipeProcessInfo.lazyMinPerSlot == null && !CommonWorldTickHandler.flushTagAndRecipeCaches) {
                    // If we don't have a lazily initialized min per slot calculation set for it yet
                    // and our cache is not invalid/out of date due to a reload
                    CachedRecipe<RECIPE> cachedRecipe = getCachedRecipe(processInfo.process());
                    if (isCachedRecipeValid(cachedRecipe, inputStack)) {
                        recipeProcessInfo.recipe = cachedRecipe.getRecipe();
                        // And our current process has a cached recipe then set the lazily initialized per slot value
                        // Note: If something goes wrong, and we end up with zero as how much we need as an input
                        // we just bump the value up to one to make sure we properly handle it
                        recipeProcessInfo.lazyMinPerSlot = (info, factory) -> factory.getNeededInput(info.recipe, info.item);
                    }
                }
            }
        }
        if (processes.isEmpty()) {
            // If all input slots are empty, just exit
            return;
        }
        for (Map.Entry<ItemResource, ICRecipeProcessInfo<ItemResource, RECIPE>> entry : processes.entrySet()) {
            ICRecipeProcessInfo<ItemResource, RECIPE> recipeProcessInfo = entry.getValue();
            if (recipeProcessInfo.lazyMinPerSlot == null) {
                // If we don't have a lazy initializer for our minPerSlot setup, that means that there is
                // no valid cached recipe for any of the slots of this type currently, so we want to try and
                // get the recipe we will have for the first slot, once we end up with more items in the stack
                recipeProcessInfo.lazyMinPerSlot = (info, factory) -> {
                    // Note: We put all of this logic in the lazy init, so that we don't actually call any of this
                    // until it is needed. That way if we have no empty slots and all our input slots are filled
                    // we don't do any extra processing here, and can properly short circuit
                    ICProcessInfo processInfo = info.processes.getFirst();
                    // Try getting a recipe for our input with a larger size, and update the cache if we find one
                    info.recipe = factory.getRecipeForInput(processInfo.process(), info.item, processInfo.outputTank(), true);
                    if (info.recipe != null) {
                        return factory.getNeededInput(info.recipe, info.item);
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

    protected void addEmptySlotsAsTargets(Map<ItemResource, ICRecipeProcessInfo<ItemResource, RECIPE>> processes, List<ICProcessInfo> emptyProcesses) {
        for (Map.Entry<ItemResource, ICRecipeProcessInfo<ItemResource, RECIPE>> entry : processes.entrySet()) {
            ICRecipeProcessInfo<ItemResource, RECIPE> recipeProcessInfo = entry.getValue();
            long minPerSlot = recipeProcessInfo.getMinPerSlot(this);
            long maxSlots = recipeProcessInfo.totalCount / minPerSlot;
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
            ItemResource sourceStack = entry.getKey();
            long emptyToAdd = maxSlots - processCount;
            int added = 0;
            List<ICProcessInfo> toRemove = new ArrayList<>();
            for (ICProcessInfo emptyProcess : emptyProcesses) {
                if (inputProducesOutput(emptyProcess.process(), sourceStack, emptyProcess.outputTank(), true)) {
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

    protected void distributeItems(Map<ItemResource, ICRecipeProcessInfo<ItemResource, RECIPE>> processes) {
        for (Map.Entry<ItemResource, ICRecipeProcessInfo<ItemResource, RECIPE>> entry : processes.entrySet()) {
            ICRecipeProcessInfo<ItemResource, RECIPE> recipeProcessInfo = entry.getValue();
            int processCount = recipeProcessInfo.processes.size();
            if (processCount == 1) {
                // If there is only one process with the item in it; short-circuit, no balancing is needed
                continue;
            }
            ItemResource item = entry.getKey();
            // Note: This isn't based on any limits the slot may have (but we currently don't have any reduced ones
            // here, so it doesn't matter)
            int maxStackSize = item.getMaxStackSize();
            long numberPerSlot = recipeProcessInfo.totalCount / processCount;
            if (numberPerSlot == maxStackSize) {
                // If all the slots are already maxed out; short-circuit, no balancing is needed
                continue;
            }
            long remainder = recipeProcessInfo.totalCount % processCount;
            long minPerSlot = recipeProcessInfo.getMinPerSlot(this);
            if (minPerSlot > 1) {
                long perSlotRemainder = numberPerSlot % minPerSlot;
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
                ICProcessInfo processInfo = recipeProcessInfo.processes.get(i);
                AdvancedFactoryInputInventorySlot inputSlot = processInfo.inputSlot();
                long sizeForSlot = numberPerSlot;
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
                inputSlot.setContents(item, sizeForSlot, null);
            }
        }
    }

    public record ICProcessInfo(int process, @NotNull AdvancedFactoryInputInventorySlot inputSlot,
                                @NotNull IChemicalTank outputTank) {}

    protected static class ICRecipeProcessInfo<ITEM, RECIPE extends MekanismRecipe<?>> {

        private final List<ICProcessInfo> processes = new ArrayList<>();
        @Nullable
        private ToIntBiFunction<ICRecipeProcessInfo<ITEM, RECIPE>, TileEntityItemToChemicalFactory<RECIPE>> lazyMinPerSlot;
        private final ITEM item;
        private RECIPE recipe;
        private long minPerSlot = 1;
        private long totalCount;

        public ICRecipeProcessInfo(ITEM item) {
            this.item = item;
        }

        public long getMinPerSlot(TileEntityItemToChemicalFactory<RECIPE> factory) {
            if (lazyMinPerSlot != null) {
                // Get the value lazily
                minPerSlot = Math.max(1, lazyMinPerSlot.applyAsInt(this, factory));
                lazyMinPerSlot = null;
            }
            return minPerSlot;
        }
    }
}
