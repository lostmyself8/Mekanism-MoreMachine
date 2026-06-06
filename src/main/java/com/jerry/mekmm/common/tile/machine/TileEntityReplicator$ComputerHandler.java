package com.jerry.mekmm.common.tile.machine;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityReplicator.class)
public class TileEntityReplicator$ComputerHandler extends ComputerMethodFactory<TileEntityReplicator> {

    public TileEntityReplicator$ComputerHandler() {
        register(MethodData.builder("getChemical", TileEntityReplicator$ComputerHandler::gasTank$getChemical).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalCapacity", TileEntityReplicator$ComputerHandler::gasTank$getChemicalCapacity).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalNeeded", TileEntityReplicator$ComputerHandler::gasTank$getChemicalNeeded).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalFilledPercentage", TileEntityReplicator$ComputerHandler::gasTank$getChemicalFilledPercentage).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getItemInput", TileEntityReplicator$ComputerHandler::inputSlot$getItemInput).returnType(Object.class).methodDescription("Get item input slot."));
        register(MethodData.builder("getOutput", TileEntityReplicator$ComputerHandler::outputSlot$getOutput).returnType(Object.class).methodDescription("Get output slot."));
        register(MethodData.builder("getChemicalInput", TileEntityReplicator$ComputerHandler::gasSlot$getChemicalInput).returnType(Object.class).methodDescription("Get chemical input slot."));
        register(MethodData.builder("getEnergyItem", TileEntityReplicator$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityReplicator$ComputerHandler::getEnergyUsage_0).returnType(Object.class));
    }

    public static Object gasTank$getChemical(TileEntityReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.gasTank));
    }

    public static Object gasTank$getChemicalCapacity(TileEntityReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.gasTank));
    }

    public static Object gasTank$getChemicalNeeded(TileEntityReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.gasTank));
    }

    public static Object gasTank$getChemicalFilledPercentage(TileEntityReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.gasTank));
    }

    public static Object inputSlot$getItemInput(TileEntityReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.inputSlot));
    }

    public static Object outputSlot$getOutput(TileEntityReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object gasSlot$getChemicalInput(TileEntityReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.gasSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsage());
    }
}
