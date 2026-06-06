package com.jerry.meklg.common.tile;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityLargeGasBurningGenerator.class)
public class TileEntityLargeGasBurningGenerator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeGasBurningGenerator> {

    public TileEntityLargeGasBurningGenerator$ComputerHandler() {
        register(MethodData.builder("getFuel", TileEntityLargeGasBurningGenerator$ComputerHandler::fuelTank$getFuel).returnType(Object.class).methodDescription("Get fuel tank."));
        register(MethodData.builder("getFuelCapacity", TileEntityLargeGasBurningGenerator$ComputerHandler::fuelTank$getFuelCapacity).returnType(Object.class).methodDescription("Get fuel tank."));
        register(MethodData.builder("getFuelNeeded", TileEntityLargeGasBurningGenerator$ComputerHandler::fuelTank$getFuelNeeded).returnType(Object.class).methodDescription("Get fuel tank."));
        register(MethodData.builder("getFuelFilledPercentage", TileEntityLargeGasBurningGenerator$ComputerHandler::fuelTank$getFuelFilledPercentage).returnType(Object.class).methodDescription("Get fuel tank."));
        register(MethodData.builder("getFuelItem", TileEntityLargeGasBurningGenerator$ComputerHandler::fuelSlot$getFuelItem).returnType(Object.class).methodDescription("Get fuel item slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeGasBurningGenerator$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy item slot."));
        register(MethodData.builder("getEfficiencyMultiplier", TileEntityLargeGasBurningGenerator$ComputerHandler::getEfficiencyMultiplier_0).returnType(Object.class));
        register(MethodData.builder("getBurnRate", TileEntityLargeGasBurningGenerator$ComputerHandler::getUsed_0).returnType(Object.class));
    }

    public static Object fuelTank$getFuel(TileEntityLargeGasBurningGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.fuelTank));
    }

    public static Object fuelTank$getFuelCapacity(TileEntityLargeGasBurningGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.fuelTank));
    }

    public static Object fuelTank$getFuelNeeded(TileEntityLargeGasBurningGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.fuelTank));
    }

    public static Object fuelTank$getFuelFilledPercentage(TileEntityLargeGasBurningGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.fuelTank));
    }

    public static Object fuelSlot$getFuelItem(TileEntityLargeGasBurningGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fuelSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeGasBurningGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEfficiencyMultiplier_0(TileEntityLargeGasBurningGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEfficiencyMultiplier());
    }

    public static Object getUsed_0(TileEntityLargeGasBurningGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getUsed());
    }
}
