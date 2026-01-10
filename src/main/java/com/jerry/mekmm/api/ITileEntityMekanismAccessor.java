package com.jerry.mekmm.api;

import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.GasHandlerManager;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.InfusionHandlerManager;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.PigmentHandlerManager;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.SlurryHandlerManager;
import mekanism.common.capabilities.resolver.manager.EnergyHandlerManager;

public interface ITileEntityMekanismAccessor {

    GasHandlerManager getGasHandlerManager();

    InfusionHandlerManager getInfusionHandlerManager();

    PigmentHandlerManager getPigmentHandlerManager();

    SlurryHandlerManager getSlurryHandlerManager();

    EnergyHandlerManager getEnergyHandlerManager();
}
