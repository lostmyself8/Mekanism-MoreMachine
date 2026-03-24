package com.jerry.meklm.common.tile.machine;

import com.jerry.mekmm.api.ITileEntityMekanismAccessor;
import com.jerry.mekmm.common.capabilities.holder.chemical.AdjustableChemicalTankHelper;
import com.jerry.mekmm.common.capabilities.holder.fluid.AdjustableFluidTankHelper;

import mekanism.api.*;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ElectrolysisRecipe;
import mekanism.api.recipes.ElectrolysisRecipe.ElectrolysisRecipeOutput;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.FixedUsageEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.sync.SyncableEnum;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.FluidRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tile.TileEntityChemicalTank.GasMode;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.interfaces.IHasGasMode;
import mekanism.common.tile.interfaces.ISustainedData;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import com.jerry.meklm.api.INotNeedConfig;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class TileEntityLargeElectrolyticSeparator extends TileEntityRecipeMachine<ElectrolysisRecipe> implements IBoundingBlock, IHasGasMode, FluidRecipeLookupHandler<ElectrolysisRecipe>,
                                                  ISustainedData, INotNeedConfig {

    public static final RecipeError NOT_ENOUGH_SPACE_LEFT_OUTPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SPACE_RIGHT_OUTPUT_ERROR = RecipeError.create();
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_INPUT,
            NOT_ENOUGH_SPACE_LEFT_OUTPUT_ERROR,
            NOT_ENOUGH_SPACE_RIGHT_OUTPUT_ERROR,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);

    private static final int BASE_DUMP_RATE = 8;
    /**
     * The maximum amount of gas this block can store.
     */
    public static final long MAX_GAS = 24 * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;
    public static final int MAX_FLUID = 24 * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;
    private static final BiFunction<FloatingLong, TileEntityLargeElectrolyticSeparator, FloatingLong> BASE_ENERGY_CALCULATOR = (base, tile) -> base.multiply(tile.getRecipeEnergyMultiplier());

    /**
     * This separator's water slot.
     */
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = { "getInput", "getInputCapacity", "getInputNeeded", "getInputFilledPercentage" }, docPlaceholder = "input tank")
    public BasicFluidTank fluidTank;
    /**
     * The amount of oxygen this block is storing.
     */
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getLeftOutput", "getLeftOutputCapacity", "getLeftOutputNeeded",
                                    "getLeftOutputFilledPercentage" },
                            docPlaceholder = "left output tank")
    public IGasTank leftTank;
    /**
     * The amount of hydrogen this block is storing.
     */
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getRightOutput", "getRightOutputCapacity", "getRightOutputNeeded",
                                    "getRightOutputFilledPercentage" },
                            docPlaceholder = "right output tank")
    public IGasTank rightTank;
    @SyntheticComputerMethod(getter = "getLeftOutputDumpingMode")
    public GasMode dumpLeft = GasMode.IDLE;
    @SyntheticComputerMethod(getter = "getRightOutputDumpingMode")
    public GasMode dumpRight = GasMode.IDLE;
    private FloatingLong clientEnergyUsed = FloatingLong.ZERO;
    @Getter
    private FloatingLong recipeEnergyMultiplier = FloatingLong.ONE;
    private int baselineMaxOperations = 1;
    private long dumpRate = BASE_DUMP_RATE;
    private int baseOperations = 1;
    private int numPowering;

    private final IOutputHandler<@NotNull ElectrolysisRecipeOutput> outputHandler;
    private final IInputHandler<@NotNull FluidStack> inputHandler;

    @Getter
    private FixedUsageEnergyContainer<TileEntityLargeElectrolyticSeparator> energyContainer;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "input item slot")
    FluidInventorySlot fluidSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getLeftOutputItem", docPlaceholder = "left output item slot")
    GasInventorySlot leftOutputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getRightOutputItem", docPlaceholder = "right output item slot")
    GasInventorySlot rightOutputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityLargeElectrolyticSeparator(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_ELECTROLYTIC_SEPARATOR, pos, state, TRACKED_ERROR_TYPES);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.GAS, TransmissionType.FLUID, TransmissionType.ENERGY);

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, true, fluidSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT_1, new InventorySlotInfo(true, true, leftOutputSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT_2, new InventorySlotInfo(true, true, rightOutputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, fluidSlot, leftOutputSlot, rightOutputSlot));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
            // Set default config directions
            itemConfig.setDataType(DataType.INPUT, RelativeSide.FRONT);
            itemConfig.setDataType(DataType.OUTPUT_1, RelativeSide.LEFT);
            itemConfig.setDataType(DataType.OUTPUT_2, RelativeSide.RIGHT);
            itemConfig.setDataType(DataType.ENERGY, RelativeSide.BACK);
        }

        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.GAS);
        if (gasConfig != null) {
            gasConfig.addSlotInfo(DataType.OUTPUT_1, new ChemicalSlotInfo.GasSlotInfo(false, true, leftTank));
            gasConfig.addSlotInfo(DataType.OUTPUT_2, new ChemicalSlotInfo.GasSlotInfo(false, true, rightTank));
            gasConfig.setDataType(DataType.OUTPUT_1, RelativeSide.LEFT);
            gasConfig.setDataType(DataType.OUTPUT_2, RelativeSide.RIGHT);
            gasConfig.setEjecting(true);
        }

        configComponent.setupInputConfig(TransmissionType.FLUID, fluidTank);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.GAS)
                .setCanTankEject(tank -> {
                    if (tank == leftTank) {
                        return dumpLeft != GasMode.DUMPING;
                    } else if (tank == rightTank) {
                        return dumpRight != GasMode.DUMPING;
                    }
                    return true;
                });

        inputHandler = InputHelper.getInputHandler(fluidTank, RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(leftTank, NOT_ENOUGH_SPACE_LEFT_OUTPUT_ERROR, rightTank, NOT_ENOUGH_SPACE_RIGHT_OUTPUT_ERROR);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        AdjustableFluidTankHelper builder = AdjustableFluidTankHelper.forSide(this::getDirection, side -> side == RelativeSide.BACK || side == RelativeSide.LEFT || side == RelativeSide.RIGHT, side -> false);
        builder.addTank(fluidTank = BasicFluidTank.input(MAX_FLUID, this::containsRecipe, recipeCacheListener), RelativeSide.BACK, RelativeSide.LEFT, RelativeSide.RIGHT);
        return builder.build();
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        AdjustableChemicalTankHelper<Gas, GasStack, IGasTank> builder = AdjustableChemicalTankHelper.forSideGas(this::getDirection, side -> false, side -> side == RelativeSide.FRONT);
        builder.addTank(leftTank = ChemicalTankBuilder.GAS.output(MAX_GAS, listener), RelativeSide.FRONT);
        builder.addTank(rightTank = ChemicalTankBuilder.GAS.output(MAX_GAS, listener), RelativeSide.FRONT);
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = FixedUsageEnergyContainer.input(this, BASE_ENERGY_CALCULATOR, listener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection, side -> side == RelativeSide.BACK || side == RelativeSide.LEFT || side == RelativeSide.RIGHT, side -> side == RelativeSide.FRONT);
        builder.addSlot(fluidSlot = FluidInventorySlot.fill(fluidTank, listener, 26, 35), RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK);
        builder.addSlot(leftOutputSlot = GasInventorySlot.drain(leftTank, listener, 59, 52), RelativeSide.LEFT);
        builder.addSlot(rightOutputSlot = GasInventorySlot.drain(rightTank, listener, 101, 52), RelativeSide.RIGHT);
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 143, 35), RelativeSide.BACK);
        fluidSlot.setSlotType(ContainerSlotType.INPUT);
        leftOutputSlot.setSlotType(ContainerSlotType.OUTPUT);
        rightOutputSlot.setSlotType(ContainerSlotType.OUTPUT);
        return builder.build();
    }

    @Override
    public void onCachedRecipeChanged(@Nullable CachedRecipe<ElectrolysisRecipe> cachedRecipe, int cacheIndex) {
        super.onCachedRecipeChanged(cachedRecipe, cacheIndex);
        recipeEnergyMultiplier = cachedRecipe == null ? FloatingLong.ONE : cachedRecipe.getRecipe().getEnergyMultiplier();
        energyContainer.updateEnergyPerTick();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        fluidSlot.fillTank();

        leftOutputSlot.drainTank();
        rightOutputSlot.drainTank();
        clientEnergyUsed = recipeCacheLookupMonitor.updateAndProcess(energyContainer);

        handleTank(leftTank, dumpLeft);
        handleTank(rightTank, dumpRight);
        handleLeftEject();
    }

    private void handleLeftEject() {
        if (MekanismUtils.canFunction(this)) {
            Set<Direction> ejectDirection = EnumSet.noneOf(Direction.class);
            Direction front = getDirection();
            Direction left = getLeftSide();
            Direction right = getRightSide();
            ejectDirection.add(front);
            BlockEntity leftEjectEntity = WorldUtils.getTileEntity(getLevel(), worldPosition.offset(front.getNormal()).offset(left.getNormal()));
            if (leftEjectEntity != null) {
                ChemicalUtil.emit(ejectDirection, leftTank, leftEjectEntity, leftTank.getCapacity());
            }
            BlockEntity rightEjectEntity = WorldUtils.getTileEntity(getLevel(), worldPosition.offset(front.getNormal()).offset(right.getNormal()));
            if (rightEjectEntity != null) {
                ChemicalUtil.emit(ejectDirection, rightTank, rightEjectEntity, rightTank.getCapacity());
            }
        }
    }

    private void handleTank(IGasTank tank, GasMode mode) {
        if (!tank.isEmpty()) {
            if (mode == GasMode.DUMPING) {
                tank.shrinkStack(dumpRate, Action.EXECUTE);
            } else if (mode == GasMode.DUMPING_EXCESS) {
                long target = getDumpingExcessTarget(tank);
                long stored = tank.getStored();
                if (target < stored) {
                    // Dump excess that we need to get to the target (capping at our eject rate for how much we can dump
                    // at once)
                    tank.shrinkStack(Math.min(stored - target, MekanismConfig.general.chemicalAutoEjectRate.get()), Action.EXECUTE);
                }
            }
        }
    }

    private long getDumpingExcessTarget(IGasTank tank) {
        return MathUtils.clampToLong(tank.getCapacity() * MekanismConfig.general.dumpExcessKeepRatio.get());
    }

    private boolean atDumpingExcessTarget(IGasTank tank) {
        // Check >= so that if we are past and our eject rate is just low then we don't continue making it, so we never
        // get to the eject rate
        return tank.getStored() >= getDumpingExcessTarget(tank);
    }

    private boolean canFunction() {
        // We can function if:
        // - the tile can function
        // - at least one side is not set to dumping excess
        // - at least one side is not at the dumping excess target
        return MekanismUtils.canFunction(this) && (dumpLeft != GasMode.DUMPING_EXCESS || dumpRight != GasMode.DUMPING_EXCESS ||
                !atDumpingExcessTarget(leftTank) || !atDumpingExcessTarget(rightTank));
    }

    @NotNull
    @ComputerMethod(nameOverride = "getEnergyUsage", methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    public FloatingLong getEnergyUsed() {
        return clientEnergyUsed;
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<ElectrolysisRecipe, InputRecipeCache.SingleFluid<ElectrolysisRecipe>> getRecipeType() {
        return MekanismRecipeType.SEPARATING;
    }

    @Nullable
    @Override
    public ElectrolysisRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandler);
    }

    @NotNull
    @Override
    public CachedRecipe<ElectrolysisRecipe> createNewCachedRecipe(@NotNull ElectrolysisRecipe recipe, int cacheIndex) {
        return OneInputCachedRecipe.separating(recipe, recheckAllRecipeErrors, inputHandler, outputHandler)
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
            double speed = Math.pow(2, upgradeCount);
            baseOperations = 4 * (upgradeCount > 0 ? upgradeCount : upgradeCount + 1);
            baselineMaxOperations = (int) speed;
            dumpRate = (long) (BASE_DUMP_RATE * baseOperations * speed);
        }
    }

    @Override
    public void nextMode(int tank) {
        if (tank == 0) {
            dumpLeft = dumpLeft.getNext();
            markForSave();
        } else if (tank == 1) {
            dumpRight = dumpRight.getNext();
            markForSave();
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
    public void writeSustainedData(CompoundTag dataMap) {
        NBTUtils.writeEnum(dataMap, NBTConstants.DUMP_LEFT, dumpLeft);
        NBTUtils.writeEnum(dataMap, NBTConstants.DUMP_RIGHT, dumpRight);
    }

    @Override
    public void readSustainedData(CompoundTag dataMap) {
        NBTUtils.setEnumIfPresent(dataMap, NBTConstants.DUMP_LEFT, GasMode::byIndexStatic, mode -> dumpLeft = mode);
        NBTUtils.setEnumIfPresent(dataMap, NBTConstants.DUMP_RIGHT, GasMode::byIndexStatic, mode -> dumpRight = mode);
    }

    @Override
    public Map<String, String> getTileDataRemap() {
        Map<String, String> remap = new Object2ObjectOpenHashMap<>();
        remap.put(NBTConstants.DUMP_LEFT, NBTConstants.DUMP_LEFT);
        remap.put(NBTConstants.DUMP_RIGHT, NBTConstants.DUMP_RIGHT);
        return remap;
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(fluidTank.getFluidAmount(), fluidTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(@Nullable SubstanceType type) {
        return type == SubstanceType.FLUID;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableEnum.create(GasMode::byIndexStatic, GasMode.IDLE, () -> dumpLeft, value -> dumpLeft = value));
        container.track(SyncableEnum.create(GasMode::byIndexStatic, GasMode.IDLE, () -> dumpRight, value -> dumpRight = value));
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

    // 以流体端口作为比较器信号口
    @Override
    public int getBoundingComparatorSignal(Vec3i offset) {
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ()))) {
            return getCurrentRedstoneLevel();
        }
        switch (getDirection()) {
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
        return 0;
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
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return notFluidPort(side, offset);
        } else if (capability == Capabilities.GAS_HANDLER) {
            return notGasPort(side, offset);
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return notItemPort(side, offset);
        } else if (canEverResolve(capability) && IBoundingBlock.super.isOffsetCapabilityDisabled(capability, side, offset)) {
            // If we are not an item handler or energy capability, and it is a capability that we can support,
            // but it is one that normally should be disabled for offset capabilities, then expose it but only do so
            // via our ports for things like computer integration capabilities, then we treat the capability as
            // disabled if it is not against one of our ports
            return notFluidPort(side, offset) && notGasPort(side, offset) && notEnergyPort(side, offset);
        }
        return false;
    }

    private boolean notGasPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, front.getStepZ())) || offset.equals(new Vec3i(right.getStepX(), 0, front.getStepZ()))) {
                    return side != front;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(front.getStepX(), 0, left.getStepZ())) || offset.equals(new Vec3i(front.getStepX(), 0, right.getStepZ()))) {
                    return side != front;
                }
            }
        }
        return true;
    }

    private boolean notFluidPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (getDirection()) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ()))) {
                    return side != back && side != left;
                }
                if (offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ()))) {
                    return side != back && side != right;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ()))) {
                    return side != back && side != left;
                }
                if (offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ()))) {
                    return side != back && side != right;
                }
            }
        }
        return true;
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        // 所有端口都可以与物品管道交互
        return notGasPort(side, offset) && notFluidPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ()))) {
            return side != back;
        }
        return true;
    }

    // Methods relating to IComputerTile
    @ComputerMethod(requiresPublicSecurity = true)
    void setLeftOutputDumpingMode(GasMode mode) throws ComputerException {
        validateSecurityIsPublic();
        if (dumpLeft != mode) {
            dumpLeft = mode;
            markForSave();
        }
    }

    @ComputerMethod(requiresPublicSecurity = true)
    void incrementLeftOutputDumpingMode() throws ComputerException {
        validateSecurityIsPublic();
        nextMode(0);
    }

    @ComputerMethod(requiresPublicSecurity = true)
    void decrementLeftOutputDumpingMode() throws ComputerException {
        validateSecurityIsPublic();
        dumpLeft = dumpLeft.getPrevious();
        markForSave();
    }

    @ComputerMethod(requiresPublicSecurity = true)
    void setRightOutputDumpingMode(GasMode mode) throws ComputerException {
        validateSecurityIsPublic();
        if (dumpRight != mode) {
            dumpRight = mode;
            markForSave();
        }
    }

    @ComputerMethod(requiresPublicSecurity = true)
    void incrementRightOutputDumpingMode() throws ComputerException {
        validateSecurityIsPublic();
        nextMode(1);
    }

    @ComputerMethod(requiresPublicSecurity = true)
    void decrementRightOutputDumpingMode() throws ComputerException {
        validateSecurityIsPublic();
        dumpRight = dumpRight.getPrevious();
        markForSave();
    }
    // End methods IComputerTile
}
