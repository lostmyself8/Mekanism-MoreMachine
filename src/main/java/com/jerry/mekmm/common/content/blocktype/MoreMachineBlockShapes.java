package com.jerry.mekmm.common.content.blocktype;

import mekanism.common.content.blocktype.BlockShapes;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoreMachineBlockShapes {

    private MoreMachineBlockShapes() {
    }

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
                box(4, 3, 0, 12, 12, 16),
                box(3.5, 4.5, 4, 4, 10, 12),
                box(12.5, 3, 0, 16, 11.5, 16),
                box(12.5, 11.5, 3, 16, 12, 13),
                box(12, 4.5, 4, 12.5, 10, 12),
                box(0, 11.5, 3, 3.5, 12, 13),
                box(0, 3, 0, 3.5, 11.5, 16),
                box(0, 12, 0, 16, 14, 16),
                box(12.5, 14, 1, 15, 28, 3.5),
                box(1, 14, 1, 3.5, 28, 3.5),
                box(1, 14, 12.5, 3.5, 28, 15),
                box(12.5, 14, 12.5, 15, 28, 15),
                box(2.4, 14, 3.5, 2.5, 28, 12.5),
                box(13.5, 14, 3.5, 13.6, 28, 12.5),
                box(3.5, 14, 2.4, 12.5, 28, 2.5),
                box(3.5, 14, 13.5, 12.5, 28, 13.6),
                box(1, 28, 1, 15, 31.98, 15),
                box(3, 14, 8, 13, 25, 8),
                box(3, 14, 8, 13, 25, 8),
                box(0, 0, 0, 16, 3, 16),
                box(-0.01, 4, 4, -0.01, 12, 12),
                box(-0.02, 5, 5, -0.02, 11, 11),
                box(16.01, 4, 4, 16.01, 12, 12),
                box(16.02, 5, 5, 16.02, 11, 11),
                box(4, 4, 16.01, 12, 12, 16.01),
                box(5, 5, 16.02, 11, 11, 16.02),
                box(4, 31.99, 4, 12, 31.99, 12),
                box(5, 32, 5, 11, 32, 11),
                box(6, 26, 6, 10, 28, 10)), PLANTING_STATION);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(1, 8, 14, 2, 10, 15),
                box(3, 8, 14, 4, 10, 15),
                box(12, 8, 14, 13, 10, 15),
                box(14, 8, 14, 15, 10, 15),
                box(7, 8, 1, 13, 15, 1),
                box(7, 15, 1, 13, 15, 9),
                box(4, 4, 15, 12, 12, 16),
                box(0, 0, 0, 16, 3, 16),
                box(1, 3, 1, 15, 5, 15),
                box(0, 5, 0, 16, 8, 16),
                box(0, 10, 10, 16, 16, 16),
                box(1, 8, 10, 15, 10, 13),
                box(13, 8, 0, 16, 16, 10),
                box(0, 8, 0, 7, 16, 10),
                box(7, 8, 9, 13, 16, 10),
                box(4, 8, 13, 12, 10, 15),
                box(6, 3, 15, 10, 4, 16),
                box(7, 14, 1, 13, 15, 2)), REPLICATOR);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                Block.box(4, 4, 16, 12, 12, 16),
                Block.box(4, 4, 15, 12, 12, 16),
                Block.box(0, 0, 0, 16, 16, 4),
                Block.box(0, 0, 12, 7, 12, 16),
                Block.box(9, 0, 12, 16, 12, 16),
                Block.box(0, 0, 4, 16, 4, 11),
                Block.box(1, 4, 4, 15, 12, 12),
                Block.box(0, 12, 5, 7, 16, 16),
                Block.box(9, 12, 5, 16, 16, 16),
                Block.box(1, 0, 11, 15, 4, 12),
                Block.box(1, 12, 4, 15, 15, 5),
                Block.box(7, 0, 12, 9, 13, 15),
                Block.box(7, 13, 5, 9, 16, 15),
                Block.box(0, 4, 5, 1, 12, 6),
                Block.box(0, 4, 10, 1, 12, 11),
                Block.box(15, 4, 5, 16, 12, 6),
                Block.box(15, 4, 10, 16, 12, 11)), RECYCLING_FACTORY);

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
                box(12, 10, 0, 16, 11, 4)), PLANTING_FACTORY);

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
                box(12, 10, 0, 16, 11, 4)), REPLICATOR_FACTORY);

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
                box(4, -10, 4, 12, -4, 12),
                box(4, 2, 4, 12, 32, 12),
                box(1, -16, 1, 15, -10, 15),
                box(3, -4, 3, 13, 2, 13),
                box(0, -12, 4, 1, -4, 12),
                box(15, -12, 4, 16, -4, 12),
                box(4, -12, 15, 12, -4, 16),
                box(4, -12, 0, 12, -4, 1),
                box(1, -10, 6, 4, -6, 10),
                box(12, -10, 6, 15, -6, 10),
                box(6, -10, 12, 10, -6, 15),
                box(6, -10, 1, 10, -6, 4)).move(0, 1, 0), WIRELESS_CHARGING_STATION);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(1, -16, 1, 15, -14, 15),
                box(5, -16, 5, 11, -16, 11),
                box(3, -14, 3, 13, -12, 13),
                box(4, -12, 4, 12, -4, 12),
                box(4, -12, 0, 12, -4, 1),
                box(5, -11, 0, 11, -5, 0),
                box(5, -11, 1, 11, -5, 4),
                box(0, -12, 4, 1, -4, 12),
                box(0, -11, 5, 0, -5, 11),
                box(1, -11, 5, 4, -5, 11),
                box(4, -12, 15, 12, -4, 16),
                box(5, -11, 16, 11, -5, 16),
                box(5, -11, 12, 11, -5, 15),
                box(15, -12, 4, 16, -4, 12),
                box(16, -11, 5, 16, -5, 11),
                box(12, -11, 5, 15, -5, 11),
                box(6, -4, 6, 10, 19, 10),
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
            case PLANTING_STATION -> BlockShapes.ENRICHING_FACTORY;
            case CNC_STAMPING -> STAMPING_FACTORY;
            case CNC_LATHING -> LATHING_FACTORY;
            case CNC_ROLLING_MILL -> ROLLING_MILL_FACTORY;
            case REPLICATING -> BlockShapes.PURIFYING_FACTORY;
        };
    }
}
