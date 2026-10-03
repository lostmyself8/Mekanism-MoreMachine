package com.jerry.mekmm.generated.mekmm;

import com.jerry.mekmm.common.tile.factory.*;
import com.jerry.mekmm.common.tile.machine.*;

import mekanism.common.integration.computer.FactoryRegistry;
import mekanism.common.integration.computer.IComputerMethodRegistry;
import mekanism.common.tile.base.CapabilityTileEntity;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.tile.prefab.*;

public class ComputerMethodRegistry_mekmm implements IComputerMethodRegistry {

    @Override
    public void register() {
        FactoryRegistry.register(TileEntityMoreMachineFactory.class, TileEntityMoreMachineFactory$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class);
        FactoryRegistry.register(TileEntityPlantingFactory.class, TileEntityPlantingFactory$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityMoreMachineFactory.class);
        FactoryRegistry.register(TileEntityReplicatingFactory.class, TileEntityReplicatingFactory$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityMoreMachineFactory.class, TileEntityMoreMachineItemToItemFactory.class);
        FactoryRegistry.register(TileEntityStampingFactory.class, TileEntityStampingFactory$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityMoreMachineFactory.class, TileEntityMoreMachineItemToItemFactory.class);

        FactoryRegistry.register(TileEntityAmbientGasCollector.class, TileEntityAmbientGasCollector$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class);
        FactoryRegistry.register(TileEntityChemicalReplicator.class, TileEntityChemicalReplicator$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class, TileEntityProgressMachine.class);
        FactoryRegistry.register(TileEntityFluidReplicator.class, TileEntityFluidReplicator$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class, TileEntityProgressMachine.class);
        FactoryRegistry.register(TileEntityPlantingStation.class, TileEntityPlantingStation$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class, TileEntityProgressMachine.class);
        FactoryRegistry.register(TileEntityPresser.class, TileEntityPresser$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class, TileEntityProgressMachine.class);
        FactoryRegistry.register(TileEntityRecycler.class, TileEntityRecycler$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class, TileEntityProgressMachine.class);
        FactoryRegistry.register(TileEntityReplicator.class, TileEntityReplicator$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class, TileEntityProgressMachine.class);
        FactoryRegistry.register(TileEntityStamper.class, TileEntityStamper$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class, TileEntityProgressMachine.class);

        FactoryRegistry.register(TileEntityWirelessChargingStation.class, TileEntityWirelessChargingStation$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class);
        FactoryRegistry.register(TileEntityWirelessTransmissionStation.class, TileEntityWirelessTransmissionStation$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class);
    }
}
