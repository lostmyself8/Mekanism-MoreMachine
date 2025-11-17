package com.jerry.mekaf.common.tile;

import com.jerry.mekaf.common.tile.base.TileEntitySlurryToSlurryFactory;
import com.jerry.mekaf.common.upgrade.FluidSlurryToSlurryUpgradeData;

import com.jerry.mekmm.Mekmm;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.FluidSlurryToSlurryRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.TwoInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
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
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityWashingFactory extends TileEntitySlurryToSlurryFactory<FluidSlurryToSlurryRecipe> implements FluidChemicalRecipeLookupHandler<Slurry, SlurryStack, FluidSlurryToSlurryRecipe>, IHasDumpButton {

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
    private static final int MAX_FLUID = 10_000;

    public BasicFluidTank fluidTank;

    private final IInputHandler<@NotNull FluidStack> fluidInputHandler;

    FluidInventorySlot fluidInputSlot;
    OutputInventorySlot fluidOutputSlot;

    public TileEntityWashingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        addSupported(TransmissionType.FLUID);

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, false, fluidInputSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(false, true, fluidOutputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, fluidInputSlot, fluidOutputSlot));
            itemConfig.setDefaults();
        }
        ConfigInfo slurryConfig = configComponent.getConfig(TransmissionType.SLURRY);
        if (slurryConfig != null) {
            slurryConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.SlurrySlotInfo(true, false, inputSlurryTanks));
            List<ISlurryTank> ioTank = outputSlurryTanks;
            ioTank.addAll(inputSlurryTanks);
            slurryConfig.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo.SlurrySlotInfo(true, true, ioTank));
        }
        configComponent.setupInputConfig(TransmissionType.FLUID, fluidTank);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.SLURRY)
                .setCanTankEject(tank -> !inputSlurryTanks.contains(tank));

        fluidInputHandler = InputHelper.getInputHandler(fluidTank, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
    }

    @Override
    protected @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addTank(fluidTank = BasicFluidTank.input(MAX_FLUID * tier.processes * tier.processes, this::containsRecipeA, markAllMonitorsChanged(listener)));
        return builder.build();
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        builder.addSlot(fluidInputSlot = FluidInventorySlot.fill(fluidTank, listener, getFluidSlotX(), 71));
        builder.addSlot(fluidOutputSlot = OutputInventorySlot.at(listener, getFluidSlotX(), 102));
        fluidInputSlot.setSlotOverlay(SlotOverlay.MINUS);
    }

    private int getFluidSlotX() {
        // 想尝试使用Emek的gui布局，但似乎有点麻烦，还是采用原始布局吧
        if (Mekmm.hooks.EMLoaded) {
            if (tier.ordinal() >= EMFactoryTier.OVERCLOCKED.ordinal()) {
                // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
                // 这个公式似乎并非完美，在index过大时可能会导致有细微的便宜，但未得到验证
                int index = tier.ordinal() - 4;
                return 180 + (36 * (index + 2)) + (2 * index);
            }
        }
        return tier == FactoryTier.ULTIMATE ? 214 : 180;
    }

    public BasicFluidTank getFluidTankBar() {
        return fluidTank;
    }

    @Override
    public boolean hasExtrasResourceBar() {
        return true;
    }

    @Override
    protected void handleExtrasFuel() {
        fluidInputSlot.fillTank(fluidOutputSlot);
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<FluidSlurryToSlurryRecipe> cached, @NotNull SlurryStack stack) {
        return false;
    }

    @Override
    protected @Nullable FluidSlurryToSlurryRecipe findRecipe(int process, @NotNull SlurryStack fallbackInput, @NotNull ISlurryTank outputTanks) {
        FluidStack inputA = fluidTank.getFluid();
        SlurryStack output = outputTanks.getStack();
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, inputA, fallbackInput, recipe -> output.isTypeEqual(recipe.getOutput(inputA, fallbackInput)));
    }

    @Override
    public boolean isChemicalValidForTank(@NotNull SlurryStack stack) {
        return containsRecipeAB(fluidTank.getFluid(), stack);
    }

    @Override
    public boolean isValidInputChemical(@NotNull SlurryStack stack) {
        return containsRecipeB(stack);
    }

    @Override
    protected int getNeededInput(FluidSlurryToSlurryRecipe recipe, SlurryStack inputStack) {
        return MathUtils.clampToInt(recipe.getChemicalInput().getNeededAmount(inputStack));
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<FluidSlurryToSlurryRecipe, InputRecipeCache.FluidChemical<Slurry, SlurryStack, FluidSlurryToSlurryRecipe>> getRecipeType() {
        return MekanismRecipeType.WASHING;
    }

    @Override
    public @Nullable FluidSlurryToSlurryRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(fluidInputHandler, slurryInputHandlers[cacheIndex]);
    }

    @Override
    public @NotNull CachedRecipe<FluidSlurryToSlurryRecipe> createNewCachedRecipe(@NotNull FluidSlurryToSlurryRecipe recipe, int cacheIndex) {
        return TwoInputCachedRecipe.fluidChemicalToChemical(recipe, recheckAllRecipeErrors[cacheIndex], fluidInputHandler, slurryInputHandlers[cacheIndex], slurryOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setBaselineMaxOperations(this::getBaselineMaxOperations)
                .setOnFinish(this::markForSave);
    }

    @Override
    protected void sortInventoryOrTank() {}

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof FluidSlurryToSlurryUpgradeData data) {
            super.parseUpgradeData(upgradeData);
            fluidTank.deserializeNBT(data.inputTank.serializeNBT());
            fluidInputSlot.deserializeNBT(data.fluidInputSlot.serializeNBT());
            fluidOutputSlot.deserializeNBT(data.fluidOutputSlot.serializeNBT());
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @Override
    public @Nullable IUpgradeData getUpgradeData() {
        return new FluidSlurryToSlurryUpgradeData(redstone, getControlType(), getEnergyContainer(), progress, null,
                energySlot, fluidInputSlot, fluidOutputSlot, inputSlurryTanks, fluidTank, outputSlurryTanks, isSorting(), getComponents());
    }

    @Override
    public void dump() {
        fluidTank.setEmpty();
    }
}
