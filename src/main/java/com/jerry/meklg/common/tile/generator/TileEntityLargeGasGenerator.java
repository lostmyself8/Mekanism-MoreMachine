package com.jerry.meklg.common.tile.generator;

import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.attribute.ChemicalAttributes;
import mekanism.api.datamaps.IMekanismDataMapTypes;
import mekanism.api.datamaps.chemical.attribute.ChemicalFuel;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.math.MathUtils;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class TileEntityLargeGasGenerator extends TileEntityMoreMachineGenerator implements IBoundingBlock {

    @SuppressWarnings("removal")
    public static final Predicate<ChemicalStack> HAS_FUEL = chemical -> chemical.getData(IMekanismDataMapTypes.INSTANCE.chemicalFuel()) != null || chemical.hasLegacy(ChemicalAttributes.Fuel.class);// TODO
    /**
     * The tank this block is storing fuel in.
     */
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class,
                            methodNames = { "getFuel", "getFuelCapacity", "getFuelNeeded",
                                    "getFuelFilledPercentage" },
                            docPlaceholder = "fuel tank")
    public FuelTank fuelTank;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getFuelItem", docPlaceholder = "fuel item slot")
    ChemicalInventorySlot fuelSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;
    private long burnTicks;
    @Getter
    private int maxBurnTicks;
    @Getter
    private long generationRate = 0;
    private double gasUsedLastTick;
    private double efficiencyMultiplier = 1.0;
    private int numPowering;

    public TileEntityLargeGasGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorBlocks.LARGE_GAS_BURNING_GENERATOR, pos, state, ChemicalUtil::hydrogenEnergyPerTick);
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        ChemicalTankHelper builder = ChemicalTankHelper.forSide(facingSupplier);
        builder.addTank(fuelTank = new FuelTank(listener), RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier);
        builder.addSlot(fuelSlot = ChemicalInventorySlot.fill(fuelTank, listener, 17, 35), RelativeSide.LEFT, RelativeSide.BACK, RelativeSide.RIGHT);
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35), RelativeSide.TOP);
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
        energySlot.drainContainer();
        fuelSlot.fillTank();

        updateEfficiency();

        if (!fuelTank.isEmpty() && canFunction() && getEnergyContainer().insert(generationRate, Action.SIMULATE, AutomationType.INTERNAL) == 0L) {
            setActive(true);
            if (!fuelTank.isEmpty()) {
                ChemicalFuel fuel = fuelTank.getFuel();
                if (fuel != null) {
                    // Ensure valid data
                    maxBurnTicks = Math.max(1, fuel.burnTicks());
                    // 不能在此处倍增，会在gui计算中倍增两次
                    generationRate = fuel.energyPerTick();
                }
            }

            // 准备使用多少燃料， 燃料消耗减半
            long toUse = (long) (getToUse() * efficiencyMultiplier);
            // 应当在这里倍增
            long toUseGeneration = MathUtils.multiplyClamped((long) (generationRate * efficiencyMultiplier), toUse);
            updateMaxOutputRaw(Math.max(ChemicalUtil.hydrogenEnergyPerTick(), toUseGeneration));

            long total = burnTicks + fuelTank.getStored() * maxBurnTicks;
            total -= toUse;
            getEnergyContainer().insert(toUseGeneration, Action.EXECUTE, AutomationType.INTERNAL);
            if (!fuelTank.isEmpty()) {
                // TODO: Improve this as it is sort of hacky
                fuelTank.setStack(fuelTank.getStack().copyWithAmount(total / maxBurnTicks));
            }
            burnTicks = total % maxBurnTicks;
            gasUsedLastTick = toUse / (double) maxBurnTicks;
        } else {
            if (fuelTank.isEmpty() && burnTicks == 0) {
                reset();
            }
            gasUsedLastTick = 0;
            setActive(false);
        }
        return sendUpdatePacket;
    }

    @Override
    protected BlockPos offSetOutput(BlockPos from, Direction side) {
        return from.offset(new Vec3i(0, 2, 0)).relative(side);
    }

    private void reset() {
        burnTicks = 0;
        maxBurnTicks = 0;
        generationRate = 0L;
        updateMaxOutputRaw(ChemicalUtil.hydrogenEnergyPerTick());
    }

    private long getToUse() {
        if (generationRate == 0L || fuelTank.isEmpty()) {
            return 0;
        }
        // 向上取整
        long max = (long) Math.ceil(256 * (fuelTank.getStored() / (double) fuelTank.getCapacity()));
        // 最大燃烧时间*储量 + 燃烧时间（一开始为0） 与 max取最小
        max = Math.min(maxBurnTicks * fuelTank.getStored() + burnTicks, max);
        // 剩余能量容量/燃料每tick生成的能量 与 max取最小
        max = Math.min(MathUtils.clampToLong(getEnergyContainer().getNeeded() / (double) generationRate), max);
        return max;
    }

    @ComputerMethod(nameOverride = "getEfficiencyMultiplier")
    public double getEfficiencyMultiplier() {
        return Math.round(efficiencyMultiplier * 100) / 100D;
    }

    @ComputerMethod(nameOverride = "getBurnRate")
    public double getUsed() {
        return Math.round(gasUsedLastTick * efficiencyMultiplier * 100) / 100D;
    }

    private void updateEfficiency() {
        if (fuelTank.isEmpty()) {
            efficiencyMultiplier = 1.0;
            return;
        }
        double fillPercentage = fuelTank.getStored() / (double) fuelTank.getCapacity();
        // 三次方曲线: 前期增长慢,后期加速
        efficiencyMultiplier = 1.0 + 21.0 * Math.pow(fillPercentage, 3);
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(fuelTank.getStored(), fuelTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(ContainerType<?, ?, ?> type) {
        return type == ContainerType.CHEMICAL;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableLong.create(this::getGenerationRate, value -> generationRate = value));
        container.track(syncableMaxOutput());
        container.track(SyncableDouble.create(this::getUsed, value -> gasUsedLastTick = value));
        container.track(SyncableInt.create(this::getMaxBurnTicks, value -> maxBurnTicks = value));
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
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
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
        return MathUtils.clampToLong(getGenerationRate() * getUsed() * getMaxBurnTicks());
    }
    // End methods IComputerTile

    // Implementation of gas tank that on no longer being empty updates the output rate of this generator
    public class FuelTank extends VariableCapacityChemicalTank {

        protected FuelTank(@Nullable IContentsListener listener) {
            super(MoreMachineConfig.generators.LGBGTankCapacity, ConstantPredicates.notExternal(), ConstantPredicates.alwaysTrueBi(), HAS_FUEL, null, listener);
        }

        @Override
        public void setStack(@org.jetbrains.annotations.NotNull ChemicalStack stack) {
            boolean wasEmpty = isEmpty();
            super.setStack(stack);
            recheckOutput(stack, wasEmpty);
        }

        @Override
        public void setStackUnchecked(@org.jetbrains.annotations.NotNull ChemicalStack stack) {
            boolean wasEmpty = isEmpty();
            super.setStackUnchecked(stack);
            recheckOutput(stack, wasEmpty);
        }

        private void recheckOutput(@NotNull ChemicalStack stack, boolean wasEmpty) {
            if (wasEmpty && !stack.isEmpty()) {
                ChemicalFuel fuel = getFuel();
                if (fuel != null) {
                    updateMaxOutputRaw(fuel.energyPerTick());
                }
            }
        }

        @Nullable
        @SuppressWarnings("removal")
        public ChemicalFuel getFuel() {
            if (isEmpty()) {
                return null;
            }
            ChemicalStack stack = getStack();
            ChemicalFuel fuel = stack.getData(IMekanismDataMapTypes.INSTANCE.chemicalFuel());
            if (fuel == null) {// TODO - 1.22: Remove this handling of legacy data
                // If there is no fuel in the data map, see if one was set manually on the stack
                ChemicalAttributes.Fuel legacyFuel = stack.getLegacy(ChemicalAttributes.Fuel.class);
                if (legacyFuel != null) {
                    // If it was, convert it to the non legacy type
                    return legacyFuel.asModern();
                }
            }
            return fuel;
        }
    }
}
