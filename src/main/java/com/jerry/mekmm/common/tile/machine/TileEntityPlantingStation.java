package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.PlantingRecipe.PlantingStationRecipeOutput;
import com.jerry.mekmm.api.recipes.cache.PlantingCacheRecipe;
import com.jerry.mekmm.api.recipes.outputs.MoreMachineOutputHelper;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.upgrade.PlantingUpgradeData;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToItemStackCachedRecipe.ChemicalUsageMultiplier;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.IRecipeLookupHandler.ConstantUsageRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TileEntityPlantingStation extends TileEntityProgressMachine<PlantingRecipe> implements ItemChemicalRecipeLookupHandler<Gas, GasStack, PlantingRecipe>, ConstantUsageRecipeLookupHandler {

    public static final RecipeError NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR = RecipeError.create();
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    public static final int BASE_TICKS_REQUIRED = 200;
    public static final long MAX_GAS = 210;

    private final ChemicalUsageMultiplier gasUsageMultiplier;
    private long baseTotalUsage;
    private long usedSoFar;

    public IGasTank gasTank;

    protected final IOutputHandler<PlantingStationRecipeOutput> outputHandler;
    protected final IInputHandler<@NotNull ItemStack> itemInputHandler;
    protected final ILongInputHandler<@NotNull GasStack> gasInputHandler;

    private MachineEnergyContainer<TileEntityPlantingStation> energyContainer;

    InputInventorySlot inputSlot;
    OutputInventorySlot mainOutputSlot;
    OutputInventorySlot secondaryOutputSlot;
    // 气罐槽
    GasInventorySlot gasSlot;
    EnergyInventorySlot energySlot;

    public TileEntityPlantingStation(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.PLANTING_STATION, pos, state, TRACKED_ERROR_TYPES, BASE_TICKS_REQUIRED);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.GAS, TransmissionType.ENERGY);
        configComponent.setupItemIOExtraConfig(inputSlot, mainOutputSlot, gasSlot, energySlot);
        configComponent.setupItemIOConfig(Collections.singletonList(inputSlot), List.of(mainOutputSlot, secondaryOutputSlot), energySlot, false);

        configComponent.setupIOConfig(TransmissionType.GAS, gasTank, RelativeSide.RIGHT).setCanEject(false);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        itemInputHandler = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT);
        gasInputHandler = InputHelper.getConstantInputHandler(gasTank);
        outputHandler = MoreMachineOutputHelper.getOutputHandler(mainOutputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE, secondaryOutputSlot, NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR);
        baseTotalUsage = baseTicksRequired;
        gasUsageMultiplier = (usedSoFar, operatingTicks) -> {
            long baseRemaining = baseTotalUsage - usedSoFar;
            int remainingTicks = getTicksRequired() - operatingTicks;
            if (baseRemaining < remainingTicks) {
                // If we already used more than we would need to use (due to removing speed upgrades or adding gas
                // upgrades)
                // then just don't use any gas this tick
                return 0;
            } else if (baseRemaining == remainingTicks) {
                return 1;
            }
            return Math.max(MathUtils.clampToLong(baseRemaining / (double) remainingTicks), 0);
        };
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSideGasWithConfig(this::getDirection, this::getConfig);
        builder.addTank(gasTank = ChemicalTankBuilder.GAS.create(MAX_GAS, ChemicalTankBuilder.GAS.alwaysTrueBi,
                (gas, automationType) -> containsRecipeBA(inputSlot.getStack(), gas), this::containsRecipeB, recipeCacheListener));
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener));
        return builder.build();
    }

    @Override
    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addSlot(inputSlot = InputInventorySlot.at(item -> containsRecipeAB(item, gasTank.getStack()), this::containsRecipeA, recipeCacheListener, 56, 17))
                .tracksWarnings(slot -> slot.warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT)));
        builder.addSlot(gasSlot = GasInventorySlot.fillOrConvert(gasTank, this::getLevel, listener, 56, 53));
        builder.addSlot(mainOutputSlot = OutputInventorySlot.at(listener, 116, 35));
        builder.addSlot(secondaryOutputSlot = OutputInventorySlot.at(listener, 132, 35));
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 31, 35));
        return builder.build();
    }

    protected boolean useStatisticalMechanics() {
        return false;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        gasSlot.fillTankOrConvert();
        recipeCacheLookupMonitor.updateAndProcess();
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<PlantingRecipe, InputRecipeCache.ItemChemical<Gas, GasStack, PlantingRecipe>> getRecipeType() {
        return MoreMachineRecipeType.PLANTING.get();
    }

    @Override
    public @Nullable PlantingRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandler, gasInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<PlantingRecipe> createNewCachedRecipe(@NotNull PlantingRecipe recipe, int cacheIndex) {
        return PlantingCacheRecipe.create(recipe, recheckAllRecipeErrors, itemInputHandler, gasInputHandler, gasUsageMultiplier,
                used -> usedSoFar = used, outputHandler)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(this::setActive)
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(this::setOperatingTicks);
    }

    public MachineEnergyContainer<TileEntityPlantingStation> getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        // Allow exact match or factories of the same type (as we will just ignore the extra data)
        return super.isConfigurationDataCompatible(tileType) || MoreMachineUtils.isSameMMTypeFactory(getBlockType(), tileType);
    }

    @Override
    public @Nullable PlantingUpgradeData getUpgradeData() {
        return new PlantingUpgradeData(redstone, getControlType(), getEnergyContainer(), getOperatingTicks(), usedSoFar, gasTank, energySlot, gasSlot, inputSlot, mainOutputSlot, secondaryOutputSlot, getComponents());
    }

    @Override
    public long getSavedUsedSoFar(int cacheIndex) {
        return usedSoFar;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        usedSoFar = nbt.getLong(NBTConstants.USED_SO_FAR);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        nbtTags.putLong(NBTConstants.USED_SO_FAR, usedSoFar);
    }
}
