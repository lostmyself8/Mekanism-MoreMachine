package com.jerry.mekmm.common.tile.machine;

import mekanism.api.*;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;
import mekanism.common.util.*;

@MethodFactory(target = TileEntityAmbientGasCollector.class)
public class TileEntityAmbientGasCollector$ComputerHandler extends ComputerMethodFactory<TileEntityAmbientGasCollector> {

    public TileEntityAmbientGasCollector$ComputerHandler() {
        register(MethodData.builder("getChemical", TileEntityAmbientGasCollector$ComputerHandler::chemicalTank$getChemical).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalCapacity", TileEntityAmbientGasCollector$ComputerHandler::chemicalTank$getChemicalCapacity).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalNeeded", TileEntityAmbientGasCollector$ComputerHandler::chemicalTank$getChemicalNeeded).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalFilledPercentage", TileEntityAmbientGasCollector$ComputerHandler::chemicalTank$getChemicalFilledPercentage).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalItem", TileEntityAmbientGasCollector$ComputerHandler::chemicalSlot$getChemicalItem).returnType(Object.class).methodDescription("Get chemical slot."));
        register(MethodData.builder("getEnergyItem", TileEntityAmbientGasCollector$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
    }

    public static Object chemicalTank$getChemical(TileEntityAmbientGasCollector subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.chemicalTank));
    }

    public static Object chemicalTank$getChemicalCapacity(TileEntityAmbientGasCollector subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.chemicalTank));
    }

    public static Object chemicalTank$getChemicalNeeded(TileEntityAmbientGasCollector subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.chemicalTank));
    }

    public static Object chemicalTank$getChemicalFilledPercentage(TileEntityAmbientGasCollector subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.chemicalTank));
    }

    public static Object chemicalSlot$getChemicalItem(TileEntityAmbientGasCollector subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.chemicalSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityAmbientGasCollector subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }
}
