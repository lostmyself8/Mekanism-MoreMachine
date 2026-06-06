package com.jerry.meklg.generated.meklg;

import com.jerry.mekmm.Mekmm;

import mekanism.common.integration.computer.FactoryRegistry;
import mekanism.common.integration.computer.IComputerMethodRegistry;

import com.jerry.meklg.common.tile.TileEntityLargeGasBurningGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeGasBurningGenerator$ComputerHandler;
import com.jerry.meklg.common.tile.TileEntityLargeHeatGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeHeatGenerator$ComputerHandler;
import com.jerry.meklg.common.tile.TileEntityLargeWindGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeWindGenerator$ComputerHandler;
import com.jerry.meklg.common.tile.TileEntityMoreGenerator;
import com.jerry.meklg.common.tile.TileEntityMoreGenerator$ComputerHandler;

public class ComputerMethodRegistry_meklg implements IComputerMethodRegistry {

    @Override
    public void register() {
        if (Mekmm.hooks.MGLoaded) {
            FactoryRegistry.register(TileEntityLargeGasBurningGenerator.class, TileEntityLargeGasBurningGenerator$ComputerHandler::new);
            FactoryRegistry.register(TileEntityLargeHeatGenerator.class, TileEntityLargeHeatGenerator$ComputerHandler::new);
            FactoryRegistry.register(TileEntityLargeWindGenerator.class, TileEntityLargeWindGenerator$ComputerHandler::new);
            FactoryRegistry.register(TileEntityMoreGenerator.class, TileEntityMoreGenerator$ComputerHandler::new);
        }
    }
}