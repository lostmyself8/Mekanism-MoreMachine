package com.jerry.meklg.common.tile;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityLargeHeatGenerator.class)
public class TileEntityLargeHeatGenerator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeHeatGenerator> {

    public TileEntityLargeHeatGenerator$ComputerHandler() {
        register(MethodData.builder("getLava", TileEntityLargeHeatGenerator$ComputerHandler::lavaTank$getLava).returnType(Object.class).methodDescription("Get lava tank."));
        register(MethodData.builder("getLavaCapacity", TileEntityLargeHeatGenerator$ComputerHandler::lavaTank$getLavaCapacity).returnType(Object.class).methodDescription("Get lava tank."));
        register(MethodData.builder("getLavaNeeded", TileEntityLargeHeatGenerator$ComputerHandler::lavaTank$getLavaNeeded).returnType(Object.class).methodDescription("Get lava tank."));
        register(MethodData.builder("getLavaFilledPercentage", TileEntityLargeHeatGenerator$ComputerHandler::lavaTank$getLavaFilledPercentage).returnType(Object.class).methodDescription("Get lava tank."));
        register(MethodData.builder("getTemperature", TileEntityLargeHeatGenerator$ComputerHandler::heatCapacitor$getTemperature).returnType(Object.class).methodDescription("Get generator."));
        register(MethodData.builder("getFuelItem", TileEntityLargeHeatGenerator$ComputerHandler::fuelSlot$getFuelItem).returnType(Object.class).methodDescription("Get fuel item slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeHeatGenerator$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy item slot."));
        register(MethodData.builder("getTransferLoss", TileEntityLargeHeatGenerator$ComputerHandler::getLastTransferLoss_0).returnType(Object.class));
        register(MethodData.builder("getEnvironmentalLoss", TileEntityLargeHeatGenerator$ComputerHandler::getLastEnvironmentLoss_0).returnType(Object.class));
    }

    public static Object lavaTank$getLava(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getStack(subject.lavaTank));
    }

    public static Object lavaTank$getLavaCapacity(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getCapacity(subject.lavaTank));
    }

    public static Object lavaTank$getLavaNeeded(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getNeeded(subject.lavaTank));
    }

    public static Object lavaTank$getLavaFilledPercentage(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getFilledPercentage(subject.lavaTank));
    }

    public static Object heatCapacitor$getTemperature(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerHeatCapacitorWrapper.getTemperature(subject.heatCapacitor));
    }

    public static Object fuelSlot$getFuelItem(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fuelSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getLastTransferLoss_0(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getLastTransferLoss());
    }

    public static Object getLastEnvironmentLoss_0(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getLastEnvironmentLoss());
    }
}
