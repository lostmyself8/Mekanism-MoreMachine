package com.jerry.mekmm.generated.mekmm;

import com.jerry.mekmm.common.tile.TileEntityWirelessChargingStation;
import com.jerry.mekmm.common.tile.TileEntityWirelessChargingStation$ComputerHandler;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory$ComputerHandler;
import com.jerry.mekmm.common.tile.factory.TileEntityPlantingFactory;
import com.jerry.mekmm.common.tile.factory.TileEntityPlantingFactory$ComputerHandler;
import com.jerry.mekmm.common.tile.factory.TileEntityReplicatingFactory;
import com.jerry.mekmm.common.tile.factory.TileEntityReplicatingFactory$ComputerHandler;
import com.jerry.mekmm.common.tile.factory.TileEntityStampingFactory;
import com.jerry.mekmm.common.tile.factory.TileEntityStampingFactory$ComputerHandler;
import com.jerry.mekmm.common.tile.machine.TileEntityAmbientGasCollector;
import com.jerry.mekmm.common.tile.machine.TileEntityAmbientGasCollector$ComputerHandler;
import com.jerry.mekmm.common.tile.machine.TileEntityFluidReplicator;
import com.jerry.mekmm.common.tile.machine.TileEntityFluidReplicator$ComputerHandler;
import com.jerry.mekmm.common.tile.machine.TileEntityPlantingStation;
import com.jerry.mekmm.common.tile.machine.TileEntityPlantingStation$ComputerHandler;
import com.jerry.mekmm.common.tile.machine.TileEntityRecycler;
import com.jerry.mekmm.common.tile.machine.TileEntityRecycler$ComputerHandler;
import com.jerry.mekmm.common.tile.machine.TileEntityReplicator;
import com.jerry.mekmm.common.tile.machine.TileEntityReplicator$ComputerHandler;
import com.jerry.mekmm.common.tile.machine.TileEntityStamper;
import com.jerry.mekmm.common.tile.machine.TileEntityStamper$ComputerHandler;

import mekanism.common.integration.computer.FactoryRegistry;
import mekanism.common.integration.computer.IComputerMethodRegistry;

public class ComputerMethodRegistry_mekmm implements IComputerMethodRegistry {

    @Override
    public void register() {
        FactoryRegistry.register(TileEntityMoreMachineFactory.class, TileEntityMoreMachineFactory$ComputerHandler::new);
        FactoryRegistry.register(TileEntityPlantingFactory.class, TileEntityPlantingFactory$ComputerHandler::new);
        FactoryRegistry.register(TileEntityReplicatingFactory.class, TileEntityReplicatingFactory$ComputerHandler::new);
        FactoryRegistry.register(TileEntityStampingFactory.class, TileEntityStampingFactory$ComputerHandler::new);
        FactoryRegistry.register(TileEntityAmbientGasCollector.class, TileEntityAmbientGasCollector$ComputerHandler::new);
        FactoryRegistry.register(TileEntityFluidReplicator.class, TileEntityFluidReplicator$ComputerHandler::new);
        FactoryRegistry.register(TileEntityPlantingStation.class, TileEntityPlantingStation$ComputerHandler::new);
        FactoryRegistry.register(TileEntityRecycler.class, TileEntityRecycler$ComputerHandler::new);
        FactoryRegistry.register(TileEntityReplicator.class, TileEntityReplicator$ComputerHandler::new);
        FactoryRegistry.register(TileEntityStamper.class, TileEntityStamper$ComputerHandler::new);
        FactoryRegistry.register(TileEntityWirelessChargingStation.class, TileEntityWirelessChargingStation$ComputerHandler::new);
    }
}
