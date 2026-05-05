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
        VoxelShapeUtils.setShape(box(0, -15, 0, 16, 33, 16), MAX_CHEMICAL_TANK);

        VoxelShape largeMachine = box(-16, -15, -16, 32, 33, 32);
        VoxelShapeUtils.setShape(largeMachine, LARGE_ROTARY_CONDENSENTRATOR);
        VoxelShapeUtils.setShape(largeMachine, LARGE_CHEMICAL_INFUSER);
        VoxelShapeUtils.setShape(largeMachine, LARGE_ELECTROLYTIC_SEPARATOR);
        VoxelShapeUtils.setShape(largeMachine, LARGE_SOLAR_NEUTRON_ACTIVATOR);
        VoxelShapeUtils.setShape(largeMachine, LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER);
        VoxelShapeUtils.setShape(largeMachine, LARGE_PIGMENT_MIXER);
    }

    public static VoxelShape[] getLargeChemicalTank(ILargeChemicalTankTier tier) {
        return tier instanceof MidChemicalTankTier ? MID_CHEMICAL_TANK : MAX_CHEMICAL_TANK;
    }

    private LargeMachineBlockShapes() {}

    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
