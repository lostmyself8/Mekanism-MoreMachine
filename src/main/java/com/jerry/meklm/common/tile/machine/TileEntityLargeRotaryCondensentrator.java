package com.jerry.meklm.common.tile.machine;

import com.jerry.mekmm.api.ITileEntityMekanismAccessor;
import com.jerry.mekmm.common.capabilities.holder.chemical.AdjustableChemicalTankHelper;
import com.jerry.mekmm.common.capabilities.holder.fluid.AdjustableFluidTankHelper;

import mekanism.api.*;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.RotaryRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.RotaryCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
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
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.RotaryInputRecipeCache;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.interfaces.IHasMode;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import com.jerry.meklm.api.INotNeedConfig;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class TileEntityLargeRotaryCondensentrator extends TileEntityRecipeMachine<RotaryRecipe> implements IBoundingBlock, IHasMode, INotNeedConfig {

    public static final RecipeError NOT_ENOUGH_FLUID_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_GAS_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SPACE_FLUID_OUTPUT_ERROR = RecipeError.create();
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            NOT_ENOUGH_FLUID_INPUT_ERROR,
            NOT_ENOUGH_GAS_INPUT_ERROR,
            NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR,
            NOT_ENOUGH_SPACE_FLUID_OUTPUT_ERROR,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    public static final int CAPACITY = 10 * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = { "getGas", "getGasCapacity", "getGasNeeded", "getGasFilledPercentage" }, docPlaceholder = "gas tank")
    public IGasTank gasTank;
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = { "getFluid", "getFluidCapacity", "getFluidNeeded", "getFluidFilledPercentage" }, docPlaceholder = "fluid tank")
    public BasicFluidTank fluidTank;
    /**
     * True: fluid -> gas
     * <p>
     * False: gas -> fluid
     */
    public boolean mode;

    private final IOutputHandler<@NotNull GasStack> gasOutputHandler;
    private final IOutputHandler<@NotNull FluidStack> fluidOutputHandler;
    private final IInputHandler<@NotNull FluidStack> fluidInputHandler;
    private final IInputHandler<@NotNull GasStack> gasInputHandler;

    private FloatingLong clientEnergyUsed = FloatingLong.ZERO;
    private int baseOperations = 1;
    private int baselineMaxOperations = 1;
    private int numPowering;

    private MachineEnergyContainer<TileEntityLargeRotaryCondensentrator> energyContainer;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getGasItemInput", docPlaceholder = "gas item input slot")
    GasInventorySlot gasInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getGasItemOutput", docPlaceholder = "gas item output slot")
    GasInventorySlot gasOutputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFluidItemInput", docPlaceholder = "fluid item input slot")
    FluidInventorySlot fluidInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFluidItemOutput", docPlaceholder = "fluid item ouput slot")
    OutputInventorySlot fluidOutputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityLargeRotaryCondensentrator(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_ROTARY_CONDENSENTRATOR, pos, state, TRACKED_ERROR_TYPES);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.GAS, TransmissionType.FLUID, TransmissionType.ENERGY);
        configComponent.setupItemIOConfig(List.of(gasInputSlot, fluidInputSlot), List.of(gasOutputSlot, fluidOutputSlot), energySlot, true);
        configComponent.setupIOConfig(TransmissionType.GAS, gasTank, RelativeSide.LEFT, true).setEjecting(true);
        configComponent.setupIOConfig(TransmissionType.FLUID, fluidTank, RelativeSide.RIGHT, true).setEjecting(true);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.GAS, TransmissionType.FLUID)
                .setCanEject(transmissionType -> {
                    if (transmissionType == TransmissionType.GAS) {
                        return mode;
                    } else if (transmissionType == TransmissionType.FLUID) {
                        return !mode;
                    }
                    return true;
                });

        gasInputHandler = InputHelper.getInputHandler(gasTank, NOT_ENOUGH_GAS_INPUT_ERROR);
        fluidInputHandler = InputHelper.getInputHandler(fluidTank, NOT_ENOUGH_FLUID_INPUT_ERROR);
        gasOutputHandler = OutputHelper.getOutputHandler(gasTank, NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR);
        fluidOutputHandler = OutputHelper.getOutputHandler(fluidTank, NOT_ENOUGH_SPACE_FLUID_OUTPUT_ERROR);
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        AdjustableChemicalTankHelper<Gas, GasStack, IGasTank> builder = AdjustableChemicalTankHelper.forSideGas(this::getDirection, side -> side == RelativeSide.BACK, side -> side == RelativeSide.LEFT);
        // Only allow extraction
        builder.addTank(gasTank = ChemicalTankBuilder.GAS.create(CAPACITY, (gas, automationType) -> automationType == AutomationType.MANUAL || mode,
                (gas, automationType) -> automationType == AutomationType.INTERNAL || !mode, this::isValidGas, ChemicalAttributeValidator.ALWAYS_ALLOW, recipeCacheListener), RelativeSide.BACK, RelativeSide.LEFT);
        return builder.build();
    }

    private boolean isValidGas(@NotNull Gas gas) {
        return getRecipeType().getInputCache().containsInput(level, gas.getStack(1));
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        AdjustableFluidTankHelper builder = AdjustableFluidTankHelper.forSide(this::getDirection, side -> side == RelativeSide.BACK, side -> side == RelativeSide.RIGHT);
        builder.addTank(fluidTank = BasicFluidTank.create(CAPACITY, (fluid, automationType) -> automationType == AutomationType.MANUAL || !mode,
                (fluid, automationType) -> automationType == AutomationType.INTERNAL || mode, this::isValidFluid, recipeCacheListener), RelativeSide.BACK, RelativeSide.RIGHT);
        return builder.build();
    }

    private boolean isValidFluid(@NotNull FluidStack fluidStack) {
        return getRecipeType().getInputCache().containsInput(level, fluidStack);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection, side -> side == RelativeSide.BACK, side -> side == RelativeSide.LEFT || side == RelativeSide.RIGHT);
        BooleanSupplier modeSupplier = () -> mode;
        builder.addSlot(gasInputSlot = GasInventorySlot.rotaryDrain(gasTank, modeSupplier, listener, 5, 25), RelativeSide.BACK);
        builder.addSlot(gasOutputSlot = GasInventorySlot.rotaryFill(gasTank, modeSupplier, listener, 5, 56), RelativeSide.LEFT);
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
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        if (mode) {// Fluid to Gas
            fluidInputSlot.fillTank(fluidOutputSlot);
            gasInputSlot.drainTank();
        } else {// Gas to Fluid
            gasOutputSlot.fillTank();
            fluidInputSlot.drainTank(fluidOutputSlot);
        }
        clientEnergyUsed = recipeCacheLookupMonitor.updateAndProcess(energyContainer);
        handleEject();
    }

    private void handleEject() {
        if (MekanismUtils.canFunction(this)) {
            Set<Direction> gasDirection = EnumSet.noneOf(Direction.class);
            Set<Direction> fluidDirection = EnumSet.noneOf(Direction.class);
            Direction up = RelativeSide.TOP.getDirection(getDirection());
            Direction left = RelativeSide.LEFT.getDirection(getDirection());
            Direction right = RelativeSide.RIGHT.getDirection(getDirection());
            gasDirection.add(left);
            fluidDirection.add(right);
            BlockEntity gasEjectEntity = WorldUtils.getTileEntity(getLevel(), worldPosition.offset(up.getNormal()).offset(left.getNormal()));
            if (gasEjectEntity != null) {
                ChemicalUtil.emit(gasDirection, gasTank, gasEjectEntity, gasTank.getCapacity());
            }
            BlockEntity fluidEjectEntity = WorldUtils.getTileEntity(getLevel(), worldPosition.offset(up.getNormal()).offset(right.getNormal()));
            if (fluidEjectEntity != null) {
                FluidUtils.emit(fluidDirection, fluidTank, fluidEjectEntity, fluidTank.getCapacity());
            }

        }
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

    @NotNull
    @ComputerMethod(nameOverride = "getEnergyUsage", methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    public FloatingLong getEnergyUsed() {
        return clientEnergyUsed;
    }

    @Override
    protected void loadGeneralPersistentData(CompoundTag data) {
        super.loadGeneralPersistentData(data);
        NBTUtils.setBooleanIfPresent(data, NBTConstants.MODE, value -> mode = value);
    }

    @Override
    protected void addGeneralPersistentData(CompoundTag data) {
        super.addGeneralPersistentData(data);
        data.putBoolean(NBTConstants.MODE, mode);
    }

    @Override
    public int getRedstoneLevel() {
        if (mode) {
            return MekanismUtils.redstoneLevelFromContents(fluidTank.getFluidAmount(), fluidTank.getCapacity());
        }
        return MekanismUtils.redstoneLevelFromContents(gasTank.getStored(), gasTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(@Nullable SubstanceType type) {
        return type == SubstanceType.FLUID || type == SubstanceType.GAS;
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<RotaryRecipe, RotaryInputRecipeCache> getRecipeType() {
        return MekanismRecipeType.ROTARY;
    }

    @Nullable
    @Override
    public RotaryRecipe getRecipe(int cacheIndex) {
        RotaryInputRecipeCache inputCache = getRecipeType().getInputCache();
        return mode ? inputCache.findFirstRecipe(level, fluidInputHandler.getInput()) : inputCache.findFirstRecipe(level, gasInputHandler.getInput());
    }

    public MachineEnergyContainer<TileEntityLargeRotaryCondensentrator> getEnergyContainer() {
        return energyContainer;
    }

    @NotNull
    @Override
    public CachedRecipe<RotaryRecipe> createNewCachedRecipe(@NotNull RotaryRecipe recipe, int cacheIndex) {
        return new RotaryCachedRecipe(recipe, recheckAllRecipeErrors, fluidInputHandler, gasInputHandler, gasOutputHandler, fluidOutputHandler, () -> mode)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
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
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        numPowering = nbt.getInt(NBTConstants.NUM_POWERING);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        nbtTags.putInt(NBTConstants.NUM_POWERING, numPowering);
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(() -> mode, value -> mode = value));
        container.track(SyncableFloatingLong.create(this::getEnergyUsed, value -> clientEnergyUsed = value));
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
    public @NotNull <T> LazyOptional<T> getOffsetCapabilityIfEnabled(@NotNull Capability<T> capability, Direction side, @NotNull Vec3i offset) {
        if (this instanceof ITileEntityMekanismAccessor accessor) {
            if (capability == ForgeCapabilities.FLUID_HANDLER) {
                return accessor.getFluidHandlerManager().resolve(capability, side);
            } else if (capability == Capabilities.GAS_HANDLER) {
                return accessor.getGasHandlerManager().resolve(capability, side);
            }
        }
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerManager.resolve(capability, side);
        }
        return getCapability(capability, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull Capability<?> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.GAS_HANDLER) {
            return notChemicalPort(side, offset);
        } else if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return notFluidPort(side, offset);
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return notItemPort(side, offset);
        } else if (canEverResolve(capability) && IBoundingBlock.super.isOffsetCapabilityDisabled(capability, side, offset)) {
            // If we are not an item handler or energy capability, and it is a capability that we can support,
            // but it is one that normally should be disabled for offset capabilities, then expose it but only do so
            // via our ports for things like computer integration capabilities, then we treat the capability as
            // disabled if it is not against one of our ports
            return notChemicalPort(side, offset) && notFluidPort(side, offset) && notEnergyPort(side, offset);
        }
        return false;
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
