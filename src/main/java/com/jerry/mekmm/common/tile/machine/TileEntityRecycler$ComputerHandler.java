package com.jerry.mekmm.common.tile.machine;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityRecycler.class)
public class TileEntityRecycler$ComputerHandler extends ComputerMethodFactory<TileEntityRecycler> {

    public TileEntityRecycler$ComputerHandler() {
        register(MethodData.builder("getInput", TileEntityRecycler$ComputerHandler::inputSlot$getInput).returnType(Object.class).methodDescription("Get input slot."));
        register(MethodData.builder("getOutput", TileEntityRecycler$ComputerHandler::chanceOutputSlot$getOutput).returnType(Object.class).methodDescription("Get output slot."));
        register(MethodData.builder("getEnergyItem", TileEntityRecycler$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityRecycler$ComputerHandler::getEnergyUsage_0).returnType(Object.class));
    }

    public static Object inputSlot$getInput(TileEntityRecycler subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.inputSlot));
    }

    public static Object chanceOutputSlot$getOutput(TileEntityRecycler subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.chanceOutputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityRecycler subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityRecycler subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsage());
    }
}
