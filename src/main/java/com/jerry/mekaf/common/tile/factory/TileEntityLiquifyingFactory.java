package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.inventory.slot.AdvancedFactoryInputInventorySlot;
import com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase;
import com.jerry.mekaf.common.upgrade.NutritionLiquifyingUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ItemStackToFluidOptionalItemRecipe;
import mekanism.api.recipes.basic.BasicItemStackToFluidOptionalItemRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.impl.NutritionalLiquifierIRecipe;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ItemRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleItem;
import mekanism.common.recipe.lookup.monitor.FactoryRecipeCacheLookupMonitor;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntBiFunction;

public class TileEntityLiquifyingFactory extends TileEntityAdvancedFactoryBase<BasicItemStackToFluidOptionalItemRecipe> implements ItemRecipeLookupHandler<BasicItemStackToFluidOptionalItemRecipe> {

    public static final RecipeError NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR = RecipeError.create();
    // 单个槽位报错，例如输入槽和输出槽
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    // GLOBAL对应要统一处理的错误例如这里的输出储罐，在监听时应该用GLOBAL声明的Error才能正常报错
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE);

    private NLProcessInfo[] processInfoSlots;

    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class,
                            methodNames = { "getOutput", "getOutputCapacity", "getOutputNeeded",
                                    "getOutputFilledPercentage" },
                            docPlaceholder = "output tank")
    public IFluidTank fluidTank;

    protected IOutputHandler<ItemStackToFluidOptionalItemRecipe.@NotNull FluidOptionalItemOutput>[] liquifiesOutputHandler;

    protected final List<IInventorySlot> inputItemSlots;
    protected final List<IInventorySlot> outputItemSlots;

    public TileEntityLiquifyingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        inputItemSlots = new ArrayList<>();
        outputItemSlots = new ArrayList<>();

        for (NLProcessInfo info : processInfoSlots) {
            inputItemSlots.add(info.inputSlot());
            outputItemSlots.add(info.outputSlot());
        }

        configComponent.setupItemIOConfig(inputItemSlots, outputItemSlots, energySlot, false);
        configComponent.setupOutputConfig(TransmissionType.FLUID, fluidTank);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.FLUID);
    }

    @Override
    protected @Nullable IContainerHolder<IFluidTank> getInitialFluidTanks(IContentsListener listener) {
        MekContainerHelper<IFluidTank> builder = MekContainerHelper.forSideWithFluidConfig(this);
        builder.addContainer(fluidTank = BasicFluidTank.output(MAX_FLUID * tier.processes, markAllMonitorsChanged(listener)));
        return builder.build();
    }

    @Override
    protected void addTanks(MekContainerHelper<IChemicalTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {}

    @Override
    protected void addSlots(MekContainerHelper<IInventorySlot> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        itemInputHandlers = new IInputHandler[tier.processes];
        liquifiesOutputHandler = new IOutputHandler[tier.processes];
        processInfoSlots = new NLProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            FactoryRecipeCacheLookupMonitor<BasicItemStackToFluidOptionalItemRecipe> lookupMonitor = recipeCacheLookupMonitors[i];
            IContentsListener updateSortingAndUnpause = () -> {
                updateSortingListener.onContentsChanged();
                lookupMonitor.unpause();
            };
            OutputInventorySlot outputSlot = OutputInventorySlot.at(updateSortingAndUnpause, getXPos(i), 57);
            // Note: As we are an item factory that has comparator's based on items we can just use the monitor as a
            // listener directly
            AdvancedFactoryInputInventorySlot inputSlot = AdvancedFactoryInputInventorySlot.create(this, i, outputSlot, fluidTank, recipeCacheLookupMonitors[i], getXPos(i), 13);
            int index = i;
            builder.addContainer(inputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT, index)));
            builder.addContainer(outputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR, index)));
            itemInputHandlers[i] = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT);
            liquifiesOutputHandler[i] = OutputHelper.getOutputHandler(fluidTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE, outputSlot, NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR);
            processInfoSlots[i] = new NLProcessInfo(i, inputSlot, outputSlot);
        }
    }

    public static boolean isValidInputStatic(ItemStack stack) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        return food != null && food.nutrition() > 0;
    }

    public boolean isValidInputItem(ItemStack stack) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        return food != null && food.nutrition() > 0;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, BasicItemStackToFluidOptionalItemRecipe, SingleItem<BasicItemStackToFluidOptionalItemRecipe>> getRecipeType() {
        return null;
    }

    @Override
    public IRecipeViewerRecipeType<BasicItemStackToFluidOptionalItemRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.NUTRITIONAL_LIQUIFICATION;
    }

    @Override
    public @Nullable BasicItemStackToFluidOptionalItemRecipe getRecipe(int cacheIndex) {
        return getRecipe(itemInputHandlers[cacheIndex].getInput());
    }

    @Nullable
    public static BasicItemStackToFluidOptionalItemRecipe getRecipe(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food == null || food.nutrition() <= 0) {
            // If the food provides no healing don't allow consuming it as it won't provide any paste
            return null;
        }
        UseRemainder remainder = stack.get(DataComponents.USE_REMAINDER);
        return new NutritionalLiquifierIRecipe(
                IngredientCreatorAccess.item().from(stack, 1),
                MekanismFluids.NUTRITIONAL_PASTE.asTemplate(food.nutrition() * 50),
                remainder == null ? null : remainder.convertInto());
    }

    @Override
    public @NotNull CachedRecipe<BasicItemStackToFluidOptionalItemRecipe> createNewCachedRecipe(@NotNull BasicItemStackToFluidOptionalItemRecipe recipe, int cacheIndex) {
        return new OneInputCachedRecipe<>(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], liquifiesOutputHandler[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(this::canFunction)
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks)
                .setBaselineMaxOperations(this::getOperationsPerTick);
    }

    public boolean inputProducesOutput(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @NotNull IFluidTank outputTank, boolean updateCache) {
        return outputTank.isEmpty() || getRecipeForInput(process, fallbackInput, outputSlot, outputTank, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<BasicItemStackToFluidOptionalItemRecipe> cached, @NotNull ItemStack stack) {
        // 不能使用cached.getRecipe().getInput().testType(stack)，会导致卡合成
        return cached != null && isValidInputStatic(stack);
    }

    @Contract("null, _ -> false")
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<BasicItemStackToFluidOptionalItemRecipe> cached, @NotNull ItemResource stack) {
        return cached != null && isValidInputStatic(stack.toStack());
    }

    @Nullable
    protected BasicItemStackToFluidOptionalItemRecipe getRecipeForInput(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @NotNull IFluidTank outputTank, boolean updateCache) {
        if (!CommonWorldTickHandler.flushTagAndRecipeCaches) {
            // If our recipe caches are valid, grab our cached recipe and see if it is still valid
            CachedRecipe<BasicItemStackToFluidOptionalItemRecipe> cached = getCachedRecipe(process);
            if (isCachedRecipeValid(cached, fallbackInput)) {
                // Our input matches the recipe we have cached for this slot
                return cached.getRecipe();
            }
        }
        // If there is no cached item input, or it doesn't match our fallback then it is an out of date cache, so we
        // ignore the fact that we have a cache
        BasicItemStackToFluidOptionalItemRecipe foundRecipe = findRecipe(process, fallbackInput, outputSlot, outputTank);
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
    protected BasicItemStackToFluidOptionalItemRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, IInventorySlot outputSlot, @NotNull IFluidTank inputTank) {
        return null;
    }

    protected int getNeededInput(BasicItemStackToFluidOptionalItemRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider, TransactionContext transaction) {
        if (upgradeData instanceof NutritionLiquifyingUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            energyContainer.copyContents(data.energyContainer, transaction);
            sorting = data.sorting;
            energySlot.copyContents(data.energySlot, transaction);
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputSlots.size(); i++) {
                inputItemSlots.get(i).copyContents(data.inputSlots.get(i), transaction);
            }
            for (int i = 0; i < data.outputSlots.size(); i++) {
                outputItemSlots.get(i).copyContents(data.outputSlots.get(i), transaction);
            }
            readUpgradeComponents(provider, data.components);
            fluidTank.copyContents(data.fluidTank, transaction);
        } else {
            super.parseUpgradeData(upgradeData, provider, transaction);
        }
    }

    @Override
    public @Nullable NutritionLiquifyingUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new NutritionLiquifyingUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), progress,
                energySlot, inputItemSlots, outputItemSlots, fluidTank, isSorting(), getComponents(), problemPath());
    }

    // Methods relating to IComputerTile
    @ComputerMethod
    ItemStack getInput(int process) throws ComputerException {
        validateValidProcess(process);
        IInventorySlot inputSlot = processInfoSlots[process].inputSlot();
        return inputSlot.resource().toStack(inputSlot.amountAsInt());
    }

    @ComputerMethod
    ItemStack getOutput(int process) throws ComputerException {
        validateValidProcess(process);
        IInventorySlot outputSlot = processInfoSlots[process].outputSlot();
        return outputSlot.resource().toStack(outputSlot.amountAsInt());
    }
    // End methods IComputerTile

    @Override
    protected void sortInventoryOrTank() {
        Map<ItemResource, NLRecipeProcessInfo> processes = new HashMap<>();
        List<NLProcessInfo> emptyProcesses = new ArrayList<>();
        for (NLProcessInfo processInfo : processInfoSlots) {
            IInventorySlot inputSlot = processInfo.inputSlot();
            if (inputSlot.isEmpty()) {
                emptyProcesses.add(processInfo);
            } else {
                ItemResource inputStack = inputSlot.resource();
                NLRecipeProcessInfo recipeProcessInfo = processes.computeIfAbsent(inputStack, i -> new NLRecipeProcessInfo());
                recipeProcessInfo.processes.add(processInfo);
                recipeProcessInfo.totalCount += inputSlot.amountAsLong();
                if (recipeProcessInfo.lazyMinPerSlot == null && !CommonWorldTickHandler.flushTagAndRecipeCaches) {
                    // If we don't have a lazily initialized min per slot calculation set for it yet
                    // and our cache is not invalid/out of date due to a reload
                    CachedRecipe<BasicItemStackToFluidOptionalItemRecipe> cachedRecipe = getCachedRecipe(processInfo.process());
                    if (isCachedRecipeValid(cachedRecipe, inputStack)) {
                        recipeProcessInfo.item = inputStack;
                        recipeProcessInfo.recipe = cachedRecipe.getRecipe();
                        // And our current process has a cached recipe then set the lazily initialized per slot value
                        // Note: If something goes wrong, and we end up with zero as how much we need as an input
                        // we just bump the value up to one to make sure we properly handle it
                        recipeProcessInfo.lazyMinPerSlot = (info, factory) -> factory.getNeededInput(info.recipe, info.item.toStack());
                    }
                }
            }
        }
        if (processes.isEmpty()) {
            // If all input slots are empty, just exit
            return;
        }
        for (Map.Entry<ItemResource, NLRecipeProcessInfo> entry : processes.entrySet()) {
            NLRecipeProcessInfo recipeProcessInfo = entry.getValue();
            if (recipeProcessInfo.lazyMinPerSlot == null) {
                recipeProcessInfo.item = entry.getKey();
                // If we don't have a lazy initializer for our minPerSlot setup, that means that there is
                // no valid cached recipe for any of the slots of this type currently, so we want to try and
                // get the recipe we will have for the first slot, once we end up with more items in the stack
                recipeProcessInfo.lazyMinPerSlot = (info, factory) -> {
                    // Note: We put all of this logic in the lazy init, so that we don't actually call any of this
                    // until it is needed. That way if we have no empty slots and all our input slots are filled
                    // we don't do any extra processing here, and can properly short circuit
                    ItemResource item = info.item;
                    ItemStack largerInput = item.toStack(Math.min(item.getMaxStackSize(), MathUtils.clampToInt(info.totalCount)));
                    NLProcessInfo processInfo = info.processes.getFirst();
                    // Try getting a recipe for our input with a larger size, and update the cache if we find one
                    info.recipe = factory.getRecipeForInput(processInfo.process(), largerInput, processInfo.outputSlot(), fluidTank, true);
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

    private void addEmptySlotsAsTargets(Map<ItemResource, NLRecipeProcessInfo> processes, List<NLProcessInfo> emptyProcesses) {
        for (Map.Entry<ItemResource, NLRecipeProcessInfo> entry : processes.entrySet()) {
            NLRecipeProcessInfo recipeProcessInfo = entry.getValue();
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
            ItemStack sourceStack = entry.getKey().toStack();
            long emptyToAdd = maxSlots - processCount;
            int added = 0;
            List<NLProcessInfo> toRemove = new ArrayList<>();
            for (NLProcessInfo emptyProcess : emptyProcesses) {
                if (inputProducesOutput(emptyProcess.process(), sourceStack, emptyProcess.outputSlot(), fluidTank, true)) {
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

    private void distributeItems(Map<ItemResource, NLRecipeProcessInfo> processes) {
        for (Map.Entry<ItemResource, NLRecipeProcessInfo> entry : processes.entrySet()) {
            NLRecipeProcessInfo recipeProcessInfo = entry.getValue();
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
                NLProcessInfo processInfo = recipeProcessInfo.processes.get(i);
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

    public record NLProcessInfo(int process, @NotNull AdvancedFactoryInputInventorySlot inputSlot,
                                @NotNull IInventorySlot outputSlot) {}

    protected static class NLRecipeProcessInfo {

        private final List<NLProcessInfo> processes = new ArrayList<>();
        @Nullable
        private ToIntBiFunction<NLRecipeProcessInfo, TileEntityLiquifyingFactory> lazyMinPerSlot;
        private ItemResource item;
        private BasicItemStackToFluidOptionalItemRecipe recipe;
        private long minPerSlot = 1;
        private long totalCount;

        public long getMinPerSlot(TileEntityLiquifyingFactory factory) {
            if (lazyMinPerSlot != null) {
                // Get the value lazily
                minPerSlot = Math.max(1, lazyMinPerSlot.applyAsInt(this, factory));
                lazyMinPerSlot = null;
            }
            return minPerSlot;
        }
    }
}
