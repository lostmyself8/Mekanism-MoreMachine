package com.jerry.meklm.common.tile.machine;

import com.jerry.mekmm.api.ITileEntityMekanismAccessor;
import com.jerry.mekmm.common.capabilities.holder.chemical.AdjustableChemicalTankHelper;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.Upgrade;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.PigmentMixingRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.ChemicalChemicalToChemicalCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.chemical.PigmentInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.IEitherSideRecipeLookupHandler.EitherSideChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.EitherSideChemical;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo.PigmentSlotInfo;
import mekanism.common.tile.component.config.slot.EnergySlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;

import com.jerry.meklm.api.INotNeedConfig;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TileEntityLargePigmentMixer extends TileEntityRecipeMachine<PigmentMixingRecipe> implements IBoundingBlock, INotNeedConfig,
                                         EitherSideChemicalRecipeLookupHandler<Pigment, PigmentStack, PigmentMixingRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_LEFT_INPUT,
            RecipeError.NOT_ENOUGH_RIGHT_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    public static final long MAX_PIGMENT = 5L * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getLeftInput", "getLeftInputCapacity", "getLeftInputNeeded",
                                    "getLeftInputFilledPercentage" },
                            docPlaceholder = "left pigment tank")
    public IPigmentTank leftInputTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getRightInput", "getRightInputCapacity", "getRightInputNeeded",
                                    "getRightInputFilledPercentage" },
                            docPlaceholder = "right pigment tank")
    public IPigmentTank rightInputTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = { "getOutput", "getOutputCapacity", "getOutputNeeded", "getOutputFilledPercentage" }, docPlaceholder = "output pigment tank")
    public IPigmentTank outputTank;

    private FloatingLong clientEnergyUsed = FloatingLong.ZERO;
    private int baseOperations = 1;
    private int baselineMaxOperations = 1;

    private final IOutputHandler<@NotNull PigmentStack> outputHandler;
    private final IInputHandler<@NotNull PigmentStack> leftInputHandler;
    private final IInputHandler<@NotNull PigmentStack> rightInputHandler;

    @Getter
    private MachineEnergyContainer<TileEntityLargePigmentMixer> energyContainer;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getLeftInputItem", docPlaceholder = "left input slot")
    PigmentInventorySlot leftInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "output slot")
    PigmentInventorySlot outputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getRightInputItem", docPlaceholder = "right input slot")
    PigmentInventorySlot rightInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityLargePigmentMixer(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_PIGMENT_MIXER, pos, state, TRACKED_ERROR_TYPES);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.PIGMENT, TransmissionType.ENERGY);

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT_1, new InventorySlotInfo(true, true, leftInputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_2, new InventorySlotInfo(true, true, rightInputSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(true, true, outputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, leftInputSlot, rightInputSlot, outputSlot));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }
        ConfigInfo pigmentConfig = configComponent.getConfig(TransmissionType.PIGMENT);
        if (pigmentConfig != null) {
            pigmentConfig.addSlotInfo(DataType.INPUT_1, new PigmentSlotInfo(true, false, leftInputTank));
            pigmentConfig.addSlotInfo(DataType.INPUT_2, new PigmentSlotInfo(true, false, rightInputTank));
            pigmentConfig.addSlotInfo(DataType.OUTPUT, new PigmentSlotInfo(false, true, outputTank));
            pigmentConfig.addSlotInfo(DataType.INPUT_OUTPUT, new PigmentSlotInfo(true, true, leftInputTank, rightInputTank, outputTank));
        }
        ConfigInfo energyConfig = configComponent.getConfig(TransmissionType.ENERGY);
        if (energyConfig != null) {
            energyConfig.addSlotInfo(DataType.INPUT, new EnergySlotInfo(true, false, energyContainer));
        }

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.PIGMENT)
                .setCanTankEject(tank -> tank == outputTank);

        leftInputHandler = InputHelper.getInputHandler(leftInputTank, RecipeError.NOT_ENOUGH_LEFT_INPUT);
        rightInputHandler = InputHelper.getInputHandler(rightInputTank, RecipeError.NOT_ENOUGH_RIGHT_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Pigment, PigmentStack, IPigmentTank> getInitialPigmentTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        AdjustableChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> builder = AdjustableChemicalTankHelper.forSidePigment(this::getDirection, side -> side == RelativeSide.LEFT || side == RelativeSide.RIGHT, side -> side == RelativeSide.FRONT);
        builder.addTank(leftInputTank = ChemicalTankBuilder.PIGMENT.input(MAX_PIGMENT, pigment -> containsRecipe(pigment, rightInputTank.getStack()),
                this::containsRecipe, recipeCacheListener), RelativeSide.LEFT);
        builder.addTank(rightInputTank = ChemicalTankBuilder.PIGMENT.input(MAX_PIGMENT, pigment -> containsRecipe(pigment, leftInputTank.getStack()),
                this::containsRecipe, recipeCacheListener), RelativeSide.RIGHT);
        builder.addTank(outputTank = ChemicalTankBuilder.PIGMENT.output(2 * MAX_PIGMENT, listener), RelativeSide.FRONT);
        return builder.build();
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
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection, side -> side == RelativeSide.LEFT || side == RelativeSide.RIGHT || side == RelativeSide.BACK, side -> side == RelativeSide.FRONT);
        builder.addSlot(leftInputSlot = PigmentInventorySlot.fill(leftInputTank, listener, 6, 56), RelativeSide.LEFT);
        builder.addSlot(rightInputSlot = PigmentInventorySlot.fill(rightInputTank, listener, 154, 56), RelativeSide.RIGHT);
        builder.addSlot(outputSlot = PigmentInventorySlot.drain(outputTank, listener, 80, 65), RelativeSide.FRONT);
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 154, 14), RelativeSide.BACK);
        leftInputSlot.setSlotType(ContainerSlotType.INPUT);
        leftInputSlot.setSlotOverlay(SlotOverlay.MINUS);
        rightInputSlot.setSlotType(ContainerSlotType.INPUT);
        rightInputSlot.setSlotOverlay(SlotOverlay.MINUS);
        outputSlot.setSlotType(ContainerSlotType.OUTPUT);
        outputSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        leftInputSlot.fillTank();
        rightInputSlot.fillTank();
        outputSlot.drainTank();
        clientEnergyUsed = recipeCacheLookupMonitor.updateAndProcess(energyContainer);
        handleEject();
    }

    private void handleEject() {
        if (MekanismUtils.canFunction(this)) {
            Set<Direction> emitDirections = EnumSet.noneOf(Direction.class);
            Direction side = RelativeSide.FRONT.getDirection(getDirection());
            emitDirections.add(side);
            for (BlockEntity blockEntity : getEjectEntity(side)) {
                if (blockEntity != null) {
                    ChemicalUtil.emit(emitDirections, outputTank, blockEntity, outputTank.getCapacity());
                }
            }
        }
    }

    private BlockEntity[] getEjectEntity(Direction side) {
        return new BlockEntity[] {
                WorldUtils.getTileEntity(getLevel(), worldPosition.offset(side.getNormal()).offset(getLeftSide().getNormal())),
                WorldUtils.getTileEntity(getLevel(), worldPosition.offset(side.getNormal()).offset(getRightSide().getNormal()))
        };
    }

    @NotNull
    @ComputerMethod(nameOverride = "getEnergyUsage", methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    public FloatingLong getEnergyUsed() {
        return clientEnergyUsed;
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<PigmentMixingRecipe, EitherSideChemical<Pigment, PigmentStack, PigmentMixingRecipe>> getRecipeType() {
        return MekanismRecipeType.PIGMENT_MIXING;
    }

    @Nullable
    @Override
    public PigmentMixingRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(leftInputHandler, rightInputHandler);
    }

    @NotNull
    @Override
    public CachedRecipe<PigmentMixingRecipe> createNewCachedRecipe(@NotNull PigmentMixingRecipe recipe, int cacheIndex) {
        return new ChemicalChemicalToChemicalCachedRecipe<>(recipe, recheckAllRecipeErrors, leftInputHandler, rightInputHandler, outputHandler)
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

    @NotNull
    @Override
    public AABB getRenderBoundingBox() {
        // We only care about the position that is above because we only use the BER to render the shaft which is in the
        // upper block
        return new AABB(worldPosition.above(2), worldPosition.offset(1, 2, 1));
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getEnergyUsed, value -> clientEnergyUsed = value));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getOffsetCapabilityIfEnabled(@NotNull Capability<T> capability, Direction side, @NotNull Vec3i offset) {
        if (this instanceof ITileEntityMekanismAccessor accessor) {
            if (capability == Capabilities.PIGMENT_HANDLER) {
                return accessor.getPigmentHandlerManager().resolve(capability, side);
            } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
                return accessor.getEnergyHandlerManager().resolve(capability, side);
            }
        }
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerManager.resolve(capability, side);
        }
        return getCapability(capability, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull Capability<?> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.PIGMENT_HANDLER) {
            return notPigmentPort(side, offset);
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return notItemPort(side, offset);
        } else if (canEverResolve(capability) && IBoundingBlock.super.isOffsetCapabilityDisabled(capability, side, offset)) {
            // If we are not an item handler or energy capability, and it is a capability that we can support,
            // but it is one that normally should be disabled for offset capabilities, then expose it but only do so
            // via our ports for things like computer integration capabilities, then we treat the capability as
            // disabled if it is not against one of our ports
            return notItemPort(side, offset);
        }
        return false;
    }

    private boolean notPigmentPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, front.getStepZ())) || offset.equals(new Vec3i(right.getStepX(), 0, front.getStepZ()))) {
                    return side != front;
                }
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ()))) {
                    return side != left;
                }
                if (offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ()))) {
                    return side != right;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(front.getStepX(), 0, left.getStepZ())) || offset.equals(new Vec3i(front.getStepX(), 0, right.getStepZ()))) {
                    return side != front;
                }
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ()))) {
                    return side != left;
                }
                if (offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ()))) {
                    return side != right;
                }
            }
        }
        return true;
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        // 所有端口都可以与物品管道交互
        return notPigmentPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (getDirection()) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ()))) {
                    return side != back;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ()))) {
                    return side != back;
                }
            }
        }
        return true;
    }
}
