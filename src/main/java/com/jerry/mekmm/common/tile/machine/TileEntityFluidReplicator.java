package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.datamaps.FluidReplicatorRecipe;
import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.api.recipes.basic.BasicFluidChemicalToFluidRecipe;
import com.jerry.mekmm.api.recipes.cache.ReplicatorCachedRecipe;
import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.recipe.impl.FluidReplicatorIRecipeSingle;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineChemicals;
import com.jerry.mekmm.common.util.MoreMachineUtils;
import com.jerry.mekmm.common.util.ValidatorUtils;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
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
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.FluidType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class TileEntityFluidReplicator extends TileEntityProgressMachine<BasicFluidChemicalToFluidRecipe> {

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

    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class,
                            methodNames = { "getInput", "getInputCapacity", "getInputNeeded",
                                    "getInputFilledPercentage" },
                            docPlaceholder = "input tank")
    public BasicFluidTank inputTank;
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class,
                            methodNames = { "getOutput", "getOutputCapacity", "getOutputNeeded",
                                    "getOutputFilledPercentage" },
                            docPlaceholder = "output tank")
    public BasicFluidTank outputTank;
    // 化学品存储槽
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getUU", "getUUCapacity", "getUUNeeded",
                                    "getUUFilledPercentage" },
                            docPlaceholder = "uu tank")
    public IChemicalTank uuTank;

    private MachineEnergyContainer<TileEntityFluidReplicator> energyContainer;

    private final IInputHandler<Fluid, @NotNull FluidStack> fluidInputHandler;
    private final IOutputHandler<@NotNull FluidStackTemplate> fluidOutputHandler;
    private final ILongInputHandler<Chemical, @NotNull ChemicalStack> chemicalInputHandler;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputTankOutputSlot", docPlaceholder = "input tank output slot")
    FluidInventorySlot inputTankOutputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputTankOutputSlot", docPlaceholder = "output tank output slot")
    FluidInventorySlot outputTankOutputSlot;
    // 流体储罐输入输出物品槽(GUI外的两个槽)
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputSlot", docPlaceholder = "input slot")
    FluidInventorySlot fluidInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputSlot", docPlaceholder = "output slot")
    OutputInventorySlot fluidOutputSlot;
    // 气罐槽
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getUUSlot", docPlaceholder = "uu slot")
    ChemicalInventorySlot uuSlot;
    EnergyInventorySlot energySlot;

    public TileEntityFluidReplicator(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.FLUID_REPLICATOR, pos, state, TRACKED_ERROR_TYPES, BASE_TICKS_REQUIRED);
        configComponent.setupItemIOConfig(List.of(fluidInputSlot, inputTankOutputSlot), List.of(outputTankOutputSlot, fluidOutputSlot), energySlot, false);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, uuSlot));
        }
        ConfigInfo fluidConfig = configComponent.getConfig(TransmissionType.FLUID);
        if (fluidConfig != null) {
            fluidConfig.addSlotInfo(DataType.INPUT, new FluidSlotInfo(true, false, inputTank));
            fluidConfig.addSlotInfo(DataType.OUTPUT, new FluidSlotInfo(false, true, outputTank));
        }
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);
        configComponent.setupInputConfig(TransmissionType.CHEMICAL, uuTank);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.FLUID, TransmissionType.ITEM)
                .setCanTankEject(tank -> tank == outputTank);

        fluidInputHandler = InputHelper.getInputHandler(inputTank, RecipeError.NOT_ENOUGH_INPUT);
        fluidOutputHandler = OutputHelper.getOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        chemicalInputHandler = InputHelper.getConstantInputHandler(uuTank);
    }

    @Override
    protected @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        FluidTankHelper builder = FluidTankHelper.forSideWithConfig(this);
        builder.addTank(inputTank = BasicFluidTank.input(MAX_FLUID / 2, TileEntityFluidReplicator::isValidFluidInput, recipeCacheListener));
        builder.addTank(outputTank = BasicFluidTank.output(MAX_FLUID, recipeCacheUnpauseListener));
        return builder.build();
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        ChemicalTankHelper builder = ChemicalTankHelper.forSideWithConfig(this);
        builder.addTank(uuTank = BasicChemicalTank.input(MAX_GAS, TileEntityFluidReplicator::isValidChemicalInput, recipeCacheListener));
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener));
        return builder.build();
    }

    @Override
    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this);
        // 输入
        builder.addSlot(fluidInputSlot = FluidInventorySlot.fill(inputTank, listener, 180, 71));
        builder.addSlot(fluidOutputSlot = OutputInventorySlot.at(listener, 180, 102));
        // 输出
        builder.addSlot(inputTankOutputSlot = FluidInventorySlot.drain(inputTank, listener, 29, 65));
        builder.addSlot(outputTankOutputSlot = FluidInventorySlot.drain(outputTank, listener, 132, 65));
        // 化学品罐槽位置
        builder.addSlot(uuSlot = ChemicalInventorySlot.fillOrConvert(uuTank, this::getLevel, listener, 8, 65));
        // 能量槽位置
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 152, 65));
        // 化学品罐槽减号图标
        uuSlot.setSlotOverlay(SlotOverlay.MINUS);
        fluidInputSlot.setSlotOverlay(SlotOverlay.MINUS);
        inputTankOutputSlot.setSlotOverlay(SlotOverlay.PLUS);
        outputTankOutputSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    public static boolean isValidFluidInput(FluidStack stack) {
        return IMoreMachineDataMapTypes.INSTANCE.getFluidReplicatorRecipe(stack.typeHolder()) != null;
    }

    public static boolean isValidChemicalInput(ChemicalStack stack) {
        return stack.is(MoreMachineChemicals.UU_MATTER);
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        fluidInputSlot.fillTank(fluidOutputSlot);
        uuSlot.fillTankOrConvert();
        inputTankOutputSlot.drainTank(fluidOutputSlot);
        outputTankOutputSlot.drainTank(fluidOutputSlot);
        recipeCacheLookupMonitor.updateAndProcess();
        return sendUpdatePacket;
    }

    public @Nullable MachineEnergyContainer<TileEntityFluidReplicator> getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, BasicFluidChemicalToFluidRecipe, ?> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable BasicFluidChemicalToFluidRecipe getRecipe(int cacheIndex) {
        return getRecipe(fluidInputHandler.getInput(), chemicalInputHandler.getInput());
    }

    @Override
    public @NotNull CachedRecipe<BasicFluidChemicalToFluidRecipe> createNewCachedRecipe(@NotNull BasicFluidChemicalToFluidRecipe recipe, int cacheIndex) {
        return ReplicatorCachedRecipe.createFluidReplicator(recipe, recheckAllRecipeErrors, fluidInputHandler, chemicalInputHandler, fluidOutputHandler)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(this::canFunction)
                .setActive(this::setActive)
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(this::setOperatingTicks)
                .setBaselineMaxOperations(this::getOperationsPerTick);
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<BasicFluidChemicalToFluidRecipe> recipeViewerType() {
        return MMRecipeViewerRecipeType.FLUID_REPLICATOR;
    }

    public static BasicFluidChemicalToFluidRecipe getRecipe(FluidStack fluidStack, ChemicalStack chemicalStack) {
        if (chemicalStack.isEmpty() || fluidStack.isEmpty()) {
            return null;
        }

        // if (customRecipeMap != null) {
        // Holder<Fluid> fluidHolder = fluidStack.getFluidHolder();
        // // 如果为空则赋值为0
        // int amount =
        // customRecipeMap.getOrDefault(Objects.requireNonNull(RegistryUtils.getName(fluidHolder)).toString(), 0);
        // // 防止null和配置文件中出现0
        // if (amount == 0) return null;
        // return new FluidReplicatorIRecipeSingle(
        // IngredientCreatorAccess.fluid().fromHolder(fluidHolder, 1000),
        // IngredientCreatorAccess.chemicalStack().fromHolder(MoreMachineChemicals.UU_MATTER, amount),
        // new FluidStack(fluidHolder, FluidType.BUCKET_VOLUME));
        // }

        Holder<Fluid> fluidHolder = fluidStack.typeHolder();
        FluidReplicatorRecipe recipe = IMoreMachineDataMapTypes.INSTANCE.getFluidReplicatorRecipe(fluidHolder);
        if (recipe != null) {
            return new FluidReplicatorIRecipeSingle(
                    IngredientCreatorAccess.fluid().fromHolder(fluidHolder, recipe.inputAmount()),
                    IngredientCreatorAccess.chemicalStack().fromHolder(MoreMachineChemicals.UU_MATTER, recipe.UUAmount()),
                    new FluidStack(fluidHolder, recipe.outputAmount()));
        }
        return null;
    }

    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameMMTypeFactory(getBlockHolder(), type);
    }

    // Methods relating to IComputerTile
    @ComputerMethod(methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    long getEnergyUsage() {
        return getActive() ? energyContainer.getEnergyPerTick() : 0;
    }
    // End methods IComputerTile
}
