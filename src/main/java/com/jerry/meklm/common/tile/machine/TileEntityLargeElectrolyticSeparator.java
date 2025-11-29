package com.jerry.meklm.common.tile.machine;

import com.jerry.meklm.common.capabilities.holder.chemical.CanAdjustChemicalTankHelper;
import com.jerry.meklm.common.capabilities.holder.fluid.CanAdjustFluidTankHelper;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import com.jerry.meklm.common.tile.INeedConfig;

import mekanism.api.*;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.functions.LongObjectToLongFunction;
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
import mekanism.api.recipes.vanilla_input.SingleFluidRecipeInput;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.attachments.containers.ContainerType;
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
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.sync.SyncableEnum;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.FluidRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.TileEntityChemicalTank.GasMode;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.interfaces.IHasGasMode;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TileEntityLargeElectrolyticSeparator extends TileEntityRecipeMachine<ElectrolysisRecipe> implements IBoundingBlock, IHasGasMode, FluidRecipeLookupHandler<ElectrolysisRecipe>, INeedConfig {

    public static final RecipeError NOT_ENOUGH_SPACE_LEFT_OUTPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_SPACE_RIGHT_OUTPUT_ERROR = RecipeError.create();
    /**
     * The maximum amount of gas this block can store.
     */
    public static final long MAX_GAS = 24 * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;
    public static final int MAX_FLUID = 24 * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_INPUT,
            NOT_ENOUGH_SPACE_LEFT_OUTPUT_ERROR,
            NOT_ENOUGH_SPACE_RIGHT_OUTPUT_ERROR,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);

    private static final int BASE_DUMP_RATE = 8;

    private static final LongObjectToLongFunction<TileEntityLargeElectrolyticSeparator> BASE_ENERGY_CALCULATOR = (base, tile) -> base * tile.getRecipeEnergyMultiplier();
    private final IOutputHandler<@NotNull ElectrolysisRecipeOutput> outputHandler;
    private final IInputHandler<@NotNull FluidStack> inputHandler;

    /**
     * This separator's water slot.
     */
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerFluidTankWrapper.class,
                            methodNames = { "getInput", "getInputCapacity", "getInputNeeded",
                                    "getInputFilledPercentage" },
                            docPlaceholder = "input tank")
    public BasicFluidTank fluidTank;
    /**
     * The amount of oxygen this block is storing.
     */
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class,
                            methodNames = { "getLeftOutput", "getLeftOutputCapacity", "getLeftOutputNeeded",
                                    "getLeftOutputFilledPercentage" },
                            docPlaceholder = "left output tank")
    public IChemicalTank leftTank;
    /**
     * The amount of hydrogen this block is storing.
     */
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class,
                            methodNames = { "getRightOutput", "getRightOutputCapacity", "getRightOutputNeeded",
                                    "getRightOutputFilledPercentage" },
                            docPlaceholder = "right output tank")
    public IChemicalTank rightTank;
    @SyntheticComputerMethod(getter = "getLeftOutputDumpingMode")
    public GasMode dumpLeft = GasMode.IDLE;
    @SyntheticComputerMethod(getter = "getRightOutputDumpingMode")
    public GasMode dumpRight = GasMode.IDLE;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "input item slot")
    FluidInventorySlot fluidSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getLeftOutputItem", docPlaceholder = "left output item slot")
    ChemicalInventorySlot leftOutputSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getRightOutputItem", docPlaceholder = "right output item slot")
    ChemicalInventorySlot rightOutputSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    private int numPowering;
    private long clientEnergyUsed = 1L;
    @Getter
    private long recipeEnergyMultiplier = 1L;
    private int baselineMaxOperations = 1;
    private long dumpRate = BASE_DUMP_RATE;
    private int baseOperations = 8;
    @Getter
    private FixedUsageEnergyContainer<TileEntityLargeElectrolyticSeparator> energyContainer;

    public TileEntityLargeElectrolyticSeparator(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_ELECTROLYTIC_SEPARATOR, pos, state, TRACKED_ERROR_TYPES);

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, true, fluidSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT_1, new InventorySlotInfo(true, true, leftOutputSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT_2, new InventorySlotInfo(true, true, rightOutputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, fluidSlot, leftOutputSlot, rightOutputSlot));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }

        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (gasConfig != null) {
            gasConfig.addSlotInfo(DataType.OUTPUT_1, new ChemicalSlotInfo(false, true, leftTank));
            gasConfig.addSlotInfo(DataType.OUTPUT_2, new ChemicalSlotInfo(false, true, rightTank));
        }

        configComponent.setupInputConfig(TransmissionType.FLUID, fluidTank);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                .setCanTankEject(tank -> {
                    if (tank == leftTank) {
                        return dumpLeft != GasMode.DUMPING;
                    } else if (tank == rightTank) {
                        return dumpRight != GasMode.DUMPING;
                    }
                    return true;
                });

        inputHandler = InputHelper.getInputHandler(fluidTank, CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(leftTank, NOT_ENOUGH_SPACE_LEFT_OUTPUT_ERROR, rightTank, NOT_ENOUGH_SPACE_RIGHT_OUTPUT_ERROR);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        CanAdjustFluidTankHelper builder = CanAdjustFluidTankHelper.forSide(facingSupplier, side -> side == RelativeSide.BACK || side == RelativeSide.LEFT || side == RelativeSide.RIGHT, null);
        builder.addTank(fluidTank = BasicFluidTank.input(MAX_FLUID, this::containsRecipe, recipeCacheListener), RelativeSide.BACK, RelativeSide.LEFT, RelativeSide.RIGHT);
        return builder.build();
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        CanAdjustChemicalTankHelper builder = CanAdjustChemicalTankHelper.forSide(facingSupplier, null, side -> side == RelativeSide.LEFT || side == RelativeSide.RIGHT);
        builder.addTank(leftTank = BasicChemicalTank.output(MAX_GAS, recipeCacheUnpauseListener), RelativeSide.LEFT);
        builder.addTank(rightTank = BasicChemicalTank.output(MAX_GAS, recipeCacheUnpauseListener), RelativeSide.RIGHT);
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(facingSupplier);
        builder.addContainer(energyContainer = FixedUsageEnergyContainer.input(this, BASE_ENERGY_CALCULATOR, recipeCacheUnpauseListener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier);
        builder.addSlot(fluidSlot = FluidInventorySlot.fill(fluidTank, listener, 26, 35), RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK);
        builder.addSlot(leftOutputSlot = ChemicalInventorySlot.drain(leftTank, listener, 59, 52), RelativeSide.LEFT);
        builder.addSlot(rightOutputSlot = ChemicalInventorySlot.drain(rightTank, listener, 101, 52), RelativeSide.RIGHT);
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 143, 35), RelativeSide.BACK);
        fluidSlot.setSlotType(ContainerSlotType.INPUT);
        leftOutputSlot.setSlotType(ContainerSlotType.OUTPUT);
        rightOutputSlot.setSlotType(ContainerSlotType.OUTPUT);
        return builder.build();
    }

    @Override
    public void onCachedRecipeChanged(@Nullable CachedRecipe<ElectrolysisRecipe> cachedRecipe, int cacheIndex) {
        super.onCachedRecipeChanged(cachedRecipe, cacheIndex);
        recipeEnergyMultiplier = cachedRecipe == null ? 1L : cachedRecipe.getRecipe().getEnergyMultiplier();
        energyContainer.updateEnergyPerTick();
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        fluidSlot.fillTank();

        leftOutputSlot.drainTank();
        rightOutputSlot.drainTank();
        clientEnergyUsed = recipeCacheLookupMonitor.updateAndProcess(energyContainer);

        handleTank(leftTank, dumpLeft);
        handleTank(rightTank, dumpRight);
        return sendUpdatePacket;
    }

    private void handleTank(IChemicalTank tank, GasMode mode) {
        if (!tank.isEmpty()) {
            if (mode == GasMode.DUMPING) {
                tank.shrinkStack(dumpRate, Action.EXECUTE);
            } else if (mode == GasMode.DUMPING_EXCESS) {
                long target = getDumpingExcessTarget(tank);
                long stored = tank.getStored();
                if (target < stored) {
                    // Dump excess that we need to get to the target (capping at our eject rate for how much we can dump
                    // at once)
                    tank.shrinkStack(Math.min(stored - target, MekanismConfig.general.chemicalAutoEjectRate.get() * 10), Action.EXECUTE);
                }
            }
        }
    }

    private long getDumpingExcessTarget(IChemicalTank tank) {
        return MathUtils.clampToLong(tank.getCapacity() * MekanismConfig.general.dumpExcessKeepRatio.get());
    }

    private boolean atDumpingExcessTarget(IChemicalTank tank) {
        // Check >= so that if we are past and our eject rate is just low then we don't continue making it, so we never
        // get to the eject rate
        return tank.getStored() >= getDumpingExcessTarget(tank);
    }

    @Override
    public boolean canFunction() {
        // We can function if:
        // - the tile can function
        // - at least one side is not set to dumping excess
        // - at least one side is not at the dumping excess target
        return super.canFunction() && (dumpLeft != GasMode.DUMPING_EXCESS || dumpRight != GasMode.DUMPING_EXCESS || !atDumpingExcessTarget(leftTank) || !atDumpingExcessTarget(rightTank));
    }

    @ComputerMethod(nameOverride = "getEnergyUsage", methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    public long getEnergyUsed() {
        return clientEnergyUsed;
    }

    @Override
    public boolean needConfig() {
        return false;
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<SingleFluidRecipeInput, ElectrolysisRecipe, InputRecipeCache.SingleFluid<ElectrolysisRecipe>> getRecipeType() {
        return MekanismRecipeType.SEPARATING;
    }

    @Override
    public IRecipeViewerRecipeType<ElectrolysisRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.SEPARATING;
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
            dumpRate = (long) (BASE_DUMP_RATE * speed);
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
    public void writeSustainedData(HolderLookup.Provider provider, CompoundTag dataMap) {
        super.writeSustainedData(provider, dataMap);
        NBTUtils.writeEnum(dataMap, SerializationConstants.DUMP_LEFT, dumpLeft);
        NBTUtils.writeEnum(dataMap, SerializationConstants.DUMP_RIGHT, dumpRight);
    }

    @Override
    public void readSustainedData(HolderLookup.Provider provider, @NotNull CompoundTag dataMap) {
        super.readSustainedData(provider, dataMap);
        NBTUtils.setEnumIfPresent(dataMap, SerializationConstants.DUMP_LEFT, GasMode.BY_ID, mode -> dumpLeft = mode);
        NBTUtils.setEnumIfPresent(dataMap, SerializationConstants.DUMP_RIGHT, GasMode.BY_ID, mode -> dumpRight = mode);
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MekanismDataComponents.DUMP_MODE, dumpLeft);
        builder.set(MekanismDataComponents.SECONDARY_DUMP_MODE, dumpRight);
    }

    @Override
    protected void applyImplicitComponents(@NotNull BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        dumpLeft = input.getOrDefault(MekanismDataComponents.DUMP_MODE, dumpLeft);
        dumpRight = input.getOrDefault(MekanismDataComponents.SECONDARY_DUMP_MODE, dumpRight);
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(fluidTank.getFluidAmount(), fluidTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(ContainerType<?, ?, ?> type) {
        return type == ContainerType.FLUID;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableEnum.create(GasMode.BY_ID, GasMode.IDLE, () -> dumpLeft, value -> dumpLeft = value));
        container.track(SyncableEnum.create(GasMode.BY_ID, GasMode.IDLE, () -> dumpRight, value -> dumpRight = value));
        container.track(SyncableLong.create(this::getEnergyUsed, value -> clientEnergyUsed = value));
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
        Direction direction = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ()))) {
            return getCurrentRedstoneLevel();
        }
        switch (direction) {
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
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, @Nullable Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.FLUID.block()) {
            return Objects.requireNonNull(fluidHandlerManager, "Expected to have fluid handler").resolve(capability, side);
        } else if (capability == Capabilities.CHEMICAL.block()) {
            return Objects.requireNonNull(chemicalHandlerManager, "Expected to have chemical handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.FLUID.block()) {
            return notFluidPort(side, offset);
        } else if (capability == Capabilities.CHEMICAL.block()) {
            return notChemicalPort(side, offset);
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == Capabilities.ITEM.block()) {
            return notItemPort(side, offset);
        }
        return notFluidPort(side, offset) && notChemicalPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notChemicalPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, front.getStepZ()))) {
                    return side != left;
                }
                if (offset.equals(new Vec3i(right.getStepX(), 0, front.getStepZ()))) {
                    return side != right;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(front.getStepX(), 0, left.getStepZ()))) {
                    return side != left;
                }
                if (offset.equals(new Vec3i(front.getStepX(), 0, right.getStepZ()))) {
                    return side != right;
                }
            }
        }
        return true;
    }

    private boolean notFluidPort(Direction side, Vec3i offset) {
        Direction direction = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (direction) {
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
