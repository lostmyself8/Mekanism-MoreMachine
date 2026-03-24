package com.jerry.mekmm.common.block.attribute;

import mekanism.api.RelativeSide;
import mekanism.api.functions.TriConsumer;
import mekanism.common.block.attribute.Attribute;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream.Builder;

public class MoreMachineBounding {

    public static final TriConsumer<BlockPos, BlockState, Builder<BlockPos>> FULL_3X3X2 = (pos, state, builder) -> {
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        builder.add(pos.offset(x, y, z));
                    }
                }
            }
        }
    };

    public static final TriConsumer<BlockPos, BlockState, Builder<BlockPos>> FULL_JAVA_ENTITY = (pos, state, builder) -> {
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        builder.add(pos.offset(x, y, z));
                    }
                }
            }
        }
    };

    public static final TriConsumer<BlockPos, BlockState, Builder<BlockPos>> LARGE_PIGMENT_MIXER = (pos, state, builder) -> {
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        builder.add(pos.offset(x, y, z));
                    }
                }
            }
        }
        builder.add(pos.offset(0, 2, 0));
        Direction back = RelativeSide.BACK.getDirection(Objects.requireNonNull(Attribute.getFacing(state)));
        builder.add(pos.offset(back.getStepX(), 2, back.getStepZ()));
    };

    private static final Map<Direction, int[]> TOP_LAYER_RANGES = Map.of(
            Direction.WEST, new int[] { 0, 1, -1, 1 },  // xStart, xEnd, zStart, zEnd
            Direction.EAST, new int[] { -1, 0, -1, 1 },
            Direction.NORTH, new int[] { -1, 1, 0, 1 },
            Direction.SOUTH, new int[] { -1, 1, -1, 0 });

    public static final TriConsumer<BlockPos, BlockState, Builder<BlockPos>> FULL_JAVA_ENTITY_BUT_TOP_BACK_2X3 = (pos, state, builder) -> {
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        builder.add(pos.offset(x, y, z));
                    }
                }
            }
        }
        Direction facing = Attribute.getFacing(state);
        int[] ranges = TOP_LAYER_RANGES.get(facing);
        if (ranges != null) {
            for (int x = ranges[0]; x <= ranges[1]; x++) {
                for (int z = ranges[2]; z <= ranges[3]; z++) {
                    builder.add(pos.offset(x, 2, z));
                }
            }
        }
    };
}
