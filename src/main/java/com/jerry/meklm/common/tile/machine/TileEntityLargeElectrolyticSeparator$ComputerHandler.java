package com.jerry.meklm.common.tile.machine;

import mekanism.api.*;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;
import mekanism.common.tile.TileEntityChemicalTank.GasMode;
import mekanism.common.util.*;

@MethodFactory(target = TileEntityLargeElectrolyticSeparator.class)
public class TileEntityLargeElectrolyticSeparator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeElectrolyticSeparator> {

    public TileEntityLargeElectrolyticSeparator$ComputerHandler() {
        register(MethodData.builder("getInput", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidTank$getInput).returnType(Object.class).methodDescription("Get input tank."));
        register(MethodData.builder("getInputCapacity", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidTank$getInputCapacity).returnType(Object.class).methodDescription("Get input tank."));
        register(MethodData.builder("getInputNeeded", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidTank$getInputNeeded).returnType(Object.class).methodDescription("Get input tank."));
        register(MethodData.builder("getInputFilledPercentage", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidTank$getInputFilledPercentage).returnType(Object.class).methodDescription("Get input tank."));
        register(MethodData.builder("getLeftOutput", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftTank$getLeftOutput).returnType(Object.class).methodDescription("Get left output tank."));
        register(MethodData.builder("getLeftOutputCapacity", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftTank$getLeftOutputCapacity).returnType(Object.class).methodDescription("Get left output tank."));
        register(MethodData.builder("getLeftOutputNeeded", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftTank$getLeftOutputNeeded).returnType(Object.class).methodDescription("Get left output tank."));
        register(MethodData.builder("getLeftOutputFilledPercentage", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftTank$getLeftOutputFilledPercentage).returnType(Object.class).methodDescription("Get left output tank."));
        register(MethodData.builder("getRightOutput", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightTank$getRightOutput).returnType(Object.class).methodDescription("Get right output tank."));
        register(MethodData.builder("getRightOutputCapacity", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightTank$getRightOutputCapacity).returnType(Object.class).methodDescription("Get right output tank."));
        register(MethodData.builder("getRightOutputNeeded", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightTank$getRightOutputNeeded).returnType(Object.class).methodDescription("Get right output tank."));
        register(MethodData.builder("getRightOutputFilledPercentage", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightTank$getRightOutputFilledPercentage).returnType(Object.class).methodDescription("Get right output tank."));
        register(MethodData.builder("getInputItem", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidSlot$getInputItem).returnType(Object.class).methodDescription("Get input item slot."));
        register(MethodData.builder("getLeftOutputItem", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftOutputSlot$getLeftOutputItem).returnType(Object.class).methodDescription("Get left output item slot."));
        register(MethodData.builder("getRightOutputItem", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightOutputSlot$getRightOutputItem).returnType(Object.class).methodDescription("Get right output item slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeElectrolyticSeparator$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getLeftOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::dumpLeft$getLeftOutputDumpingMode).returnType(Object.class));
        register(MethodData.builder("getRightOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::dumpRight$getRightOutputDumpingMode).returnType(Object.class));
        register(MethodData.builder("getEnergyUsage", TileEntityLargeElectrolyticSeparator$ComputerHandler::getEnergyUsed_0).returnType(Object.class));
        register(MethodData.builder("setLeftOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::setLeftOutputDumpingMode_1).returnType(Object.class).arguments(new String[] { "mode" }, new Class[] { GasMode.class }).requiresPublicSecurity());
        register(MethodData.builder("incrementLeftOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::incrementLeftOutputDumpingMode_0).returnType(Object.class).requiresPublicSecurity());
        register(MethodData.builder("decrementLeftOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::decrementLeftOutputDumpingMode_0).returnType(Object.class).requiresPublicSecurity());
        register(MethodData.builder("setRightOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::setRightOutputDumpingMode_1).returnType(Object.class).arguments(new String[] { "mode" }, new Class[] { GasMode.class }).requiresPublicSecurity());
        register(MethodData.builder("incrementRightOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::incrementRightOutputDumpingMode_0).returnType(Object.class).requiresPublicSecurity());
        register(MethodData.builder("decrementRightOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::decrementRightOutputDumpingMode_0).returnType(Object.class).requiresPublicSecurity());
    }

    public static Object fluidTank$getInput(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getStack(subject.fluidTank));
    }

    public static Object fluidTank$getInputCapacity(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getCapacity(subject.fluidTank));
    }

    public static Object fluidTank$getInputNeeded(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getNeeded(subject.fluidTank));
    }

    public static Object fluidTank$getInputFilledPercentage(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getFilledPercentage(subject.fluidTank));
    }

    public static Object leftTank$getLeftOutput(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.leftTank));
    }

    public static Object leftTank$getLeftOutputCapacity(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.leftTank));
    }

    public static Object leftTank$getLeftOutputNeeded(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.leftTank));
    }

    public static Object leftTank$getLeftOutputFilledPercentage(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.leftTank));
    }

    public static Object rightTank$getRightOutput(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.rightTank));
    }

    public static Object rightTank$getRightOutputCapacity(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.rightTank));
    }

    public static Object rightTank$getRightOutputNeeded(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.rightTank));
    }

    public static Object rightTank$getRightOutputFilledPercentage(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.rightTank));
    }

    public static Object fluidSlot$getInputItem(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidSlot));
    }

    public static Object leftOutputSlot$getLeftOutputItem(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.leftOutputSlot));
    }

    public static Object rightOutputSlot$getRightOutputItem(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.rightOutputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object dumpLeft$getLeftOutputDumpingMode(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.dumpLeft);
    }

    public static Object dumpRight$getRightOutputDumpingMode(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.dumpRight);
    }

    public static Object getEnergyUsed_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }

    public static Object setLeftOutputDumpingMode_1(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.setLeftOutputDumpingMode(helper.getEnum(0, GasMode.class));
        return helper.voidResult();
    }

    public static Object incrementLeftOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.incrementLeftOutputDumpingMode();
        return helper.voidResult();
    }

    public static Object decrementLeftOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.decrementLeftOutputDumpingMode();
        return helper.voidResult();
    }

    public static Object setRightOutputDumpingMode_1(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.setRightOutputDumpingMode(helper.getEnum(0, GasMode.class));
        return helper.voidResult();
    }

    public static Object incrementRightOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.incrementRightOutputDumpingMode();
        return helper.voidResult();
    }

    public static Object decrementRightOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.decrementRightOutputDumpingMode();
        return helper.voidResult();
    }
}
