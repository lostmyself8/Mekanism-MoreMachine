package com.jerry.mekmm.common.tile.machine;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityPlantingStation.class)
public class TileEntityPlantingStation$ComputerHandler extends ComputerMethodFactory<TileEntityPlantingStation> {

    public TileEntityPlantingStation$ComputerHandler() {
        register(MethodData.builder("getChemical", TileEntityPlantingStation$ComputerHandler::gasTank$getChemical).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalCapacity", TileEntityPlantingStation$ComputerHandler::gasTank$getChemicalCapacity).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalNeeded", TileEntityPlantingStation$ComputerHandler::gasTank$getChemicalNeeded).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalFilledPercentage", TileEntityPlantingStation$ComputerHandler::gasTank$getChemicalFilledPercentage).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getInput", TileEntityPlantingStation$ComputerHandler::inputSlot$getInput).returnType(Object.class).methodDescription("Get input slot."));
        register(MethodData.builder("getOutput", TileEntityPlantingStation$ComputerHandler::mainOutputSlot$getOutput).returnType(Object.class).methodDescription("Get output slot."));
        register(MethodData.builder("getSecondaryOutput", TileEntityPlantingStation$ComputerHandler::secondaryOutputSlot$getSecondaryOutput).returnType(Object.class).methodDescription("Get secondary output slot."));
        register(MethodData.builder("getChemicalItem", TileEntityPlantingStation$ComputerHandler::gasSlot$getChemicalItem).returnType(Object.class).methodDescription("Get chemical input slot."));
        register(MethodData.builder("getEnergyItem", TileEntityPlantingStation$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityPlantingStation$ComputerHandler::getEnergyUsage_0).returnType(Object.class));
    }

    public static Object gasTank$getChemical(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.gasTank));
    }

    public static Object gasTank$getChemicalCapacity(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.gasTank));
    }

    public static Object gasTank$getChemicalNeeded(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.gasTank));
    }

    public static Object gasTank$getChemicalFilledPercentage(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.gasTank));
    }

    public static Object inputSlot$getInput(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.inputSlot));
    }

    public static Object mainOutputSlot$getOutput(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.mainOutputSlot));
    }

    public static Object secondaryOutputSlot$getSecondaryOutput(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.secondaryOutputSlot));
    }

    public static Object gasSlot$getChemicalItem(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.gasSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityPlantingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsage());
    }
}
