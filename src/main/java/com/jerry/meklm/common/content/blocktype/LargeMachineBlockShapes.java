package com.jerry.meklm.common.content.blocktype;

import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.jerry.meklm.common.tier.ILargeTankTier;
import com.jerry.meklm.common.tier.MidChemicalTankTier;

public class LargeMachineBlockShapes {

    private LargeMachineBlockShapes() {}

    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static final VoxelShape[] MID_CHEMICAL_TANK = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] MAX_CHEMICAL_TANK = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];

    static {
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, 4, 16, 12, 12, 16),
                box(3, 29, 3, 13, 29, 13),
                box(3, 29, 3, 6, 32, 4),
                box(10, 29, 3, 12, 32, 4),
                box(6, 29, 3, 10, 30, 4),
                box(3, 29, 4, 4, 32, 13),
                box(4, 29, 12, 13, 32, 13),
                box(12, 29, 3, 13, 32, 12),
                box(6, 29, 6, 10, 30, 10),
                box(5.5, 30, 5.5, 10.5, 31, 10.5),
                box(4, 4, 15, 12, 12, 16),
                box(5, 5, 14, 11, 11, 15),
                box(2, 2, 2, 14, 29, 14),
                box(3, 0, 3, 13, 2, 13),
                box(4.5, 16, 1.99, 11.5, 24, 1.99),
                box(1.99, 16, 4.5, 1.99, 24, 11.5),
                box(14.01, 16, 4.5, 14.01, 24, 11.5),
                box(4.5, 16, 14.01, 11.5, 24, 14.01)), MID_CHEMICAL_TANK);

        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(4, -12, 16, 12, -4, 16),
                box(3, 29, 3, 13, 29, 13),
                box(3, 29, 3, 6, 32, 4),
                box(10, 29, 3, 12, 32, 4),
                box(6, 29, 3, 10, 30, 4),
                box(3, 29, 4, 4, 32, 13),
                box(4, 29, 12, 13, 32, 13),
                box(12, 29, 3, 13, 32, 12),
                box(6, 29, 6, 10, 30, 10),
                box(5.5, 30, 5.5, 10.5, 31, 10.5),
                box(4, -12, 15, 12, -4, 16),
                box(1, -13, 1, 15, 28, 15),
                box(2, -16, 2, 14, -14, 14),
                box(1.5, -14, 1.5, 14.5, -13, 14.5),
                box(2, 28, 2, 14, 29, 14),
                box(15.01, 4, 4.5, 15.01, 12, 11.5),
                box(0.99, 4, 4.5, 0.99, 12, 11.5),
                box(4.5, 4, 0.99, 11.5, 12, 0.99),
                box(4.5, 4, 15.01, 11.5, 12, 15.01)).move(0, 1, 0), MAX_CHEMICAL_TANK);
    }

    // 不要使用这个，可以自行写过一个方法
    public static VoxelShape[] getTankShape(ILargeTankTier tier) {
        // 本模组中之有两个类型
        return tier instanceof MidChemicalTankTier ? MID_CHEMICAL_TANK : MAX_CHEMICAL_TANK;
    }
}
