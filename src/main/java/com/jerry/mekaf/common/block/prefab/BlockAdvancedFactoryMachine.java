package com.jerry.mekaf.common.block.prefab;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactory;
import com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.tile.base.TileEntityMekanism;

public class BlockAdvancedFactoryMachine<TILE extends TileEntityMekanism, MACHINE extends MoreMachineMachine.MoreMachineFactoryMachine<TILE>> extends BlockTile<TILE, MACHINE> {

    public BlockAdvancedFactoryMachine(MACHINE machine, Properties properties) {
        super(machine, properties);
    }

    public static class MMBlockAdvancedFactoryMachineModel<TILE extends TileEntityMekanism, MACHINE extends MoreMachineMachine.MoreMachineFactoryMachine<TILE>> extends BlockAdvancedFactoryMachine<TILE, MACHINE> implements IStateFluidLoggable {

        public MMBlockAdvancedFactoryMachineModel(MACHINE machineType, Properties properties) {
            super(machineType, properties);
        }
    }

    public static class BlockAdvancedFactory<TILE extends TileEntityAdvancedFactoryBase<?>> extends MMBlockAdvancedFactoryMachineModel<TILE, AdvancedFactory<TILE>> {

        public BlockAdvancedFactory(AdvancedFactory<TILE> factoryType, Properties properties) {
            super(factoryType, defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
        }
    }
}
