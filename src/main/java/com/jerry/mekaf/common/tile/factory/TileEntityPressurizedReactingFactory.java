package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.inventory.slot.AdvancedFactoryInputInventorySlot;
import com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase;
import com.jerry.mekaf.common.upgrade.PRCUpgradeData;

import mekanism.api.Action;
import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.radiation.IRadiationManager;
import mekanism.api.recipes.PressurizedReactionRecipe;
import mekanism.api.recipes.PressurizedReactionRecipe.PressurizedReactionRecipeOutput;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.PressurizedReactionCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.api.recipes.vanilla_input.ReactionRecipeInput;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ITripleRecipeLookupHandler.ItemFluidChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.ItemFluidChemical;
import mekanism.common.recipe.lookup.monitor.FactoryRecipeCacheLookupMonitor;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.MekanismUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.ItemStackMap;
import net.neoforged.neoforge.fluids.FluidStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntBiFunction;

public class TileEntityPressurizedReactingFactory extends TileEntityAdvancedFactoryBase<PressurizedReactionRecipe, AdvancedFactoryType> implements IHasDumpButton,
                                                  ItemFluidChemicalRecipeLookupHandler<PressurizedReactionRecipe> {

    public static final RecipeError NOT_ENOUGH_ITEM_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_FLUID_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_CHEMICAL_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR = RecipeError.create();
    // 单个槽位报错，例如输入槽和输出槽
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            NOT_ENOUGH_ITEM_INPUT_ERROR,
            NOT_ENOUGH_FLUID_INPUT_ERROR,
            NOT_ENOUGH_CHEMICAL_INPUT_ERROR,
            NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR,
            NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    // GLOBAL对应要统一处理的错误例如这里的输出储罐，在监听时应该用GLOBAL声明的Error才能正常报错
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            NOT_ENOUGH_FLUID_INPUT_ERROR,
            NOT_ENOUGH_CHEMICAL_INPUT_ERROR,
            NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR);

    private static final int BASE_DURATION = 5 * SharedConstants.TICKS_PER_SECOND;

    private PRCProcessInfo[] processInfoSlots;

    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class,
                            methodNames = { "getInputFluid", "getInputFluidCapacity", "getInputFluidNeeded",
                                    "getInputFluidFilledPercentage" },
                            docPlaceholder = "fluid input")
    public BasicFluidTank inputFluidTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getInputGas", "getInputGasCapacity", "getInputGasNeeded",
                                    "getInputGasFilledPercentage" },
                            docPlaceholder = "gas input")
    public IChemicalTank inputChemicalTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getOutputGas", "getOutputGasCapacity", "getOutputGasNeeded",
                                    "getOutputGasFilledPercentage" },
                            docPlaceholder = "gas output")
    public IChemicalTank outputChemicalTank;

    private long recipeEnergyRequired = 0;
    private final IInputHandler<@NotNull FluidStack> fluidInputHandler;
    private final IInputHandler<@NotNull ChemicalStack> chemicalInputHandler;
    protected IOutputHandler<@NotNull PressurizedReactionRecipeOutput>[] reactionOutputHandlers;

    protected final List<IInventorySlot> inputItemSlots;
    protected final List<IInventorySlot> outputItemSlots;

    public TileEntityPressurizedReactingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES, Attribute.getOrThrow(blockProvider, AttributeAdvancedFactoryType.class).getAdvancedFactoryType());
        inputItemSlots = new ArrayList<>();
        outputItemSlots = new ArrayList<>();

        for (PRCProcessInfo info : processInfoSlots) {
            inputItemSlots.add(info.inputSlot());
            outputItemSlots.add(info.outputSlot());
        }

        configComponent.setupItemIOConfig(inputItemSlots, outputItemSlots, energySlot, false);
        configComponent.setupInputConfig(TransmissionType.FLUID, inputFluidTank);
        ConfigInfo config = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (config != null) {
            config.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo(true, true, inputChemicalTank));
            config.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo(false, true, outputChemicalTank));
            config.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo(true, true, List.of(inputChemicalTank, outputChemicalTank)));
        }

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                .setCanTankEject(tank -> tank != inputChemicalTank);

        fluidInputHandler = InputHelper.getInputHandler(inputFluidTank, NOT_ENOUGH_FLUID_INPUT_ERROR);
        chemicalInputHandler = InputHelper.getInputHandler(inputChemicalTank, NOT_ENOUGH_CHEMICAL_INPUT_ERROR);
    }

    @Override
    protected void addTanks(ChemicalTankHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        builder.addTank(inputChemicalTank = BasicChemicalTank.createModern(MAX_CHEMICAL * tier.processes, ChemicalTankHelper.radioactiveInputTankPredicate(() -> outputChemicalTank),
                ConstantPredicates.alwaysTrueBi(), this::containsRecipeC, ChemicalAttributeValidator.ALWAYS_ALLOW, markAllMonitorsChanged(listener)));
        builder.addTank(outputChemicalTank = BasicChemicalTank.output(MAX_CHEMICAL * tier.processes, markAllMonitorsChanged(listener)));
    }

    @Override
    protected @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSideWithConfig(this);
        builder.addTank(inputFluidTank = BasicFluidTank.input(MAX_FLUID * tier.processes, ConstantPredicates.alwaysTrue(),
                this::containsRecipeB, markAllMonitorsChanged(listener)));
        return builder.build();
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        itemInputHandlers = new IInputHandler[tier.processes];
        reactionOutputHandlers = new IOutputHandler[tier.processes];
        processInfoSlots = new PRCProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            FactoryRecipeCacheLookupMonitor<PressurizedReactionRecipe> lookupMonitor = recipeCacheLookupMonitors[i];
            IContentsListener updateSortingAndUnpause = () -> {
                updateSortingListener.onContentsChanged();
                lookupMonitor.unpause();
            };
            OutputInventorySlot outputSlot = OutputInventorySlot.at(updateSortingAndUnpause, getXPos(i), 57);
            // Note: As we are an item factory that has comparator's based on items we can just use the monitor as a
            // listener directly
            AdvancedFactoryInputInventorySlot inputSlot = AdvancedFactoryInputInventorySlot.create(this, i, outputSlot, outputChemicalTank, recipeCacheLookupMonitors[i], getXPos(i), 13);
            int index = i;
            builder.addSlot(inputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(NOT_ENOUGH_ITEM_INPUT_ERROR, index)));
            builder.addSlot(outputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR, index)));
            itemInputHandlers[i] = InputHelper.getInputHandler(inputSlot, NOT_ENOUGH_ITEM_INPUT_ERROR);
            reactionOutputHandlers[i] = OutputHelper.getOutputHandler(outputSlot, NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR, outputChemicalTank, NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR);
            processInfoSlots[i] = new PRCProcessInfo(i, inputSlot, outputSlot);
        }
    }

    @Override
    public int getBarCount() {
        return 2;
    }

    @Override
    public void onCachedRecipeChanged(@Nullable CachedRecipe<PressurizedReactionRecipe> cachedRecipe, int cacheIndex) {
        super.onCachedRecipeChanged(cachedRecipe, cacheIndex);
        int recipeDuration;
        if (cachedRecipe == null) {
            recipeDuration = BASE_DURATION;
            recipeEnergyRequired = 0L;
        } else {
            PressurizedReactionRecipe recipe = cachedRecipe.getRecipe();
            recipeDuration = recipe.getDuration();
            recipeEnergyRequired = recipe.getEnergyRequired();
        }
        boolean update = getTicksRequired() != recipeDuration;
        setTicksRequired(recipeDuration);
        if (update) {
            recalculateUpgrades(Upgrade.SPEED);
        }
        // Ensure we take our recipe's energy per tick into account
        energyContainer.updateEnergyPerTick();
    }

    @Override
    public long getRecipeEnergyRequired() {
        return recipeEnergyRequired;
    }

    @Override
    public IChemicalTank getChemicalTankBar() {
        return inputChemicalTank;
    }

    public BasicFluidTank getFluidTankBar() {
        return inputFluidTank;
    }

    @Override
    public boolean hasExtraResourceBar() {
        return true;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<ReactionRecipeInput, PressurizedReactionRecipe, ItemFluidChemical<PressurizedReactionRecipe>> getRecipeType() {
        return MekanismRecipeType.REACTION;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<PressurizedReactionRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.REACTION;
    }

    @Override
    public @Nullable PressurizedReactionRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandlers[cacheIndex], fluidInputHandler, chemicalInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<PressurizedReactionRecipe> createNewCachedRecipe(@NotNull PressurizedReactionRecipe recipe, int cacheIndex) {
        return new PressurizedReactionCachedRecipe(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], fluidInputHandler, chemicalInputHandler, reactionOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(this::canFunction)
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks)
                .setBaselineMaxOperations(this::getOperationsPerTick);
    }

    public boolean inputProducesOutput(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @NotNull IChemicalTank outputTank, boolean updateCache) {
        return outputTank.isEmpty() || getRecipeForInput(process, fallbackInput, outputSlot, outputTank, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<PressurizedReactionRecipe> cached, @NotNull ItemStack stack) {
        if (cached != null) {
            PressurizedReactionRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getInputSolid().testType(stack) &&
                    (inputFluidTank.isEmpty() || cachedRecipe.getInputFluid().testType(inputFluidTank.getFluid())) &&
                    (inputChemicalTank.isEmpty() || cachedRecipe.getInputChemical().testType(inputChemicalTank.getStack()));
        }
        return false;
    }

    @Nullable
    protected PressurizedReactionRecipe getRecipeForInput(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @NotNull IChemicalTank outputTank, boolean updateCache) {
        if (!CommonWorldTickHandler.flushTagAndRecipeCaches) {
            // If our recipe caches are valid, grab our cached recipe and see if it is still valid
            CachedRecipe<PressurizedReactionRecipe> cached = getCachedRecipe(process);
            if (isCachedRecipeValid(cached, fallbackInput)) {
                // Our input matches the recipe we have cached for this slot
                return cached.getRecipe();
            }
        }
        // If there is no cached item input, or it doesn't match our fallback then it is an out of date cache, so we
        // ignore the fact that we have a cache
        PressurizedReactionRecipe foundRecipe = findRecipe(process, fallbackInput, outputSlot, outputTank);
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
    protected PressurizedReactionRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, IInventorySlot outputSlot, @NotNull IChemicalTank outputTank) {
        return getRecipeType().getInputCache().findFirstRecipe(level, fallbackInput, inputFluidTank.getFluid(), inputChemicalTank.getStack());
    }

    protected int getNeededInput(PressurizedReactionRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getInputSolid().getNeededAmount(inputStack));
    }

    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return containsRecipeBAC(stack, inputFluidTank.getFluid(), inputChemicalTank.getStack()) || containsRecipeCAB(stack, inputFluidTank.getFluid(), inputChemicalTank.getStack());
    }

    // 判断输入物品是否符合配方
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipeA(stack);
    }

    @Override
    public void parseUpgradeData(HolderLookup.Provider provider, @NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof PRCUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            getEnergyContainer().setEnergy(data.energyContainer.getEnergy());
            sorting = data.sorting;
            energySlot.deserializeNBT(provider, data.energySlot.serializeNBT(provider));
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputSlots.size(); i++) {
                // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
                inputItemSlots.get(i).deserializeNBT(provider, data.inputSlots.get(i).serializeNBT(provider));
            }
            for (int i = 0; i < data.outputSlots.size(); i++) {
                outputItemSlots.get(i).setStack(data.outputSlots.get(i).getStack());
            }
            for (ITileComponent component : getComponents()) {
                component.read(data.components, provider);
            }
            inputChemicalTank.deserializeNBT(provider, data.inputChemicalTank.serializeNBT(provider));
            inputFluidTank.deserializeNBT(provider, data.inputFluidTank.serializeNBT(provider));
            outputChemicalTank.deserializeNBT(provider, data.outputTank.serializeNBT(provider));
        } else {
            super.parseUpgradeData(provider, upgradeData);
        }
    }

    @Override
    public @Nullable PRCUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new PRCUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), progress, energySlot,
                inputChemicalTank, inputFluidTank, inputItemSlots, outputItemSlots, outputChemicalTank, isSorting(), getComponents());
    }

    @Override
    public void dump() {
        inputFluidTank.setStack(FluidStack.EMPTY);
        if (!isRemote() && IRadiationManager.INSTANCE.isRadiationEnabled() && shouldDumpRadiation()) {
            // If we are on a server and radiation is enabled dump all gas tanks with radioactive materials
            // Note: we handle clearing radioactive contents later in drop calculation due to when things are written to
            // NBT
            // 点击按钮后只需要释放输入储罐的辐射
            IRadiationManager.INSTANCE.dumpRadiation(getWorldNN(), worldPosition, List.of(inputChemicalTank), false);
        }
        inputChemicalTank.setEmpty();
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
        Map<ItemStack, PRCRecipeProcessInfo> processes = ItemStackMap.createTypeAndTagMap();
        List<PRCProcessInfo> emptyProcesses = new ArrayList<>();
        for (PRCProcessInfo processInfo : processInfoSlots) {
            IInventorySlot inputSlot = processInfo.inputSlot();
            if (inputSlot.isEmpty()) {
                emptyProcesses.add(processInfo);
            } else {
                ItemStack inputStack = inputSlot.getStack();
                PRCRecipeProcessInfo recipeProcessInfo = processes.computeIfAbsent(inputStack, i -> new PRCRecipeProcessInfo());
                recipeProcessInfo.processes.add(processInfo);
                recipeProcessInfo.totalCount += inputStack.getCount();
                if (recipeProcessInfo.lazyMinPerSlot == null && !CommonWorldTickHandler.flushTagAndRecipeCaches) {
                    // If we don't have a lazily initialized min per slot calculation set for it yet
                    // and our cache is not invalid/out of date due to a reload
                    CachedRecipe<PressurizedReactionRecipe> cachedRecipe = getCachedRecipe(processInfo.process());
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
        for (Map.Entry<ItemStack, PRCRecipeProcessInfo> entry : processes.entrySet()) {
            PRCRecipeProcessInfo recipeProcessInfo = entry.getValue();
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
                    PRCProcessInfo processInfo = info.processes.getFirst();
                    // Try getting a recipe for our input with a larger size, and update the cache if we find one
                    info.recipe = factory.getRecipeForInput(processInfo.process(), largerInput, processInfo.outputSlot(), outputChemicalTank, true);
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

    protected void addEmptySlotsAsTargets(Map<ItemStack, PRCRecipeProcessInfo> processes, List<PRCProcessInfo> emptyProcesses) {
        for (Map.Entry<ItemStack, PRCRecipeProcessInfo> entry : processes.entrySet()) {
            PRCRecipeProcessInfo recipeProcessInfo = entry.getValue();
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
            List<PRCProcessInfo> toRemove = new ArrayList<>();
            for (PRCProcessInfo emptyProcess : emptyProcesses) {
                if (inputProducesOutput(emptyProcess.process(), sourceStack, emptyProcess.outputSlot(), outputChemicalTank, true)) {
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

    protected void distributeItems(Map<ItemStack, PRCRecipeProcessInfo> processes) {
        for (Map.Entry<ItemStack, PRCRecipeProcessInfo> entry : processes.entrySet()) {
            PRCRecipeProcessInfo recipeProcessInfo = entry.getValue();
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
                PRCProcessInfo processInfo = recipeProcessInfo.processes.get(i);
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

    public record PRCProcessInfo(int process, @NotNull AdvancedFactoryInputInventorySlot inputSlot,
                                 @NotNull IInventorySlot outputSlot) {}

    protected static class PRCRecipeProcessInfo {

        private final List<PRCProcessInfo> processes = new ArrayList<>();
        @Nullable
        private ToIntBiFunction<PRCRecipeProcessInfo, TileEntityPressurizedReactingFactory> lazyMinPerSlot;
        private Object item;
        private PressurizedReactionRecipe recipe;
        private int minPerSlot = 1;
        private int totalCount;

        public int getMinPerSlot(TileEntityPressurizedReactingFactory factory) {
            if (lazyMinPerSlot != null) {
                // Get the value lazily
                minPerSlot = Math.max(1, lazyMinPerSlot.applyAsInt(this, factory));
                lazyMinPerSlot = null;
            }
            return minPerSlot;
        }
    }
}
