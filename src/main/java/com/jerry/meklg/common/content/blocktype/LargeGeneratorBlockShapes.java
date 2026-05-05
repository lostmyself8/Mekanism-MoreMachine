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

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(-40, 0, -40, 56, 8, 56),
                box(-32, 8, -32, 48, 24, 48),
                box(-16, 0, -48, 32, 20, -32),
                box(-48, 0, -16, -32, 20, 32),
                box(48, 0, -16, 64, 20, 32),
                box(-16, 0, 48, 32, 34, 64),
                box(-21, 32, -21, 37, 160, 37),
                box(-17, 160, -17, 33, 288, 33),
                box(-14, 288, -14, 30, 416, 30),
                box(-11, 416, -11, 27, 510, 27),
                box(-21, 510, -22, 37, 572, 58),
                box(-18, 515, -52, 34, 567, -22),
                box(-16, 510, 58, 32, 567, 88),
                box(-8, 569, -16, 24, 575, 20),
                box(6, 556, 64, 10, 582, 79)), LARGE_WIND_GENERATOR);
    }

    private LargeGeneratorBlockShapes() {}

    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
