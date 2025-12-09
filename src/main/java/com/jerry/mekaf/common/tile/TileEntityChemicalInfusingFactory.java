package com.jerry.mekaf.common.tile;

import com.jerry.mekaf.common.tile.base.TileEntityGasToGasFactory;
import com.jerry.mekaf.common.upgrade.GasGasToGasUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.Upgrade;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ChemicalInfuserRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.ChemicalChemicalToChemicalCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.IEitherSideRecipeLookupHandler.EitherSideChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UpgradeUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TileEntityChemicalInfusingFactory extends TileEntityGasToGasFactory<ChemicalInfuserRecipe> implements EitherSideChemicalRecipeLookupHandler<Gas, GasStack, ChemicalInfuserRecipe>, IHasDumpButton {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_LEFT_INPUT,
            RecipeError.NOT_ENOUGH_RIGHT_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_RIGHT_INPUT);
    public static final long MAX_GAS = 10_000;

    public IGasTank rightTank;

    private final IInputHandler<@NotNull GasStack> rightInputHandler;

    GasInventorySlot rightInputSlot;

    public TileEntityChemicalInfusingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, false, rightInputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, rightInputSlot));
            itemConfig.setDataType(DataType.EXTRA, RelativeSide.BOTTOM);
            itemConfig.setDataType(DataType.ENERGY, RelativeSide.BACK);
        }
        ConfigInfo chemicalConfig = configComponent.getConfig(TransmissionType.GAS);
        if (chemicalConfig != null) {
            chemicalConfig.addSlotInfo(DataType.INPUT_2, new ChemicalSlotInfo.GasSlotInfo(true, false, rightTank));
            chemicalConfig.addSlotInfo(DataType.INPUT_1, new ChemicalSlotInfo.GasSlotInfo(true, false, inputGasTanks));
            List<IGasTank> ioTank = new ArrayList<>(List.of(rightTank));
            ioTank.addAll(inputGasTanks);
            chemicalConfig.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo.GasSlotInfo(true, true, ioTank));
            chemicalConfig.setDataType(DataType.INPUT_1, RelativeSide.LEFT);
            chemicalConfig.setDataType(DataType.INPUT_2, RelativeSide.RIGHT);
            chemicalConfig.setDataType(DataType.OUTPUT, RelativeSide.FRONT);
            chemicalConfig.setEjecting(true);
        }

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.GAS)
                .setCanTankEject(outputGasTanks::contains);
        rightInputHandler = InputHelper.getInputHandler(rightTank, RecipeError.NOT_ENOUGH_RIGHT_INPUT);
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ChemicalInfuserRecipe> cached, @NotNull GasStack stack) {
        if (cached != null) {
            ChemicalInfuserRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getLeftInput().testType(stack) && (rightTank.isEmpty() || cachedRecipe.getRightInput().testType(rightTank.getStack()));
        }
        return false;
    }

    @Override
    protected @Nullable ChemicalInfuserRecipe findRecipe(int process, @NotNull GasStack fallbackInput, @NotNull IGasTank outputTank) {
        return getRecipeType().getInputCache().findFirstRecipe(level, fallbackInput, outputTank.getStack());
    }

    @Override
    public boolean isChemicalValidForTank(@NotNull GasStack stack) {
        return containsRecipe(rightTank.getStack(), stack) || containsRecipe(stack, rightTank.getStack());
    }

    @Override
    public boolean isValidInputChemical(@NotNull GasStack stack) {
        return containsRecipe(stack, rightTank.getStack()) || containsRecipe(rightTank.getStack(), stack);
    }

    @Override
    protected int getNeededInput(ChemicalInfuserRecipe recipe, GasStack inputStack) {
        return MathUtils.clampToInt(recipe.getLeftInput().getNeededAmount(inputStack));
    }

    @Override
    protected void addGasTanks(ChemicalTankHelper<Gas, GasStack, IGasTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addGasTanks(builder, listener, updateSortingListener);
        builder.addTank(rightTank = ChemicalTankBuilder.GAS.create(MAX_GAS * tier.processes, this::containsRecipe, markAllMonitorsChanged(listener)));
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        builder.addSlot(rightInputSlot = GasInventorySlot.fill(rightTank, listener, 7, 83));
        rightInputSlot.setSlotType(ContainerSlotType.INPUT);
        rightInputSlot.setSlotOverlay(SlotOverlay.MINUS);
    }

    @Override
    public IGasTank getGasTankBar() {
        return rightTank;
    }

    @Override
    protected void handleExtrasFuel() {
        rightInputSlot.fillTank();
    }

    @Override
    public boolean hasExtrasResourceBar() {
        return true;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<ChemicalInfuserRecipe, InputRecipeCache.EitherSideChemical<Gas, GasStack, ChemicalInfuserRecipe>> getRecipeType() {
        return MekanismRecipeType.CHEMICAL_INFUSING;
    }

    @Override
    public @Nullable ChemicalInfuserRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(gasInputHandlers[cacheIndex], rightInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<ChemicalInfuserRecipe> createNewCachedRecipe(@NotNull ChemicalInfuserRecipe recipe, int cacheIndex) {
        return new ChemicalChemicalToChemicalCachedRecipe<>(recipe, recheckAllRecipeErrors[cacheIndex], gasInputHandlers[cacheIndex], rightInputHandler, gasOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setBaselineMaxOperations(this::getBaselineMaxOperations)
                .setOnFinish(this::markForSave);
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            baselineMaxOperations = (int) Math.pow(2, upgradeComponent.getUpgrades(Upgrade.SPEED));
        }
    }

    // 更改加速升级的显示的，默认是10x，气体工厂是256x，当然只有速度升级需要更改
    @NotNull
    @Override
    public List<Component> getInfo(@NotNull Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? UpgradeUtils.getExpScaledInfo(this, upgrade) : super.getInfo(upgrade);
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof GasGasToGasUpgradeData data) {
            super.parseUpgradeData(upgradeData);
            rightTank.deserializeNBT(data.inputTank.serializeNBT());
            rightInputSlot.deserializeNBT(data.chemicalSlot.serializeNBT());
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @Override
    public @Nullable IUpgradeData getUpgradeData() {
        return new GasGasToGasUpgradeData(redstone, getControlType(), energyContainer, progress, null,
                energySlot, rightInputSlot, inputGasTanks, rightTank, outputGasTanks, isSorting(), getComponents());
    }

    @Override
    public void dump() {
        rightTank.setEmpty();
    }
}
