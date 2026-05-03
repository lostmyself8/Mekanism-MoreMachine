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

    public static final AttributeHasBounding LARGE_WIND_GENERATOR = new AttributeHasBounding(new HandleBoundingBlock() {

        @Override
        public <DATA> boolean handle(Level level, BlockPos pos, BlockState state, DATA data, TriBooleanFunction<Level, BlockPos, DATA> predicate) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            Direction facing = Attribute.getFacing(state);
            if (facing == null) return false;
            // 获取垂直于朝向的水平轴
            Vec3i axis = facing.getClockWise().getUnitVec3i();
            // 朝向的前方
            Vec3i front = facing.getOpposite().getUnitVec3i();
            // 底座
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
            // 底座上一层
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == -3 || x == 3 || z == -3 || z == 3) {
                        if ((x == -3 || x == -2 || x == 2 || x == 3) &&
                                (z == -3 || z == -2 || z == 2 || z == 3)) {
                            continue;
                        }
                    }
                    mutable.setWithOffset(pos, x, 1, z);
                    if (!predicate.accept(level, mutable, data)) {
                        return false;
                    }
                }
            }
            // 柱子一层
            for (int i = -1; i <= 1; i++) {
                mutable.setWithOffset(pos, front.getX() * 3 + axis.getX() * i, 2, front.getZ() * 3 + axis.getZ() * i);
                if (!predicate.accept(level, mutable, data)) {
                    return false;
                }
            }
            // 柱子二到十三层
            for (int x = -2; x <= 2; x++) {
                for (int y = 2; y <= 13; y++) {
                    for (int z = -2; z <= 2; z++) {
                        mutable.setWithOffset(pos, x, y, z);
                        if (!predicate.accept(level, mutable, data)) {
                            return false;
                        }
                    }
                }
            }
            // 柱子十四到三十层
            for (int x = -1; x <= 1; x++) {
                for (int y = 14; y <= 30; y++) {
                    for (int z = -1; z <= 1; z++) {
                        mutable.setWithOffset(pos, x, y, z);
                        if (!predicate.accept(level, mutable, data)) {
                            return false;
                        }
                    }
                }
            }
            // 三十一层（朝向前方延伸，前方多一格，后方少一格）
            int fx = front.getX(), fz = front.getZ();
            int ax = axis.getX(), az = axis.getZ();
            for (int a = -2; a <= 2; a++) {       // 侧轴 -2~2
                for (int f = -2; f <= 4; f++) {   // 前后轴 -2(后)~4(前)
                    if (f == 4 && (a == 2 || a == -2)) continue;
                    mutable.setWithOffset(pos, fx * f + ax * a, 31, fz * f + az * a);
                    if (!predicate.accept(level, mutable, data)) {
                        return false;
                    }
                }
            }
            // 三十二到三十五层
            for (int a = -2; a <= 2; a++) {
                for (int y = 32; y <= 35; y++) {
                    for (int f = -3; f <= 4; f++) {
                        if (a == -2 || a == 2 || f == -3 || f == 4) {
                            if ((a == -2 || a == 2) && (f == -3 || f == 4)) continue;
                        }
                        mutable.setWithOffset(pos, fx * f + ax * a, y, fz * f + az * a);
                        if (!predicate.accept(level, mutable, data)) {
                            return false;
                        }
                    }
                }
            }
            // 三十二到三十四层，后方突出部分
            for (int i = -1; i <= 1; i++) {
                for (int y = 32; y <= 34; y++) {
                    mutable.setWithOffset(pos, fx * 5 + ax * i, y, fz * 5 + az * i);
                    if (!predicate.accept(level, mutable, data)) {
                        return false;
                    }
                }
            }
            // 三十六层中心 3x3
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    mutable.setWithOffset(pos, x, 36, z);
                    if (!predicate.accept(level, mutable, data)) {
                        return false;
                    }
                }
            }
            // 三十六层，后方两格
            mutable.setWithOffset(pos, fx * 3, 36, fz * 3);
            if (!predicate.accept(level, mutable, data)) {
                return false;
            }
            mutable.setWithOffset(pos, fx * 4, 36, fz * 4);
            return predicate.accept(level, mutable, data);
        }
    });
}
