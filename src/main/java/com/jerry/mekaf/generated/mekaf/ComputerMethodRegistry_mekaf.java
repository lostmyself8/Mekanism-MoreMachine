package com.jerry.mekaf.generated.mekaf;

import com.jerry.mekaf.common.tile.factory.*;

import mekanism.common.integration.computer.FactoryRegistry;
import mekanism.common.integration.computer.IComputerMethodRegistry;
import mekanism.common.tile.base.CapabilityTileEntity;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;

public class ComputerMethodRegistry_mekaf implements IComputerMethodRegistry {

    @Override
    public void register() {
        FactoryRegistry.register(TileEntityAdvancedFactoryBase.class, TileEntityAdvancedFactoryBase$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class);
        FactoryRegistry.register(TileEntityChemicalToChemicalFactory.class, TileEntityChemicalToChemicalFactory$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityAdvancedFactoryBase.class);
        FactoryRegistry.register(TileEntityChemicalToItemFactory.class, TileEntityChemicalToItemFactory$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityAdvancedFactoryBase.class);
        FactoryRegistry.register(TileEntityItemToChemicalFactory.class, TileEntityItemToChemicalFactory$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityAdvancedFactoryBase.class);
        FactoryRegistry.register(TileEntityLiquifyingFactory.class, TileEntityLiquifyingFactory$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityAdvancedFactoryBase.class);
        FactoryRegistry.register(TileEntityPressurizedReactingFactory.class, TileEntityPressurizedReactingFactory$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityAdvancedFactoryBase.class);
    }
}
