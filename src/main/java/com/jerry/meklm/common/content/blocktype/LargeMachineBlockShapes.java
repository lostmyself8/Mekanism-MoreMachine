package com.jerry.meklm.common.content.blocktype;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;
import com.jerry.meklm.common.tier.MidChemicalTankTier;

import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LargeMachineBlockShapes {

    public static final VoxelShape[] MID_CHEMICAL_TANK = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] MAX_CHEMICAL_TANK = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LARGE_ROTARY_CONDENSENTRATOR = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LARGE_CHEMICAL_INFUSER = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LARGE_ELECTROLYTIC_SEPARATOR = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LARGE_SOLAR_NEUTRON_ACTIVATOR = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];
    public static final VoxelShape[] LARGE_PIGMENT_MIXER = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];

    static {
        VoxelShapeUtils.setShape(box(1, 0, 1, 15, 32, 15), MID_CHEMICAL_TANK);
        VoxelShapeUtils.setShape(box(0, 0, 0, 16, 48, 16), MAX_CHEMICAL_TANK);

        VoxelShape twoHighLargeMachine = box(-16, 0, -16, 32, 32, 32);
        VoxelShape threeHighLargeMachine = box(-16, 0, -16, 32, 48, 32);
        VoxelShapeUtils.setShape(threeHighLargeMachine, LARGE_ROTARY_CONDENSENTRATOR);
        VoxelShapeUtils.setShape(threeHighLargeMachine, LARGE_CHEMICAL_INFUSER);
        VoxelShapeUtils.setShape(twoHighLargeMachine, LARGE_ELECTROLYTIC_SEPARATOR);
        VoxelShapeUtils.setShape(threeHighLargeMachine, LARGE_SOLAR_NEUTRON_ACTIVATOR);
        VoxelShapeUtils.setShape(threeHighLargeMachine, LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER);
        VoxelShapeUtils.setShape(threeHighLargeMachine, LARGE_PIGMENT_MIXER);
    }

    public static VoxelShape[] getLargeChemicalTank(ILargeChemicalTankTier tier) {
        return tier instanceof MidChemicalTankTier ? MID_CHEMICAL_TANK : MAX_CHEMICAL_TANK;
    }

    private LargeMachineBlockShapes() {}

    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
