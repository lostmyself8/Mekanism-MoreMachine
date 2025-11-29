package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.capabilities.energy.AdvancedFactoryEnergyContainer;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.Upgrade;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
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
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.UpgradeUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

public abstract class TileEntityAdvancedFactoryBase<RECIPE extends MekanismRecipe<?>> extends TileEntityConfigurableMachine implements IRecipeLookupHandler<RECIPE> {

    /**
     * How many ticks it takes, by default, to run an operation.
     */
    protected static final int BASE_TICKS_REQUIRED = 10 * SharedConstants.TICKS_PER_SECOND;
    public static final long MAX_CHEMICAL = 10L * FluidType.BUCKET_VOLUME;
    public static final int MAX_FLUID = 10 * FluidType.BUCKET_VOLUME;

    protected FactoryRecipeCacheLookupMonitor<RECIPE>[] recipeCacheLookupMonitors;
    protected BooleanSupplier[] recheckAllRecipeErrors;
    protected final ErrorTracker errorTracker;
    private final boolean[] activeStates;
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
    private int operationsPerTick = 1;// will increase for modified upgrade multipliers
    protected boolean sorting;
    private boolean sortingNeeded = true;
    private long lastUsage = 0L;

    /**
     * This machine's factory type.
     */
    @NotNull
    protected final AdvancedFactoryType type;

    // 为了加压工厂而更改
    @Getter
    protected AdvancedFactoryEnergyContainer energyContainer;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;
    protected IInputHandler<@NotNull ChemicalStack>[] chemicalInputHandlers;
    protected IOutputHandler<@NotNull ChemicalStack>[] chemicalOutputHandlers;
    protected IInputHandler<@NotNull ItemStack>[] itemInputHandlers;
    protected IOutputHandler<@NotNull ItemStack>[] itemOutputHandlers;
    protected IInputHandler<@NotNull FluidStack>[] fluidInputHandlers;
    protected IOutputHandler<@NotNull FluidStack>[] fluidOutputHandlers;

    protected TileEntityAdvancedFactoryBase(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state);
        type = Attribute.getOrThrow(blockProvider, AttributeAdvancedFactoryType.class).getAdvancedFactoryType();

        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

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
        tier = Attribute.getTier(getBlockHolder(), FactoryTier.class);
        Runnable setSortingNeeded = () -> sortingNeeded = true;
        recipeCacheLookupMonitors = new FactoryRecipeCacheLookupMonitor[tier.processes];
        for (int i = 0; i < recipeCacheLookupMonitors.length; i++) {
            recipeCacheLookupMonitors[i] = new FactoryRecipeCacheLookupMonitor<>(this, i, setSortingNeeded);
        }
    }

    @Override
    public @Nullable IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        ChemicalTankHelper builder = ChemicalTankHelper.forSideWithConfig(this);
        addTanks(builder, listener, () -> {
            listener.onContentsChanged();
            // Mark sorting as being needed again
            sortingNeeded = true;
        });
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this);
        builder.addContainer(energyContainer = AdvancedFactoryEnergyContainer.input(this, () -> {
            listener.onContentsChanged();
            for (FactoryRecipeCacheLookupMonitor<RECIPE> cacheLookupMonitor : recipeCacheLookupMonitors) {
                cacheLookupMonitor.unpause();
            }
        }));
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this);
        addSlots(builder, listener, () -> {
            listener.onContentsChanged();
            // Mark sorting as being needed again
            sortingNeeded = true;
        });
        // Add the energy slot after adding the other slots so that it has the lowest priority in shift clicking
        // Note: We can just pass ourselves as the listener instead of the listener that updates sorting as well,
        // as changes to it won't change anything about the sorting of the recipe
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 7, 13));
        return builder.build();
    }

    protected abstract void addTanks(ChemicalTankHelper builder, IContentsListener listener, IContentsListener updateSortingListener);

    protected abstract void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener);

    public int getXPos(int index) {
        int baseX = tier == FactoryTier.BASIC ? 55 : tier == FactoryTier.ADVANCED ? 35 : tier == FactoryTier.ELITE ? 29 : 27;
        int baseXMult = tier == FactoryTier.BASIC ? 38 : tier == FactoryTier.ADVANCED ? 26 : 19;
        return baseX + (index * baseXMult);
    }

    /**
     * 当你重写{@link #handleSecondaryFuel()}该方法才有用。
     * 如果你覆写了{@link #handleSecondaryFuel()}但没有覆写该方法则可能会导致空指针异常。
     * WashingFactory是个特殊情况，它只有流体储罐，所以不覆写这个方法不会造成空指针。
     *
     * @return chemicalTankBar
     */
    public IChemicalTank getChemicalTankBar() {
        return null;
    }

    @Nullable
    protected IInventorySlot getExtraSlot() {
        return null;
    }

    public AdvancedFactoryType getAdvancedFactoryType() {
        return type;
    }

    public long getRecipeEnergyRequired() {
        return 0;
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.fillContainerOrConvert();

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
            sortInventoryOrTank();
        } else if (!sortingNeeded && CommonWorldTickHandler.flushTagAndRecipeCaches) {
            // Otherwise, if sorting isn't currently needed and the recipe cache is invalid
            // Mark sorting as being needed again for the next check as recipes may
            // have changed so our current sort may be incorrect
            sortingNeeded = true;
        }

        // Copy this so that if it changes we still have the original amount. Don't bother making it a constant though
        // as this way
        // we can then use minusEqual instead of subtract to remove an extra copy call
        long prev = energyContainer.getEnergy();
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
        lastUsage = isActive ? prev - energyContainer.getEnergy() : 0L;
        return sendUpdatePacket;
    }

    @Nullable
    protected CachedRecipe<RECIPE> getCachedRecipe(int cacheIndex) {
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

    public int getOperationsPerTick() {
        return operationsPerTick;
    }

    public void setTicksRequired(int value) {
        ticksRequired = value;
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        if (nbt.contains(SerializationConstants.PROGRESS, Tag.TAG_INT_ARRAY)) {
            int[] savedProgress = nbt.getIntArray(SerializationConstants.PROGRESS);
            if (tier.processes != savedProgress.length) {
                Arrays.fill(progress, 0);
            }
            for (int i = 0; i < tier.processes && i < savedProgress.length; i++) {
                progress[i] = savedProgress[i];
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(nbtTags, provider);
        nbtTags.putIntArray(SerializationConstants.PROGRESS, Arrays.copyOf(progress, progress.length));
    }

    @Override
    public void writeSustainedData(HolderLookup.Provider provider, CompoundTag data) {
        super.writeSustainedData(provider, data);
        data.putBoolean(SerializationConstants.SORTING, isSorting());
    }

    @Override
    public void readSustainedData(HolderLookup.Provider provider, @NotNull CompoundTag data) {
        super.readSustainedData(provider, data);
        NBTUtils.setBooleanIfPresent(data, SerializationConstants.SORTING, value -> sorting = value);
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MekanismDataComponents.SORTING, isSorting());
    }

    @Override
    protected void applyImplicitComponents(@NotNull BlockEntity.DataComponentInput input) {
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
        return super.isConfigurationDataCompatible(blockType) || MoreMachineUtils.isSameAFTypeFactory(getBlockHolder(), blockType);
    }

    public boolean hasExtraResourceBar() {
        return false;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.trackArray(progress);
        errorTracker.track(container);
        container.track(SyncableLong.create(this::getLastUsage, value -> lastUsage = value));
        container.track(SyncableBoolean.create(this::isSorting, value -> sorting = value));
        container.track(SyncableInt.create(this::getTicksRequired, value -> ticksRequired = value));
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

    protected abstract void sortInventoryOrTank();

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
