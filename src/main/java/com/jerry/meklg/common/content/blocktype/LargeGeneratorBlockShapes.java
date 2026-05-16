package com.jerry.meklg.common.content.blocktype;

import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LargeGeneratorBlockShapes {

    public static final VoxelShape[] LARGE_HEAT_GENERATOR = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LARGE_GAS_BURNING_GENERATOR = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LARGE_WIND_GENERATOR = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];

    static {
        VoxelShape largeGenerator = box(-16, 0, -16, 32, 48, 32);
        VoxelShapeUtils.setShape(largeGenerator, LARGE_GAS_BURNING_GENERATOR);
        VoxelShapeUtils.setShape(largeGenerator, LARGE_HEAT_GENERATOR);

        // VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
        // box(-40, 0, -40, 56, 8, 56),
        // box(-32, 8, -32, 48, 24, 48),
        // box(-16, 0, -48, 32, 20, -32),
        // box(-48, 0, -16, -32, 20, 32),
        // box(48, 0, -16, 64, 20, 32),
        // box(-16, 0, 48, 32, 34, 64),
        // box(-21, 32, -21, 37, 160, 37),
        // box(-17, 160, -17, 33, 288, 33),
        // box(-14, 288, -14, 30, 416, 30),
        // box(-11, 416, -11, 27, 510, 27),
        // box(-21, 510, -22, 37, 572, 58),
        // box(-18, 515, -52, 34, 567, -22),
        // box(-16, 510, 58, 32, 567, 88),
        // box(-8, 569, -16, 24, 575, 20),
        // box(6, 556, 64, 10, 582, 79)), LARGE_WIND_GENERATOR);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(-29, 24, -29, 45, 32, 45),
                box(0, 32, -22, 16, 64, -20),
                box(1, 43, -23, 2, 53, -22),
                box(14, 43, -23, 15, 53, -22),
                box(-21, 510, -22, 37, 572, 58),
                box(-22, 526, -15, -21, 558, 17),
                box(37, 526, -15, 38, 558, 17),
                box(-8, 572, 35, -7, 573, 43),
                box(5, 572, 35, 6, 573, 43),
                box(10, 572, 35, 11, 573, 43),
                box(23, 572, 35, 24, 573, 43),
                box(-16, 0, -47, 32, 16, -40),
                box(-47, 0, -15, -40, 16, 32),
                box(56, 0, -16, 63, 16, 32),
                box(3, 3, -48, 13, 13, -47),
                box(20, 4, -48, 28, 12, -47),
                box(-12, 4, -48, -4, 12, -47),
                box(-40, 0, -40, 56, 8, 56),
                box(-32, 8, -32, 48, 24, 48),
                box(5.5, 16, 53.5, 10.5, 21, 54.5),
                box(-0.5, 21, 53.5, 16.5, 25, 54.5),
                box(16.5, 21, 53.5, 27.5, 25, 54.5),
                box(-12.5, 21, 53.5, -0.5, 25, 54.5),
                box(1, 14.2, 54, 15, 15.2, 60),
                box(16, 0, 56, 32, 16, 63),
                box(-16, 0, 56, 0, 16, 63),
                box(0, 0, 56, 16, 13, 62),
                box(-16, 8, 45, 32, 32, 52),
                box(-16, 32, 45, 32, 34, 62),
                box(17, 18, 54.5, 31, 28, 56.5),
                box(1, 18, 54.5, 15, 28, 56.5),
                box(-15, 18, 54.5, -1, 28, 56.5),
                box(-14, 519, -52, 30, 563, -28),
                box(-21, 32, -21, 37, 96, 37),
                box(-19, 96, -19, 35, 160, 35),
                box(-17, 160, -17, 33, 224, 33),
                box(-16, 224, -16, 32, 288, 32),
                box(-14, 288, -14, 30, 352, 30),
                box(-13, 352, -13, 29, 416, 29),
                box(-11, 416, -11, 27, 480, 27),
                box(-9, 480, -9, 25, 510, 25),
                box(-16, 510, 58, 32, 567, 72),
                box(-15, 8, -40, 31, 20, -32),
                box(-8, 515, 71, 24, 557, 88),
                box(6, 556, 64, 10, 582, 79),
                box(-8, 569, -16, 24, 575, 0),
                box(-8, 569, 4, 24, 575, 20),
                box(5, 510, 51, 11, 526, 67),
                box(5, 504, 35, 11, 520, 51),
                box(5, 497, 19, 11, 513, 35),
                box(-15, 499, -15, 31, 505, 31),
                box(-18, 515, -25, 34, 567, -22),
                box(-15, 9, 44, 31, 20, 56),
                box(-48, 4, 20, -47, 12, 28),
                box(-48, 4, -12, -47, 12, -4),
                box(63, 4, -12, 64, 12, -4),
                box(63, 4, 20, 64, 12, 28),
                box(-48, 3, 3, -47, 13, 13),
                box(63, 3, 3, 64, 13, 13),
                box(-12, 4, 63, -4, 12, 64),
                box(20, 4, 63, 28, 12, 64),
                box(-40, 8, -15, -32, 20, 31),
                box(48, 8, -15, 56, 20, 31)), LARGE_WIND_GENERATOR);
    }

    private LargeGeneratorBlockShapes() {}

    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
