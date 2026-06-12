package com.jerry.mekmm.common.tile.factory;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.block.attribute.MoreMachineAttributeFactoryType;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;
import com.jerry.mekmm.common.inventory.slot.MoreMachineFactoryInputInventorySlot;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.Upgrade;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.Mekanism;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.energy.EnergyConfigHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.monitor.FactoryRecipeCacheLookupMonitor;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.upgrade.MachineUpgradeData;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UpgradeUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.ToIntBiFunction;

public abstract class TileEntityMoreMachineFactory<RECIPE extends MekanismRecipe<?>> extends TileEntityConfigurableMachine implements IRecipeLookupHandler<RECIPE> {

    /**
     * How many ticks it takes, by default, to run an operation.
     */
    protected static final int BASE_TICKS_REQUIRED = 10 * SharedConstants.TICKS_PER_SECOND;

    protected FactoryRecipeCacheLookupMonitor<RECIPE>[] recipeCacheLookupMonitors;
    protected BooleanSupplier[] recheckAllRecipeErrors;
    protected final ErrorTracker errorTracker;
    private final boolean[] activeStates;
    protected ProcessInfo[] processInfoSlots;
    /**
     * This Factory's tier.
     */
    public FactoryTier tier;
    /**
     * An int[] used to track all current operations' progress.
     */
    public final int[] progress;
    /**
     * How many ticks it takes, with upgrades, to run an operation
     */
    private int ticksRequired = BASE_TICKS_REQUIRED;
    @Getter
    private int operationsPerTick = 1;// will increase for modified upgrade multipliers
    private boolean sorting;
    private boolean sortingNeeded = true;
    private long lastUsage = 0L;

    /**
     * This machine's factory type.
     */
    @NotNull
    protected final MoreMachineFactoryType type;

    protected MachineEnergyContainer<TileEntityMoreMachineFactory<?>> energyContainer;
    protected final List<IInventorySlot> inputSlots;
    protected final List<IInventorySlot> outputSlots;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    protected TileEntityMoreMachineFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state);
        type = Attribute.getOrThrow(blockProvider, MoreMachineAttributeFactoryType.class).getMoreMachineFactoryType();
        inputSlots = new ArrayList<>();
        outputSlots = new ArrayList<>();

        for (ProcessInfo info : processInfoSlots) {
            inputSlots.add(info.inputSlot());
            outputSlots.add(info.outputSlot());
            if (info.secondaryOutputSlot() != null) {
                outputSlots.add(info.secondaryOutputSlot());
            }
        }
        configComponent.setupItemIOConfig(inputSlots, outputSlots, energySlot, false);
        IInventorySlot extraSlot = getExtraSlot();
        if (extraSlot != null) {
            ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
            if (itemConfig != null) {
                itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, extraSlot));
            }
        }
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        progress = new int[tier.processes];
        activeStates = new boolean[tier.processes];
        recheckAllRecipeErrors = new BooleanSupplier[tier.processes];
        for (int i = 0; i < recheckAllRecipeErrors.length; i++) {
            // Note: We store one per slot so that we can recheck the different slots at different times to reduce the
            // load on the server
            recheckAllRecipeErrors[i] = TileEntityRecipeMachine.shouldRecheckAllErrors(this);
        }
        errorTracker = new ErrorTracker(errorTypes, globalErrorTypes, tier.processes);
    }

    public boolean isEMLoadAndTierOrdinalAboveOverLocked() {
        if (Mekmm.hooks.evolvedMekanism.isLoaded()) {
            // return tier.ordinal() >= EMFactoryTier.OVERCLOCKED.ordinal();
            return true;
        }
        return false;
    }

    /**
     * Used for slots/contents pertaining to the inventory checks to mark sorting as being needed again and recipes as
     * needing to be rechecked. This combines with the
     * passed in listener to allow for abstracting the comparator type checks up to the base level.
     */
    protected IContentsListener markAllMonitorsChanged(IContentsListener listener) {
        return () -> {
            listener.onContentsChanged();
            // Note: Updating sorting is handled by the onChange calls
            for (FactoryRecipeCacheLookupMonitor<RECIPE> cacheLookupMonitor : recipeCacheLookupMonitors) {
                cacheLookupMonitor.onChange();
            }
        };
    }

    @Override
    protected void presetVariables() {
        super.presetVariables();
        tier = Attribute.getTierNN(getBlockHolder(), FactoryTier.class);
        Runnable setSortingNeeded = () -> sortingNeeded = true;
        recipeCacheLookupMonitors = new FactoryRecipeCacheLookupMonitor[tier.processes];
        for (int i = 0; i < recipeCacheLookupMonitors.length; i++) {
            recipeCacheLookupMonitors[i] = new FactoryRecipeCacheLookupMonitor<>(this, i, setSortingNeeded);
        }
    }

    @Override
    protected @Nullable IEnergyContainerHolder getInitialEnergyContainer(IContentsListener listener) {
        energyContainer = MachineEnergyContainer.input(this, () -> {
            listener.onContentsChanged();
            for (FactoryRecipeCacheLookupMonitor<RECIPE> cacheLookupMonitor : recipeCacheLookupMonitors) {
                cacheLookupMonitor.unpause();
            }
        });
        return new EnergyConfigHolder(energyContainer, this);
    }

    public MachineEnergyContainer<TileEntityMoreMachineFactory<?>> getEnergyContainerTyped() {
        return energyContainer;
    }

    @NotNull
    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        addSlots(builder, listener, () -> {
            listener.onContentsChanged();
            // Mark sorting as being needed again
            sortingNeeded = true;
        });
        // Add the energy slot after adding the other slots so that it has the lowest priority in shift clicking
        // Note: We can just pass ourselves as the listener instead of the listener that updates sorting as well,
        // as changes to it won't change anything about the sorting of the recipe
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 7, 13));
        return builder.build();
    }

    protected abstract void addSlots(MekContainerHelper<IInventorySlot> builder, IContentsListener listener, IContentsListener updateSortingListener);

    @Nullable
    protected IInventorySlot getExtraSlot() {
        return null;
    }

    public MoreMachineFactoryType getMMFactoryType() {
        return type;
    }

    @Override
    protected boolean onUpdateServer(net.minecraft.server.level.ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        energySlot.fillContainerOrConvert(null);

        handleSecondaryFuel();
        if (sortingNeeded && isSorting()) {
            // If sorting is needed, and we have sorting enabled mark
            // sorting as no longer needed and sort the inventory
            sortingNeeded = false;
            // Note: If sorting happens, sorting will be marked as needed once more
            // (due to changes in the inventory), but this is fine, and we purposely
            // mark sorting being needed as false before instead of after this method
            // call, because while it tries to optimize the layout, if the optimization
            // would make it so that some slots are now empty (because of stacked inputs
            // being required), we want to make sure we are able to fill those slots
            // with other items.
            sortInventory();
        } else if (!sortingNeeded && CommonWorldTickHandler.flushTagAndRecipeCaches) {
            // Otherwise, if sorting isn't currently needed and the recipe cache is invalid
            // Mark sorting as being needed again for the next check as recipes may
            // have changed so our current sort may be incorrect
            sortingNeeded = true;
        }

        // Copy this so that if it changes we still have the original amount. Don't bother making it a constant though
        // as this way
        // we can then use minusEqual instead of subtract to remove an extra copy call
        long prev = energyContainer.getAmountAsLong();
        for (int i = 0; i < recipeCacheLookupMonitors.length; i++) {
            if (!recipeCacheLookupMonitors[i].updateAndProcess()) {
                // If we don't have a recipe in that slot make sure that our active state for that position is false
                activeStates[i] = false;
            }
        }

        // Update the active state based on the current active state of each recipe
        boolean isActive = false;
        for (boolean state : activeStates) {
            if (state) {
                isActive = true;
                break;
            }
        }
        setActive(isActive);
        // If none of the recipes are actively processing don't bother with any subtraction
        lastUsage = isActive ? prev - energyContainer.getAmountAsLong() : 0L;
        return sendUpdatePacket;
    }

    /**
     * Checks if the cached recipe (or recipe for current factory if the cache is out of date) can produce a specific
     * output.
     *
     * @param process             Which process the cache recipe is.
     * @param fallbackInput       Used if the cached recipe is null or to validate the cached recipe is not out of date.
     * @param outputSlot          The output slot for this slot.
     * @param secondaryOutputSlot The secondary output slot or null if we only have one output slot
     * @param updateCache         True to make the cached recipe get updated if it is out of date.
     *
     * @return True if the recipe produces the given output.
     */
    public boolean inputProducesOutput(int process, @NotNull ItemResource fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot,
                                       boolean updateCache) {
        return outputSlot.isEmpty() || getRecipeForInput(process, fallbackInput, outputSlot, secondaryOutputSlot, updateCache) != null;
    }

    @Contract("null, _ -> false")
    protected abstract boolean isCachedRecipeValid(@Nullable CachedRecipe<RECIPE> cached, @NotNull ItemResource stack);

    @Nullable
    protected RECIPE getRecipeForInput(int process, @NotNull ItemResource fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot,
                                       boolean updateCache) {
        return getRecipeForInput(process, fallbackInput, outputSlot, secondaryOutputSlot, updateCache, false);
    }

    @Nullable
    protected RECIPE getRecipeForInput(int process, @NotNull ItemResource fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot,
                                       boolean updateCache, boolean ignoreCache) {
        if (!ignoreCache && !CommonWorldTickHandler.flushTagAndRecipeCaches) {
            // If our recipe caches are valid, grab our cached recipe and see if it is still valid
            CachedRecipe<RECIPE> cached = getCachedRecipe(process);
            if (isCachedRecipeValid(cached, fallbackInput)) {
                // Our input matches the recipe we have cached for this slot
                return cached.getRecipe();
            }
        }
        // If there is no cached item input, or it doesn't match our fallback then it is an out of date cache, so we
        // ignore the fact that we have a cache
        RECIPE foundRecipe = findRecipe(process, fallbackInput, outputSlot, secondaryOutputSlot);
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
    protected abstract RECIPE findRecipe(int process, @NotNull ItemResource fallbackInput, @NotNull IInventorySlot outputSlot,
                                         @Nullable IInventorySlot secondaryOutputSlot);

    protected abstract int getNeededInput(RECIPE recipe, ItemResource inputStack);

    @Nullable
    private CachedRecipe<RECIPE> getCachedRecipe(int cacheIndex) {
        // TODO: Sanitize that cacheIndex is in bounds?
        return recipeCacheLookupMonitors[cacheIndex].getCachedRecipe(cacheIndex);
    }

    public BooleanSupplier getWarningCheck(RecipeError error, int processIndex) {
        return errorTracker.getWarningCheck(error, processIndex);
    }

    @Override
    public void clearRecipeErrors(int cacheIndex) {
        Arrays.fill(errorTracker.trackedErrors[cacheIndex], false);
    }

    protected void setActiveState(boolean state, int cacheIndex) {
        activeStates[cacheIndex] = state;
    }

    /**
     * Handles filling the secondary fuel tank based on the item in the extra slot
     */
    protected void handleSecondaryFuel() {}

    public abstract boolean isItemValidForSlot(@NotNull ItemResource stack);

    /**
     * Like isItemValidForSlot makes no assumptions about current stored types
     */
    public abstract boolean isValidInputItem(@NotNull ItemResource stack);

    public int getProgress(int cacheIndex) {
        return progress[cacheIndex];
    }

    @Override
    public int getSavedOperatingTicks(int cacheIndex) {
        return getProgress(cacheIndex);
    }

    public double getScaledProgress(int i, int process) {
        return (double) getProgress(process) * i / ticksRequired;
    }

    public void toggleSorting() {
        sorting = !isSorting();
        markForSave();
    }

    @ComputerMethod(nameOverride = "isAutoSortEnabled")
    public boolean isSorting() {
        return sorting;
    }

    @ComputerMethod(nameOverride = "getEnergyUsage", methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    public long getLastUsage() {
        return lastUsage;
    }

    @ComputerMethod(methodDescription = "Total number of ticks it takes currently for the recipe to complete")
    public int getTicksRequired() {
        return ticksRequired;
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        Optional<int[]> optionalProgress = input.getIntArray(SerializationConstants.PROGRESS);
        if (optionalProgress.isPresent()) {
            int[] savedProgress = optionalProgress.get();
            if (tier.processes != savedProgress.length) {
                Arrays.fill(progress, 0);
            }
            for (int i = 0; i < tier.processes && i < savedProgress.length; i++) {
                progress[i] = savedProgress[i];
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.putIntArray(SerializationConstants.PROGRESS, Arrays.copyOf(progress, progress.length));
    }

    @Override
    public void writeSustainedData(@NotNull ValueOutput output) {
        super.writeSustainedData(output);
        output.putBoolean(SerializationConstants.SORTING, isSorting());
    }

    @Override
    public void readSustainedData(@NotNull ValueInput input) {
        super.readSustainedData(input);
        sorting = input.getBooleanOr(SerializationConstants.SORTING, sorting);
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MekanismDataComponents.SORTING, isSorting());
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentGetter input) {
        super.applyImplicitComponents(input);
        sorting = input.getOrDefault(MekanismDataComponents.SORTING, sorting);
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            ticksRequired = MekanismUtils.getTicks(this, BASE_TICKS_REQUIRED);
            operationsPerTick = MekanismUtils.getOperationsPerTick(this, BASE_TICKS_REQUIRED, 1);
        }
    }

    @NotNull
    @Override
    public List<Component> getInfo(@NotNull Upgrade upgrade) {
        return UpgradeUtils.getMultScaledInfo(this, upgrade);
    }

    @Override
    public boolean isConfigurationDataCompatible(Block blockType) {
        // Allow exact match or factories of the same type (as we will just ignore the extra data)
        return super.isConfigurationDataCompatible(blockType) || MoreMachineUtils.isSameMMTypeFactory(getBlockHolder(), blockType);
    }

    public boolean hasSecondaryResourceBar() {
        return false;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.trackArray(progress);
        errorTracker.track(container);
        container.track(SyncableLong.create(this::getLastUsage, v -> lastUsage = v));
        container.track(SyncableBoolean.create(this::isSorting, v -> sorting = v));
        container.track(SyncableInt.create(this::getTicksRequired, v -> ticksRequired = v));
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, Provider provider, TransactionContext transaction) {
        if (upgradeData instanceof MachineUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            energyContainer.copyContents(data.energyContainer, transaction);
            sorting = data.sorting;
            ProblemReporter.PathElement problemPath = problemPath();
            energySlot.copyContents(data.energySlot, transaction);
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.inputSlots.size(); i++) {
                inputSlots.get(i).copyContents(data.inputSlots.get(i), transaction);
            }
            for (int i = 0; i < data.outputSlots.size(); i++) {
                outputSlots.get(i).copyContents(data.outputSlots.get(i), transaction);
            }
            try (var reporter = new ProblemReporter.ScopedCollector(problemPath(), Mekanism.logger)) {
                ValueInput input = TagValueInput.create(reporter, provider, data.components);
                for (ITileComponent component : getComponents()) {
                    component.read(input);
                }
            }
        } else {
            super.parseUpgradeData(upgradeData, provider, transaction);
        }
    }

    // Methods relating to IComputerTile
    protected void validateValidProcess(int process) throws ComputerException {
        if (process < 0 || process >= progress.length) {
            throw new ComputerException("Process: '%d' is out of bounds, as this factory only has '%d' processes (zero indexed).", process, progress.length);
        }
    }

    @ComputerMethod(requiresPublicSecurity = true)
    void setAutoSort(boolean enabled) throws ComputerException {
        validateSecurityIsPublic();
        if (sorting != enabled) {
            sorting = enabled;
            markForSave();
        }
    }

    @ComputerMethod
    int getRecipeProgress(int process) throws ComputerException {
        validateValidProcess(process);
        return getProgress(process);
    }

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInput", docPlaceholder = "input slot")
    IInventorySlot getInputSlot(int process) throws ComputerException {
        validateValidProcess(process);
        return processInfoSlots[process].inputSlot();
    }

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutput", docPlaceholder = "output slot")
    IInventorySlot getOutputSlot(int process) throws ComputerException {
        validateValidProcess(process);
        return processInfoSlots[process].outputSlot();
    }
    // End methods IComputerTile

    private void sortInventory() {
        Map<ItemResource, RecipeProcessInfo<ItemResource, RECIPE>> processes = new HashMap<>();
        List<ProcessInfo> emptyProcesses = new ArrayList<>();
        // 遍历processInfoSlots
        for (ProcessInfo processInfo : processInfoSlots) {
            // 获取每个输入槽
            IInventorySlot inputSlot = processInfo.inputSlot();
            // 如果输入槽为空
            if (inputSlot.isEmpty()) {
                // 则添加到emptyProcesses
                emptyProcesses.add(processInfo);
            } else {
                // 如果不为空，获取该输入槽内的物品
                ItemResource inputStack = inputSlot.resource();
                // 如果processes中没有这个物品记录（第一个被记录有物品的槽位，或多个不同的物品），则添加这个物品的键和默认值
                RecipeProcessInfo<ItemResource, RECIPE> recipeProcessInfo = processes.computeIfAbsent(inputStack, RecipeProcessInfo::new);
                // 将不为空的processInfoSlots[i]添加到RecipeProcessInfo.processes中
                recipeProcessInfo.processes.add(processInfo);
                // 计算该堆栈的物品总数
                recipeProcessInfo.totalCount += inputSlot.amountAsLong();
                // 如果lazyMinPerSlot尚未初始化，并且配方缓存未被刷新，则尝试从缓存中获取配方。
                if (recipeProcessInfo.lazyMinPerSlot == null && !CommonWorldTickHandler.flushTagAndRecipeCaches) {
                    // If we don't have a lazily initialized min per slot calculation set for it yet
                    // and our cache is not invalid/out of date due to a reload
                    // 获得每个线程的cachedRecipe
                    CachedRecipe<RECIPE> cachedRecipe = getCachedRecipe(processInfo.process());
                    // 验证配方是否正确
                    if (isCachedRecipeValid(cachedRecipe, inputStack)) {
                        // 赋值
                        recipeProcessInfo.recipe = cachedRecipe.getRecipe();
                        // And our current process has a cached recipe then set the lazily initialized per slot value
                        // Note: If something goes wrong, and we end up with zero as how much we need as an input
                        // we just bump the value up to one to make sure we properly handle it
                        // 这是一个延迟初始化的函数，用于计算每槽所需的最小输入数量（参考HDPE的富集）
                        recipeProcessInfo.lazyMinPerSlot = (info, factory) -> factory.getNeededInput(info.recipe, info.item);
                    }
                }
            }
        }
        // 如果所有槽位为空，则返回不做处理
        if (processes.isEmpty()) {
            // If all input slots are empty, just exit
            return;
        }
        Collection<RecipeProcessInfo<ItemResource, RECIPE>> processInfos = processes.values();
        // 遍历每个键值对
        for (RecipeProcessInfo<ItemResource, RECIPE> recipeProcessInfo : processInfos) {
            // 如果lazyMinPerSlot为空
            if (recipeProcessInfo.lazyMinPerSlot == null) {
                // If we don't have a lazy initializer for our minPerSlot setup, that means that there is
                // no valid cached recipe for any of the slots of this type currently, so we want to try and
                // get the recipe we will have for the first slot, once we end up with more items in the stack
                recipeProcessInfo.lazyMinPerSlot = (info, factory) -> {
                    // Note: We put all of this logic in the lazy init, so that we don't actually call any of this
                    // until it is needed. That way if we have no empty slots and all our input slots are filled
                    // we don't do any extra processing here, and can properly short circuit
                    ProcessInfo processInfo = info.processes.getFirst();
                    // Try getting a recipe for our input with a larger size, and update the cache if we find one
                    info.recipe = factory.getRecipeForInput(processInfo.process(), info.item, processInfo.outputSlot(), processInfo.secondaryOutputSlot(), true, true);
                    if (info.recipe != null) {
                        return factory.getNeededInput(info.recipe, info.item);
                    }
                    return 1;
                };
            }
        }
        // 如果记录空槽位的List不为空
        if (!emptyProcesses.isEmpty()) {
            // If we have any empty slots, we need to factor them in as valid slots for items to transferred to
            // 如果我们有任何空槽，我们需要将它们作为要转移到的项目的有效槽
            addEmptySlotsAsTargets(processInfos, emptyProcesses);
            // Note: Any remaining empty slots are "ignored" as we don't have any
            // spare items to distribute to them
        }
        // Distribute items among the slots
        distributeItems(processInfos);
    }

    private void addEmptySlotsAsTargets(Collection<RecipeProcessInfo<ItemResource, RECIPE>> processes, List<ProcessInfo> emptyProcesses) {
        for (RecipeProcessInfo<ItemResource, RECIPE> recipeProcessInfo : processes) {
            // 获取有物品槽位的物品数量
            // 获取minPerSlot（一般为1，富集聚乙烯为3）
            long minPerSlot = recipeProcessInfo.getMinPerSlot(this);
            // 需要的最大槽数
            long maxSlots = recipeProcessInfo.totalCount / minPerSlot;
            // 如果一份都做不来了
            if (maxSlots <= 1) {
                // If we don't have enough to even fill the input for a slot for a single recipe; skip
                continue;
            }
            // Otherwise, if we have at least enough items for two slots see how many we already have with items in them
            // 获取工厂非空槽的数量
            int processCount = recipeProcessInfo.processes.size();
            // 如果需要的最大槽数小于工厂非空槽的数量
            if (maxSlots <= processCount) {
                // If we don't have enough extra to fill another slot skip
                continue;
            }
            // 有多少需要被添加到到空槽位
            long emptyToAdd = maxSlots - processCount;
            int added = 0;
            for (Iterator<ProcessInfo> iter = emptyProcesses.iterator(); iter.hasNext();) {
                ProcessInfo emptyProcess = iter.next();
                if (inputProducesOutput(emptyProcess.process(), recipeProcessInfo.item, emptyProcess.outputSlot(), emptyProcess.secondaryOutputSlot(), true)) {
                    // If the input is valid for the stuff in the empty process' output slot
                    // then add our empty process to our recipeProcessInfo, and mark
                    // the empty process as accounted for
                    recipeProcessInfo.processes.add(emptyProcess);
                    iter.remove();
                    if (++added >= emptyToAdd) {
                        // If we added as many as we could based on how much input we have; exit
                        break;
                    }
                }
            }
            if (emptyProcesses.isEmpty()) {
                // We accounted for all our empty processes, stop looking at inputs
                // for purposes of distributing empty slots among them
                break;
            }
        }
    }

    private void distributeItems(Collection<RecipeProcessInfo<ItemResource, RECIPE>> processes) {
        for (RecipeProcessInfo<ItemResource, RECIPE> recipeProcessInfo : processes) {
            int processCount = recipeProcessInfo.processes.size();
            if (processCount == 1) {
                // If there is only one process with the item in it; short-circuit, no balancing is needed
                continue;
            }
            // Note: This isn't based on any limits the slot may have (but we currently don't have any reduced ones
            // here, so it doesn't matter)
            int maxStackSize = recipeProcessInfo.item.getMaxStackSize();
            long numberPerSlot = recipeProcessInfo.totalCount / processCount;
            if (numberPerSlot == maxStackSize) {
                // If all the slots are already maxed out; short-circuit, no balancing is needed
                continue;
            }
            // 平分后剩余多少
            long remainder = recipeProcessInfo.totalCount % processCount;
            // 执行配方的最小所需物品数
            long minPerSlot = recipeProcessInfo.getMinPerSlot(this);
            // 针对聚乙烯等配方进行二次平分
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
            for (ProcessInfo processInfo : recipeProcessInfo.processes) {
                IInventorySlot inputSlot = processInfo.inputSlot();
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
                inputSlot.setContents(recipeProcessInfo.item, sizeForSlot, null);
            }
        }
    }

    public record ProcessInfo(int process, @NotNull MoreMachineFactoryInputInventorySlot inputSlot, @NotNull IInventorySlot outputSlot,
                              @Nullable IInventorySlot secondaryOutputSlot) {}

    private static class RecipeProcessInfo<ITEM, RECIPE extends MekanismRecipe<?>> {

        private final List<ProcessInfo> processes = new ArrayList<>();
        private final ITEM item;
        @Nullable
        private ToIntBiFunction<RecipeProcessInfo<ITEM, RECIPE>, TileEntityMoreMachineFactory<RECIPE>> lazyMinPerSlot;
        private RECIPE recipe;
        private long minPerSlot = 1;
        private long totalCount;

        public RecipeProcessInfo(ITEM item) {
            this.item = item;
        }

        public long getMinPerSlot(TileEntityMoreMachineFactory<RECIPE> factory) {
            if (lazyMinPerSlot != null) {
                // Get the value lazily
                minPerSlot = Math.max(1, lazyMinPerSlot.applyAsInt(this, factory));
                lazyMinPerSlot = null;
            }
            return minPerSlot;
        }
    }

    protected static class ErrorTracker {

        private final List<RecipeError> errorTypes;
        private final IntSet globalTypes;

        // TODO: See if we can get it so we only have to sync a single version of global types?
        private final boolean[][] trackedErrors;
        private final int processes;

        public ErrorTracker(List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes, int processes) {
            // Copy the list if it is mutable to ensure it doesn't get changed, otherwise just use the list
            this.errorTypes = List.copyOf(errorTypes);
            globalTypes = new IntArraySet(globalErrorTypes.size());
            for (int i = 0; i < this.errorTypes.size(); i++) {
                RecipeError error = this.errorTypes.get(i);
                if (globalErrorTypes.contains(error)) {
                    globalTypes.add(i);
                }
            }
            this.processes = processes;
            trackedErrors = new boolean[this.processes][];
            int errors = this.errorTypes.size();
            for (int i = 0; i < trackedErrors.length; i++) {
                trackedErrors[i] = new boolean[errors];
            }
        }

        private void track(MekanismContainer container) {
            container.trackArray(trackedErrors);
        }

        public void onErrorsChanged(Set<RecipeError> errors, int processIndex) {
            boolean[] processTrackedErrors = trackedErrors[processIndex];
            for (int i = 0; i < processTrackedErrors.length; i++) {
                processTrackedErrors[i] = errors.contains(errorTypes.get(i));
            }
        }

        private BooleanSupplier getWarningCheck(RecipeError error, int processIndex) {
            if (processIndex >= 0 && processIndex < processes) {
                int errorIndex = errorTypes.indexOf(error);
                if (errorIndex >= 0) {
                    if (globalTypes.contains(errorIndex)) {
                        return () -> {
                            for (boolean[] tracked : trackedErrors) {
                                if (tracked[errorIndex]) {
                                    return true;
                                }
                            }
                            return false;
                        };
                    }
                    return () -> trackedErrors[processIndex][errorIndex];
                }
            }
            // Something went wrong
            return () -> false;
        }
    }
}
