package com.jerry.meklm.generated.meklm;

import mekanism.common.integration.computer.FactoryRegistry;
import mekanism.common.integration.computer.IComputerMethodRegistry;

import com.jerry.meklm.common.tile.machine.TileEntityLargeAntiprotonicNucleosynthesizer;
import com.jerry.meklm.common.tile.machine.TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler;
import com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser;
import com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser$ComputerHandler;
import com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator$ComputerHandler;
import com.jerry.meklm.common.tile.machine.TileEntityLargePigmentMixer;
import com.jerry.meklm.common.tile.machine.TileEntityLargePigmentMixer$ComputerHandler;
import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator$ComputerHandler;
import com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator$ComputerHandler;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank$ComputerHandler;

public class ComputerMethodRegistry_meklm implements IComputerMethodRegistry {

    @Override
    public void register() {
        FactoryRegistry.register(TileEntityLargeAntiprotonicNucleosynthesizer.class, TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::new);
        FactoryRegistry.register(TileEntityLargeChemicalInfuser.class, TileEntityLargeChemicalInfuser$ComputerHandler::new);
        FactoryRegistry.register(TileEntityLargeElectrolyticSeparator.class, TileEntityLargeElectrolyticSeparator$ComputerHandler::new);
        FactoryRegistry.register(TileEntityLargePigmentMixer.class, TileEntityLargePigmentMixer$ComputerHandler::new);
        FactoryRegistry.register(TileEntityLargeRotaryCondensentrator.class, TileEntityLargeRotaryCondensentrator$ComputerHandler::new);
        FactoryRegistry.register(TileEntityLargeSolarNeutronActivator.class, TileEntityLargeSolarNeutronActivator$ComputerHandler::new);
        FactoryRegistry.register(TileEntityLargeChemicalTank.class, TileEntityLargeChemicalTank$ComputerHandler::new);
    }
}
