package com.jerry.mekaf.common.tile.base;

import com.jerry.mekaf.common.inventory.slot.AdvancedFactoryInputInventorySlot;
import com.jerry.mekaf.common.upgrade.ItemToMergedUpgradeData;

import mekanism.api.Action;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.BoxedChemicalOutputHandler;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.lib.inventory.HashedItem;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.IntSupplier;

public abstract class TileEntityItemToMergedFactory<RECIPE extends MekanismRecipe> extends TileEntityAdvancedFactoryBase<RECIPE> {

    private static final long MAX_CHEMICAL = 10_000;

    protected ItemToMergedProcessInfo[] processInfoSlots;
    AdvancedFactoryInputInventorySlot[] inputSlot;
    MergedChemicalTank[] outputTank;

    public final List<IInventorySlot> inputItemSlots;
    public final List<MergedChemicalTank> outputChemicalTanks;
    public final List<IGasTank> outputGasTanks;
    public final List<IInfusionTank> outputInfusionTanks;
    public final List<IPigmentTank> outputPigmentTanks;
    public final List<ISlurryTank> outputSlurryTanks;

    protected TileEntityItemToMergedFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, Set<CachedRecipe.OperationTracker.RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes);
        inputItemSlots = new ArrayList<>();
        outputChemicalTanks = new ArrayList<>();

        processInfoSlots = new ItemToMergedProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            processInfoSlots[i] = new ItemToMergedProcessInfo(i, inputSlot[i], outputTank[i]);
        }

        for (ItemToMergedProcessInfo info : processInfoSlots) {
            inputItemSlots.add(info.inputSlot());
            outputChemicalTanks.add(info.outputTank());
        }

        addSupported(TransmissionType.GAS, TransmissionType.INFUSION, TransmissionType.PIGMENT, TransmissionType.SLURRY);
        // 初始化其他储罐
        outputGasTanks = new ArrayList<>();
        outputInfusionTanks = new ArrayList<>();
        outputPigmentTanks = new ArrayList<>();
        outputSlurryTanks = new ArrayList<>();
        for (MergedChemicalTank tank : outputChemicalTanks) {
            outputGasTanks.add(tank.getGasTank());
            outputInfusionTanks.add(tank.getInfusionTank());
            outputPigmentTanks.add(tank.getPigmentTank());
            outputSlurryTanks.add(tank.getSlurryTank());
        }
        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.GAS);
        if (gasConfig != null) {
            gasConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo.GasSlotInfo(false, true, outputGasTanks));
            gasConfig.fill(DataType.INPUT);
            gasConfig.setDataType(DataType.OUTPUT, RelativeSide.RIGHT);
            gasConfig.setEjecting(true);
        }
        ConfigInfo infusionConfig = configComponent.getConfig(TransmissionType.INFUSION);
        if (infusionConfig != null) {
            infusionConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo.InfusionSlotInfo(false, true, outputInfusionTanks));
            infusionConfig.setDataType(DataType.OUTPUT, RelativeSide.RIGHT);
            infusionConfig.setEjecting(true);
        }
        ConfigInfo pigmentConfig = configComponent.getConfig(TransmissionType.PIGMENT);
        if (pigmentConfig != null) {
            pigmentConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo.PigmentSlotInfo(false, true, outputPigmentTanks));
            pigmentConfig.setDataType(DataType.OUTPUT, RelativeSide.RIGHT);
            pigmentConfig.setEjecting(true);
        }
        ConfigInfo slurryConfig = configComponent.getConfig(TransmissionType.SLURRY);
        if (slurryConfig != null) {
            slurryConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo.SlurrySlotInfo(false, true, outputSlurryTanks));
            slurryConfig.setDataType(DataType.OUTPUT, RelativeSide.RIGHT);
            slurryConfig.setEjecting(true);
        }
        configComponent.setupItemIOConfig(inputItemSlots, Collections.emptyList(), energySlot, false);
    }

    @Override
    protected void presetVariables() {
        super.presetVariables();
        // 在初始化所有储罐之前
        outputTank = new MergedChemicalTank[tier.processes];
        mergedOutputHandlers = new BoxedChemicalOutputHandler[tier.processes];
        IContentsListener saveOnlyListener = this::markForSave;
        for (int i = 0; i < tier.processes; i++) {
            outputTank[i] = MergedChemicalTank.create(
                    ChemicalTankBuilder.GAS.output(MAX_CHEMICAL * tier.processes, getListener(SubstanceType.GAS, saveOnlyListener)),
                    ChemicalTankBuilder.INFUSION.output(MAX_CHEMICAL * tier.processes, getListener(SubstanceType.INFUSION, saveOnlyListener)),
                    ChemicalTankBuilder.PIGMENT.output(MAX_CHEMICAL * tier.processes, getListener(SubstanceType.PIGMENT, saveOnlyListener)),
                    ChemicalTankBuilder.SLURRY.output(MAX_CHEMICAL * tier.processes, getListener(SubstanceType.SLURRY, saveOnlyListener)));
            mergedOutputHandlers[i] = new BoxedChemicalOutputHandler(outputTank[i], CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        }
    }

    @Override
    protected void addGasTanks(ChemicalTankHelper<Gas, GasStack, IGasTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        for (int i = 0; i < tier.processes; i++) {
            builder.addTank(outputTank[i].getGasTank());

        }
    }

    @Override
    protected void addInfusionTanks(ChemicalTankHelper<InfuseType, InfusionStack, IInfusionTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        for (int i = 0; i < tier.processes; i++) {
            builder.addTank(outputTank[i].getInfusionTank());
        }
    }

    @Override
    protected void addPigmentTanks(ChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        for (int i = 0; i < tier.processes; i++) {
            builder.addTank(outputTank[i].getPigmentTank());
        }
    }

    @Override
    protected void addSlurryTanks(ChemicalTankHelper<Slurry, SlurryStack, ISlurryTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        for (int i = 0; i < tier.processes; i++) {
            builder.addTank(outputTank[i].getSlurryTank());
        }
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        inputSlot = new AdvancedFactoryInputInventorySlot[tier.processes];
        itemInputHandlers = new IInputHandler[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            inputSlot[i] = AdvancedFactoryInputInventorySlot.create(this, i, outputTank[i], recipeCacheLookupMonitors[i], getXPos(i), 13);
            int index = i;
            builder.addSlot(inputSlot[i]).tracksWarnings(slot -> slot.warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT, index)));
            itemInputHandlers[i] = InputHelper.getInputHandler(inputSlot[i], CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT);
        }
    }

    public boolean inputProducesOutput(int process, @NotNull ItemStack fallbackInput, @NotNull MergedChemicalTank outputTank, boolean updateCache) {
        return outputTank.getAllTanks().isEmpty() || getRecipeForInput(process, fallbackInput, outputTank, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected abstract boolean isCachedRecipeValid(@Nullable CachedRecipe<RECIPE> cached, @NotNull ItemStack stack);

    @Nullable
    protected RECIPE getRecipeForInput(int process, @NotNull ItemStack fallbackInput, @Nullable MergedChemicalTank outputTank, boolean updateCache) {
        if (!CommonWorldTickHandler.flushTagAndRecipeCaches) {
            // If our recipe caches are valid, grab our cached recipe and see if it is still valid
            CachedRecipe<RECIPE> cached = getCachedRecipe(process);
            if (cached != null && isCachedRecipeValid(cached, fallbackInput)) {
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
    protected abstract RECIPE findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull MergedChemicalTank outputTanks);

    /**
     * Like isItemValidForSlot makes no assumptions about current stored types
     */
    public abstract boolean isValidInputItem(@NotNull ItemStack stack);

    protected abstract int getNeededInput(RECIPE recipe, ItemStack inputStack);

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof ItemToMergedUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            getEnergyContainer().setEnergy(data.energyContainer.getEnergy());
            sorting = data.sorting;
            energySlot.deserializeNBT(data.energySlot.serializeNBT());
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputSlots.size(); i++) {
                // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
                inputItemSlots.get(i).deserializeNBT(data.inputSlots.get(i).serializeNBT());
            }
            for (int i = 0; i < data.outputTanks.size(); i++) {
                outputChemicalTanks.get(i).getGasTank().setStack(data.outputTanks.get(i).getGasTank().getStack());
                outputChemicalTanks.get(i).getInfusionTank().setStack(data.outputTanks.get(i).getInfusionTank().getStack());
                outputChemicalTanks.get(i).getPigmentTank().setStack(data.outputTanks.get(i).getPigmentTank().getStack());
                outputChemicalTanks.get(i).getSlurryTank().setStack(data.outputTanks.get(i).getSlurryTank().getStack());
            }
            for (ITileComponent component : getComponents()) {
                component.read(data.components);
            }
        } else {
            super.parseUpgradeData(upgradeData);
        }
    }

    @Override
    protected void sortInventoryOrTank() {
        Map<HashedItem, ItemToFourMergedRecipeProcessInfo> processes = new HashMap<>();
        List<ItemToMergedProcessInfo> emptyProcesses = new ArrayList<>();
        for (ItemToMergedProcessInfo processInfo : processInfoSlots) {
            IInventorySlot inputSlot = processInfo.inputSlot();
            if (inputSlot.isEmpty()) {
                emptyProcesses.add(processInfo);
            } else {
                ItemStack inputStack = inputSlot.getStack();
                HashedItem item = HashedItem.raw(inputStack);
                ItemToFourMergedRecipeProcessInfo recipeProcessInfo = processes.computeIfAbsent(item, i -> new ItemToFourMergedRecipeProcessInfo());
                recipeProcessInfo.processes.add(processInfo);
                recipeProcessInfo.totalCount += inputStack.getCount();
                if (recipeProcessInfo.lazyMinPerSlot == null && !CommonWorldTickHandler.flushTagAndRecipeCaches) {
                    // If we don't have a lazily initialized min per slot calculation set for it yet
                    // and our cache is not invalid/out of date due to a reload
                    CachedRecipe<RECIPE> cachedRecipe = getCachedRecipe(processInfo.process());
                    if (isCachedRecipeValid(cachedRecipe, inputStack)) {
                        // And our current process has a cached recipe then set the lazily initialized per slot value
                        // Note: If something goes wrong, and we end up with zero as how much we need as an input
                        // we just bump the value up to one to make sure we properly handle it
                        recipeProcessInfo.lazyMinPerSlot = () -> Math.max(1, getNeededInput(cachedRecipe.getRecipe(), inputStack));
                    }
                }
            }
        }
        if (processes.isEmpty()) {
            // If all input slots are empty, just exit
            return;
        }
        for (Map.Entry<HashedItem, ItemToFourMergedRecipeProcessInfo> entry : processes.entrySet()) {
            ItemToFourMergedRecipeProcessInfo recipeProcessInfo = entry.getValue();
            if (recipeProcessInfo.lazyMinPerSlot == null) {
                // If we don't have a lazy initializer for our minPerSlot setup, that means that there is
                // no valid cached recipe for any of the slots of this type currently, so we want to try and
                // get the recipe we will have for the first slot, once we end up with more items in the stack
                recipeProcessInfo.lazyMinPerSlot = () -> {
                    // Note: We put all of this logic in the lazy init, so that we don't actually call any of this
                    // until it is needed. That way if we have no empty slots and all our input slots are filled
                    // we don't do any extra processing here, and can properly short circuit
                    HashedItem item = entry.getKey();
                    ItemStack largerInput = item.createStack(Math.min(item.getMaxStackSize(), recipeProcessInfo.totalCount));
                    ItemToMergedProcessInfo processInfo = recipeProcessInfo.processes.get(0);
                    // Try getting a recipe for our input with a larger size, and update the cache if we find one
                    RECIPE recipe = getRecipeForInput(processInfo.process(), largerInput, processInfo.outputTank, true);
                    if (recipe != null) {
                        return Math.max(1, getNeededInput(recipe, largerInput));
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

    private void addEmptySlotsAsTargets(Map<HashedItem, ItemToFourMergedRecipeProcessInfo> processes, List<ItemToMergedProcessInfo> emptyProcesses) {
        for (Map.Entry<HashedItem, ItemToFourMergedRecipeProcessInfo> entry : processes.entrySet()) {
            ItemToFourMergedRecipeProcessInfo recipeProcessInfo = entry.getValue();
            int minPerSlot = recipeProcessInfo.getMinPerSlot();
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
            ItemStack sourceStack = entry.getKey().getInternalStack();
            int emptyToAdd = maxSlots - processCount;
            int added = 0;
            List<ItemToMergedProcessInfo> toRemove = new ArrayList<>();
            for (ItemToMergedProcessInfo emptyProcess : emptyProcesses) {
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

    private void distributeItems(Map<HashedItem, ItemToFourMergedRecipeProcessInfo> processes) {
        for (Map.Entry<HashedItem, ItemToFourMergedRecipeProcessInfo> entry : processes.entrySet()) {
            ItemToFourMergedRecipeProcessInfo recipeProcessInfo = entry.getValue();
            int processCount = recipeProcessInfo.processes.size();
            if (processCount == 1) {
                // If there is only one process with the item in it; short-circuit, no balancing is needed
                continue;
            }
            HashedItem item = entry.getKey();
            // Note: This isn't based on any limits the slot may have (but we currently don't have any reduced ones
            // here, so it doesn't matter)
            int maxStackSize = item.getMaxStackSize();
            int numberPerSlot = recipeProcessInfo.totalCount / processCount;
            if (numberPerSlot == maxStackSize) {
                // If all the slots are already maxed out; short-circuit, no balancing is needed
                continue;
            }
            int remainder = recipeProcessInfo.totalCount % processCount;
            int minPerSlot = recipeProcessInfo.getMinPerSlot();
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
                ItemToMergedProcessInfo processInfo = recipeProcessInfo.processes.get(i);
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
                        inputSlot.setStackUnchecked(item.createStack(sizeForSlot));
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

    public record ItemToMergedProcessInfo(int process, @NotNull AdvancedFactoryInputInventorySlot inputSlot,
                                          @NotNull MergedChemicalTank outputTank) {}

    public static class ItemToFourMergedRecipeProcessInfo {

        private final List<ItemToMergedProcessInfo> processes = new ArrayList<>();
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
