package com.jerry.mekmm.common.block.attribute;

import mekanism.api.RelativeSide;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeHasBounding;
import mekanism.common.block.attribute.AttributeHasBounding.HandleBoundingBlock;
import mekanism.common.block.attribute.AttributeHasBounding.TriBooleanFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.jerry.meklg.common.block.attribute.AttributeLargeWindGeneratorProxy;
import com.jerry.meklg.common.content.blocktype.LargeGeneratorBlockShapes;

import java.util.Map;
import java.util.Objects;

public class MoreMachineBounding {

    public static final AttributeHasBounding VERTICAL_THREE_BLOCK = new AttributeHasBounding(new HandleBoundingBlock() {

        @Override
        public <DATA> boolean handle(Level level, BlockPos pos, BlockState state, DATA data, TriBooleanFunction<Level, BlockPos, DATA> consumer) {
            return consumer.accept(level, pos.above(), data) && consumer.accept(level, pos.above().above(), data);
        }
    });

    public static final AttributeHasBounding FULL_JAVA_ENTITY = new AttributeHasBounding(new HandleBoundingBlock() {

        @Override
        public <DATA> boolean handle(Level level, BlockPos pos, BlockState state, DATA data, TriBooleanFunction<Level, BlockPos, DATA> predicate) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 2; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x != 0 || y != 0 || z != 0) {
                            mutable.setWithOffset(pos, x, y, z);
                            if (!predicate.accept(level, mutable, data)) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
    });

    private static final Map<Direction, int[]> TOP_LAYER_RANGES = Map.of(
            Direction.WEST, new int[] { 0, 1, -1, 1 },  // xStart, xEnd, zStart, zEnd
            Direction.EAST, new int[] { -1, 0, -1, 1 },
            Direction.NORTH, new int[] { -1, 1, 0, 1 },
            Direction.SOUTH, new int[] { -1, 1, -1, 0 });

    // 3x3x3但是顶层只有靠后的2x3区域
    public static final AttributeHasBounding FULL_JAVA_ENTITY_BUT_TOP_BACK_2X3 = new AttributeHasBounding(new HandleBoundingBlock() {

        @Override
        public <DATA> boolean handle(Level level, BlockPos pos, BlockState state, DATA data, TriBooleanFunction<Level, BlockPos, DATA> predicate) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x != 0 || y != 0 || z != 0) {
                            mutable.setWithOffset(pos, x, y, z);
                            if (!predicate.accept(level, mutable, data)) {
                                return false;
                            }
                        }
                    }
                }
            }
            Direction facing = Attribute.getFacing(state);
            int[] ranges = TOP_LAYER_RANGES.get(facing);
            if (ranges != null) {
                for (int x = ranges[0]; x <= ranges[1]; x++) {
                    for (int z = ranges[2]; z <= ranges[3]; z++) {
                        mutable.setWithOffset(pos, x, 2, z);
                        if (!predicate.accept(level, mutable, data)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    });

    public static final AttributeHasBounding LARGE_PIGMENT_MIXER = new AttributeHasBounding(new HandleBoundingBlock() {

        @Override
        public <DATA> boolean handle(Level level, BlockPos pos, BlockState state, DATA data, TriBooleanFunction<Level, BlockPos, DATA> predicate) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x != 0 || y != 0 || z != 0) {
                            mutable.setWithOffset(pos, x, y, z);
                            if (!predicate.accept(level, mutable, data)) {
                                return false;
                            }
                        }
                    }
                }
            }
            Direction back = RelativeSide.BACK.getDirection(Objects.requireNonNull(Attribute.getFacing(state)));
            mutable.setWithOffset(pos, 0, 2, 0);
            if (!predicate.accept(level, mutable, data)) {
                return false;
            }
            mutable.setWithOffset(pos, back.getStepX(), 2, back.getStepZ());
            return predicate.accept(level, mutable, data);
        }
    });

    public static final AttributeLargeWindGeneratorProxy LARGE_WIND_GENERATOR = new AttributeLargeWindGeneratorProxy(new HandleBoundingBlock() {

        @Override
        public <DATA> boolean handle(Level level, BlockPos pos, BlockState state, DATA data, TriBooleanFunction<Level, BlockPos, DATA> predicate) {
            Direction facing = Attribute.getFacing(state);
            if (facing == null || !facing.getAxis().isHorizontal()) {
                return false;
            }
            for (BlockPos offset : LargeGeneratorBlockShapes.getLargeWindGeneratorPartOffsets(facing)) {
                if (!BlockPos.ZERO.equals(offset) && !predicate.accept(level, pos.offset(offset), data)) {
                    return false;
                }
            }
            return true;
        }
    });

    public static final AttributeHasBounding SOLAR_HEAT_GENERATOR = new AttributeHasBounding(new HandleBoundingBlock() {

        @Override
        public <DATA> boolean handle(Level level, BlockPos pos, BlockState state, DATA data, TriBooleanFunction<Level, BlockPos, DATA> predicate) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            Direction facing = Attribute.getFacing(state);
            if (facing == null) {
                return false;
            }
            Direction left = facing.getClockWise();
            Direction right = facing.getCounterClockWise();
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x != 0 || z != 0) {
                        mutable.setWithOffset(pos, x, 0, z);
                        if (!predicate.accept(level, mutable, data)) {
                            return false;
                        }
                    }
                }
            }
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == -3 || x == 3 || z == -3 || z == 3) {
                        if ((x == -3 || x == -2 || x == 2 || x == 3) && (z == -3 || z == -2 || z == 2 || z == 3)) {
                            continue;
                        }
                    }
                    mutable.setWithOffset(pos, x, 1, z);
                    if (!predicate.accept(level, mutable, data)) {
                        return false;
                    }
                }
            }
            for (int i = -1; i <= 1; i++) {
                if (!handlePanel(level, pos, data, predicate, mutable, facing, i) ||
                        !handlePanel(level, pos, data, predicate, mutable, left, i) ||
                        !handlePanel(level, pos, data, predicate, mutable, right, i)) {
                    return false;
                }
            }
            for (int x = -3; x <= 3; x++) {
                for (int y = 3; y <= 6; y++) {
                    for (int z = -3; z <= 3; z++) {
                        mutable.setWithOffset(pos, x, y, z);
                        if (!predicate.accept(level, mutable, data)) {
                            return false;
                        }
                    }
                }
            }
            mutable.setWithOffset(pos, 0, 3, 0);
            return predicate.accept(level, mutable, data);
        }

        private <DATA> boolean handlePanel(Level level, BlockPos pos, DATA data, TriBooleanFunction<Level, BlockPos, DATA> predicate,
                                           BlockPos.MutableBlockPos mutable, Direction side, int offset) {
            Vec3i forward = side.getUnitVec3i();
            Vec3i axis = side.getClockWise().getUnitVec3i();
            mutable.setWithOffset(pos, forward.getX() * 3 + axis.getX() * offset, 2, forward.getZ() * 3 + axis.getZ() * offset);
            return predicate.accept(level, mutable, data);
        }
    });
}
