package com.jerry.mekmm.common.content.blocktype;

import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoreMachineBlockShapes {

    private MoreMachineBlockShapes() {}

    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    // Machine
    public static final VoxelShape[] PLANTING_STATION = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] REPLICATOR = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    // Factories
    public static final VoxelShape[] RECYCLING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] PLANTING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] STAMPING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LATHING_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] ROLLING_MILL_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] REPLICATOR_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] AMBIENT_GAS_COLLECTOR = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] WIRELESS_CHARGING_STATION = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] WIRELESS_TRANSMISSION_STATION = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    // Doll
    public static final VoxelShape[] AUTHOR_DOLL = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];

    static {
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(0, 4, 4, 0, 12, 12),
                box(16, 4, 4, 16, 12, 12),
                box(4, 32, 4, 12, 32, 12),
                box(6, 26, 6, 10, 28, 10),
                box(4, 4, 15, 12, 12, 16),
                box(0, 4, 4, 1, 12, 12),
                box(15, 4, 4, 16, 12, 12),
                box(4, 31, 4, 12, 32, 12),
                box(4, 16, 8, 12, 25, 8),
                box(4, 16, 8, 12, 25, 8),
                box(0, 0, 0, 16, 4, 16),
                box(1, 4, 1, 15, 13, 15),
                box(0, 4, 0, 3, 12, 3),
                box(13, 4, 0, 16, 12, 3),
                box(0, 4, 13, 3, 12, 16),
                box(13, 4, 13, 16, 12, 16),
                box(4, 4, 0, 12, 12, 1),
                box(0, 13, 0, 16, 16, 4),
                box(0, 13, 12, 16, 16, 16),
                box(0, 13, 4, 4, 16, 12),
                box(12, 13, 4, 16, 16, 12),
                box(4, 13, 4, 12, 15, 12),
                box(4.5, 15, 4.5, 11.5, 16, 11.5),
                box(1, 16, 1, 4, 28, 4),
                box(12, 16, 1, 15, 28, 4),
                box(1, 16, 12, 4, 28, 15),
                box(12, 16, 12, 15, 28, 15),
                box(1, 28, 1, 15, 31, 15),
                box(4, 16, 2, 12, 28, 3),
                box(2, 16, 4, 3, 28, 12),
                box(4, 16, 13, 12, 28, 14),
                box(13, 16, 4, 14, 28, 12)), PLANTING_STATION);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                Block.box(4, 4, 16, 12, 12, 16),
                Block.box(1, 8, 14, 2, 10, 15),
                Block.box(3, 8, 14, 4, 10, 15),
                Block.box(12, 8, 14, 13, 10, 15),
                Block.box(14, 8, 14, 15, 10, 15),
                Block.box(5, 3, 1, 6, 5, 2),
                Block.box(10, 3, 1, 11, 5, 2),
                Block.box(4, 4, 15, 12, 12, 16),
                Block.box(0, 0, 0, 16, 3, 16),
                Block.box(1, 3, 2, 15, 5, 15),
                Block.box(0, 5, 0, 16, 8, 10),
                Block.box(0, 5, 10, 16, 8, 16),
                Block.box(0, 10, 10, 16, 16, 16),
                Block.box(1, 8, 10, 15, 10, 13),
                Block.box(13, 8, 0, 16, 16, 10),
                Block.box(0, 8, 0, 7, 16, 10),
                Block.box(7, 8, 9, 13, 16, 10),
                Block.box(4, 8, 13, 12, 10, 15),
                Block.box(6, 3, 15, 10, 4, 16),
                Block.box(7, 14, 1, 13, 15, 2),
                Block.box(15, 3, 2, 16, 5, 6),
                Block.box(0, 3, 2, 1, 5, 6),
                Block.box(0, 3, 0, 4, 5, 2),
                Block.box(12, 3, 0, 16, 5, 2),
                Block.box(7, 10, 4, 8, 13, 6),
                Block.box(12, 10, 4, 13, 13, 6),
                Block.box(7, 8, 1, 13, 15, 1),
                Block.box(7, 15, 1, 13, 15, 9)), REPLICATOR);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 0, 16, 16, 4),
                box(0, 0, 12, 7, 12, 16),
                box(9, 0, 12, 16, 12, 16),
                box(0, 0, 4, 16, 4, 11),
                box(1, 4, 4, 15, 12, 12),
                box(0, 12, 5, 7, 16, 16),
                box(9, 12, 5, 16, 16, 16),
                box(1, 0, 11, 15, 4, 12),
                box(1, 12, 4, 15, 15, 5),
                box(7, 0, 12, 9, 13, 15),
                box(7, 13, 5, 9, 16, 15),
                box(0, 4, 5, 1, 12, 6),
                box(0, 4, 10, 1, 12, 11),
                box(15, 4, 5, 16, 12, 6),
                box(15, 4, 10, 16, 12, 11)), RECYCLING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(6, 25, 6, 10, 27, 10),
                box(4, 4, 15, 12, 12, 16),
                box(4, 4, 14, 12, 12, 15),
                box(0, 0, 0, 16, 16, 4),
                box(0, 0, 4, 16, 4, 16),
                box(0, 12, 4, 4, 16, 16),
                box(12, 12, 4, 16, 16, 16),
                box(4, 12, 12, 12, 16, 16),
                box(4, 12, 4, 12, 15, 12),
                box(4.5, 15, 4.5, 11.5, 16, 11.5),
                box(1, 4, 14, 3, 12, 16),
                box(0, 4, 11, 1, 12, 14),
                box(0, 4, 6, 1, 12, 9),
                box(13, 4, 14, 15, 12, 16),
                box(15, 4, 11, 16, 12, 14),
                box(15, 4, 6, 16, 12, 9),
                box(1, 4, 4, 15, 12, 14),
                box(12, 16, 0, 16, 27, 4),
                box(0, 16, 0, 4, 27, 4),
                box(0, 16, 12, 4, 27, 16),
                box(12, 16, 12, 16, 27, 16),
                box(4, 31, 4, 12, 32, 12),
                box(0, 27, 0, 16, 31, 16),
                box(4, 16, 2, 12, 27, 3),
                box(2, 16, 4, 3, 27, 12),
                box(4, 16, 13, 12, 27, 14),
                box(13, 16, 4, 14, 27, 12)), PLANTING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 0, 16, 16, 4),
                box(0, 12, 4, 16, 16, 16),
                box(0, 3, 5, 3, 12, 8),
                box(0, 0, 13, 3, 12, 16),
                box(0, 0, 5, 1, 3, 8),
                box(13, 3, 5, 16, 12, 8),
                box(13, 0, 13, 16, 12, 16),
                box(15, 0, 5, 16, 3, 8),
                box(1, 0, 4, 15, 3, 13),
                box(1, 9, 4, 15, 12, 15),
                box(3, 0, 13, 13, 3, 16),
                box(2, 3, 4, 14, 9, 14),
                box(4, 3, 14, 12, 9, 15)), STAMPING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(4, 3, 14, 12, 12, 15),
                box(2, 3, 4, 14, 12, 14),
                box(3, 0, 13, 13, 3, 16),
                box(1, 0, 4, 15, 3, 13),
                box(15, 0, 5, 16, 3, 8),
                box(13, 0, 13, 16, 12, 16),
                box(13, 3, 5, 16, 12, 8),
                box(0, 0, 5, 1, 3, 8),
                box(0, 0, 12.9, 3, 12, 15.9),
                box(0, 3, 5, 3, 12, 8),
                box(0, 12, 4, 16, 16, 16),
                box(0, 0, 0, 16, 16, 4),
                box(1, 5, 8, 2, 6, 13),
                box(1, 7, 8, 2, 8, 13),
                box(1, 9, 8, 2, 10, 13),
                box(14, 5, 8, 15, 6, 13),
                box(14, 7, 8, 15, 8, 13),
                box(14, 9, 8, 15, 10, 13),
                box(3, 5, 14, 4, 6, 15),
                box(3, 7, 14, 4, 8, 15),
                box(3, 9, 14, 4, 10, 15),
                box(12, 5, 14, 13, 6, 15),
                box(12, 7, 14, 13, 8, 15),
                box(12, 9, 14, 13, 10, 15)), LATHING_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(4, 3, 14, 12, 9, 15),
                box(2, 3, 4, 14, 12, 14),
                box(3, 0, 13, 13, 3, 16),
                box(1, 0, 4, 15, 3, 13),
                box(13, 0, 13, 16, 12, 16),
                box(0, 0, 13, 3, 12, 16),
                box(0, 12, 4, 16, 16, 16),
                box(0, 0, 0, 16, 16, 4),
                box(1, 3, 6, 2, 7, 10),
                box(1, 8, 6, 2, 12, 10),
                box(14, 3, 6, 15, 7, 10),
                box(14, 8, 6, 15, 12, 10)), ROLLING_MILL_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 0, 16, 16, 4),
                box(0, 0, 4, 16, 4, 16),
                box(0, 10, 10, 16, 16, 16),
                box(1, 4, 4, 15, 5, 14),
                box(1, 9, 9, 15, 15, 14),
                box(0, 5, 4, 16, 9, 14),
                box(0, 9, 4, 16, 16, 9),
                box(1, 4, 14, 2, 10, 15),
                box(3, 4, 14, 4, 10, 15),
                box(12, 4, 14, 13, 10, 15),
                box(14, 4, 14, 15, 10, 15),
                box(0, 5, 14, 16, 6, 16),
                box(0, 8, 14, 16, 9, 16)), REPLICATOR_FACTORY);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 0, 12, 12, 0),
                box(0, 4, 4, 0, 12, 12),
                box(4, 4, 16, 12, 12, 16),
                box(16, 4, 4, 16, 12, 12),
                box(4, -0.01, 4, 12, -0.01, 12),
                box(4, 4, 0, 12, 12, 1),
                box(0, 4, 4, 1, 12, 12),
                box(4, 4, 15, 12, 12, 16),
                box(15, 4, 4, 16, 12, 12),
                box(4, -0.01, 4, 12, 0.99, 12),
                box(0, 0, 0, 16, 4, 16),
                box(13, 4, 0, 16, 8, 3),
                box(0, 4, 0, 3, 8, 3),
                box(0, 4, 13, 3, 8, 16),
                box(13, 4, 13, 16, 8, 16),
                box(1, 4, 1, 15, 12, 15),
                box(0, 12, 0, 16, 16, 16),
                box(0, 8, 0, 4, 9, 4),
                box(0, 10, 0, 4, 11, 4),
                box(0, 8, 12, 4, 9, 16),
                box(0, 10, 12, 4, 11, 16),
                box(12, 8, 12, 16, 9, 16),
                box(12, 10, 12, 16, 11, 16),
                box(12, 8, 0, 16, 9, 4),
                box(12, 10, 0, 16, 11, 4)), AMBIENT_GAS_COLLECTOR);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, -16, 4, 12, -16, 12),
                box(4, -12, 0, 12, -4, 0),
                box(0, -12, 4, 0, -4, 12),
                box(4, -12, 16, 12, -4, 16),
                box(16, -12, 4, 16, -4, 12),
                box(4, -12, 0, 12, -4, 1),
                box(5, -11, 1, 11, -5, 4),
                box(0, -12, 4, 1, -4, 12),
                box(1, -11, 5, 4, -5, 11),
                box(4, -12, 15, 12, -4, 16),
                box(5, -11, 12, 11, -5, 15),
                box(15, -12, 4, 16, -4, 12),
                box(12, -11, 5, 15, -5, 11),
                box(4, -16, 4, 12, -15, 12),
                box(5, 19, 5, 11, 24, 11),
                box(6, 28, 6, 10, 29, 10),
                box(7, 21, 2, 9, 23, 5),
                box(4, 20.5, 1, 12, 23.5, 2),
                box(7, 21, 11, 9, 23, 14),
                box(4, 20.5, 14, 12, 23.5, 15),
                box(0, 20.5, 1, 4, 23.5, 2),
                box(12, 20.5, 1, 16, 23.5, 2),
                box(0, 20.5, 14, 4, 23.5, 15),
                box(12, 20.5, 14, 16, 23.5, 15),
                box(5, 25, 5, 11, 26, 11),
                box(5, 27, 5, 11, 28, 11),
                box(1, -16, 1, 15, -14, 15),
                box(3, -14, 3, 13, -12, 13),
                box(4, -12, 4, 12, -4, 12),
                box(6, -2, 6, 10, 19, 10),
                box(5, -4, 5, 11, -2, 11),
                box(6, 24, 6, 10, 25, 10),
                box(6, 26, 6, 10, 27, 10),
                box(7, 18, 4, 9, 21, 6),
                box(7, 18, 10, 9, 21, 12),
                box(4, 18, 7, 6, 23, 9),
                box(10, 18, 7, 12, 23, 9),
                box(4, 21.5, 1, 12, 22.5, 1),
                box(4, 21.5, 15, 12, 22.5, 15),
                box(12, 21.5, 15, 16, 22.5, 15),
                box(0, 21.5, 15, 4, 22.5, 15),
                box(12, 21.5, 1, 16, 22.5, 1),
                box(0, 21.5, 1, 4, 22.5, 1)).move(0, 1, 0), WIRELESS_CHARGING_STATION);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(0, 20, 1, 4, 22, 1),
                box(12, 20, 1, 16, 22, 1),
                box(0, 20, 15, 4, 22, 15),
                box(12, 20, 15, 16, 22, 15),
                box(4, 20, 15, 12, 22, 15),
                box(4, 20, 1, 12, 22, 1),
                box(4, 17, 7, 6, 21, 9),
                box(10, 17, 7, 12, 21, 9),
                box(4, -16, 4, 12, -16, 12),
                box(4, -12, 0, 12, -4, 0),
                box(0, -12, 4, 0, -4, 12),
                box(4, -12, 16, 12, -4, 16),
                box(16, -12, 4, 16, -4, 12),
                box(4, -12, 0, 12, -4, 1),
                box(5, -11, 1, 11, -5, 4),
                box(0, -12, 4, 1, -4, 12),
                box(1, -11, 5, 4, -5, 11),
                box(4, -12, 15, 12, -4, 16),
                box(5, -11, 12, 11, -5, 15),
                box(15, -12, 4, 16, -4, 12),
                box(12, -11, 5, 15, -5, 11),
                box(4, -16, 4, 12, -15, 12),
                box(1, -16, 1, 15, -14, 15),
                box(3, -14, 3, 13, -12, 13),
                box(4, -12, 4, 12, -4, 12),
                box(6, -2, 6, 10, 19, 10),
                box(5, -4, 5, 11, -2, 11),
                box(5, 19, 5, 11, 23, 11),
                box(6, 23, 6, 10, 28, 10),
                box(6.5, 28, 6.5, 9.5, 29, 9.5),
                box(7, 20, 2, 9, 22, 5),
                box(4, 19, 1, 12, 23, 2),
                box(7, 20, 11, 9, 22, 14),
                box(4, 19, 14, 12, 23, 15),
                box(0, 19, 1, 4, 23, 2),
                box(12, 19, 1, 16, 23, 2),
                box(0, 19, 14, 4, 23, 15),
                box(12, 19, 14, 16, 23, 15),
                box(4, 17, 7, 6, 21, 9),
                box(10, 17, 7, 12, 21, 9)).move(0, 1, 0), WIRELESS_TRANSMISSION_STATION);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4.35, 6.2, 7.35, 11.65, 13.5, 14.65), // head
                box(5, 4.2, 7.08248, 6.5, 6.2, 13.08248), // left_arm
                box(9.6, 4.54246, 6.979, 11.1, 6.54246, 12.979), // right_arm
                box(8, 0.1, 4, 10, 2.1, 10), // left_leg
                box(6, 0.1, 4, 8, 2.1, 10), // right_leg
                box(5.6, 0.2, 9.6, 10.4, 6.2, 12.4) // body
        ), AUTHOR_DOLL);
    }

    public static VoxelShape[] getShape(MoreMachineFactoryType type) {
        return switch (type) {
            case RECYCLING -> RECYCLING_FACTORY;
            case PLANTING_STATION -> PLANTING_FACTORY;
            case CNC_STAMPING -> STAMPING_FACTORY;
            case CNC_LATHING -> LATHING_FACTORY;
            case CNC_ROLLING_MILL -> ROLLING_MILL_FACTORY;
            case REPLICATING -> REPLICATOR_FACTORY;
        };
    }
}
