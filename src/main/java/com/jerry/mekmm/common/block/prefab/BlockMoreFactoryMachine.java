package com.jerry.mekmm.common.block.prefab;

import com.jerry.mekmm.common.content.blocktype.MoreMachineFactory;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.tile.base.TileEntityMekanism;

public class BlockMoreFactoryMachine<TILE extends TileEntityMekanism, MACHINE extends MoreMachineFactoryMachine<TILE>> extends BlockTile<TILE, MACHINE> {

    public BlockMoreFactoryMachine(MACHINE machine, Properties properties) {
        super(machine, properties);
    }

    public static class BlockMoreFactoryMachineModel<TILE extends TileEntityMekanism, MACHINE extends MoreMachineFactoryMachine<TILE>> extends BlockMoreFactoryMachine<TILE, MACHINE> implements IStateFluidLoggable {

        public BlockMoreFactoryMachineModel(MACHINE machineType, Properties properties) {
            super(machineType, properties);
        }
    }

    public static class BlockMoreMachineFactory<TILE extends TileEntityMoreMachineFactory<?>> extends BlockMoreFactoryMachineModel<TILE, MoreMachineFactory<TILE>> {

        public BlockMoreMachineFactory(MoreMachineFactory<TILE> factoryType, Properties properties) {
            super(factoryType, defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
        }
    }
}
