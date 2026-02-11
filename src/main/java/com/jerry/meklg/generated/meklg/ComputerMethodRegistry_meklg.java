package com.jerry.meklg.generated.meklg;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;

import mekanism.common.integration.computer.FactoryRegistry;
import mekanism.common.integration.computer.IComputerMethodRegistry;
import mekanism.common.tile.base.CapabilityTileEntity;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.TileEntityUpdateable;

import com.jerry.meklg.common.tile.generator.TileEntityLargeGasGenerator;
import com.jerry.meklg.common.tile.generator.TileEntityLargeGasGenerator$ComputerHandler;
import com.jerry.meklg.common.tile.generator.TileEntityLargeHeatGenerator;
import com.jerry.meklg.common.tile.generator.TileEntityLargeHeatGenerator$ComputerHandler;

public class ComputerMethodRegistry_meklg implements IComputerMethodRegistry {

    @Override
    public void register() {
        if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
            FactoryRegistry.register(TileEntityLargeGasGenerator.class, TileEntityLargeGasGenerator$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityMoreMachineGenerator.class);
            FactoryRegistry.register(TileEntityLargeHeatGenerator.class, TileEntityLargeHeatGenerator$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityMoreMachineGenerator.class);
        }
    }
}
