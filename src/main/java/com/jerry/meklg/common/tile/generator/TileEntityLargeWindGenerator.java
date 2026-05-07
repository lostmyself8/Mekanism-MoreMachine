package com.jerry.meklg.common.tile.generator;

import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;

import mekanism.api.*;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TileEntityLargeWindGenerator extends TileEntityMoreMachineGenerator implements IBoundingBlock {

    private static final float SPEED = 32F;
    public static final int TOP_Y = 36;
    public static final int CHUNK_RADIUS = 2;
    // 检测冷却，从1s逐渐提升到10s
    private static final int DETECTION_COOLDOWN_MIN = 20;
    private static final int DETECTION_COOLDOWN_MAX = 200;
    private static final int DETECTION_COOLDOWN_STEP = 20;

    @Getter
    private float angle;
    @Getter
    private double currentMultiplier = 0;
    private boolean isBlacklistDimension;
    private boolean hasSameBlockNearby;
    private int detectionCooldown = DETECTION_COOLDOWN_MIN;
    private int detectionTicker = 0;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;

    public TileEntityLargeWindGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorBlocks.LARGE_WIND_GENERATOR, pos, state, MoreMachineConfig.generators.largeWindGenerationMax);
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier);
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35));
        return builder.build();
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[] { RelativeSide.FRONT, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK };
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.drainContainer();
        // If we're in a blacklisted dimension, there's nothing more to do
        if (isBlacklistDimension) {
            return sendUpdatePacket;
        }
        // 先更新hasSameBlockNearby的值，方便后续使用
        detectionTicker = Math.min(detectionTicker + 1, detectionCooldown);
        if (detectionTicker >= detectionCooldown && canFunction()) {
            detectionTicker = 0;
            hasSameBlockNearby = checkSameBlockNearby();
            detectionCooldown = hasSameBlockNearby ? DETECTION_COOLDOWN_MIN : Math.min(detectionCooldown + DETECTION_COOLDOWN_STEP, DETECTION_COOLDOWN_MAX);
        }
        if (ticker % SharedConstants.TICKS_PER_SECOND == 0) {
            // Recalculate the current multiplier once a second
            currentMultiplier = getMultiplier();
        }
        setActive(canFunction() && currentMultiplier != 0L && !hasSameBlockNearby);
        // 这里不需要判断hasSameBlockNearby，currentMultiplier为0已经包括了这种情况
        if (currentMultiplier != 0L && canFunction() && getEnergyContainer().getNeeded() > 0L) {
            getEnergyContainer().insert(getCurrentGeneration(), Action.EXECUTE, AutomationType.INTERNAL);
        }
        return sendUpdatePacket;
    }

    @Override
    protected int portCount(int input) {
        return 11;
    }

    @Override
    protected BlockPos[] offSetOutput(BlockPos from, Direction side) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = getRightSide();
        return new BlockPos[] {
                // 前
                from.offset(new Vec3i(front.getStepX() * 3, 0, front.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(left.getStepX(), 0, left.getStepZ())).offset(new Vec3i(front.getStepX() * 3, 0, front.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(right.getStepX(), 0, right.getStepZ())).offset(new Vec3i(front.getStepX() * 3, 0, front.getStepZ() * 3)).relative(side),
                // 左
                from.offset(new Vec3i(left.getStepX() * 3, 0, left.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(front.getStepX(), 0, front.getStepZ())).offset(new Vec3i(left.getStepX() * 3, 0, left.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(back.getStepX(), 0, back.getStepZ())).offset(new Vec3i(left.getStepX() * 3, 0, left.getStepZ() * 3)).relative(side),
                // 右
                from.offset(new Vec3i(right.getStepX() * 3, 0, right.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(front.getStepX(), 0, front.getStepZ())).offset(new Vec3i(right.getStepX() * 3, 0, right.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(back.getStepX(), 0, back.getStepZ())).offset(new Vec3i(right.getStepX() * 3, 0, right.getStepZ() * 3)).relative(side),
                // 后
                from.offset(new Vec3i(left.getStepX(), 0, left.getStepZ())).offset(new Vec3i(back.getStepX() * 3, 0, back.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(right.getStepX(), 0, right.getStepZ())).offset(new Vec3i(back.getStepX() * 3, 0, back.getStepZ() * 3)).relative(side),
        };
    }

    public long getCurrentGeneration() {
        return MathUtils.clampToLong(MoreMachineConfig.generators.largeWindGenerationMin.get() * currentMultiplier);
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        if (getActive()) {
            angle = (angle + getHeightSpeedRatio()) % 360;
        }
    }

    public float getHeightSpeedRatio() {
        int height = getBlockPos().getY() + TOP_Y;
        if (level == null) {
            // Fallback to default values, but in general this is not going to happen
            return SPEED * height / 384F;
        }
        // Shift so that a wind generator at the min build height acts as if it was at a height of zero
        int minBuildHeight = level.getMinBuildHeight();
        height -= minBuildHeight;
        return SPEED * height / (level.getMaxBuildHeight() - minBuildHeight);
    }

    /**
     * Determines the current output multiplier, taking sky visibility and height into account.
     **/
    private double getMultiplier() {
        if (level != null) {
            BlockPos top = getBlockPos().above(TOP_Y);
            // Validate it isn't fluid logged to help try and prevent https://github.com/mekanism/Mekanism/issues/7344
            // Clamp the height limits as the logical bounds of the world
            if (canSeeSky(top) && !hasSameBlockNearby) {
                int minBuildHeight = level.getMinBuildHeight();
                // Based off of how PortalForcer#createPortal calculates
                // The minus one is to handle that the max level height is treated as exclusive
                int maxLevelHeight = Math.min(level.getMaxBuildHeight(), minBuildHeight + level.dimensionType().logicalHeight()) - 1;
                int minY = Math.max(MekanismGeneratorsConfig.generators.windGenerationMinY.get(), minBuildHeight);
                int maxY = Math.min(MekanismGeneratorsConfig.generators.windGenerationMaxY.get(), maxLevelHeight);
                int clampedY = Math.min(maxY, Math.max(minY, top.getY()));
                long minG = MoreMachineConfig.generators.largeWindGenerationMin.get();
                long maxG = MoreMachineConfig.generators.largeWindGenerationMax.get();
                double slope = ((double) (maxG - minG)) / (maxY - minY);
                double toGen = minG + (slope * (clampedY - minY));
                return (toGen / minG);
            }
        }
        return 0L;
    }

    /**
     * Checks if there is another Large Wind Generator of the same block type
     * within a 5x5 chunk area (CHUNK_RADIUS chunks in each horizontal direction).
     */
    private boolean checkSameBlockNearby() {
        if (level == null) {
            return false;
        }
        // 5x5 chunks: center chunk +-2 chunks in X and Z
        // 使用移位运算更快
        int selfChunkX = getBlockPos().getX() >> 4;
        int selfChunkZ = getBlockPos().getZ() >> 4;
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();

        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        for (int cx = selfChunkX - CHUNK_RADIUS; cx <= selfChunkX + CHUNK_RADIUS; cx++) {
            for (int cz = selfChunkZ - CHUNK_RADIUS; cz <= selfChunkZ + CHUNK_RADIUS; cz++) {
                // 跳过没加载的区块
                if (!level.isLoaded(candidate.set(cx << 4, getBlockPos().getY(), cz << 4))) {
                    continue;
                }
                int startX = cx << 4;
                int startZ = cz << 4;
                for (int x = startX; x < startX + 16; x++) {
                    for (int z = startZ; z < startZ + 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            candidate.set(x, y, z);
                            // Skip self
                            if (candidate.equals(getBlockPos())) {
                                continue;
                            }
                            if (level.getBlockState(candidate).getBlock() == getBlockState().getBlock()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean canSeeSky(BlockPos pos) {
        if (level != null) {
            return level.getFluidState(pos).isEmpty() && level.canSeeSky(pos);
        }
        return false;
    }

    @Override
    public void setLevel(@NotNull Level world) {
        super.setLevel(world);
        // Check the blacklist and force an update if we're in the blacklist. Otherwise, we'll never send
        // an initial activity status and the client (in MP) will show the windmills turning while not
        // generating any power
        isBlacklistDimension = world.dimensionTypeRegistration().is(MekanismAPITags.DimensionTypes.NO_WIND);
        if (isBlacklistDimension) {
            setActive(false);
        }
    }

    @ComputerMethod(nameOverride = "isBlacklistedDimension")
    public boolean isBlacklistDimension() {
        return isBlacklistDimension;
    }

    @ComputerMethod(nameOverride = "hasSameGeneratorNearby")
    public boolean hasSameGeneratorNearby() {
        return hasSameBlockNearby;
    }

    @Override
    public SoundSource getSoundCategory() {
        return SoundSource.WEATHER;
    }

    @Override
    public BlockPos getSoundPos() {
        return super.getSoundPos().above(TOP_Y - 2);
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableDouble.create(this::getCurrentMultiplier, value -> currentMultiplier = value));
        container.track(SyncableBoolean.create(this::isBlacklistDimension, value -> isBlacklistDimension = value));
        container.track(SyncableBoolean.create(this::hasSameGeneratorNearby, value -> hasSameBlockNearby = value));
    }

    @Override
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == Capabilities.ITEM.block()) {
            return notItemPort(side, offset);
        }
        return notEnergyPort(side, offset);
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        return notEnergyPort(side, offset);
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = getRightSide();
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ() * 3))) {
                    return side != back;
                }
                if (offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ() * 3))) {
                    return side != back;
                }
                if (offset.equals(new Vec3i(left.getStepX() * 3, 0, 0)) || offset.equals(new Vec3i(left.getStepX() * 3, 0, back.getStepZ())) || offset.equals(new Vec3i(left.getStepX() * 3, 0, front.getStepZ()))) {
                    return side != left;
                }
                if (offset.equals(new Vec3i(right.getStepX() * 3, 0, 0)) || offset.equals(new Vec3i(right.getStepX() * 3, 0, back.getStepZ())) || offset.equals(new Vec3i(right.getStepX() * 3, 0, front.getStepZ()))) {
                    return side != right;
                }
                if (offset.equals(new Vec3i(0, 0, front.getStepZ() * 3)) || offset.equals(new Vec3i(left.getStepX(), 0, front.getStepZ() * 3)) || offset.equals(new Vec3i(right.getStepX(), 0, front.getStepZ() * 3))) {
                    return side != front;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX() * 3, 0, left.getStepZ()))) {
                    return side != back;
                }
                if (offset.equals(new Vec3i(back.getStepX() * 3, 0, right.getStepZ()))) {
                    return side != back;
                }
                if (offset.equals(new Vec3i(0, 0, left.getStepZ() * 3)) || offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ() * 3)) || offset.equals(new Vec3i(front.getStepX(), 0, left.getStepZ() * 3))) {
                    return side != left;
                }
                if (offset.equals(new Vec3i(0, 0, right.getStepZ() * 3)) || offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ() * 3)) || offset.equals(new Vec3i(front.getStepX(), 0, right.getStepZ() * 3))) {
                    return side != right;
                }
                if (offset.equals(new Vec3i(front.getStepX() * 3, 0, 0)) || offset.equals(new Vec3i(front.getStepX() * 3, 0, left.getStepZ())) || offset.equals(new Vec3i(front.getStepX() * 3, 0, right.getStepZ()))) {
                    return side != front;
                }
            }
        }
        return true;
    }

    // Methods relating to IComputerTile
    @Override
    protected long getProductionRate() {
        return getActive() ? getCurrentGeneration() : 0L;
    }
    // End methods IComputerTile
}
