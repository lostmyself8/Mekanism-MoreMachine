package com.jerry.meklg.common.tile;

import com.jerry.mekmm.api.ITileEntityMekanismAccessor;
import com.jerry.mekmm.common.config.MoreMachineConfig;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.variable.VariableCapacityChemicalTankBuilder.VariableCapacityGasTank;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import com.jerry.meklg.common.registries.LargeGeneratorsBlocks;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TileEntityLargeGasBurningGenerator extends TileEntityMoreGenerator implements IBoundingBlock {

    /**
     * The tank this block is storing fuel in.
     */
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = { "getFuel", "getFuelCapacity", "getFuelNeeded", "getFuelFilledPercentage" }, docPlaceholder = "fuel tank")
    public FuelTank fuelTank;
    private long burnTicks;
    @Getter
    private int maxBurnTicks;
    @Getter
    private FloatingLong generationRate = FloatingLong.ZERO;
    private double gasUsedLastTick;
    private double efficiencyMultiplier = 1.0;
    private int numPowering;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFuelItem", docPlaceholder = "fuel item slot")
    GasInventorySlot fuelSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;

    public TileEntityLargeGasBurningGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorsBlocks.LARGE_GAS_BURNING_GENERATOR, pos, state, MekanismConfig.general.FROM_H2);
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSide(this::getDirection);
        builder.addTank(fuelTank = new FuelTank(listener), RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(fuelSlot = GasInventorySlot.fill(fuelTank, listener, 17, 35), RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK);
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35), RelativeSide.TOP);
        fuelSlot.setSlotOverlay(SlotOverlay.MINUS);
        return builder.build();
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[] { RelativeSide.TOP };
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.drainContainer();
        fuelSlot.fillTank();

        updateEfficiency();

        if (!fuelTank.isEmpty() && MekanismUtils.canFunction(this) && getEnergyContainer().insert(generationRate, Action.SIMULATE, AutomationType.INTERNAL).isZero()) {
            setActive(true);
            if (!fuelTank.isEmpty()) {
                fuelTank.getType().ifAttributePresent(GasAttributes.Fuel.class, fuel -> {
                    // Ensure valid data
                    maxBurnTicks = Math.max(1, fuel.getBurnTicks());
                    generationRate = fuel.getEnergyPerTick();
                });
            }

            long toUse = (long) (getToUse() * efficiencyMultiplier);
            FloatingLong toUseGeneration = generationRate.multiply(toUse);
            updateMaxOutputRaw(MekanismConfig.general.FROM_H2.get().max(toUseGeneration));

            long total = burnTicks + fuelTank.getStored() * maxBurnTicks;
            total -= toUse;
            getEnergyContainer().insert(toUseGeneration, Action.EXECUTE, AutomationType.INTERNAL);
            if (!fuelTank.isEmpty()) {
                // TODO: Improve this as it is sort of hacky
                fuelTank.setStack(new GasStack(fuelTank.getStack(), total / maxBurnTicks));
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
    }

    @Override
    protected List<BlockEntity> getEjectEntity(RelativeSide side, Direction direction) {
        List<BlockEntity> ejectEntity = new ArrayList<>();
        ejectEntity.add(WorldUtils.getTileEntity(getLevel(), worldPosition.relative(direction).above(2)));
        return ejectEntity;
    }

    private void reset() {
        burnTicks = 0;
        maxBurnTicks = 0;
        generationRate = FloatingLong.ZERO;
        updateMaxOutputRaw(MekanismConfig.general.FROM_H2.get());
    }

    private long getToUse() {
        if (generationRate.isZero() || fuelTank.isEmpty()) {
            return 0;
        }
        long max = (long) Math.ceil(256 * (fuelTank.getStored() / (double) fuelTank.getCapacity()));
        max = Math.min(maxBurnTicks * fuelTank.getStored() + burnTicks, max);
        max = Math.min(getEnergyContainer().getNeeded().divide(generationRate).intValue(), max);
        return max;
    }

    @ComputerMethod(nameOverride = "getEfficiencyMultiplier")
    public double getEfficiencyMultiplier() {
        return Math.round(efficiencyMultiplier * 100) / 100D;
    }

    @ComputerMethod(nameOverride = "getBurnRate")
    public double getUsed() {
        return Math.round(gasUsedLastTick * 100) / 100D;
    }

    private void updateEfficiency() {
        if (fuelTank.isEmpty()) {
            efficiencyMultiplier = 1.0;
            return;
        }
        double fillPercentage = fuelTank.getStored() / (double) fuelTank.getCapacity();
        // 三次方曲线: 前期增长慢,后期加速，最大效率乘数为64
        efficiencyMultiplier = 1.0 + 63.0 * Math.pow(fillPercentage, 3);
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(fuelTank.getStored(), fuelTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(@Nullable SubstanceType type) {
        return type == SubstanceType.GAS;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getGenerationRate, value -> generationRate = value));
        container.track(syncableMaxOutput());
        container.track(SyncableDouble.create(this::getUsed, value -> gasUsedLastTick = value));
        container.track(SyncableInt.create(this::getMaxBurnTicks, value -> maxBurnTicks = value));
        container.track(SyncableDouble.create(this::getEfficiencyMultiplier, value -> efficiencyMultiplier = value));
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
    public @NotNull <T> LazyOptional<T> getOffsetCapabilityIfEnabled(@NotNull Capability<T> capability, Direction side, @NotNull Vec3i offset) {
        if (this instanceof ITileEntityMekanismAccessor accessor) {
            if (capability == Capabilities.GAS_HANDLER) {
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
            return notGasPort(side, offset);
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return notItemPort(side, offset);
        } else if (canEverResolve(capability) && IBoundingBlock.super.isOffsetCapabilityDisabled(capability, side, offset)) {
            return notItemPort(side, offset);
        }
        return false;
    }

    private boolean notGasPort(Direction side, Vec3i offset) {
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
        return notGasPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        if (offset.equals(new Vec3i(0, 2, 0))) {
            return side != Direction.UP;
        }
        return true;
    }

    @Override
    public FloatingLong getProductionRate() {
        return getGenerationRate().multiply(getUsed()).multiply(getMaxBurnTicks());
    }

    private class FuelTank extends VariableCapacityGasTank {

        protected FuelTank(@Nullable IContentsListener listener) {
            super(MoreMachineConfig.generator.lGBGTankCapacity, ChemicalTankBuilder.GAS.notExternal, ChemicalTankBuilder.GAS.alwaysTrueBi,
                    gas -> gas.has(GasAttributes.Fuel.class), null, listener);
        }

        @Override
        public void setStack(@NotNull GasStack stack) {
            boolean wasEmpty = isEmpty();
            super.setStack(stack);
            recheckOutput(stack, wasEmpty);
        }

        @Override
        public void setStackUnchecked(@NotNull GasStack stack) {
            boolean wasEmpty = isEmpty();
            super.setStackUnchecked(stack);
            recheckOutput(stack, wasEmpty);
        }

        private void recheckOutput(@NotNull GasStack stack, boolean wasEmpty) {
            if (wasEmpty && !stack.isEmpty()) {
                getType().ifAttributePresent(GasAttributes.Fuel.class, fuel -> updateMaxOutputRaw(fuel.getEnergyPerTick()));
            }
        }
    }
}
