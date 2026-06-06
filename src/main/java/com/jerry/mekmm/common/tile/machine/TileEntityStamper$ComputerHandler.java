package com.jerry.mekmm.common.tile.machine;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityStamper.class)
public class TileEntityStamper$ComputerHandler extends ComputerMethodFactory<TileEntityStamper> {

    public TileEntityStamper$ComputerHandler() {
        register(MethodData.builder("getItemInput", TileEntityStamper$ComputerHandler::inputSlot$getItemInput).returnType(Object.class).methodDescription("Get item input slot."));
        register(MethodData.builder("getMoldInput", TileEntityStamper$ComputerHandler::moldSlot$getMoldInput).returnType(Object.class).methodDescription("Get mold input slot."));
        register(MethodData.builder("getOutput", TileEntityStamper$ComputerHandler::outputSlot$getOutput).returnType(Object.class).methodDescription("Get output slot."));
        register(MethodData.builder("getEnergyItem", TileEntityStamper$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityStamper$ComputerHandler::getEnergyUsage_0).returnType(Object.class));
    }

    public static Object inputSlot$getItemInput(TileEntityStamper subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.inputSlot));
    }

    public static Object moldSlot$getMoldInput(TileEntityStamper subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.moldSlot));
    }

    public static Object outputSlot$getOutput(TileEntityStamper subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityStamper subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityStamper subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsage());
    }
}
