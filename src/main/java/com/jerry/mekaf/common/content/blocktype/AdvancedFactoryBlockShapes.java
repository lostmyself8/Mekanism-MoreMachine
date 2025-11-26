package com.jerry.mekaf.common.content.blocktype;

import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AdvancedFactoryBlockShapes {

    private AdvancedFactoryBlockShapes() {}

    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static final VoxelShape[] OXIDIZING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] CHEMICAL_INFUSING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] DISSOLVING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] WASHING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] CRYSTALLIZING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] PRESSURISED_REACTING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] CENTRIFUGING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LIQUIFYING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];

    static {
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 4, 16, 4, 16),
                box(0, 0, 0, 16, 16, 4),
                box(0, 5, 4, 7, 16, 16),
                box(9, 5, 4, 16, 16, 9),
                box(9, 5, 10, 16, 16, 16),
                box(1, 4, 4, 15, 14, 15),
                box(7, 14, 12, 9, 15, 14),
                box(11, 14, 9, 12, 15, 10),
                box(13, 14, 9, 14, 15, 10)), OXIDIZING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 4, 16, 4, 16),
                box(0, 0, 0, 16, 16, 4),
                box(1, 4, 4, 15, 14, 15),
                box(0, 4, 9, 7, 16, 16),
                box(9, 4, 9, 16, 16, 16),
                box(5, 14, 6, 11, 16, 9),
                box(0, 12, 4, 16, 16, 6),
                box(0, 4, 5, 1, 11, 9),
                box(15, 4, 5, 16, 11, 9),
                box(3, 14, 7, 4, 15, 9),
                box(4, 14, 7, 5, 15, 8),
                box(12, 14, 7, 13, 15, 9),
                box(11, 14, 7, 12, 15, 8),
                box(7, 14, 12, 9, 15, 13),
                box(7, 14, 10, 9, 15, 11),
                box(7, 14, 14, 9, 15, 15)), CHEMICAL_INFUSING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 0, 16, 16, 4),
                box(0, 0, 4, 16, 4, 16),
                box(0, 12, 4, 16, 16, 16),
                box(2, 4, 4, 14, 12, 15),
                box(0, 9, 5, 16, 11, 16),
                box(0, 5, 5, 16, 7, 16),
                box(1, 7, 4, 15, 9, 13)), DISSOLVING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 0, 16, 16, 4),
                box(0, 0, 4, 16, 4, 16),
                box(0, 5, 4, 7, 16, 9),
                box(9, 5, 4, 16, 16, 9),
                box(0, 4, 10, 3, 13, 16),
                box(13, 4, 10, 16, 13, 16),
                box(3, 4, 10, 13, 13, 15),
                box(0, 13, 10, 16, 16, 16),
                box(7, 4, 4, 9, 14, 9),
                box(1, 4, 9, 15, 14, 10),
                box(1, 4, 4, 7, 5, 9),
                box(9, 4, 4, 15, 5, 9),
                box(7, 14, 5, 9, 16, 6),
                box(7, 14, 7, 9, 16, 8),
                box(3, 14, 9, 6, 15, 10),
                box(10, 14, 9, 13, 15, 10)), WASHING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 0, 16, 16, 4),
                box(0, 0, 4, 16, 4, 16),
                box(0, 12, 4, 16, 16, 16),
                box(2, 4, 4, 14, 12, 15),
                box(0, 6, 5, 16, 10, 16),
                box(1, 5, 4, 2, 11, 13),
                box(14, 5, 4, 15, 11, 13),
                box(0, 4, 9, 1, 6, 10),
                box(0, 4, 11, 1, 6, 12),
                box(0, 4, 13, 1, 6, 14),
                box(0, 10, 13, 1, 12, 14),
                box(0, 10, 11, 1, 12, 12),
                box(0, 10, 9, 1, 12, 10),
                box(15, 4, 9, 16, 6, 10),
                box(15, 4, 11, 16, 6, 12),
                box(15, 4, 13, 16, 6, 14),
                box(15, 10, 13, 16, 12, 14),
                box(15, 10, 11, 16, 12, 12),
                box(15, 10, 9, 16, 12, 10)), CRYSTALLIZING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 0, 16, 16, 4),
                box(0, 0, 4, 16, 4, 16),
                box(0, 4, 7, 2, 16, 9),
                box(0, 4, 10, 2, 16, 12),
                box(14, 4, 7, 16, 16, 9),
                box(14, 4, 10, 16, 16, 12),
                box(2, 15, 7, 14, 16, 13),
                box(2, 4, 4, 14, 15, 15),
                box(0, 12, 4, 16, 16, 6),
                box(0, 13, 13, 16, 16, 16),
                box(0, 5, 13, 3, 13, 16),
                box(13, 5, 13, 16, 13, 16),
                box(0, 6, 9, 2, 7, 10),
                box(0, 8, 9, 2, 9, 10),
                box(0, 10, 9, 2, 11, 10),
                box(0, 12, 9, 2, 13, 10),
                box(0, 14, 9, 2, 15, 10),
                box(14, 6, 9, 16, 7, 10),
                box(14, 8, 9, 16, 9, 10),
                box(14, 10, 9, 16, 11, 10),
                box(14, 12, 9, 16, 13, 10),
                box(14, 14, 9, 16, 15, 10)), PRESSURISED_REACTING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                Block.box(4, 4, 16, 12, 12, 16),
                Block.box(4, 4, 15, 12, 12, 16),
                Block.box(4, 16, 1, 12, 26, 1),
                Block.box(1, 16, 4, 1, 26, 12),
                Block.box(4, 16, 15, 12, 26, 15),
                Block.box(15, 16, 4, 15, 26, 12),
                Block.box(0, 0, 0, 16, 16, 4),
                Block.box(0, 12, 4, 16, 16, 16),
                Block.box(1, 4, 4, 15, 12, 15),
                Block.box(0, 0, 4, 16, 4, 16),
                Block.box(0, 16, 0, 4, 26, 4),
                Block.box(0, 16, 12, 4, 26, 16),
                Block.box(12, 16, 12, 16, 26, 16),
                Block.box(12, 16, 0, 16, 26, 4),
                Block.box(0, 26, 0, 16, 30, 16),
                Block.box(6, 30, 6, 10, 31, 10),
                Block.box(4, 30, 4, 6, 32, 6),
                Block.box(10, 30, 10, 12, 32, 12),
                Block.box(4, 30, 11, 5, 32, 12),
                Block.box(11, 30, 4, 12, 32, 5),
                Block.box(0, 5, 4, 1, 6, 16),
                Block.box(0, 7, 4, 1, 8, 16),
                Block.box(0, 9, 4, 1, 10, 16),
                Block.box(15, 5, 4, 16, 6, 16),
                Block.box(15, 7, 4, 16, 8, 16),
                Block.box(15, 9, 4, 16, 10, 16),
                Block.box(1, 5, 15, 15, 6, 16),
                Block.box(1, 7, 15, 15, 8, 16),
                Block.box(1, 9, 15, 15, 10, 16),
                Block.box(0, 10, 12, 1, 12, 13),
                Block.box(15, 10, 12, 16, 12, 13),
                Block.box(7, 16, 7, 9, 26, 9),
                Block.box(8, 16, 4, 9, 18, 5),
                Block.box(6, 18, 4, 9, 19, 5),
                Block.box(6, 19, 4, 7, 25, 5),
                Block.box(7, 16, 11, 8, 18, 12),
                Block.box(7, 18, 11, 10, 19, 12),
                Block.box(9, 19, 11, 10, 25, 12)), CENTRIFUGING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 0, 16, 16, 4),
                box(0, 0, 5, 16, 4, 12),
                box(0, 0, 12, 16, 16, 16),
                box(0, 12, 4, 16, 16, 11),
                box(0, 5, 5, 2, 11, 11),
                box(14, 5, 5, 16, 11, 11),
                box(1, 5, 11, 2, 11, 12),
                box(2, 4, 11, 14, 15, 12),
                box(2, 0, 4, 14, 4, 5),
                box(14, 5, 11, 15, 11, 12),
                box(2, 4, 4, 14, 12, 11),
                box(0, 5, 11, 1, 6, 12),
                box(0, 10, 11, 1, 11, 12),
                box(15, 10, 11, 16, 11, 12),
                box(15, 5, 11, 16, 6, 12)), LIQUIFYING_FACTORY);
    }

    public static VoxelShape[] getShape(AdvancedFactoryType type) {
        return switch (type) {
            case OXIDIZING -> OXIDIZING_FACTORY;
            case CHEMICAL_INFUSING -> CHEMICAL_INFUSING_FACTORY;
            case DISSOLVING -> DISSOLVING_FACTORY;
            case WASHING -> WASHING_FACTORY;
            case CRYSTALLIZING -> CRYSTALLIZING_FACTORY;
            case PRESSURISED_REACTING -> PRESSURISED_REACTING_FACTORY;
            case CENTRIFUGING -> CENTRIFUGING_FACTORY;
            case LIQUIFYING -> LIQUIFYING_FACTORY;
        };
    }
}
