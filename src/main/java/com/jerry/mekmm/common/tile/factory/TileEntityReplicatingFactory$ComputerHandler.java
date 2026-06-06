package com.jerry.mekmm.common.tile.factory;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityReplicatingFactory.class)
public class TileEntityReplicatingFactory$ComputerHandler extends ComputerMethodFactory<TileEntityReplicatingFactory> {

    public TileEntityReplicatingFactory$ComputerHandler() {
        register(MethodData.builder("getChemical", TileEntityReplicatingFactory$ComputerHandler::gasTank$getChemical).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalCapacity", TileEntityReplicatingFactory$ComputerHandler::gasTank$getChemicalCapacity).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalNeeded", TileEntityReplicatingFactory$ComputerHandler::gasTank$getChemicalNeeded).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalFilledPercentage", TileEntityReplicatingFactory$ComputerHandler::gasTank$getChemicalFilledPercentage).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalItem", TileEntityReplicatingFactory$ComputerHandler::gasSlot$getChemicalItem).returnType(Object.class).methodDescription("Get chemical item slot."));
    }

    public static Object gasTank$getChemical(TileEntityReplicatingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.gasTank));
    }

    public static Object gasTank$getChemicalCapacity(TileEntityReplicatingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.gasTank));
    }

    public static Object gasTank$getChemicalNeeded(TileEntityReplicatingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.gasTank));
    }

    public static Object gasTank$getChemicalFilledPercentage(TileEntityReplicatingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.gasTank));
    }

    public static Object gasSlot$getChemicalItem(TileEntityReplicatingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.gasSlot));
    }
}
