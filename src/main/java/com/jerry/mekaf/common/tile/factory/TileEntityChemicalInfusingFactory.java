package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.upgrade.ChemicalChemicalToChemicalUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ChemicalChemicalToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.ChemicalChemicalToChemicalCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.IEitherSideRecipeLookupHandler.EitherSideChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.EitherSideChemical;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.UpgradeUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TileEntityChemicalInfusingFactory extends TileEntityChemicalToChemicalFactory<ChemicalChemicalToChemicalRecipe> implements IHasDumpButton, EitherSideChemicalRecipeLookupHandler<ChemicalChemicalToChemicalRecipe> {

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

    // 原右侧储罐
    public IChemicalTank rightTank;

    private final IInputHandler<@NotNull ChemicalStack> rightInputHandler;

    ChemicalInventorySlot rightInputSlot;

    public TileEntityChemicalInfusingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, false, rightInputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, rightInputSlot));
        }
        ConfigInfo chemicalConfig = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (chemicalConfig != null) {
            chemicalConfig.addSlotInfo(DataType.INPUT_2, new ChemicalSlotInfo(true, false, rightTank));
            chemicalConfig.addSlotInfo(DataType.INPUT_1, new ChemicalSlotInfo(true, false, inputChemicalTanks));
            List<IChemicalTank> ioTank = new ArrayList<>(outputChemicalTanks);
            ioTank.add(rightTank);
            ioTank.addAll(inputChemicalTanks);
            chemicalConfig.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo(true, true, ioTank));
        }

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                .setCanTankEject(tank -> outputChemicalTanks.contains(tank));
        rightInputHandler = InputHelper.getInputHandler(rightTank, RecipeError.NOT_ENOUGH_RIGHT_INPUT);
    }

    @Override
    protected void addTanks(ChemicalTankHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addTanks(builder, listener, updateSortingListener);
        builder.addTank(rightTank = BasicChemicalTank.inputModern(MAX_CHEMICAL * tier.processes, this::containsRecipe, markAllMonitorsChanged(listener)));
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        builder.addSlot(rightInputSlot = ChemicalInventorySlot.fill(rightTank, listener, 7, 83));
        rightInputSlot.setSlotType(ContainerSlotType.INPUT);
        rightInputSlot.setSlotOverlay(SlotOverlay.MINUS);
    }

    @Override
    protected void handleSecondaryFuel() {
        rightInputSlot.fillTank();
    }

    @Override
    public boolean hasExtraResourceBar() {
        return true;
    }

    @Override
    public IChemicalTank getChemicalTankBar() {
        return rightTank;
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ChemicalChemicalToChemicalRecipe> cached, @NotNull ChemicalStack stack) {
        if (cached != null) {
            ChemicalChemicalToChemicalRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getLeftInput().testType(stack) && (rightTank.isEmpty() || cachedRecipe.getRightInput().testType(rightTank.getTypeHolder()));
        }
        return false;
    }

    @Override
    protected @Nullable ChemicalChemicalToChemicalRecipe findRecipe(int process, @NotNull ChemicalStack fallbackInput, @NotNull IChemicalTank outputSlot) {
        return getRecipeType().getInputCache().findFirstRecipe(level, fallbackInput, outputSlot.getStack());
    }

    @Override
    public boolean isChemicalValidForTank(@NotNull ChemicalStack stack) {
        return containsRecipe(rightTank.getStack(), stack) || containsRecipe(stack, rightTank.getStack());
    }

    @Override
    public boolean isValidInputChemical(@NotNull ChemicalStack stack) {
        return containsRecipe(stack, rightTank.getStack()) || containsRecipe(rightTank.getStack(), stack);
    }

    @Override
    protected int getNeededInput(ChemicalChemicalToChemicalRecipe recipe, ChemicalStack inputStack) {
        return MathUtils.clampToInt(recipe.getLeftInput().getNeededAmount(inputStack));
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, ChemicalChemicalToChemicalRecipe, EitherSideChemical<ChemicalChemicalToChemicalRecipe>> getRecipeType() {
        return MekanismRecipeType.CHEMICAL_INFUSING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<ChemicalChemicalToChemicalRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.CHEMICAL_INFUSING;
    }

    @Override
    public @Nullable ChemicalChemicalToChemicalRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(chemicalInputHandlers[cacheIndex], rightInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<ChemicalChemicalToChemicalRecipe> createNewCachedRecipe(@NotNull ChemicalChemicalToChemicalRecipe recipe, int cacheIndex) {
        return new ChemicalChemicalToChemicalCachedRecipe<>(recipe, recheckAllRecipeErrors[cacheIndex], chemicalInputHandlers[cacheIndex], rightInputHandler, chemicalOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(this::canFunction)
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setBaselineMaxOperations(() -> baselineMaxOperations)
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
    public void parseUpgradeData(HolderLookup.Provider provider, @NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof ChemicalChemicalToChemicalUpgradeData data) {
            super.parseUpgradeData(provider, upgradeData);
            rightTank.deserializeNBT(provider, data.inputTank.serializeNBT(provider));
            rightInputSlot.deserializeNBT(provider, data.chemicalSlot.serializeNBT(provider));
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @Override
    public @Nullable IUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new ChemicalChemicalToChemicalUpgradeData(provider, redstone, getControlType(), energyContainer, progress, null,
                energySlot, rightInputSlot, inputChemicalTanks, rightTank, outputChemicalTanks, isSorting(), getComponents());
    }

    @Override
    public void dump() {
        rightTank.setEmpty();
    }
}
