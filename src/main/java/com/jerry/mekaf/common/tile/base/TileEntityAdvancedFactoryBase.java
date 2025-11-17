package com.jerry.mekaf.common.tile.base;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.capabilities.energy.AdvancedFactoryEnergyContainer;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.registries.AdvancedFactoryTileEntityTypes;

import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.Upgrade;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.BoxedChemicalInputHandler;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.BoxedChemicalOutputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.monitor.FactoryRecipeCacheLookupMonitor;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.interfaces.ISustainedData;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.UpgradeUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BooleanSupplier;

public abstract class TileEntityAdvancedFactoryBase<RECIPE extends MekanismRecipe> extends TileEntityConfigurableMachine implements IRecipeLookupHandler<RECIPE>, ISustainedData {

    /**
     * How many ticks it takes, by default, to run an operation.
     */
    protected static final int BASE_TICKS_REQUIRED = 200;

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
    private int ticksRequired = 200;
    private int baselineMaxOperations = 1;
    protected boolean sorting;
    private boolean sortingNeeded = true;
    private FloatingLong lastUsage = FloatingLong.ZERO;

    /**
     * This machine's factory type.
     */
    @NotNull
    protected final AdvancedFactoryType type;

    protected AdvancedFactoryEnergyContainer energyContainer;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    protected EnergyInventorySlot energySlot;

    protected IInputHandler<@NotNull ItemStack>[] itemInputHandlers;
    protected IOutputHandler<@NotNull ItemStack>[] itemOutputHandlers;
    protected IInputHandler<@NotNull GasStack>[] gasInputHandlers;
    protected IOutputHandler<@NotNull GasStack>[] gasOutputHandlers;
    protected IInputHandler<@NotNull InfusionStack>[] infusionInputHandlers;
    protected IOutputHandler<@NotNull InfusionStack>[] infusionOutputHandlers;
    protected IInputHandler<@NotNull PigmentStack>[] pigmentInputHandlers;
    protected IOutputHandler<@NotNull PigmentStack>[] pigmentOutputHandlers;
    protected IInputHandler<@NotNull SlurryStack>[] slurryInputHandlers;
    protected IOutputHandler<@NotNull SlurryStack>[] slurryOutputHandlers;
    protected BoxedChemicalInputHandler[] mergedInputHandlers;
    protected BoxedChemicalOutputHandler[] mergedOutputHandlers;
    protected IInputHandler<@NotNull FluidStack>[] fluidInputHandlers;
    protected IOutputHandler<@NotNull FluidStack>[] fluidOutputHandlers;

    protected TileEntityAdvancedFactoryBase(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state);
        type = Objects.requireNonNull(Attribute.get(blockProvider, AttributeAdvancedFactoryType.class)).getAdvancedFactoryType();

        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY);
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

    protected void addSupported(TransmissionType... types) {
        for (TransmissionType type : types) {
            configComponent.addSupported(type);
        }
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
        tier = Attribute.getTier(getBlockType(), FactoryTier.class);
        Runnable setSortingNeeded = () -> sortingNeeded = true;
        recipeCacheLookupMonitors = new FactoryRecipeCacheLookupMonitor[tier.processes];
        for (int i = 0; i < recipeCacheLookupMonitors.length; i++) {
            recipeCacheLookupMonitors[i] = new FactoryRecipeCacheLookupMonitor<>(this, i, setSortingNeeded);
        }
    }

    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSideGasWithConfig(this::getDirection, this::getConfig);
        addGasTanks(builder, listener, () -> {
            listener.onContentsChanged();
            // Mark sorting as being needed again
            sortingNeeded = true;
        });
        return builder.build();
    }

    @Override
    public IChemicalTankHolder<InfuseType, InfusionStack, IInfusionTank> getInitialInfusionTanks(IContentsListener listener) {
        ChemicalTankHelper<InfuseType, InfusionStack, IInfusionTank> builder = ChemicalTankHelper.forSideInfusionWithConfig(this::getDirection, this::getConfig);
        addInfusionTanks(builder, listener, () -> {
            listener.onContentsChanged();
            // Mark sorting as being needed again
            sortingNeeded = true;
        });
        return builder.build();
    }

    // 虽然没有工厂有颜料储罐，但还是写在这里以防有人需要
    @Override
    public @Nullable IChemicalTankHolder<Pigment, PigmentStack, IPigmentTank> getInitialPigmentTanks(IContentsListener listener) {
        ChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> builder = ChemicalTankHelper.forSidePigmentWithConfig(this::getDirection, this::getConfig);
        addPigmentTanks(builder, listener, () -> {
            listener.onContentsChanged();
            // Mark sorting as being needed again
            sortingNeeded = true;
        });
        return builder.build();
    }

    @Override
    public @Nullable IChemicalTankHolder<Slurry, SlurryStack, ISlurryTank> getInitialSlurryTanks(IContentsListener listener) {
        ChemicalTankHelper<Slurry, SlurryStack, ISlurryTank> builder = ChemicalTankHelper.forSideSlurryWithConfig(this::getDirection, this::getConfig);
        addSlurryTanks(builder, listener, () -> {
            listener.onContentsChanged();
            // Mark sorting as being needed again
            sortingNeeded = true;
        });
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addContainer(energyContainer = AdvancedFactoryEnergyContainer.input(this, listener));
        return builder.build();
    }

    @Override
    protected @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSideWithConfig(this::getDirection, this::getConfig);
        addFluidTanks(builder, listener, () -> {
            listener.onContentsChanged();
            // Mark sorting as being needed again
            sortingNeeded = true;
        });
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this::getDirection, this::getConfig);
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

    protected abstract void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener);

    protected void addFluidTanks(FluidTankHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {}

    protected void addGasTanks(ChemicalTankHelper<Gas, GasStack, IGasTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {}

    protected void addInfusionTanks(ChemicalTankHelper<InfuseType, InfusionStack, IInfusionTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {}

    protected void addPigmentTanks(ChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {}

    protected void addSlurryTanks(ChemicalTankHelper<Slurry, SlurryStack, ISlurryTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {}

    public IGasTank getGasTankBar() {
        return null;
    }

    public int getXPos(int index) {
        int baseX = tier == FactoryTier.BASIC ? 55 : tier == FactoryTier.ADVANCED ? 35 : tier == FactoryTier.ELITE ? 29 : 27;
        int baseXMult = tier == FactoryTier.BASIC ? 38 : tier == FactoryTier.ADVANCED ? 26 : 19;
        return baseX + (index * baseXMult);
    }

    public AdvancedFactoryType getAdvancedFactoryType() {
        return type;
    }

    public FloatingLong getRecipeEnergyRequired() {
        return FloatingLong.ZERO;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();

        handleExtrasFuel();
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
        FloatingLong prev = energyContainer.getEnergy().copy();
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
        lastUsage = isActive ? prev.minusEqual(energyContainer.getEnergy()) : FloatingLong.ZERO;
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
    protected void handleExtrasFuel() {}

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

    @NotNull
    @ComputerMethod(nameOverride = "getEnergyUsage", methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    public FloatingLong getLastUsage() {
        return lastUsage;
    }

    @ComputerMethod(methodDescription = "Total number of ticks it takes currently for the recipe to complete")
    public int getTicksRequired() {
        return ticksRequired;
    }

    public int getBaselineMaxOperations() {
        return this.baselineMaxOperations;
    }

    public void setTicksRequired(int value) {
        ticksRequired = value;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(NBTConstants.PROGRESS, Tag.TAG_INT_ARRAY)) {
            int[] savedProgress = nbt.getIntArray(NBTConstants.PROGRESS);
            if (tier.processes != savedProgress.length) {
                Arrays.fill(progress, 0);
            }
            for (int i = 0; i < tier.processes && i < savedProgress.length; i++) {
                progress[i] = savedProgress[i];
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        nbtTags.put(NBTConstants.PROGRESS, new IntArrayTag(Arrays.copyOf(progress, progress.length)));
    }

    @Override
    public void writeSustainedData(CompoundTag data) {
        data.putBoolean(NBTConstants.SORTING, isSorting());
    }

    @Override
    public void readSustainedData(CompoundTag data) {
        NBTUtils.setBooleanIfPresent(data, NBTConstants.SORTING, value -> sorting = value);
    }

    @Override
    public Map<String, String> getTileDataRemap() {
        Map<String, String> remap = new Object2ObjectOpenHashMap<>();
        remap.put(NBTConstants.SORTING, NBTConstants.SORTING);
        return remap;
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            ticksRequired = MekanismUtils.getTicks(this, BASE_TICKS_REQUIRED);
            baselineMaxOperations = (int) Math.pow(2, upgradeComponent.getUpgrades(Upgrade.SPEED));
        }
    }

    @NotNull
    @Override
    public List<Component> getInfo(@NotNull Upgrade upgrade) {
        return UpgradeUtils.getMultScaledInfo(this, upgrade);
    }

    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        if (super.isConfigurationDataCompatible(tileType)) {
            // Check exact match first
            return true;
        }
        // Then check other factory tiers
        for (FactoryTier factoryTier : EnumUtils.FACTORY_TIERS) {
            if (factoryTier != tier && AdvancedFactoryTileEntityTypes.getAdvancedFactoryTile(factoryTier, type).get() == tileType) {
                return true;
            }
        }
        // And finally check if it is the non factory version (it will be missing sorting data, but we can gracefully
        // ignore that)
        return type.getBaseMachine().getTileType().get() == tileType;
    }

    public boolean hasExtrasResourceBar() {
        return false;
    }

    public AdvancedFactoryEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.trackArray(progress);
        errorTracker.track(container);
        container.track(SyncableFloatingLong.create(this::getLastUsage, value -> lastUsage = value));
        container.track(SyncableBoolean.create(this::isSorting, value -> sorting = value));
        container.track(SyncableInt.create(this::getTicksRequired, value -> ticksRequired = value));
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
                        return () -> Arrays.stream(trackedErrors).anyMatch(processTrackedErrors -> processTrackedErrors[errorIndex]);
                    }
                    return () -> trackedErrors[processIndex][errorIndex];
                }
            }
            // Something went wrong
            return () -> false;
        }
    }
}
