package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.inventory.slot.AdvancedFactoryInputInventorySlot;
import com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase;
import com.jerry.mekaf.common.upgrade.PRCUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.fluid.IFluidTank;
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
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
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
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
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

public class TileEntityPressurizedReactingFactory extends TileEntityAdvancedFactoryBase<PressurizedReactionRecipe> implements IHasDumpButton,
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
    private final IInputHandler<Fluid, @NotNull FluidStack> fluidInputHandler;
    private final IInputHandler<Chemical, @NotNull ChemicalStack> chemicalInputHandler;
    protected IOutputHandler<@NotNull PressurizedReactionRecipeOutput>[] reactionOutputHandlers;

    protected final List<IInventorySlot> inputItemSlots;
    protected final List<IInventorySlot> outputItemSlots;

    public TileEntityPressurizedReactingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
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
    protected void addTanks(MekContainerHelper<IChemicalTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        builder.addContainer(inputChemicalTank = BasicChemicalTank.create(MAX_CHEMICAL * tier.processes, MekContainerHelper.radioactiveInputTankPredicate(() -> outputChemicalTank),
                (chemicalType, automationType) -> containsRecipeCAB(ItemResource.EMPTY, inputFluidTank.resource(), chemicalType), this::containsRecipeC, ChemicalAttributeValidator.ALWAYS_ALLOW,
                markAllMonitorsChanged(listener)));
        builder.addContainer(outputChemicalTank = BasicChemicalTank.output(MAX_CHEMICAL * tier.processes, markAllMonitorsChanged(listener)));
    }

    @Override
    protected @Nullable IContainerHolder<IFluidTank> getInitialFluidTanks(IContentsListener listener) {
        MekContainerHelper<IFluidTank> builder = MekContainerHelper.forSideWithFluidConfig(this);
        builder.addContainer(inputFluidTank = BasicFluidTank.input(MAX_FLUID * tier.processes,
                (fluidType, automationType) -> containsRecipeBAC(ItemResource.EMPTY, fluidType, inputChemicalTank.resource()),
                this::containsRecipeB, markAllMonitorsChanged(listener)));
        return builder.build();
    }

    @Override
    protected void addSlots(MekContainerHelper<IInventorySlot> builder, IContentsListener listener, IContentsListener updateSortingListener) {
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
            builder.addContainer(inputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(NOT_ENOUGH_ITEM_INPUT_ERROR, index)));
            builder.addContainer(outputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(NOT_ENOUGH_SPACE_ITEM_OUTPUT_ERROR, index)));
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
                    (inputFluidTank.isEmpty() || cachedRecipe.getInputFluid().testType(inputFluidTank.resource())) &&
                    (inputChemicalTank.isEmpty() || cachedRecipe.getInputChemical().testType(inputChemicalTank.resource()));
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
    protected PressurizedReactionRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, IInventorySlot outputSlot, @NotNull IChemicalTank inputTank) {
        return getRecipeType().getInputCache().findFirstRecipe(level, fallbackInput, inputFluidTank.resource().toStack(inputFluidTank.amountAsInt()),
                inputChemicalTank.resource().toStack(inputChemicalTank.amountAsInt()));
    }

    protected int getNeededInput(PressurizedReactionRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getInputSolid().getNeededAmount(inputStack));
    }

    @Contract("null, _ -> false")
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<PressurizedReactionRecipe> cached, @NotNull ItemResource stack) {
        if (cached != null) {
            PressurizedReactionRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getInputSolid().testType(stack) &&
                    (inputFluidTank.isEmpty() || cachedRecipe.getInputFluid().testType(inputFluidTank.resource())) &&
                    (inputChemicalTank.isEmpty() || cachedRecipe.getInputChemical().testType(inputChemicalTank.resource()));
        }
        return false;
    }

    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return containsRecipeBAC(stack, inputFluidTank.resource(), inputChemicalTank.resource()) || containsRecipeCAB(stack, inputFluidTank.resource(), inputChemicalTank.resource());
    }

    // 判断输入物品是否符合配方
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipeA(stack);
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider, TransactionContext transaction) {
        if (upgradeData instanceof PRCUpgradeData data) {
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
            inputChemicalTank.copyContents(data.inputChemicalTank, transaction);
            inputFluidTank.copyContents(data.inputFluidTank, transaction);
            outputChemicalTank.copyContents(data.outputTank, transaction);
        } else {
            super.parseUpgradeData(upgradeData, provider, transaction);
        }
    }

    @Override
    public @Nullable PRCUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new PRCUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), progress, energySlot,
                inputChemicalTank, inputFluidTank, inputItemSlots, outputItemSlots, outputChemicalTank, isSorting(), getComponents(), problemPath());
    }

    @Override
    public void dump() {
        inputFluidTank.setContents(FluidResource.EMPTY, 0, null);
        if (!isRemote() && IRadiationManager.INSTANCE.isRadiationEnabled() && shouldDumpRadiation()) {
            // If we are on a server and radiation is enabled dump all gas tanks with radioactive materials
            // Note: we handle clearing radioactive contents later in drop calculation due to when things are written to
            // NBT
            // 点击按钮后只需要释放输入储罐的辐射
            IRadiationManager.INSTANCE.dumpRadiation(getWorldNN(), worldPosition, inputChemicalTank.resource(), inputChemicalTank.amountAsLong());
        }
        inputChemicalTank.setContents(ChemicalResource.EMPTY, 0, null);
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
        Map<ItemResource, PRCRecipeProcessInfo> processes = new HashMap<>();
        List<PRCProcessInfo> emptyProcesses = new ArrayList<>();
        for (PRCProcessInfo processInfo : processInfoSlots) {
            IInventorySlot inputSlot = processInfo.inputSlot();
            if (inputSlot.isEmpty()) {
                emptyProcesses.add(processInfo);
            } else {
                ItemResource inputStack = inputSlot.resource();
                PRCRecipeProcessInfo recipeProcessInfo = processes.computeIfAbsent(inputStack, i -> new PRCRecipeProcessInfo());
                recipeProcessInfo.processes.add(processInfo);
                recipeProcessInfo.totalCount += inputSlot.amountAsLong();
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
                        recipeProcessInfo.lazyMinPerSlot = (info, factory) -> factory.getNeededInput(info.recipe, info.item.toStack());
                    }
                }
            }
        }
        if (processes.isEmpty()) {
            // If all input slots are empty, just exit
            return;
        }
        for (Map.Entry<ItemResource, PRCRecipeProcessInfo> entry : processes.entrySet()) {
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
                    ItemResource item = info.item;
                    ItemStack largerInput = item.toStack(Math.min(item.getMaxStackSize(), MathUtils.clampToInt(info.totalCount)));
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

    protected void addEmptySlotsAsTargets(Map<ItemResource, PRCRecipeProcessInfo> processes, List<PRCProcessInfo> emptyProcesses) {
        for (Map.Entry<ItemResource, PRCRecipeProcessInfo> entry : processes.entrySet()) {
            PRCRecipeProcessInfo recipeProcessInfo = entry.getValue();
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

    protected void distributeItems(Map<ItemResource, PRCRecipeProcessInfo> processes) {
        for (Map.Entry<ItemResource, PRCRecipeProcessInfo> entry : processes.entrySet()) {
            PRCRecipeProcessInfo recipeProcessInfo = entry.getValue();
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
                PRCProcessInfo processInfo = recipeProcessInfo.processes.get(i);
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

    public record PRCProcessInfo(int process, @NotNull AdvancedFactoryInputInventorySlot inputSlot,
                                 @NotNull IInventorySlot outputSlot) {}

    protected static class PRCRecipeProcessInfo {

        private final List<PRCProcessInfo> processes = new ArrayList<>();
        @Nullable
        private ToIntBiFunction<PRCRecipeProcessInfo, TileEntityPressurizedReactingFactory> lazyMinPerSlot;
        private ItemResource item;
        private PressurizedReactionRecipe recipe;
        private long minPerSlot = 1;
        private long totalCount;

        public long getMinPerSlot(TileEntityPressurizedReactingFactory factory) {
            if (lazyMinPerSlot != null) {
                // Get the value lazily
                minPerSlot = Math.max(1, lazyMinPerSlot.applyAsInt(this, factory));
                lazyMinPerSlot = null;
            }
            return minPerSlot;
        }
    }
}
