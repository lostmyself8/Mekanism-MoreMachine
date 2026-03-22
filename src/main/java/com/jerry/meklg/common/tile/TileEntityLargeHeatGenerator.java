package com.jerry.meklg.common.tile;

import com.jerry.mekmm.api.ITileEntityMekanismAccessor;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.math.FloatingLong;
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
import mekanism.common.config.listener.ConfigBasedCachedFLSupplier;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.slot.FluidFuelInventorySlot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import com.jerry.meklg.common.registries.LargeGeneratorsBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TileEntityLargeHeatGenerator extends TileEntityMoreGenerator implements IBoundingBlock {

    public static final double HEAT_CAPACITY = 10;
    public static final double INVERSE_CONDUCTION_COEFFICIENT = 5;
    public static final double INVERSE_INSULATION_COEFFICIENT = 100;
    private static final double THERMAL_EFFICIENCY = 0.5;

    // Default configs this is 510 compared to the previous 500
    private static final ConfigBasedCachedFLSupplier MAX_PRODUCTION = new ConfigBasedCachedFLSupplier(() -> {
        FloatingLong passiveMax = MekanismGeneratorsConfig.generators.heatGenerationLava.get().multiply(81);
        passiveMax = passiveMax.plusEqual(MekanismGeneratorsConfig.generators.heatGenerationNether.get());
        return passiveMax.plusEqual(MekanismGeneratorsConfig.generators.heatGeneration.get());
    }, MekanismGeneratorsConfig.generators.heatGeneration, MekanismGeneratorsConfig.generators.heatGenerationLava, MekanismGeneratorsConfig.generators.heatGenerationNether);

    /**
     * The FluidTank for this generator.
     */
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = { "getLava", "getLavaCapacity", "getLavaNeeded", "getLavaFilledPercentage" }, docPlaceholder = "lava tank")
    public BasicFluidTank lavaTank;
    private FloatingLong producingEnergy = FloatingLong.ZERO;
    private double efficiencyMultiplier = 1.0;
    private double lastTransferLoss;
    private double lastEnvironmentLoss;
    private int numPowering;

    @WrappingComputerMethod(wrapper = ComputerHeatCapacitorWrapper.class, methodNames = "getTemperature", docPlaceholder = "generator")
    BasicHeatCapacitor heatCapacitor;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFuelItem", docPlaceholder = "fuel item slot")
    FluidFuelInventorySlot fuelSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;

    public TileEntityLargeHeatGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorsBlocks.LARGE_HEAT_GENERATOR, pos, state, MAX_PRODUCTION);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSide(this::getDirection);
        builder.addTank(lavaTank = VariableCapacityFluidTank.input(MekanismGeneratorsConfig.generators.heatTankCapacity,
                fluidStack -> MekanismTags.Fluids.LAVA_LOOKUP.contains(fluidStack.getFluid()), listener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        // Divide the burn time by 20 as that is the ratio of how much a bucket of lava would burn for
        // Eventually we may want to grab the 20 dynamically in case some mod is changing the burn time of a lava bucket
        builder.addSlot(fuelSlot = FluidFuelInventorySlot.forFuel(lavaTank, stack -> ForgeHooks.getBurnTime(stack, null) / 20, size -> new FluidStack(Fluids.LAVA, size),
                listener, 17, 35), RelativeSide.BACK);
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        HeatCapacitorHelper builder = HeatCapacitorHelper.forSide(this::getDirection);
        builder.addCapacitor(heatCapacitor = BasicHeatCapacitor.create(HEAT_CAPACITY, INVERSE_CONDUCTION_COEFFICIENT, INVERSE_INSULATION_COEFFICIENT, ambientTemperature, listener), RelativeSide.BACK);
        return builder.build();
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[] { RelativeSide.BACK };
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.drainContainer();
        fuelSlot.fillOrBurn();
        FloatingLong prev = getEnergyContainer().getEnergy().copyAsConst();
        heatCapacitor.handleHeat(getBoost().doubleValue());
        if (MekanismUtils.canFunction(this) && !getEnergyContainer().getNeeded().isZero()) {
            // 计算流体占比 (0.0 到 1.0)
            double fluidRatio = (double) lavaTank.getFluidAmount() / lavaTank.getCapacity();
            // 使用指数函数实现非线性增长
            // 流体速率：从 1 增长到约 100
            efficiencyMultiplier = (int) Math.max(1, Math.pow(10, fluidRatio * 2));
            int fluidRate = (int) (efficiencyMultiplier * MekanismGeneratorsConfig.generators.heatGenerationFluidRate.get());
            if (lavaTank.extract(fluidRate, Action.SIMULATE, AutomationType.INTERNAL).getAmount() == fluidRate) {
                setActive(true);
                lavaTank.extract(fluidRate, Action.EXECUTE, AutomationType.INTERNAL);
                heatCapacitor.handleHeat(MekanismGeneratorsConfig.generators.heatGeneration.get().doubleValue() * efficiencyMultiplier);
            } else {
                setActive(false);
            }
        } else {
            setActive(false);
        }
        HeatAPI.HeatTransfer loss = simulate();
        lastTransferLoss = loss.adjacentTransfer();
        lastEnvironmentLoss = loss.environmentTransfer();
        producingEnergy = getEnergyContainer().getEnergy().subtract(prev);
    }

    protected List<BlockEntity> getEjectEntity(RelativeSide side, Direction direction) {
        List<BlockEntity> ejectEntity = new ArrayList<>();
        ejectEntity.add(WorldUtils.getTileEntity(getLevel(), worldPosition.relative(direction).above()));
        return ejectEntity;
    }

    private FloatingLong getBoost() {
        if (level == null) {
            return FloatingLong.ZERO;
        }
        FloatingLong boost;
        FloatingLong passiveLavaAmount = MekanismGeneratorsConfig.generators.heatGenerationLava.get();
        if (passiveLavaAmount.isZero()) {
            // If neighboring lava blocks produce no energy, don't bother checking the sides for them
            boost = FloatingLong.ZERO;
        } else {
            // Otherwise, calculate boost to apply from lava
            MutableBlockPos mutable = new MutableBlockPos();
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
            boost = passiveLavaAmount.multiply(lavaSides);
        }
        if (level.dimensionType().ultraWarm()) {
            boost = boost.plusEqual(MekanismGeneratorsConfig.generators.heatGenerationNether.get());
        }
        return boost;
    }

    @Override
    public double getInverseInsulation(int capacitor, @Nullable Direction side) {
        return side == Direction.DOWN ? 0 : super.getInverseInsulation(capacitor, side);
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
        FloatingLong energyFromHeat = FloatingLong.create(Math.abs(heatLost) * carnotEfficiency);
        getEnergyContainer().insert(energyFromHeat.min(MAX_PRODUCTION.get()).multiply(efficiencyMultiplier), Action.EXECUTE, AutomationType.INTERNAL);
        return super.simulate();
    }

    @Nullable
    @Override
    public IHeatHandler getAdjacent(@NotNull Direction side) {
        if (side == Direction.DOWN) {
            BlockEntity adj = WorldUtils.getTileEntity(getLevel(), worldPosition.below());
            return CapabilityUtils.getCapability(adj, Capabilities.HEAT_HANDLER, side.getOpposite()).resolve().orElse(null);
        }
        return null;
    }

    @Override
    public FloatingLong getProductionRate() {
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
    protected boolean makesComparatorDirty(@Nullable SubstanceType type) {
        return type == SubstanceType.FLUID;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableFloatingLong.create(this::getProductionRate, value -> producingEnergy = value));
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
    public @NotNull <T> LazyOptional<T> getOffsetCapabilityIfEnabled(@NotNull Capability<T> capability, Direction side, @NotNull Vec3i offset) {
        if (this instanceof ITileEntityMekanismAccessor accessor) {
            if (capability == ForgeCapabilities.FLUID_HANDLER) {
                return accessor.getFluidHandlerManager().resolve(capability, side);
            } else if (capability == Capabilities.HEAT_HANDLER) {
                return accessor.getHeatHandlerManager().resolve(capability, side);
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
        } else if (capability == Capabilities.HEAT_HANDLER) {
            return notHeatPort(side, offset);
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return notItemPort(side, offset);
        } else if (canEverResolve(capability) && IBoundingBlock.super.isOffsetCapabilityDisabled(capability, side, offset)) {
            return notItemPort(side, offset);
        }
        return false;
    }

    private boolean notHeatPort(Direction side, Vec3i offset) {
        return notFluidPort(side, offset);
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
