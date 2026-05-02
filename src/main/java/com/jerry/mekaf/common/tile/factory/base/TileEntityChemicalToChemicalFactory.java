package com.jerry.mekaf.common.tile.factory.base;

import com.jerry.mekaf.common.upgrade.ChemicalToChemicalUpgradeData;

import com.jerry.mekmm.common.util.ChemicalStackMap;

import mekanism.api.Action;
import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
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
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.lookup.monitor.FactoryRecipeCacheLookupMonitor;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntBiFunction;

public abstract class TileEntityChemicalToChemicalFactory<RECIPE extends MekanismRecipe<?>> extends TileEntityAdvancedFactoryBase<RECIPE> {

    protected CCProcessInfo[] processInfoSlots;
    protected IChemicalTank[] outputTank;
    protected IChemicalTank[] inputTank;

    protected int baselineMaxOperations = 1;

    public List<IChemicalTank> inputChemicalTanks;
    public List<IChemicalTank> outputChemicalTanks;

    protected TileEntityChemicalToChemicalFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes);
        inputChemicalTanks = new ArrayList<>();
        outputChemicalTanks = new ArrayList<>();

        for (CCProcessInfo info : processInfoSlots) {
            inputChemicalTanks.add(info.inputTank());
            outputChemicalTanks.add(info.outputTank());
        }

        // 172行的setupIOConfig()并不适用
        ConfigInfo chemicalConfig = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (chemicalConfig != null) {
            chemicalConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo(true, false, inputChemicalTanks));
            chemicalConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo(false, true, outputChemicalTanks));
            List<IChemicalTank> ioTank = outputChemicalTanks;
            ioTank.addAll(inputChemicalTanks);
            chemicalConfig.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo(true, true, ioTank));
        }
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            IInventorySlot extraSlot = getExtraSlot();
            if (extraSlot != null) {
                itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, extraSlot));
            }
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }
    }

    @Override
    protected void addTanks(ChemicalTankHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        inputTank = new IChemicalTank[tier.processes];
        outputTank = new IChemicalTank[tier.processes];
        chemicalInputHandlers = new IInputHandler[tier.processes];
        chemicalOutputHandlers = new IOutputHandler[tier.processes];
        processInfoSlots = new CCProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            FactoryRecipeCacheLookupMonitor<RECIPE> lookupMonitor = recipeCacheLookupMonitors[i];
            IContentsListener updateSortingAndUnpause = () -> {
                updateSortingListener.onContentsChanged();
                lookupMonitor.unpause();
            };
            int index = i;
            outputTank[i] = BasicChemicalTank.output(MAX_CHEMICAL * tier.processes, updateSortingAndUnpause);
            inputTank[i] = BasicChemicalTank.create(MAX_CHEMICAL * tier.processes,
                    // 这个type似乎没什么用，就不增加isValidInputChemical的参数了
                    ChemicalTankHelper.radioactiveInputTankPredicate(() -> outputTank[index]), (stack, type) -> isValidInputChemical(stack),
                    stack -> isChemicalValidForTank(stack) && inputProducesOutput(index, stack, outputTank[index], false),
                    ChemicalAttributeValidator.ALWAYS_ALLOW, recipeCacheLookupMonitors[index]);
            builder.addTank(inputTank[i]);
            builder.addTank(outputTank[i]);
            chemicalInputHandlers[i] = InputHelper.getInputHandler(inputTank[i], RecipeError.NOT_ENOUGH_INPUT);
            chemicalOutputHandlers[i] = OutputHelper.getOutputHandler(outputTank[i], RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
            processInfoSlots[i] = new CCProcessInfo(i, inputTank[i], outputTank[i]);
        }
    }

    @Override
    public int UpperTankCount() {
        return 1;
    }

    @Override
    public int getTankCount() {
        return 2;
    }

    public boolean inputProducesOutput(int process, @NotNull ChemicalStack fallbackInput, @NotNull IChemicalTank outputTank, boolean updateCache) {
        return outputTank.isEmpty() || getRecipeForInput(process, fallbackInput, outputTank, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected abstract boolean isCachedRecipeValid(@Nullable CachedRecipe<RECIPE> cached, @NotNull ChemicalStack stack);

    @Nullable
    protected RECIPE getRecipeForInput(int process, @NotNull ChemicalStack fallbackInput, @NotNull IChemicalTank outputTank, boolean updateCache) {
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
    protected abstract RECIPE findRecipe(int process, @NotNull ChemicalStack fallbackInput, @NotNull IChemicalTank outputTank);

    public abstract boolean isChemicalValidForTank(@NotNull ChemicalStack stack);

    /**
     * Like isItemValidForSlot makes no assumptions about current stored types
     */
    public abstract boolean isValidInputChemical(@NotNull ChemicalStack stack);

    protected abstract int getNeededInput(RECIPE recipe, ChemicalStack inputStack);

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            baselineMaxOperations = (int) Math.pow(2, upgradeComponent.getUpgrades(Upgrade.SPEED));
        }
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider) {
        if (upgradeData instanceof ChemicalToChemicalUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            getEnergyContainer().setEnergy(data.energyContainer.getEnergy());
            sorting = data.sorting;
            ContainerType.ITEM.copy(data.energySlot, energySlot);
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputTanks.size(); i++) {
                // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
                ContainerType.CHEMICAL.copy(data.inputTanks.get(i), inputChemicalTanks.get(i));
            }
            for (int i = 0; i < data.outputTanks.size(); i++) {
                outputChemicalTanks.get(i).setStack(data.outputTanks.get(i).getStack());
            }
            readUpgradeComponents(provider, data.components);
        } else {
            super.parseUpgradeData(upgradeData, provider);
        }
    }

    // Methods relating to IComputerTile
    @ComputerMethod
    ChemicalStack getInput(int process) throws ComputerException {
        validateValidProcess(process);
        return processInfoSlots[process].inputTank().getStack();
    }

    @ComputerMethod
    IChemicalTank getInputTank(int process) throws ComputerException {
        validateValidProcess(process);
        return inputTank[process];
    }

    @ComputerMethod
    ChemicalStack getOutput(int process) throws ComputerException {
        validateValidProcess(process);
        return processInfoSlots[process].outputTank().getStack();
    }

    @ComputerMethod
    IChemicalTank getOutputTank(int process) throws ComputerException {
        validateValidProcess(process);
        return outputTank[process];
    }
    // End methods IComputerTile

    @Override
    protected void sortInventoryOrTank() {
        Map<ChemicalStack, CCRecipeProcessInfo<RECIPE>> processes = ChemicalStackMap.createTypeAndComponentsMap();
        List<CCProcessInfo> emptyProcesses = new ArrayList<>();
        for (CCProcessInfo processInfo : processInfoSlots) {
            IChemicalTank inputTank = processInfo.inputTank();
            if (inputTank.isEmpty()) {
                emptyProcesses.add(processInfo);
            } else {
                ChemicalStack inputStack = inputTank.getStack();
                CCRecipeProcessInfo<RECIPE> recipeProcessInfo = processes.computeIfAbsent(inputStack, i -> new CCRecipeProcessInfo<>());
                recipeProcessInfo.processes.add(processInfo);
                recipeProcessInfo.totalAmount += inputStack.amount();
                if (recipeProcessInfo.lazyMinPerTank == null && !CommonWorldTickHandler.flushTagAndRecipeCaches) {
                    // If we don't have a lazily initialized min per slot calculation set for it yet
                    // and our cache is not invalid/out of date due to a reload
                    CachedRecipe<RECIPE> cachedRecipe = getCachedRecipe(processInfo.process());
                    if (isCachedRecipeValid(cachedRecipe, inputStack)) {
                        recipeProcessInfo.item = inputStack;
                        recipeProcessInfo.recipe = cachedRecipe.getRecipe();
                        // And our current process has a cached recipe then set the lazily initialized per slot value
                        // Note: If something goes wrong, and we end up with zero as how much we need as an input
                        // we just bump the value up to one to make sure we properly handle it
                        recipeProcessInfo.lazyMinPerTank = (info, factory) -> factory.getNeededInput(info.recipe, (ChemicalStack) info.item);
                    }
                }
            }
        }
        if (processes.isEmpty()) {
            // If all input slots are empty, just exit
            return;
        }
        for (Map.Entry<ChemicalStack, CCRecipeProcessInfo<RECIPE>> entry : processes.entrySet()) {
            CCRecipeProcessInfo<RECIPE> recipeProcessInfo = entry.getValue();
            if (recipeProcessInfo.lazyMinPerTank == null) {
                recipeProcessInfo.item = entry.getKey();
                // If we don't have a lazy initializer for our minPerSlot setup, that means that there is
                // no valid cached recipe for any of the slots of this type currently, so we want to try and
                // get the recipe we will have for the first slot, once we end up with more items in the stack
                recipeProcessInfo.lazyMinPerTank = (info, factory) -> {
                    // Note: We put all of this logic in the lazy init, so that we don't actually call any of this
                    // until it is needed. That way if we have no empty slots and all our input slots are filled
                    // we don't do any extra processing here, and can properly short circuit
                    ChemicalStack item = (ChemicalStack) info.item;
                    ChemicalStack largerInput = item.copyWithAmount(Math.min(MAX_CHEMICAL * tier.processes, info.totalAmount));
                    CCProcessInfo processInfo = info.processes.getFirst();
                    // Try getting a recipe for our input with a larger size, and update the cache if we find one
                    info.recipe = factory.getRecipeForInput(processInfo.process(), largerInput, processInfo.outputTank(), true);
                    if (info.recipe != null) {
                        return factory.getNeededInput(info.recipe, largerInput);
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

    protected void addEmptyTanksAsTargets(Map<ChemicalStack, CCRecipeProcessInfo<RECIPE>> processes, List<CCProcessInfo> emptyProcesses) {
        for (Map.Entry<ChemicalStack, CCRecipeProcessInfo<RECIPE>> entry : processes.entrySet()) {
            CCRecipeProcessInfo<RECIPE> recipeProcessInfo = entry.getValue();
            long minPerTank = recipeProcessInfo.getMinPerTank(this);
            long maxTanks = recipeProcessInfo.totalAmount / minPerTank;
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
            ChemicalStack sourceStack = entry.getKey();
            long emptyToAdd = maxTanks - processAmount;
            int added = 0;
            List<CCProcessInfo> toRemove = new ArrayList<>();
            for (CCProcessInfo emptyProcess : emptyProcesses) {
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

    protected void distributeItems(Map<ChemicalStack, CCRecipeProcessInfo<RECIPE>> processes) {
        for (Map.Entry<ChemicalStack, CCRecipeProcessInfo<RECIPE>> entry : processes.entrySet()) {
            CCRecipeProcessInfo<RECIPE> recipeProcessInfo = entry.getValue();
            long processAmount = recipeProcessInfo.processes.size();
            if (processAmount == 1) {
                // If there is only one process with the item in it; short-circuit, no balancing is needed
                continue;
            }
            ChemicalStack item = entry.getKey();
            // Note: This isn't based on any limits the tank may have (but we currently don't have any reduced ones
            // here, so it doesn't matter)
            long maxAmount = MAX_CHEMICAL * tier.processes;
            long numberPerTank = recipeProcessInfo.totalAmount / processAmount;
            if (numberPerTank == maxAmount) {
                // If all the slots are already maxed out; short-circuit, no balancing is needed
                continue;
            }
            long remainder = recipeProcessInfo.totalAmount % processAmount;
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
                    // processAmount * maxAmount - 1 items being distributed and
                    // have: processAmount * numberPerTank + remainder
                    // which means that our remainder is actually at most:
                    // processAmount * (maxAmount - numberPerTank) - 1
                    // so we can safely set our per slot distribution to maxAmount - numberPerTank
                    minPerTank = maxAmount - numberPerTank;
                }
            }
            for (int i = 0; i < processAmount; i++) {
                CCProcessInfo processInfo = recipeProcessInfo.processes.get(i);
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
                if (inputTank.isEmpty()) {
                    // Note: sizeForTank should never be zero here as we would not have added
                    // the empty slot to this item's distribution grouping if it would not
                    // end up getting any items; check it just in case though before creating
                    // a stack for the slot and setting it
                    if (sizeForTank > 0) {
                        // Note: We use setStackUnchecked here, as there is a very small chance that
                        // the stack is not actually valid for the slot because of a reload causing
                        // recipes to change. If this is the case, then we want to properly not crash,
                        // but we would rather not add any extra overhead about revalidating the item
                        // each time as it can get somewhat expensive.
                        inputTank.setStackUnchecked(item.copyWithAmount(sizeForTank));
                    }
                } else {
                    // Slot is not currently empty
                    if (sizeForTank == 0) {
                        // If the amount of the item we want to set it to is zero (all got used by earlier stacks, which
                        // might
                        // happen if the recipe requires a stacked input (minPerTank > 1)), then we need to set the slot
                        // to empty
                        inputTank.setEmpty();
                    } else if (inputTank.getCapacity() != sizeForTank) {
                        // Otherwise, if our slot doesn't already contain the amount we want it to,
                        // we need to adjust how much is stored in it, and log an error if it changed
                        // by a different amount then we expected
                        // Note: We use setStackSize here rather than setStack to avoid an unnecessary stack copy call
                        // as copying item stacks can sometimes be rather expensive in a heavily modded environment
                        MekanismUtils.logMismatchedStackSize(sizeForTank, inputTank.setStackSize(sizeForTank, Action.EXECUTE));
                    }
                }
            }
        }
    }

    public record CCProcessInfo(int process, @NotNull IChemicalTank inputTank, @NotNull IChemicalTank outputTank) {}

    protected static class CCRecipeProcessInfo<RECIPE extends MekanismRecipe<?>> {

        private final List<CCProcessInfo> processes = new ArrayList<>();
        @Nullable
        private ToIntBiFunction<CCRecipeProcessInfo<RECIPE>, TileEntityChemicalToChemicalFactory<RECIPE>> lazyMinPerTank;
        private Object item;
        private RECIPE recipe;
        private long minPerTank = 1;
        private long totalAmount;

        public long getMinPerTank(TileEntityChemicalToChemicalFactory<RECIPE> factory) {
            if (lazyMinPerTank != null) {
                // Get the value lazily
                minPerTank = Math.max(1, lazyMinPerTank.applyAsInt(this, factory));
                lazyMinPerTank = null;
            }
            return minPerTank;
        }
    }
}
