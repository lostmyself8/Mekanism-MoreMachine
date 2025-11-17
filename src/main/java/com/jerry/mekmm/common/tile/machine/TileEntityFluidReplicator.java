package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.recipes.FluidStackGasToFluidStackRecipe;
import com.jerry.mekmm.api.recipes.cache.ReplicatorCachedRecipe;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.recipe.impl.FluidReplicatorIRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineGas;
import com.jerry.mekmm.common.util.ValidatorUtils;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.RegistryUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TileEntityFluidReplicator extends TileEntityProgressMachine<FluidStackGasToFluidStackRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);

    public static final int MAX_FLUID = 10 * FluidType.BUCKET_VOLUME;
    public static final long MAX_GAS = 10 * FluidType.BUCKET_VOLUME;
    private static final int BASE_TICKS_REQUIRED = 10 * SharedConstants.TICKS_PER_SECOND;

    public static HashMap<String, Integer> customRecipeMap = ValidatorUtils.getRecipeFromConfig(MoreMachineConfig.general.fluidReplicatorRecipe.get());

    public BasicFluidTank fluidInputTank;
    public BasicFluidTank fluidOutputTank;
    // 化学品存储槽
    public IGasTank gasTank;

    private MachineEnergyContainer<TileEntityFluidReplicator> energyContainer;

    private final IInputHandler<@NotNull FluidStack> fluidInputHandler;
    private final IOutputHandler<@NotNull FluidStack> fluidOutputHandler;
    private final ILongInputHandler<GasStack> chemicalInputHandler;

    FluidInventorySlot lFluidInputSlot;
    FluidInventorySlot rFluidInputSlot;
    // 流体储罐输入输出物品槽
    FluidInventorySlot fluidInputSlot;
    OutputInventorySlot fluidOutputSlot;
    // 气罐槽
    GasInventorySlot chemicalSlot;
    EnergyInventorySlot energySlot;

    public TileEntityFluidReplicator(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.FLUID_REPLICATOR, pos, state, TRACKED_ERROR_TYPES, BASE_TICKS_REQUIRED);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.FLUID, TransmissionType.GAS, TransmissionType.ENERGY);
        configComponent.setupItemIOConfig(List.of(fluidInputSlot, lFluidInputSlot), List.of(rFluidInputSlot, fluidOutputSlot), energySlot, false);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, chemicalSlot));
        }
        ConfigInfo fluidConfig = configComponent.getConfig(TransmissionType.FLUID);
        if (fluidConfig != null) {
            fluidConfig.addSlotInfo(DataType.INPUT, new FluidSlotInfo(true, false, fluidInputTank));
            fluidConfig.addSlotInfo(DataType.OUTPUT, new FluidSlotInfo(false, true, fluidOutputTank));
        }
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);
        configComponent.setupInputConfig(TransmissionType.GAS, gasTank);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.FLUID, TransmissionType.ITEM)
                .setCanTankEject(tank -> tank == fluidOutputTank);

        fluidInputHandler = InputHelper.getInputHandler(fluidInputTank, RecipeError.NOT_ENOUGH_INPUT);
        fluidOutputHandler = OutputHelper.getOutputHandler(fluidOutputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        chemicalInputHandler = InputHelper.getConstantInputHandler(gasTank);
    }

    @Override
    protected @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        FluidTankHelper builder = FluidTankHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addTank(fluidInputTank = BasicFluidTank.input(FluidType.BUCKET_VOLUME, TileEntityFluidReplicator::isValidFluidInput, recipeCacheListener));
        builder.addTank(fluidOutputTank = BasicFluidTank.output(MAX_FLUID, listener));
        return builder.build();
    }

    @Override
    protected @Nullable IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSideGasWithConfig(this::getDirection, this::getConfig);
        builder.addTank(gasTank = ChemicalTankBuilder.GAS.create(MAX_GAS, TileEntityFluidReplicator::isValidGasInput, recipeCacheListener));
        return builder.build();
    }

    @Override
    protected @Nullable IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener));
        return builder.build();
    }

    @Override
    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this::getDirection, this::getConfig);
        // 输入
        builder.addSlot(fluidInputSlot = FluidInventorySlot.fill(fluidInputTank, listener, 180, 71));
        builder.addSlot(fluidOutputSlot = OutputInventorySlot.at(listener, 180, 102));
        // 输出
        builder.addSlot(lFluidInputSlot = FluidInventorySlot.drain(fluidInputTank, listener, 29, 65));
        builder.addSlot(rFluidInputSlot = FluidInventorySlot.drain(fluidOutputTank, listener, 132, 65));
        // 化学品罐槽位置
        builder.addSlot(chemicalSlot = GasInventorySlot.fillOrConvert(gasTank, this::getLevel, listener, 8, 65));
        // 能量槽位置
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 152, 65));
        // 化学品罐槽减号图标
        chemicalSlot.setSlotOverlay(SlotOverlay.MINUS);
        fluidInputSlot.setSlotOverlay(SlotOverlay.MINUS);
        lFluidInputSlot.setSlotOverlay(SlotOverlay.PLUS);
        rFluidInputSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    public static boolean isValidFluidInput(FluidStack stack) {
        if (customRecipeMap != null) {
            return customRecipeMap.containsKey(Objects.requireNonNull(RegistryUtils.getName(stack.getFluid())).toString());
        }
        return false;
    }

    public static boolean isValidGasInput(Gas gas) {
        return gas.equals(MoreMachineGas.UU_MATTER.getChemical());
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        fluidInputSlot.fillTank(fluidOutputSlot);
        chemicalSlot.fillTankOrConvert();
        lFluidInputSlot.drainTank(fluidOutputSlot);
        rFluidInputSlot.drainTank(fluidOutputSlot);
        recipeCacheLookupMonitor.updateAndProcess();
    }

    public @Nullable MachineEnergyContainer<TileEntityFluidReplicator> getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<FluidStackGasToFluidStackRecipe, ?> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable FluidStackGasToFluidStackRecipe getRecipe(int cacheIndex) {
        return getRecipe(fluidInputHandler.getInput(), chemicalInputHandler.getInput());
    }

    @Override
    public @NotNull CachedRecipe<FluidStackGasToFluidStackRecipe> createNewCachedRecipe(@NotNull FluidStackGasToFluidStackRecipe recipe, int cacheIndex) {
        return ReplicatorCachedRecipe.fluidReplicator(recipe, recheckAllRecipeErrors, fluidInputHandler, chemicalInputHandler, fluidOutputHandler)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(this::setActive)
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(this::setOperatingTicks);
    }

    public static FluidStackGasToFluidStackRecipe getRecipe(FluidStack fluidStack, GasStack chemicalStack) {
        if (chemicalStack.isEmpty() || fluidStack.isEmpty()) {
            return null;
        }
        if (customRecipeMap != null) {
            Fluid fluid = fluidStack.getFluid();
            // 如果为空则赋值为0
            int amount = customRecipeMap.getOrDefault(RegistryUtils.getName(fluid).toString(), 0);
            // 防止null和配置文件中出现0
            if (amount == 0) return null;
            return new FluidReplicatorIRecipe(fluid, IngredientCreatorAccess.fluid().from(fluid, 1000),
                    IngredientCreatorAccess.gas().from(MoreMachineGas.UU_MATTER.getChemical(), amount),
                    new FluidStack(fluid, FluidType.BUCKET_VOLUME));
        }
        return null;
    }
}
