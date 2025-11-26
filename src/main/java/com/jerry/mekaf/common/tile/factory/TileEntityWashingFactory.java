package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.upgrade.FluidChemicalToChemicalUpgradeData;

import com.jerry.mekmm.Mekmm;

import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.FluidChemicalToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.TwoInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.FluidChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.DoubleInputRecipeCache;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tier.FactoryTier;
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
import net.neoforged.neoforge.fluids.FluidStack;

import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityWashingFactory extends TileEntityChemicalToChemicalFactory<FluidChemicalToChemicalRecipe> implements IHasDumpButton, FluidChemicalRecipeLookupHandler<FluidChemicalToChemicalRecipe> {

    protected static final DoubleInputRecipeCache.CheckRecipeType<FluidStack, ChemicalStack, FluidChemicalToChemicalRecipe, ChemicalStack> OUTPUT_CHECK = (recipe, fluidInput, chemicalInput, output) -> ChemicalStack.isSameChemical(recipe.getOutput(fluidInput, chemicalInput), output);
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT);

    public BasicFluidTank fluidTank;

    private final IInputHandler<@NotNull FluidStack> fluidInputHandler;

    FluidInventorySlot fluidSlot;
    OutputInventorySlot fluidOutputSlot;

    public TileEntityWashingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, false, fluidSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(false, true, fluidOutputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, fluidSlot, fluidOutputSlot));
        }
        ConfigInfo config = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (config != null) {
            config.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo(true, false, inputChemicalTanks));
            List<IChemicalTank> ioTank = outputChemicalTanks;
            ioTank.addAll(inputChemicalTanks);
            config.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo(true, true, ioTank));
        }

        configComponent.setupInputConfig(TransmissionType.FLUID, fluidTank);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                .setCanTankEject(tank -> !inputChemicalTanks.contains(tank));

        fluidInputHandler = InputHelper.getInputHandler(fluidTank, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSideWithConfig(this);
        builder.addTank(fluidTank = BasicFluidTank.input(MAX_FLUID * tier.processes * tier.processes, this::containsRecipeA, markAllMonitorsChanged(listener)));
        return builder.build();
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        builder.addSlot(fluidSlot = FluidInventorySlot.fill(fluidTank, listener, slotX(), 71));
        builder.addSlot(fluidOutputSlot = OutputInventorySlot.at(listener, slotX(), 102));
        fluidSlot.setSlotOverlay(SlotOverlay.MINUS);
    }

    private int slotX() {
        if (Mekmm.hooks.evolvedMekanism.isLoaded() && tier.ordinal() >= EMFactoryTier.OVERCLOCKED.ordinal()) {
            return 214 + 8 * tier.ordinal();
        }
        return tier == FactoryTier.ULTIMATE ? 214 : 180;
    }

    public BasicFluidTank getFluidTankBar() {
        return fluidTank;
    }

    @Override
    public boolean hasExtraResourceBar() {
        return true;
    }

    @Override
    protected void handleSecondaryFuel() {
        fluidSlot.fillTank(fluidOutputSlot);
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<FluidChemicalToChemicalRecipe> cached, @NotNull ChemicalStack stack) {
        if (cached != null) {
            FluidChemicalToChemicalRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getChemicalInput().testType(stack) && (fluidTank.isEmpty() || cachedRecipe.getFluidInput().testType(fluidTank.getFluid()));
        }
        return false;
    }

    @Override
    protected @Nullable FluidChemicalToChemicalRecipe findRecipe(int process, @NotNull ChemicalStack fallbackInput, @NotNull IChemicalTank outputSlot) {
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fluidTank.getFluid(), fallbackInput, outputSlot.getStack(), OUTPUT_CHECK);
    }

    @Override
    public boolean isChemicalValidForTank(@NotNull ChemicalStack stack) {
        return containsRecipeAB(fluidTank.getFluid(), stack);
    }

    @Override
    public boolean isValidInputChemical(@NotNull ChemicalStack stack) {
        return containsRecipeB(stack);
    }

    @Override
    protected int getNeededInput(FluidChemicalToChemicalRecipe recipe, ChemicalStack inputStack) {
        return MathUtils.clampToInt(recipe.getChemicalInput().getNeededAmount(inputStack));
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, FluidChemicalToChemicalRecipe, InputRecipeCache.FluidChemical<FluidChemicalToChemicalRecipe>> getRecipeType() {
        return MekanismRecipeType.WASHING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<FluidChemicalToChemicalRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.WASHING;
    }

    @Override
    public @Nullable FluidChemicalToChemicalRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(fluidInputHandler, chemicalInputHandlers[cacheIndex]);
    }

    @Override
    public @NotNull CachedRecipe<FluidChemicalToChemicalRecipe> createNewCachedRecipe(@NotNull FluidChemicalToChemicalRecipe recipe, int cacheIndex) {
        return TwoInputCachedRecipe.fluidChemicalToChemical(recipe, recheckAllRecipeErrors[cacheIndex], fluidInputHandler, chemicalInputHandlers[cacheIndex], chemicalOutputHandlers[cacheIndex])
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

    // 更改加速升级的显示的，默认是10x，气体工厂是256x
    @NotNull
    @Override
    public List<Component> getInfo(@NotNull Upgrade upgrade) {
        return UpgradeUtils.getExpScaledInfo(this, upgrade);
    }

    @Override
    public void parseUpgradeData(HolderLookup.Provider provider, @NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof FluidChemicalToChemicalUpgradeData data) {
            super.parseUpgradeData(provider, upgradeData);
            fluidTank.deserializeNBT(provider, data.inputTank.serializeNBT(provider));
            fluidSlot.deserializeNBT(provider, data.fluidInputSlot.serializeNBT(provider));
            fluidOutputSlot.deserializeNBT(provider, data.fluidOutputSlot.serializeNBT(provider));
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @Override
    public @Nullable IUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new FluidChemicalToChemicalUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), progress, null,
                energySlot, fluidSlot, fluidOutputSlot, inputChemicalTanks, fluidTank, outputChemicalTanks, isSorting(), getComponents());
    }

    @Override
    public void dump() {
        fluidTank.setStack(FluidStack.EMPTY);
    }
}
