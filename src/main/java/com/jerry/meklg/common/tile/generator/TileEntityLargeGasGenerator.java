package com.jerry.meklg.common.tile.generator;

import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.datamaps.IMekanismDataMapTypes;
import mekanism.api.datamaps.chemical.attribute.ChemicalFuel;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.resource.LargeResourceStack;
import mekanism.common.attachments.containers.type.ContainerType;
import mekanism.common.attachments.containers.type.IContainerType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class TileEntityLargeGasGenerator extends TileEntityMoreMachineGenerator implements IBoundingBlock {

    public static final Predicate<ChemicalResource> HAS_FUEL = chemical -> chemical.getData(IMekanismDataMapTypes.INSTANCE.chemicalFuel()) != null;
    /**
     * The tank this block is storing fuel in.
     */
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getFuel", "getFuelCapacity", "getFuelNeeded",
                                    "getFuelFilledPercentage" },
                            docPlaceholder = "fuel tank")
    public FuelTank fuelTank;

    @Nullable
    private ChemicalFuel cachedFuel = null;
    private double gasUsedLastTick;
    private double efficiencyMultiplier = 1.0;
    private int numPowering;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFuelItem", docPlaceholder = "fuel item slot")
    ChemicalInventorySlot fuelSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;

    public TileEntityLargeGasGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorBlocks.LARGE_GAS_BURNING_GENERATOR, pos, state);
    }

    @NotNull
    @Override
    public IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener) {
        MekContainerHelper<IChemicalTank> builder = MekContainerHelper.forSide(facingSupplier);
        builder.addContainer(fuelTank = new FuelTank(listener), RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSide(facingSupplier);
        builder.addContainer(fuelSlot = ChemicalInventorySlot.fill(fuelTank, listener, 17, 35), RelativeSide.LEFT, RelativeSide.BACK, RelativeSide.RIGHT);
        builder.addContainer(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35), RelativeSide.TOP);
        fuelSlot.setSlotOverlay(SlotOverlay.MINUS);
        return builder.build();
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[] { RelativeSide.TOP };
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.drainContainerIntoSlot(null);
        fuelSlot.fillTankFromSlot(null);
        gasUsedLastTick = 0;

        // updateEfficiency();

        if (!fuelTank.isEmpty() && canFunction() && cachedFuel != null) {

            // how full the tank is, poor-man's "pressure" measurement
            ChemicalResource fuel = fuelTank.resource();
            double fullness = fuelTank.amountAsLong() / (double) fuelTank.capacityAsLong(fuel);

            // maximum amount that can be produced AND stored
            int energyDensity = cachedFuel.energyDensity();
            int maxEnergyThisTick = MathUtils.clampToInt((long) energyDensity * Math.min((long) Math.ceil(cachedFuel.maxBurnPerTick() * fullness), fuelTank.amountAsLong()));
            if (maxEnergyThisTick > 0) {
                try (Transaction transaction = Transaction.openRoot()) {
                    int inserted = getEnergyContainer().insert(maxEnergyThisTick, transaction, AutomationType.INTERNAL);
                    if (inserted > 0) {
                        int mbThisTick = Math.ceilDiv(inserted, energyDensity);
                        if (fuelTank.extract(fuel, mbThisTick, transaction, AutomationType.INTERNAL) == mbThisTick) {
                            gasUsedLastTick = mbThisTick;
                            transaction.commit();
                        }
                    }
                }
            }
        }

        setActive(gasUsedLastTick != 0);
        return sendUpdatePacket;
    }

    @Override
    protected BlockPos offSetOutput(BlockPos from, Direction side) {
        return from.offset(new Vec3i(0, 2, 0)).relative(side);
    }

    @ComputerMethod(nameOverride = "getEfficiencyMultiplier")
    public double getEfficiencyMultiplier() {
        return Math.round(efficiencyMultiplier * 100) / 100D;
    }

    @ComputerMethod(nameOverride = "getBurnRate")
    public double getUsed() {
        return Math.round(gasUsedLastTick * 100) / 100D;
    }

    @Override
    public int getRedstoneLevel() {
        return ContainerType.CHEMICAL.getRedstoneSignalFromContainer(fuelTank);
    }

    @Override
    protected boolean makesComparatorDirty(IContainerType<?, ?> type) {
        return type == ContainerType.CHEMICAL;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        // container.track(SyncableLong.create(this::getGenerationRate, v -> generationRate = v));
        container.track(SyncableDouble.create(this::getUsed, v -> gasUsedLastTick = v));
        // container.track(SyncableInt.create(this::getMaxBurnTicks, v -> maxBurnTicks = v));
        // container.track(SyncableDouble.create(this::getEfficiencyMultiplier, v -> efficiencyMultiplier = v));
    }

    @Nullable
    public ChemicalFuel getCachedFuel() {
        return this.cachedFuel;
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
        Direction back = getOppositeDirection();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 1, back.getStepZ()))) {
            return getCurrentRedstoneLevel();
        }
        Direction left = getLeftSide();
        if (offset.equals(new Vec3i(left.getStepX(), 0, left.getStepZ())) || offset.equals(new Vec3i(left.getStepX(), 1, left.getStepZ()))) {
            return getCurrentRedstoneLevel();
        }
        Direction right = left.getOpposite();
        if (offset.equals(new Vec3i(right.getStepX(), 0, right.getStepZ())) || offset.equals(new Vec3i(right.getStepX(), 1, right.getStepZ()))) {
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
        Direction back = getOppositeDirection();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 1, back.getStepZ()))) {
            return side != back;
        }
        Direction left = getLeftSide();
        if (offset.equals(new Vec3i(left.getStepX(), 0, left.getStepZ())) || offset.equals(new Vec3i(left.getStepX(), 1, left.getStepZ()))) {
            return side != left;
        }
        Direction right = left.getOpposite();
        if (offset.equals(new Vec3i(right.getStepX(), 0, right.getStepZ())) || offset.equals(new Vec3i(right.getStepX(), 1, right.getStepZ()))) {
            return side != right;
        }
        return true;
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        // 所有端口都可以与物品管道交互
        return notChemicalPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        if (offset.equals(new Vec3i(0, 2, 0))) {
            return side != Direction.UP;
        }
        return true;
    }

    // Methods relating to IComputerTile
    @Override
    protected long getProductionRate() {
        // return MathUtils.clampToLong(getGenerationRate() * getUsed() * getMaxBurnTicks());
        if (cachedFuel == null) {
            return 0;
        }
        return MathUtils.clampToLong(cachedFuel.energyDensity() * getUsed());
    }
    // End methods IComputerTile

    // Implementation of gas tank that on no longer being empty updates the output rate of this generator
    public class FuelTank extends VariableCapacityChemicalTank {

        protected FuelTank(@Nullable IContentsListener listener) {
            super(MoreMachineConfig.generators.LGBGTankCapacity, ConstantPredicates.notExternal(), ConstantPredicates.alwaysTrueBi(), HAS_FUEL, null, null, null, listener);
        }

        @Override
        protected void onContentsChanged(@NotNull LargeResourceStack<ChemicalResource> originalState) {
            super.onContentsChanged(originalState);
            ChemicalResource newType = resource();
            if (!newType.isEmpty() && !originalState.matches(newType)) {
                cachedFuel = newType.getData(IMekanismDataMapTypes.INSTANCE.chemicalFuel());
            }
        }
    }
}
