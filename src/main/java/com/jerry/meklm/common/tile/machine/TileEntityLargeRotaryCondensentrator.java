package com.jerry.meklm.common.tile.machine;

import com.jerry.meklm.common.capabilities.holder.chemical.AdjustableChemicalTankHelper;
import com.jerry.meklm.common.capabilities.holder.fluid.AdjustableFluidTankHelper;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import com.jerry.meklm.common.tile.INotNeedConfig;

import mekanism.api.*;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.recipes.RotaryRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.RotaryCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.api.recipes.vanilla_input.RotaryRecipeInput;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.RotaryInputRecipeCache;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.EnergySlotInfo;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.interfaces.IHasMode;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.FluidType;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class TileEntityLargeRotaryCondensentrator extends TileEntityRecipeMachine<RotaryRecipe> implements IHasMode, IBoundingBlock, INotNeedConfig {

    public static final RecipeError NOT_ENOUGH_FLUID_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_GAS_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SPACE_FLUID_OUTPUT_ERROR = RecipeError.create();
    public static final int CAPACITY = 10 * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            NOT_ENOUGH_FLUID_INPUT_ERROR,
            NOT_ENOUGH_GAS_INPUT_ERROR,
            NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR,
            NOT_ENOUGH_SPACE_FLUID_OUTPUT_ERROR,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private final IOutputHandler<@NotNull ChemicalStack> chemicalOutputHandler;
    private final IOutputHandler<@NotNull FluidStackTemplate> fluidOutputHandler;
    private final IInputHandler<Fluid, @NotNull FluidStack> fluidInputHandler;
    private final IInputHandler<Chemical, @NotNull ChemicalStack> chemicalInputHandler;

    @Nullable
    private List<BlockCapabilityCache<IChemicalHandler, @Nullable Direction>> leftOutputCaches;
    @Nullable
    @SuppressWarnings("removal")
    private List<BlockCapabilityCache<net.neoforged.neoforge.fluids.capability.IFluidHandler, @Nullable Direction>> rightOutputCaches;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getChemical", "getChemicalCapacity", "getChemicalNeeded",
                                    "getChemicalFilledPercentage" },
                            docPlaceholder = "gas tank")
    public IChemicalTank chemicalTank;
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class,
                            methodNames = { "getFluid", "getFluidCapacity", "getFluidNeeded",
                                    "getFluidFilledPercentage" },
                            docPlaceholder = "fluid tank")
    public BasicFluidTank fluidTank;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getChemicalItemInput", docPlaceholder = "gas item input slot")
    ChemicalInventorySlot gasInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getChemicalItemOutput", docPlaceholder = "gas item output slot")
    ChemicalInventorySlot gasOutputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFluidItemInput", docPlaceholder = "fluid item input slot")
    FluidInventorySlot fluidInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFluidItemOutput", docPlaceholder = "fluid item ouput slot")
    OutputInventorySlot fluidOutputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;
    /**
     * True: fluid -> chemical
     * <p>
     * False: chemical -> fluid
     */
    private boolean mode;
    private long clientEnergyUsed = 0;
    private int baselineMaxOperations = 1;
    private int baseOperations = 1;
    private int numPowering;
    @Getter
    private MachineEnergyContainer<TileEntityLargeRotaryCondensentrator> energyContainer;

    public TileEntityLargeRotaryCondensentrator(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_ROTARY_CONDENSENTRATOR, pos, state, TRACKED_ERROR_TYPES);

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, true, List.of(gasInputSlot, fluidInputSlot)));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(true, true, List.of(gasOutputSlot, fluidOutputSlot)));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, List.of(gasInputSlot, fluidInputSlot, gasOutputSlot, fluidOutputSlot)));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }
        ConfigInfo fluidConfig = configComponent.getConfig(TransmissionType.FLUID);
        if (fluidConfig != null) {
            fluidConfig.addSlotInfo(DataType.INPUT, new FluidSlotInfo(true, true, fluidTank));
            fluidConfig.addSlotInfo(DataType.OUTPUT, new FluidSlotInfo(true, true, fluidTank));
            fluidConfig.addSlotInfo(DataType.INPUT_OUTPUT, new FluidSlotInfo(true, true, fluidTank));
        }
        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (gasConfig != null) {
            gasConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo(true, true, chemicalTank));
            gasConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo(true, true, chemicalTank));
            gasConfig.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo(true, true, chemicalTank));
        }
        ConfigInfo energyConfig = configComponent.getConfig(TransmissionType.ENERGY);
        if (energyConfig != null) {
            energyConfig.addSlotInfo(DataType.INPUT, new EnergySlotInfo(true, false, energyContainer));
        }

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL, TransmissionType.FLUID)
                .setCanEject(transmissionType -> {
                    if (transmissionType == TransmissionType.CHEMICAL) {
                        return mode;
                    } else if (transmissionType == TransmissionType.FLUID) {
                        return !mode;
                    }
                    return true;
                });

        chemicalInputHandler = InputHelper.getInputHandler(chemicalTank, NOT_ENOUGH_GAS_INPUT_ERROR);
        fluidInputHandler = InputHelper.getInputHandler(fluidTank, NOT_ENOUGH_FLUID_INPUT_ERROR);
        chemicalOutputHandler = OutputHelper.getOutputHandler(chemicalTank, NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR);
        fluidOutputHandler = OutputHelper.getOutputHandler(fluidTank, NOT_ENOUGH_SPACE_FLUID_OUTPUT_ERROR);
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        AdjustableChemicalTankHelper builder = AdjustableChemicalTankHelper.forSide(facingSupplier, side -> side == RelativeSide.BACK, side -> side == RelativeSide.LEFT);
        builder.addTank(chemicalTank = BasicChemicalTank.create(CAPACITY, (gas, automationType) -> automationType == AutomationType.MANUAL || mode,
                (gas, automationType) -> automationType == AutomationType.INTERNAL || !mode, this::isValidChemical, ChemicalAttributeValidator.ALWAYS_ALLOW,
                recipeCacheListener), RelativeSide.BACK, RelativeSide.LEFT);
        return builder.build();
    }

    private boolean isValidChemical(@NotNull ChemicalStack chemical) {
        return getRecipeType().getInputCache().containsInputChemical(level, chemical);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        AdjustableFluidTankHelper builder = AdjustableFluidTankHelper.forSide(facingSupplier, side -> side == RelativeSide.BACK, side -> side == RelativeSide.RIGHT);
        builder.addTank(fluidTank = BasicFluidTank.create(CAPACITY, (fluid, automationType) -> automationType == AutomationType.MANUAL || !mode,
                (fluid, automationType) -> automationType == AutomationType.INTERNAL || mode, this::isValidFluid, recipeCacheListener), RelativeSide.BACK, RelativeSide.RIGHT);
        return builder.build();
    }

    private boolean isValidFluid(@NotNull FluidStack fluidStack) {
        return getRecipeType().getInputCache().containsInputFluid(level, fluidStack);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(facingSupplier);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier);
        BooleanSupplier modeSupplier = this::getMode;
        builder.addSlot(gasInputSlot = ChemicalInventorySlot.rotaryDrain(chemicalTank, modeSupplier, listener, 5, 25), RelativeSide.BACK);
        builder.addSlot(gasOutputSlot = ChemicalInventorySlot.rotaryFill(chemicalTank, modeSupplier, listener, 5, 56), RelativeSide.LEFT);
        builder.addSlot(fluidInputSlot = FluidInventorySlot.rotary(fluidTank, modeSupplier, listener, 155, 25), RelativeSide.BACK);
        builder.addSlot(fluidOutputSlot = OutputInventorySlot.at(listener, 155, 56), RelativeSide.RIGHT);
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 155, 5), RelativeSide.BACK);
        gasInputSlot.setSlotType(ContainerSlotType.INPUT);
        gasInputSlot.setSlotOverlay(SlotOverlay.PLUS);
        gasOutputSlot.setSlotType(ContainerSlotType.OUTPUT);
        gasOutputSlot.setSlotOverlay(SlotOverlay.MINUS);
        fluidInputSlot.setSlotType(ContainerSlotType.INPUT);
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        if (mode) {// Fluid to Chemical
            fluidInputSlot.fillTank(fluidOutputSlot);
            gasInputSlot.drainTank();
        } else {// Chemical to Fluid
            gasOutputSlot.fillTank();
            fluidInputSlot.drainTank(fluidOutputSlot);
        }
        clientEnergyUsed = recipeCacheLookupMonitor.updateAndProcess(energyContainer);
        handleEject();
        return sendUpdatePacket;
    }

    private void handleEject() {
        if (leftOutputCaches == null) {
            leftOutputCaches = new ArrayList<>(1);
            Direction left = RelativeSide.LEFT.getDirection(getDirection());
            Direction up = RelativeSide.TOP.getDirection(getDirection());
            leftOutputCaches.add(Capabilities.CHEMICAL.createCache((ServerLevel) level, worldPosition.offset(up.getUnitVec3i()).offset(getLeftSide().getUnitVec3i()).relative(left), left.getOpposite()));
        }
        ChemicalUtil.emit(leftOutputCaches, chemicalTank, chemicalTank.getCapacity());
        if (rightOutputCaches == null) {
            rightOutputCaches = new ArrayList<>(1);
            Direction right = RelativeSide.RIGHT.getDirection(getDirection());
            Direction up = RelativeSide.TOP.getDirection(getDirection());
            rightOutputCaches.add(Capabilities.FLUID.createCache((ServerLevel) level, worldPosition.offset(up.getUnitVec3i()).offset(getRightSide().getUnitVec3i()).relative(right), right.getOpposite()));
        }
        FluidUtils.emit(rightOutputCaches, fluidTank, fluidTank.getCapacity());
    }

    @Override
    protected void invalidateDirectionCaches(Direction newDirection) {
        super.invalidateDirectionCaches(newDirection);
        leftOutputCaches = null;
        rightOutputCaches = null;
    }

    public boolean getMode() {
        return mode;
    }

    @Override
    public void nextMode() {
        mode = !mode;
        setChanged();
    }

    @Override
    public void previousMode() {
        // We only have two modes just flip it
        nextMode();
    }

    @ComputerMethod(nameOverride = "getEnergyUsage", methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    public long getEnergyUsed() {
        return clientEnergyUsed;
    }

    @Override
    public void readSustainedData(@NotNull ValueInput input) {
        super.readSustainedData(input);
        mode = input.getBooleanOr(SerializationConstants.MODE, mode);
    }

    @Override
    public void writeSustainedData(@NotNull ValueOutput output) {
        super.writeSustainedData(output);
        output.putBoolean(SerializationConstants.MODE, mode);
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentGetter input) {
        super.applyImplicitComponents(input);
        mode = input.getOrDefault(MekanismDataComponents.ROTARY_MODE, mode);
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MekanismDataComponents.ROTARY_MODE, mode);
    }

    @Override
    public int getRedstoneLevel() {
        if (mode) {
            return MekanismUtils.redstoneLevelFromContents(fluidTank.getFluidAmount(), fluidTank.getCapacity());
        }
        return MekanismUtils.redstoneLevelFromContents(chemicalTank.getStored(), chemicalTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(ContainerType<?, ?, ?> type) {
        return type == ContainerType.FLUID || type == ContainerType.CHEMICAL;
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<RotaryRecipeInput, RotaryRecipe, RotaryInputRecipeCache> getRecipeType() {
        return MekanismRecipeType.ROTARY;
    }

    @Nullable
    @Override
    public RotaryRecipe getRecipe(int cacheIndex) {
        RotaryInputRecipeCache inputCache = getRecipeType().getInputCache();
        return mode ? inputCache.findFirstRecipe(level, fluidInputHandler.getInput()) : inputCache.findFirstRecipe(level, chemicalInputHandler.getInput());
    }

    @NotNull
    @Override
    public CachedRecipe<RotaryRecipe> createNewCachedRecipe(@NotNull RotaryRecipe recipe, int cacheIndex) {
        return new RotaryCachedRecipe(recipe, recheckAllRecipeErrors, fluidInputHandler, chemicalInputHandler, chemicalOutputHandler, fluidOutputHandler, this::getMode)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(this::canFunction)
                .setActive(this::setActive)
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setBaselineMaxOperations(() -> baseOperations * baselineMaxOperations)
                .setOnFinish(this::markForSave);
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            int upgradeCount = upgradeComponent.getUpgrades(Upgrade.SPEED);
            baseOperations = 4 * (upgradeCount > 0 ? upgradeCount : upgradeCount + 1);
            baselineMaxOperations = (int) Math.pow(2, upgradeCount);
        }
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::getMode, v -> mode = v));
        container.track(SyncableLong.create(this::getEnergyUsed, v -> clientEnergyUsed = v));
    }

    @Override
    public boolean isPowered() {
        return redstone || numPowering > 0;
    }

    @Override
    public void onBoundingBlockPowerChange(BlockPos boundingPos, int oldLevel, int newLevel) {
        if (oldLevel > 0) {
            if (newLevel == 0) {
                numPowering--;
            }
        } else if (newLevel > 0) {
            numPowering++;
        }
    }

    @Override
    public int getBoundingComparatorSignal(Vec3i offset) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = getRightSide();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ()))) {
            return getCurrentRedstoneLevel();
        }
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ()))) {
                    return getCurrentRedstoneLevel();
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ()))) {
                    return getCurrentRedstoneLevel();
                }
            }
        }
        return Redstone.SIGNAL_NONE;
    }

    @Override
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.CHEMICAL.block()) {
            return Objects.requireNonNull(chemicalHandlerManager, "Expected to have chemical handler").resolve(capability, side);
        } else if (capability == Capabilities.FLUID.block()) {
            return Objects.requireNonNull(fluidHandlerManager, "Expected to have fluid handler").resolve(capability, side);
        } else if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.CHEMICAL.block()) {
            return notChemicalPort(side, offset);
        } else if (capability == Capabilities.FLUID.block()) {
            return notFluidPort(side, offset);
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == Capabilities.ITEM.block()) {
            return notItemPort(side, offset);
        }
        return notChemicalPort(side, offset) && notFluidPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notChemicalPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        if (offset.equals(new Vec3i(left.getStepX(), 1, left.getStepZ()))) {
            return side != left;
        }
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ()))) {
                    return side != back;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ()))) {
                    return side != back;
                }
            }
        }
        return true;
    }

    private boolean notFluidPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction right = getRightSide();
        if (offset.equals(new Vec3i(right.getStepX(), 1, right.getStepZ()))) {
            return side != right;
        }
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ()))) {
                    return side != back;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ()))) {
                    return side != back;
                }
            }
        }
        return true;
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        // 所有端口都可以与物品管道交互
        return notChemicalPort(side, offset) && notFluidPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ()))) {
            return side != back;
        }
        return true;
    }

    // Methods relating to IComputerTile
    @ComputerMethod
    boolean isCondensentrating() {
        return !mode;
    }

    @ComputerMethod(requiresPublicSecurity = true)
    void setCondensentrating(boolean value) throws ComputerException {
        validateSecurityIsPublic();
        if (mode != value) {
            mode = value;
            setChanged();
        }
    }
    // End methods IComputerTile
}
