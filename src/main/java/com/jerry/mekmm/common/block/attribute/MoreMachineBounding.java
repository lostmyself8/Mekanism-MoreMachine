package com.jerry.mekmm.common.block.attribute;

import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeHasBounding;
import mekanism.common.block.attribute.AttributeHasBounding.HandleBoundingBlock;
import mekanism.common.block.attribute.AttributeHasBounding.TriBooleanFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

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
}
