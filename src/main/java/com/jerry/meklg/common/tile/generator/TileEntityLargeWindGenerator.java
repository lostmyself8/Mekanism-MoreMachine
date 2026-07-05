package com.jerry.meklg.common.tile.generator;

import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.MekanismAPITags;
import mekanism.api.RelativeSide;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TileEntityLargeWindGenerator extends TileEntityMoreMachineGenerator implements IBoundingBlock {

    private static final float SPEED = 32.0F;
    public static final int TOP_Y = 36;
    public static final int CHUNK_RADIUS = 2;
    private static final int DETECTION_COOLDOWN_MIN = 20;
    private static final int DETECTION_COOLDOWN_MAX = 200;
    private static final int DETECTION_COOLDOWN_STEP = 20;

    private static final RelativeSide[] ENERGY_SIDES = new RelativeSide[] { RelativeSide.FRONT, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK };

    @Getter
    private float angle;
    @Getter
    private double currentMultiplier = 0.0F;
    private boolean isBlacklistDimension;
    private boolean hasSameBlockNearby;
    private int detectionCooldown = DETECTION_COOLDOWN_MIN;
    private int detectionTicker = 0;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = { "getEnergyItem" }, docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;

    public TileEntityLargeWindGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorBlocks.LARGE_WIND_GENERATOR, pos, state);
        updateMaxOutput();
    }

    @NotNull
    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSide(facingSupplier);
        builder.addContainer(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35));
        return builder.build();
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return ENERGY_SIDES;
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        energySlot.drainContainerIntoSlot(null);
        // If we're in a blacklisted dimension, there's nothing more to do
        if (isBlacklistDimension) {
            return sendUpdatePacket;
        }
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
        if (currentMultiplier != 0L && canFunction() && !hasSameBlockNearby && getEnergyContainer().getNeededAsLong() > 0L) {
            try (Transaction transaction = Transaction.openRoot()) {
                getEnergyContainer().insert(MathUtils.clampToInt(getCurrentGeneration()), transaction, AutomationType.INTERNAL);
                transaction.commit();
            }
        }
        return sendUpdatePacket;
    }

    @Override
    protected int portCount(int input) {
        return 11;
    }

    @Override
    protected BlockPos[] offSetOutputs(BlockPos from, Direction side) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = getRightSide();
        return new BlockPos[] {
                // Front energy ports.
                from.offset(new Vec3i(front.getStepX() * 3, 0, front.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(left.getStepX(), 0, left.getStepZ())).offset(new Vec3i(front.getStepX() * 3, 0, front.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(right.getStepX(), 0, right.getStepZ())).offset(new Vec3i(front.getStepX() * 3, 0, front.getStepZ() * 3)).relative(side),
                // Left energy ports.
                from.offset(new Vec3i(left.getStepX() * 3, 0, left.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(front.getStepX(), 0, front.getStepZ())).offset(new Vec3i(left.getStepX() * 3, 0, left.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(back.getStepX(), 0, back.getStepZ())).offset(new Vec3i(left.getStepX() * 3, 0, left.getStepZ() * 3)).relative(side),
                // Right energy ports.
                from.offset(new Vec3i(right.getStepX() * 3, 0, right.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(front.getStepX(), 0, front.getStepZ())).offset(new Vec3i(right.getStepX() * 3, 0, right.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(back.getStepX(), 0, back.getStepZ())).offset(new Vec3i(right.getStepX() * 3, 0, right.getStepZ() * 3)).relative(side),
                // Back energy ports.
                from.offset(new Vec3i(left.getStepX(), 0, left.getStepZ())).offset(new Vec3i(back.getStepX() * 3, 0, back.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(right.getStepX(), 0, right.getStepZ())).offset(new Vec3i(back.getStepX() * 3, 0, back.getStepZ() * 3)).relative(side),
        };
    }

    public long getCurrentGeneration() {
        return MathUtils.clampToLong(MoreMachineConfig.generators.largeWindGenerationMin.get() * currentMultiplier);
    }

    @Override
    protected void onUpdateClient(Level level) {
        super.onUpdateClient(level);
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
        int minBuildHeight = level.getMinY();
        height -= minBuildHeight;
        return SPEED * height / (level.getMaxY() + 1 - minBuildHeight);
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
                int minBuildHeight = level.getMinY();
                // Based off of how PortalForcer#createPortal calculates
                // The minus one is to handle that the max level height is treated as exclusive
                int maxLevelHeight = Math.min(level.getMaxY() + 1, minBuildHeight + level.dimensionType().logicalHeight()) - 1;
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

    private boolean checkSameBlockNearby() {
        if (level == null) {
            return false;
        }
        int selfChunkX = getBlockPos().getX() >> 4;
        int selfChunkZ = getBlockPos().getZ() >> 4;
        int minY = level.getMinY();
        int maxY = level.getMaxY() + 1;

        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        for (int cx = selfChunkX - CHUNK_RADIUS; cx <= selfChunkX + CHUNK_RADIUS; cx++) {
            for (int cz = selfChunkZ - CHUNK_RADIUS; cz <= selfChunkZ + CHUNK_RADIUS; cz++) {
                if (!level.isLoaded(candidate.set(cx << 4, getBlockPos().getY(), cz << 4))) {
                    continue;
                }
                int startX = cx << 4;
                int startZ = cz << 4;
                for (int x = startX; x < startX + 16; x++) {
                    for (int z = startZ; z < startZ + 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            candidate.set(x, y, z);
                            if (!candidate.equals(getBlockPos()) && level.getBlockState(candidate).getBlock() == getBlockState().getBlock()) {
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
        return level != null && level.getFluidState(pos).isEmpty() && level.canSeeSky(pos);
    }

    @Override
    public void setLevel(@NotNull Level world) {
        super.setLevel(world);
        // Check the blacklist and force an update if we're in the blacklist. Otherwise, we'll never send
        // an initial activity status and the client (in MP) will show the windmills turning while not
        // generating any power
        updateMaxOutput();
        isBlacklistDimension = world.dimensionTypeRegistration().is(MekanismAPITags.DimensionTypes.NO_WIND);
        if (isBlacklistDimension) {
            setActive(false);
        }
    }

    private void updateMaxOutput() {
        updateMaxOutputRaw(MathUtils.multiplyClamped(MoreMachineConfig.generators.largeWindGenerationMax.get(), 2));
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
        container.track(SyncableDouble.create(this::getCurrentMultiplier, v -> currentMultiplier = v));
        container.track(SyncableBoolean.create(this::isBlacklistDimension, v -> isBlacklistDimension = v));
        container.track(SyncableBoolean.create(this::hasSameGeneratorNearby, v -> hasSameBlockNearby = v));
    }

    @Override
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, @Nullable Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
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
