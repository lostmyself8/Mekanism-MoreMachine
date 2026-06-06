package com.jerry.mekaf.generated.mekaf;

import com.jerry.mekaf.common.tile.TileEntityPaintingFactory;
import com.jerry.mekaf.common.tile.TileEntityPaintingFactory$ComputerHandler;
import com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase;
import com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase$ComputerHandler;

import mekanism.common.integration.computer.FactoryRegistry;
import mekanism.common.integration.computer.IComputerMethodRegistry;

public class ComputerMethodRegistry_mekaf implements IComputerMethodRegistry {

    @Override
    public void register() {
        FactoryRegistry.register(TileEntityAdvancedFactoryBase.class, TileEntityAdvancedFactoryBase$ComputerHandler::new);
        FactoryRegistry.register(TileEntityPaintingFactory.class, TileEntityPaintingFactory$ComputerHandler::new);
    }
}
