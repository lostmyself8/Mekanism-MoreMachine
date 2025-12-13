package com.jerry.meklg.common.tile.generator;

import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.math.MathUtils;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.listener.ConfigBasedCachedLongSupplier;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.slot.FluidFuelInventorySlot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidStack;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TileEntityLargeHeatGenerator extends TileEntityMoreMachineGenerator implements IBoundingBlock {

    public static final double HEAT_CAPACITY = 10;
    public static final double INVERSE_CONDUCTION_COEFFICIENT = 5;
    public static final double INVERSE_INSULATION_COEFFICIENT = 100;
    private static final double THERMAL_EFFICIENCY = 0.5;

    // Default configs this is 510 compared to the previous 500
    private static final ConfigBasedCachedLongSupplier MAX_PRODUCTION = new ConfigBasedCachedLongSupplier(() -> {
        long passiveMax = MoreMachineConfig.generators.largeHeatGenerationLava.get() * 81;
        passiveMax += MoreMachineConfig.generators.largeHeatGenerationNether.get();
        return passiveMax + MoreMachineConfig.generators.largeHeatGeneration.get();
    }, MoreMachineConfig.generators.largeHeatGeneration, MoreMachineConfig.generators.largeHeatGenerationLava, MoreMachineConfig.generators.largeHeatGenerationNether);

    /**
     * The FluidTank for this generator.
     */
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerFluidTankWrapper.class,
                            methodNames = { "getLava", "getLavaCapacity", "getLavaNeeded",
                                    "getLavaFilledPercentage" },
                            docPlaceholder = "lava tank")
    public BasicFluidTank lavaTank;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper.class, methodNames = "getTemperature", docPlaceholder = "generator")
    BasicHeatCapacitor heatCapacitor;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getFuelItem", docPlaceholder = "fuel item slot")
    FluidFuelInventorySlot fuelSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;

    private long producingEnergy = 0;
    private double efficiencyMultiplier = 1.0;
    private double lastTransferLoss;
    private double lastEnvironmentLoss;
    private int numPowering;

    public TileEntityLargeHeatGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorBlocks.LARGE_HEAT_GENERATOR, pos, state, MAX_PRODUCTION);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSide(facingSupplier);
        builder.addTank(lavaTank = VariableCapacityFluidTank.input(MoreMachineConfig.generators.largeHeatTankCapacity,
                fluidStack -> fluidStack.is(FluidTags.LAVA), listener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier);
        // Divide the burn time by 20 as that is the ratio of how much a bucket of lava would burn for
        // TODO: Eventually we may want to grab the 20 dynamically in case some mod is changing the burn time of a lava
        // bucket
        builder.addSlot(fuelSlot = FluidFuelInventorySlot.forFuel(lavaTank, stack -> stack.getBurnTime(null) / 20, size -> new FluidStack(Fluids.LAVA, size),
                listener, 17, 35), RelativeSide.FRONT, RelativeSide.LEFT, RelativeSide.BACK, RelativeSide.TOP, RelativeSide.BOTTOM);
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35), RelativeSide.RIGHT);
        return builder.build();
    }

    @NotNull
    @Override
    protected IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        HeatCapacitorHelper builder = HeatCapacitorHelper.forSide(facingSupplier);
        builder.addCapacitor(heatCapacitor = BasicHeatCapacitor.create(HEAT_CAPACITY, INVERSE_CONDUCTION_COEFFICIENT, INVERSE_INSULATION_COEFFICIENT, ambientTemperature, listener), RelativeSide.BACK);
        return builder.build();
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[] { RelativeSide.BACK };
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.drainContainer();
        fuelSlot.fillOrBurn();
        long prev = getEnergyContainer().getEnergy();
        heatCapacitor.handleHeat(getBoost());
        if (canFunction() && getEnergyContainer().getNeeded() > 0L) {
            // 计算流体占比 (0.0 到 1.0)
            double fluidRatio = (double) lavaTank.getFluidAmount() / lavaTank.getCapacity();
            // 使用指数函数实现非线性增长
            // 流体速率：从 1 增长到约 100
            efficiencyMultiplier = (int) Math.max(1, Math.pow(10, fluidRatio * 2));
            int fluidRate = (int) (efficiencyMultiplier * MoreMachineConfig.generators.largeHeatGenerationFluidRate.get());
            if (lavaTank.extract(fluidRate, Action.SIMULATE, AutomationType.INTERNAL).getAmount() == fluidRate) {
                setActive(true);
                lavaTank.extract(fluidRate, Action.EXECUTE, AutomationType.INTERNAL);
                heatCapacitor.handleHeat(MoreMachineConfig.generators.largeHeatGeneration.get() * efficiencyMultiplier);
            } else {
                setActive(false);
            }
        } else {
            setActive(false);
        }
        HeatAPI.HeatTransfer loss = simulate();
        lastTransferLoss = loss.adjacentTransfer();
        lastEnvironmentLoss = loss.environmentTransfer();
        producingEnergy = getEnergyContainer().getEnergy() - prev;
        updateMaxOutputRaw(producingEnergy + MAX_PRODUCTION.getAsLong());
        return sendUpdatePacket;
    }

    @Override
    protected BlockPos offSetOutput(BlockPos from, Direction side) {
        Direction back = getOppositeDirection();
        return from.offset(new Vec3i(back.getStepX(), 1, back.getStepZ())).relative(side);
    }

    private double getBoost() {
        if (level == null) {
            return 0L;
        }
        long boost;
        long passiveLavaAmount = MoreMachineConfig.generators.largeHeatGenerationLava.get();
        if (passiveLavaAmount == 0L) {
            // If neighboring lava blocks produce no energy, don't bother checking the sides for them
            boost = 0L;
        } else {
            // Otherwise, calculate boost to apply from lava
            // Only check and add loaded neighbors to the which sides have lava on them
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            int lavaSides = 0;
            for (int x = -2; x <= 2; x++) {
                for (int y = -1; y <= 3; y++) {
                    for (int z = -2; z <= 2; z++) {
                        if (x != 0 || y != 0 || z != 0) {
                            // 计算有多少个坐标分量在边界上
                            int edgeCount = 0;
                            if (x == -2 || x == 2) edgeCount++;
                            if (y == -1 || y == 3) edgeCount++;
                            if (z == -2 || z == 2) edgeCount++;

                            // 排除棱(edgeCount == 2)和顶点(edgeCount == 3)
                            // 保留内部的点(edgeCount == 0)和面上的点(edgeCount == 1)
                            if (edgeCount <= 1) {
                                mutable.setWithOffset(worldPosition, x, y, z);
                                if (WorldUtils.getFluidState(level, mutable).filter(state -> state.is(FluidTags.LAVA)).isPresent()) {
                                    lavaSides++;
                                }
                            }
                        }
                    }
                }
            }
            if (getBlockState().getFluidState().is(FluidTags.LAVA)) {
                // If the heat generator is lava-logged then add it as another side that is adjacent to lava for the
                // heat calculations
                lavaSides++;
            }
            boost = passiveLavaAmount * lavaSides;
        }
        if (level.dimensionType().ultraWarm()) {
            boost += MoreMachineConfig.generators.largeHeatGenerationNether.get();
        }
        return boost;
    }

    @Override
    public double getInverseInsulation(int capacitor, @Nullable Direction side) {
        return side == Direction.DOWN ? HeatAPI.DEFAULT_INVERSE_INSULATION : super.getInverseInsulation(capacitor, side);
    }

    @Override
    public double getTotalInverseInsulation(@Nullable Direction side) {
        return side == Direction.DOWN ? HeatAPI.DEFAULT_INVERSE_INSULATION : super.getTotalInverseInsulation(side);
    }

    @NotNull
    @Override
    public HeatAPI.HeatTransfer simulate() {
        double ambientTemp = ambientTemperature.getAsDouble();
        double temp = getTotalTemperature();
        // 1 - Qc / Qh
        double carnotEfficiency = 1 - Math.min(ambientTemp, temp) / Math.max(ambientTemp, temp);
        double heatLost = THERMAL_EFFICIENCY * (temp - ambientTemp);
        heatCapacitor.handleHeat(-heatLost);
        long energyFromHeat = MathUtils.clampToLong(Math.abs(heatLost) * carnotEfficiency);
        getEnergyContainer().insert((long) (Math.min(energyFromHeat, MAX_PRODUCTION.getAsLong()) * efficiencyMultiplier), Action.EXECUTE, AutomationType.INTERNAL);
        return super.simulate();
    }

    @Nullable
    @Override
    public IHeatHandler getAdjacent(@NotNull Direction side) {
        return side == Direction.DOWN ? getAdjacentUnchecked(side) : null;
    }

    @Override
    public long getProductionRate() {
        return producingEnergy;
    }

    @ComputerMethod(nameOverride = "getTransferLoss")
    public double getLastTransferLoss() {
        return lastTransferLoss;
    }

    @ComputerMethod(nameOverride = "getEnvironmentalLoss")
    public double getLastEnvironmentLoss() {
        return lastEnvironmentLoss;
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(lavaTank.getFluidAmount(), lavaTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(ContainerType<?, ?, ?> type) {
        return type == ContainerType.FLUID;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableLong.create(this::getProductionRate, value -> producingEnergy = value));
        container.track(SyncableDouble.create(this::getLastTransferLoss, value -> lastTransferLoss = value));
        container.track(SyncableDouble.create(this::getLastEnvironmentLoss, value -> lastEnvironmentLoss = value));
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
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 1, back.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 2, back.getStepZ()))) {
            return getCurrentRedstoneLevel();
        }
        return 0;
    }

    @Override
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, @Nullable Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.FLUID.block()) {
            return Objects.requireNonNull(fluidHandlerManager, "Expected to have fluid handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        } else if (capability == Capabilities.HEAT) {
            return Objects.requireNonNull(heatHandlerManager, "Expected to have heat handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.FLUID.block()) {
            return notFluidPort(side, offset);
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == Capabilities.ITEM.block()) {
            return notItemPort(side, offset);
        } else if (capability == Capabilities.HEAT) {
            return notHeatPort(side, offset);
        }
        return notFluidPort(side, offset) && notHeatPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notHeatPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 2, back.getStepZ()))) {
            return side != back;
        }
        return true;
    }

    private boolean notFluidPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 2, back.getStepZ()))) {
            return side != back;
        }
        return true;
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        // 所有端口都可以与物品管道交互（除了热量）
        return notFluidPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        if (offset.equals(new Vec3i(back.getStepX(), 1, back.getStepZ()))) {
            return side != back;
        }
        return true;
    }
}
