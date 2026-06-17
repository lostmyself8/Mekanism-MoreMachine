package com.jerry.meklm.common.tile.machine;

import com.jerry.meklm.common.capabilities.holder.chemical.AdjustableChemicalTankHelper;
import com.jerry.meklm.common.registries.LargeMachineBlocks;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.Upgrade;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalStackTemplate;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ChemicalChemicalToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OrderlessTwoInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.api.recipes.vanilla_input.BiChemicalRecipeInput;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.capabilities.holder.single.SingleConfigHolder;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.IEitherSideRecipeLookupHandler.EitherSideChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.EitherSideChemical;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.ResourceUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TileEntityLargePigmentMixer extends TileEntityRecipeMachine<ChemicalChemicalToChemicalRecipe> implements IBoundingBlock,
                                         EitherSideChemicalRecipeLookupHandler<ChemicalChemicalToChemicalRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_LEFT_INPUT,
            RecipeError.NOT_ENOUGH_RIGHT_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);

    @Nullable
    private List<BlockCapabilityCache<ResourceHandler<ChemicalResource>, @Nullable Direction>> chemicalOutputCaches;

    public static final long MAX_INPUT_PIGMENT = FluidType.BUCKET_VOLUME;
    public static final long MAX_OUTPUT_PIGMENT = 2 * MAX_INPUT_PIGMENT;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getLeftInput", "getLeftInputCapacity", "getLeftInputNeeded",
                                    "getLeftInputFilledPercentage" },
                            docPlaceholder = "left pigment tank")
    public IChemicalTank leftInputTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getRightInput", "getRightInputCapacity", "getRightInputNeeded",
                                    "getRightInputFilledPercentage" },
                            docPlaceholder = "right pigment tank")
    public IChemicalTank rightInputTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getOutput", "getOutputCapacity", "getOutputNeeded",
                                    "getOutputFilledPercentage" },
                            docPlaceholder = "output pigment tank")
    public IChemicalTank outputTank;

    private long clientEnergyUsed = 0;
    private int baselineMaxOperations = 1;
    private int baseOperations = 1;
    private int numPowering;

    private final IOutputHandler<@NotNull ChemicalStackTemplate> outputHandler;
    private final IInputHandler<Chemical, @NotNull ChemicalStack> leftInputHandler;
    private final IInputHandler<Chemical, @NotNull ChemicalStack> rightInputHandler;

    private MachineEnergyContainer<TileEntityLargePigmentMixer> energyContainer;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getLeftInputItem", docPlaceholder = "left input slot")
    ChemicalInventorySlot leftInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "output slot")
    ChemicalInventorySlot outputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getRightInputItem", docPlaceholder = "right input slot")
    ChemicalInventorySlot rightInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityLargePigmentMixer(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_PIGMENT_MIXER, pos, state, TRACKED_ERROR_TYPES);

        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT_1, new InventorySlotInfo(true, true, leftInputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_2, new InventorySlotInfo(true, true, rightInputSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(true, true, outputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, leftInputSlot, rightInputSlot, outputSlot));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }

        ConfigInfo pigmentConfig = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (pigmentConfig != null) {
            pigmentConfig.addSlotInfo(DataType.INPUT_1, new ChemicalSlotInfo(true, false, leftInputTank));
            pigmentConfig.addSlotInfo(DataType.INPUT_2, new ChemicalSlotInfo(true, false, rightInputTank));
            pigmentConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo(false, true, outputTank));
            pigmentConfig.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo(true, true, leftInputTank, rightInputTank, outputTank));
        }

        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);
        configComponent.addDisabledSides(RelativeSide.TOP);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                .setCanTankEject(tank -> tank == outputTank);

        leftInputHandler = InputHelper.getInputHandler(leftInputTank, RecipeError.NOT_ENOUGH_LEFT_INPUT);
        rightInputHandler = InputHelper.getInputHandler(rightInputTank, RecipeError.NOT_ENOUGH_RIGHT_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @NotNull
    @Override
    public IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        AdjustableChemicalTankHelper builder = AdjustableChemicalTankHelper.forSide(facingSupplier, side -> side == RelativeSide.LEFT || side == RelativeSide.RIGHT, side -> side == RelativeSide.FRONT);
        builder.addTank(leftInputTank = BasicChemicalTank.input(MAX_INPUT_PIGMENT, (pigment, automationType) -> containsRecipe(pigment, rightInputTank.resource()),
                this::containsRecipe, recipeCacheListener), RelativeSide.LEFT);
        builder.addTank(rightInputTank = BasicChemicalTank.input(MAX_INPUT_PIGMENT, (pigment, automationType) -> containsRecipe(pigment, leftInputTank.resource()),
                this::containsRecipe, recipeCacheListener), RelativeSide.RIGHT);
        builder.addTank(outputTank = BasicChemicalTank.output(MAX_OUTPUT_PIGMENT, recipeCacheUnpauseListener), RelativeSide.FRONT);
        return builder.build();
    }

    @NotNull
    @Override
    protected ISingleContainerHolder<IEnergyContainer> getInitialEnergyContainer(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener);
        return SingleConfigHolder.energy(energyContainer, this);
    }

    @NotNull
    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSide(facingSupplier, side -> side == RelativeSide.LEFT || side == RelativeSide.RIGHT || side == RelativeSide.BACK, side -> side == RelativeSide.FRONT);
        builder.addContainer(leftInputSlot = ChemicalInventorySlot.fill(leftInputTank, listener, 6, 56), RelativeSide.LEFT);
        builder.addContainer(rightInputSlot = ChemicalInventorySlot.fill(rightInputTank, listener, 154, 56), RelativeSide.RIGHT);
        builder.addContainer(outputSlot = ChemicalInventorySlot.drain(outputTank, listener, 80, 65), RelativeSide.FRONT);
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 154, 14), RelativeSide.BACK);
        leftInputSlot.setSlotType(ContainerSlotType.INPUT);
        leftInputSlot.setSlotOverlay(SlotOverlay.MINUS);
        rightInputSlot.setSlotType(ContainerSlotType.INPUT);
        rightInputSlot.setSlotOverlay(SlotOverlay.MINUS);
        outputSlot.setSlotType(ContainerSlotType.OUTPUT);
        outputSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer(net.minecraft.server.level.ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        energySlot.fillContainerOrConvert(null);
        leftInputSlot.fillTankFromSlot(null);
        rightInputSlot.fillTankFromSlot(null);
        outputSlot.drainTankIntoSlot(null);
        clientEnergyUsed = recipeCacheLookupMonitor.updateAndProcess(energyContainer);
        handleEject();
        return sendUpdatePacket;
    }

    private void handleEject() {
        if (chemicalOutputCaches == null) {
            chemicalOutputCaches = new ArrayList<>(2);
            Direction side = RelativeSide.FRONT.getDirection(getDirection());
            chemicalOutputCaches.add(Capabilities.CHEMICAL.createCache((ServerLevel) level, worldPosition.offset(side.getUnitVec3i()).offset(getLeftSide().getUnitVec3i()).relative(side), side.getOpposite()));
            chemicalOutputCaches.add(Capabilities.CHEMICAL.createCache((ServerLevel) level, worldPosition.offset(side.getUnitVec3i()).offset(getRightSide().getUnitVec3i()).relative(side), side.getOpposite()));
        }
        ResourceUtils.emit(chemicalOutputCaches, outputTank, MathUtils.clampToInt(outputTank.capacityAsLong(outputTank.resource())), null);
    }

    @ComputerMethod(nameOverride = "getEnergyUsage", methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    public long getEnergyUsed() {
        return clientEnergyUsed;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<BiChemicalRecipeInput, ChemicalChemicalToChemicalRecipe, EitherSideChemical<ChemicalChemicalToChemicalRecipe>> getRecipeType() {
        return MekanismRecipeType.PIGMENT_MIXING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<ChemicalChemicalToChemicalRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.PIGMENT_MIXING;
    }

    @Override
    public @Nullable ChemicalChemicalToChemicalRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(leftInputHandler, rightInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<ChemicalChemicalToChemicalRecipe> createNewCachedRecipe(@NotNull ChemicalChemicalToChemicalRecipe recipe, int cacheIndex) {
        return new OrderlessTwoInputCachedRecipe<>(recipe, recheckAllRecipeErrors, leftInputHandler, rightInputHandler, outputHandler)
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
            int upgradeCount = getUpgrades(Upgrade.SPEED);
            baseOperations = 4 * (upgradeCount > 0 ? upgradeCount : upgradeCount + 1);
            baselineMaxOperations = (int) Math.pow(2, upgradeCount);
        }
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
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

    @Override
    public int getBoundingComparatorSignal(Vec3i offset) {
        // 化学品输入口
        Direction direction = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
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
        // 能量输入口
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ()))) {
            return getCurrentRedstoneLevel();
        }
        return 0;
    }

    @Override
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, @Nullable Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.CHEMICAL.block()) {
            return Objects.requireNonNull(chemicalHandlerManager, "Expected to have chemical handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.CHEMICAL.block()) {
            return notChemicalPort(side, offset);
        } else if (capability == Capabilities.ENERGY.block()) {
            return notEnergyPort(side, offset);
        } else if (capability == Capabilities.ITEM.block()) {
            return notItemPort(side, offset);
        }
        return notChemicalPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notChemicalPort(Direction side, Vec3i offset) {
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
        return notChemicalPort(side, offset) && notEnergyPort(side, offset);
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
