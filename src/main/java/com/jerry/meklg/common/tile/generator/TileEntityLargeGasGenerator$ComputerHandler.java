package com.jerry.meklg.common.tile.generator;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityLargeGasGenerator.class)
public class TileEntityLargeGasGenerator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeGasGenerator> {

    public TileEntityLargeGasGenerator$ComputerHandler() {
        register(MethodData.builder("getFuel", TileEntityLargeGasGenerator$ComputerHandler::fuelTank$getFuel).returnType(ChemicalStack.class).methodDescription("Get the contents of the fuel tank."));
        register(MethodData.builder("getFuelCapacity", TileEntityLargeGasGenerator$ComputerHandler::fuelTank$getFuelCapacity).returnType(long.class).methodDescription("Get the capacity of the fuel tank."));
        register(MethodData.builder("getFuelNeeded", TileEntityLargeGasGenerator$ComputerHandler::fuelTank$getFuelNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the fuel tank."));
        register(MethodData.builder("getFuelFilledPercentage", TileEntityLargeGasGenerator$ComputerHandler::fuelTank$getFuelFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the fuel tank."));
        register(MethodData.builder("getFuelItem", TileEntityLargeGasGenerator$ComputerHandler::fuelSlot$getFuelItem).returnType(ItemStack.class).methodDescription("Get the contents of the fuel item slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeGasGenerator$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy item slot."));
        register(MethodData.builder("getBurnRate", TileEntityLargeGasGenerator$ComputerHandler::getBurnRate_0).returnType(double.class));
    }

    public static Object fuelTank$getFuel(TileEntityLargeGasGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.fuelTank));
    }

    public static Object fuelTank$getFuelCapacity(TileEntityLargeGasGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.fuelTank));
    }

    public static Object fuelTank$getFuelNeeded(TileEntityLargeGasGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.fuelTank));
    }

    public static Object fuelTank$getFuelFilledPercentage(TileEntityLargeGasGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.fuelTank));
    }

    public static Object fuelSlot$getFuelItem(TileEntityLargeGasGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.fuelSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeGasGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getBurnRate_0(TileEntityLargeGasGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getUsed());
    }
}
