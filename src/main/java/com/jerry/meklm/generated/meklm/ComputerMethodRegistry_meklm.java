package com.jerry.meklm.generated.meklm;

import com.jerry.meklm.common.tile.machine.*;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank$ComputerHandler;

import mekanism.common.integration.computer.FactoryRegistry;
import mekanism.common.integration.computer.IComputerMethodRegistry;
import mekanism.common.tile.base.CapabilityTileEntity;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;

public class ComputerMethodRegistry_meklm implements IComputerMethodRegistry {

    @Override
    public void register() {
        FactoryRegistry.register(TileEntityLargeChemicalInfuser.class, TileEntityLargeChemicalInfuser$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class);
        FactoryRegistry.register(TileEntityLargeElectrolyticSeparator.class, TileEntityLargeElectrolyticSeparator$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class);
        FactoryRegistry.register(TileEntityLargeRotaryCondensentrator.class, TileEntityLargeRotaryCondensentrator$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class);
        FactoryRegistry.register(TileEntityLargeSolarNeutronActivator.class, TileEntityLargeSolarNeutronActivator$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class, TileEntityRecipeMachine.class);

        FactoryRegistry.register(TileEntityLargeChemicalTank.class, TileEntityLargeChemicalTank$ComputerHandler::new, TileEntityUpdateable.class, CapabilityTileEntity.class, TileEntityMekanism.class, TileEntityConfigurableMachine.class);
    }
}
