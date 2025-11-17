package com.jerry.mekmm.common.block.prefab;

import com.jerry.mekmm.common.content.blocktype.MoreMachineFactory;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.tile.base.TileEntityMekanism;

import java.util.function.UnaryOperator;

public class BlockMoreMachineFactoryMachine<TILE extends TileEntityMekanism, MACHINE extends MoreMachineFactoryMachine<TILE>> extends BlockTile<TILE, MACHINE> {

    public BlockMoreMachineFactoryMachine(MACHINE machine, UnaryOperator<Properties> propertiesModifier) {
        super(machine, propertiesModifier);
    }

    public static class BlockMoreMachineFactoryMachineModel<TILE extends TileEntityMekanism, MACHINE extends MoreMachineFactoryMachine<TILE>> extends BlockMoreMachineFactoryMachine<TILE, MACHINE> implements IStateFluidLoggable {

        public BlockMoreMachineFactoryMachineModel(MACHINE machineType, UnaryOperator<Properties> propertiesModifier) {
            super(machineType, propertiesModifier);
        }
    }

    public static class BlockMoreMachineFactory<TILE extends TileEntityMoreMachineFactory<?>> extends BlockMoreMachineFactoryMachineModel<TILE, MoreMachineFactory<TILE>> {

        public BlockMoreMachineFactory(MoreMachineFactory<TILE> factoryType) {
            super(factoryType, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
        }
    }
}
