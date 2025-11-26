package com.jerry.mekmm.common.content.blocktype;

import mekanism.common.content.blocktype.BlockShapes;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoreMachineBlockShapes {

    private MoreMachineBlockShapes() {}

    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static final VoxelShape[] FULL_BLOCK_SHAPE = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    // Machine
    public static final VoxelShape[] RECYCLER = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] PLANTING_STATION = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] CNC_STAMPER = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] CNC_LATHE = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] CNC_ROLLING_MILL = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    // Factories
    public static final VoxelShape[] RECYCLER_FACTORY = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] WIRELESS_CHARGING_STATION = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] WIRELESS_TRANSMISSION_STATION = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    // Doll
    public static final VoxelShape[] AUTHOR_DOLL = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];

    static {
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(0, 0, 0, 16, 16, 16)), FULL_BLOCK_SHAPE);

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
                box(0, 0, 0, 16, 3, 10),
                box(0, 8, 0, 16, 16, 10),
                box(0, 0, 10, 16, 16, 16),
                box(1, 3, 1, 15, 8, 10)), CNC_LATHE);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(0, 0, 0, 16, 16, 4), // front_panel
                box(4, 4, 14, 12, 12, 16), // port
                box(0, 5, 5, 7, 16, 16), // saw2
                box(9, 5, 5, 16, 16, 16), // saw1
                box(0, 0, 4, 16, 4, 16), // base
                box(1, 4, 4, 15, 15, 15) // core
        ), RECYCLER_FACTORY);

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

    public static VoxelShape[] getShape(FactoryTier tier, MoreMachineFactoryType type) {
        return switch (type) {
            case RECYCLING -> BlockShapes.SMELTING_FACTORY;
            case PLANTING_STATION -> BlockShapes.ENRICHING_FACTORY;
            case CNC_STAMPING -> BlockShapes.CRUSHING_FACTORY;
            case CNC_LATHING -> BlockShapes.COMPRESSING_FACTORY;
            case CNC_ROLLING_MILL -> BlockShapes.COMBINING_FACTORY;
            case REPLICATING -> BlockShapes.PURIFYING_FACTORY;
        };
    }
}
