package com.jerry.mekaf.common.block.prefab;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactory;
import com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.tile.base.TileEntityMekanism;

import java.util.function.UnaryOperator;

public class BlockAdvancedFactoryMachine<TILE extends TileEntityMekanism, MACHINE extends MoreMachineFactoryMachine<TILE>> extends BlockTile<TILE, MACHINE> {

    public BlockAdvancedFactoryMachine(MACHINE machine, UnaryOperator<Properties> propertiesModifier) {
        super(machine, propertiesModifier);
    }

    public static class BlockAdvancedFactoryMachineModel<TILE extends TileEntityMekanism, MACHINE extends MoreMachineFactoryMachine<TILE>> extends BlockAdvancedFactoryMachine<TILE, MACHINE> implements IStateFluidLoggable {

        public BlockAdvancedFactoryMachineModel(MACHINE machineType, UnaryOperator<Properties> propertiesModifier) {
            super(machineType, propertiesModifier);
        }
    }

    public static class BlockAdvancedFactory<TILE extends TileEntityAdvancedFactoryBase<?>> extends BlockAdvancedFactoryMachineModel<TILE, AdvancedFactory<TILE>> {

        public BlockAdvancedFactory(AdvancedFactory<TILE> factoryType) {
            super(factoryType, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
        }
    }
}
