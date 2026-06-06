package com.jerry.mekmm.common.tile.factory;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityPlantingFactory.class)
public class TileEntityPlantingFactory$ComputerHandler extends ComputerMethodFactory<TileEntityPlantingFactory> {

    public TileEntityPlantingFactory$ComputerHandler() {
        register(MethodData.builder("getChemical", TileEntityPlantingFactory$ComputerHandler::gasTank$getChemical).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalCapacity", TileEntityPlantingFactory$ComputerHandler::gasTank$getChemicalCapacity).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalNeeded", TileEntityPlantingFactory$ComputerHandler::gasTank$getChemicalNeeded).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalFilledPercentage", TileEntityPlantingFactory$ComputerHandler::gasTank$getChemicalFilledPercentage).returnType(Object.class).methodDescription("Get chemical tank."));
    }

    public static Object gasTank$getChemical(TileEntityPlantingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.gasTank));
    }

    public static Object gasTank$getChemicalCapacity(TileEntityPlantingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.gasTank));
    }

    public static Object gasTank$getChemicalNeeded(TileEntityPlantingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.gasTank));
    }

    public static Object gasTank$getChemicalFilledPercentage(TileEntityPlantingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.gasTank));
    }
}
