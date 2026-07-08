package com.jerry.meklg.common.block.attribute;

import com.jerry.mekmm.Mekmm;

import mekanism.common.block.BlockBounding;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeHasBounding;
import mekanism.common.block.attribute.AttributeHasBounding.HandleBoundingBlock;
import mekanism.common.block.attribute.AttributeHasBounding.TriBooleanFunction;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.registries.MekanismBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.jerry.meklg.common.block.BlockLargeWindGeneratorProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class AttributeLargeWindGeneratorProxy extends AttributeHasBounding {

    private static final int MAX_HORIZONTAL_OFFSET = 5;
    private static final int MAX_VERTICAL_OFFSET = 36;

    private Supplier<BlockState> proxyStateSupplier;

    public AttributeLargeWindGeneratorProxy(HandleBoundingBlock boundingPosHandlers) {
        super(boundingPosHandlers);
    }

    public AttributeLargeWindGeneratorProxy withProxyState(Supplier<BlockState> proxyStateSupplier) {
        this.proxyStateSupplier = Objects.requireNonNull(proxyStateSupplier, "Large wind generator proxy state supplier cannot be null");
        return this;
    }

    public AttributeLargeWindGeneratorProxy withProxyBlock(Supplier<? extends Block> proxyBlockSupplier) {
        Objects.requireNonNull(proxyBlockSupplier, "Large wind generator proxy block supplier cannot be null");
        return withProxyState(() -> proxyBlockSupplier.get().defaultBlockState());
    }

    @Override
    public void placeBoundingBlocks(Level world, BlockPos orig, BlockState state) {
        Direction facing = Attribute.getFacing(state);
        if (facing == null || !facing.getAxis().isHorizontal()) {
            Mekmm.LOGGER.warn("Unable to place large wind generator proxy blocks at {} in {}; invalid facing {}", orig,
                    world.dimension().identifier(), facing);
            return;
        }
        SyncStats stats = new SyncStats();
        handle(world, orig, state, new PlacementData(world, orig, facing), (level, boundingLocation, data) -> {
            stats.expected++;
            if (setProxyBlock(level, boundingLocation, data)) {
                stats.placed++;
            } else {
                stats.skipped++;
            }
            return true;
        });
        logStats("place", world, orig, stats);
    }

    @Override
    public void removeBoundingBlocks(Level world, BlockPos pos, BlockState state) {
        handle(world, pos, state, null, (level, boundingLocation, data) -> {
            BlockState boundingState = level.getBlockState(boundingLocation);
            if (!boundingState.isAir()) {
                if (boundingState.getBlock() instanceof BlockLargeWindGeneratorProxy) {
                    level.invalidateCapabilities(boundingLocation);
                    BlockLargeWindGeneratorProxy.removeProxyBlock(level, boundingLocation);
                } else {
                    Mekmm.LOGGER.warn("Skipping removing block, expected large wind generator proxy block but the block at {} in {} was {}",
                            boundingLocation, level.dimension().identifier(), boundingState.typeHolder().getRegisteredName());
                }
            }
            return true;
        });
    }

    @Override
    public void syncMasterPosition(Level world, BlockPos orig, BlockState state) {
        if (!world.isClientSide()) {
            Direction facing = Attribute.getFacing(state);
            if (facing == null || !facing.getAxis().isHorizontal()) {
                Mekmm.LOGGER.warn("Unable to sync large wind generator proxy blocks at {} in {}; invalid facing {}", orig,
                        world.dimension().identifier(), facing);
                return;
            }
            Set<BlockPos> expectedLocations = new HashSet<>();
            SyncStats stats = new SyncStats();
            handle(world, orig, state, new PlacementData(world, orig, facing), (level, boundingLocation, data) -> {
                stats.expected++;
                expectedLocations.add(boundingLocation.immutable());
                BlockState boundingState = level.getBlockState(boundingLocation);
                if (boundingState.getBlock() instanceof BlockLargeWindGeneratorProxy) {
                    BlockState proxyState = createProxyState(data, boundingLocation);
                    if (boundingState != proxyState) {
                        if (setProxyBlock(level, boundingLocation, data)) {
                            stats.replaced++;
                        } else {
                            stats.skipped++;
                        }
                    } else {
                        stats.skipped++;
                    }
                    level.invalidateCapabilities(boundingLocation);
                } else if (boundingState.isAir()) {
                    if (setProxyBlock(level, boundingLocation, data)) {
                        stats.placed++;
                    } else {
                        stats.skipped++;
                    }
                } else if (isOwnedMekanismBoundingBlock(level, boundingLocation, data.mainPos())) {
                    if (replaceOwnedMekanismBoundingBlock(level, boundingLocation, data)) {
                        stats.migrated++;
                    } else {
                        stats.skipped++;
                    }
                } else {
                    stats.skipped++;
                }
                return true;
            });
            stats.removed = removeStaleProxyBlocks(world, orig, expectedLocations);
            logStats("sync", world, orig, stats);
        }
    }

    @Override
    public <DATA> boolean handle(Level level, BlockPos pos, BlockState state, DATA data, TriBooleanFunction<Level, BlockPos, DATA> predicate) {
        return super.handle(level, pos, state, data, predicate);
    }

    private BlockState createProxyState(PlacementData data, BlockPos boundingLocation) {
        Supplier<BlockState> supplier = Objects.requireNonNull(proxyStateSupplier,
                "Large wind generator proxy state supplier has not been configured");
        BlockState state = BlockLargeWindGeneratorProxy.applyProxyData(supplier.get(), data.facing(), boundingLocation.subtract(data.mainPos()));
        return BlockStateHelper.getStateForPlacement(state, data.level(), boundingLocation, null, Direction.NORTH);
    }

    private boolean setProxyBlock(Level level, BlockPos boundingLocation, PlacementData data) {
        if (level.setBlockAndUpdate(boundingLocation, createProxyState(data, boundingLocation))) {
            level.invalidateCapabilities(boundingLocation);
            return true;
        }
        Mekmm.LOGGER.warn("Unable to set large wind generator proxy block at {} in {}", boundingLocation, level.dimension().identifier());
        return false;
    }

    private boolean replaceOwnedMekanismBoundingBlock(Level level, BlockPos boundingLocation, PlacementData data) {
        level.removeBlockEntity(boundingLocation);
        return setProxyBlock(level, boundingLocation, data);
    }

    private boolean isOwnedMekanismBoundingBlock(Level level, BlockPos boundingLocation, BlockPos mainPos) {
        BlockState boundingState = level.getBlockState(boundingLocation);
        return boundingState.is(MekanismBlocks.BOUNDING_BLOCK) && mainPos.equals(BlockBounding.getMainBlockPos(level, boundingLocation));
    }

    private int removeStaleProxyBlocks(Level level, BlockPos mainPos, Set<BlockPos> expectedLocations) {
        int removed = 0;
        for (int x = -MAX_HORIZONTAL_OFFSET; x <= MAX_HORIZONTAL_OFFSET; x++) {
            for (int y = 0; y <= MAX_VERTICAL_OFFSET; y++) {
                for (int z = -MAX_HORIZONTAL_OFFSET; z <= MAX_HORIZONTAL_OFFSET; z++) {
                    BlockPos boundingLocation = mainPos.offset(x, y, z);
                    if (!expectedLocations.contains(boundingLocation)) {
                        if (removeIfOwnedProxyBlock(level, boundingLocation, mainPos)) {
                            removed++;
                        }
                    }
                }
            }
        }
        return removed;
    }

    private boolean removeIfOwnedProxyBlock(Level level, BlockPos boundingLocation, BlockPos mainPos) {
        BlockState boundingState = level.getBlockState(boundingLocation);
        if (boundingState.getBlock() instanceof BlockLargeWindGeneratorProxy && mainPos.equals(BlockLargeWindGeneratorProxy.getMainBlockPos(boundingState, boundingLocation))) {
            level.invalidateCapabilities(boundingLocation);
            BlockLargeWindGeneratorProxy.removeProxyBlock(level, boundingLocation);
            return true;
        }
        return false;
    }

    private void logStats(String operation, Level level, BlockPos mainPos, SyncStats stats) {
        if (!level.isClientSide()) {
            Mekmm.LOGGER.info("Large wind generator proxy {} at {} in {}: expected={}, placed={}, replaced={}, migrated={}, skipped={}, removed={}",
                    operation, mainPos, level.dimension().identifier(), stats.expected, stats.placed, stats.replaced, stats.migrated, stats.skipped, stats.removed);
        }
    }

    private record PlacementData(Level level, BlockPos mainPos, Direction facing) {}

    private static final class SyncStats {

        private int expected;
        private int placed;
        private int replaced;
        private int migrated;
        private int skipped;
        private int removed;
    }
}
