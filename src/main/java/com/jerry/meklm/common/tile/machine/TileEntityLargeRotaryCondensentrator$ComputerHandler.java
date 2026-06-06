package com.jerry.meklm.common.tile.machine;

import mekanism.api.*;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;
import mekanism.common.util.*;

@MethodFactory(target = TileEntityLargeRotaryCondensentrator.class)
public class TileEntityLargeRotaryCondensentrator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeRotaryCondensentrator> {

    public TileEntityLargeRotaryCondensentrator$ComputerHandler() {
        register(MethodData.builder("getGas", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasTank$getGas).returnType(Object.class).methodDescription("Get gas tank."));
        register(MethodData.builder("getGasCapacity", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasTank$getGasCapacity).returnType(Object.class).methodDescription("Get gas tank."));
        register(MethodData.builder("getGasNeeded", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasTank$getGasNeeded).returnType(Object.class).methodDescription("Get gas tank."));
        register(MethodData.builder("getGasFilledPercentage", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasTank$getGasFilledPercentage).returnType(Object.class).methodDescription("Get gas tank."));
        register(MethodData.builder("getFluid", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidTank$getFluid).returnType(Object.class).methodDescription("Get fluid tank."));
        register(MethodData.builder("getFluidCapacity", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidTank$getFluidCapacity).returnType(Object.class).methodDescription("Get fluid tank."));
        register(MethodData.builder("getFluidNeeded", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidTank$getFluidNeeded).returnType(Object.class).methodDescription("Get fluid tank."));
        register(MethodData.builder("getFluidFilledPercentage", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidTank$getFluidFilledPercentage).returnType(Object.class).methodDescription("Get fluid tank."));
        register(MethodData.builder("getGasItemInput", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasInputSlot$getGasItemInput).returnType(Object.class).methodDescription("Get gas item input slot."));
        register(MethodData.builder("getGasItemOutput", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasOutputSlot$getGasItemOutput).returnType(Object.class).methodDescription("Get gas item output slot."));
        register(MethodData.builder("getFluidItemInput", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidInputSlot$getFluidItemInput).returnType(Object.class).methodDescription("Get fluid item input slot."));
        register(MethodData.builder("getFluidItemOutput", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidOutputSlot$getFluidItemOutput).returnType(Object.class).methodDescription("Get fluid item ouput slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeRotaryCondensentrator$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityLargeRotaryCondensentrator$ComputerHandler::getEnergyUsed_0).returnType(Object.class));
        register(MethodData.builder("isCondensentrating", TileEntityLargeRotaryCondensentrator$ComputerHandler::isCondensentrating_0).returnType(Object.class));
        register(MethodData.builder("setCondensentrating", TileEntityLargeRotaryCondensentrator$ComputerHandler::setCondensentrating_1).returnType(Object.class).arguments(new String[] { "value" }, new Class[] { boolean.class }).requiresPublicSecurity());
    }

    public static Object gasTank$getGas(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.gasTank));
    }

    public static Object gasTank$getGasCapacity(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.gasTank));
    }

    public static Object gasTank$getGasNeeded(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.gasTank));
    }

    public static Object gasTank$getGasFilledPercentage(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.gasTank));
    }

    public static Object fluidTank$getFluid(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getStack(subject.fluidTank));
    }

    public static Object fluidTank$getFluidCapacity(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getCapacity(subject.fluidTank));
    }

    public static Object fluidTank$getFluidNeeded(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getNeeded(subject.fluidTank));
    }

    public static Object fluidTank$getFluidFilledPercentage(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getFilledPercentage(subject.fluidTank));
    }

    public static Object gasInputSlot$getGasItemInput(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.gasInputSlot));
    }

    public static Object gasOutputSlot$getGasItemOutput(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.gasOutputSlot));
    }

    public static Object fluidInputSlot$getFluidItemInput(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidInputSlot));
    }

    public static Object fluidOutputSlot$getFluidItemOutput(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidOutputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsed_0(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }

    public static Object isCondensentrating_0(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.isCondensentrating());
    }

    public static Object setCondensentrating_1(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        subject.setCondensentrating(helper.getBoolean(0));
        return helper.voidResult();
    }
}
