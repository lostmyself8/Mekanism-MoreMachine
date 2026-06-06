package com.jerry.meklm.common.tile.machine;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityLargeChemicalInfuser.class)
public class TileEntityLargeChemicalInfuser$ComputerHandler extends ComputerMethodFactory<TileEntityLargeChemicalInfuser> {

    public TileEntityLargeChemicalInfuser$ComputerHandler() {
        register(MethodData.builder("getLeftInput", TileEntityLargeChemicalInfuser$ComputerHandler::leftTank$getLeftInput).returnType(Object.class).methodDescription("Get left input tank."));
        register(MethodData.builder("getLeftInputCapacity", TileEntityLargeChemicalInfuser$ComputerHandler::leftTank$getLeftInputCapacity).returnType(Object.class).methodDescription("Get left input tank."));
        register(MethodData.builder("getLeftInputNeeded", TileEntityLargeChemicalInfuser$ComputerHandler::leftTank$getLeftInputNeeded).returnType(Object.class).methodDescription("Get left input tank."));
        register(MethodData.builder("getLeftInputFilledPercentage", TileEntityLargeChemicalInfuser$ComputerHandler::leftTank$getLeftInputFilledPercentage).returnType(Object.class).methodDescription("Get left input tank."));
        register(MethodData.builder("getRightInput", TileEntityLargeChemicalInfuser$ComputerHandler::rightTank$getRightInput).returnType(Object.class).methodDescription("Get right input tank."));
        register(MethodData.builder("getRightInputCapacity", TileEntityLargeChemicalInfuser$ComputerHandler::rightTank$getRightInputCapacity).returnType(Object.class).methodDescription("Get right input tank."));
        register(MethodData.builder("getRightInputNeeded", TileEntityLargeChemicalInfuser$ComputerHandler::rightTank$getRightInputNeeded).returnType(Object.class).methodDescription("Get right input tank."));
        register(MethodData.builder("getRightInputFilledPercentage", TileEntityLargeChemicalInfuser$ComputerHandler::rightTank$getRightInputFilledPercentage).returnType(Object.class).methodDescription("Get right input tank."));
        register(MethodData.builder("getLeftInputItem", TileEntityLargeChemicalInfuser$ComputerHandler::leftInputSlot$getLeftInputItem).returnType(Object.class).methodDescription("Get left input item slot."));
        register(MethodData.builder("getOutputItem", TileEntityLargeChemicalInfuser$ComputerHandler::outputSlot$getOutputItem).returnType(Object.class).methodDescription("Get output item slot."));
        register(MethodData.builder("getRightInputItem", TileEntityLargeChemicalInfuser$ComputerHandler::rightInputSlot$getRightInputItem).returnType(Object.class).methodDescription("Get right input item slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeChemicalInfuser$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityLargeChemicalInfuser$ComputerHandler::getEnergyUsed_0).returnType(Object.class));
    }

    public static Object leftTank$getLeftInput(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.leftTank));
    }

    public static Object leftTank$getLeftInputCapacity(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.leftTank));
    }

    public static Object leftTank$getLeftInputNeeded(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.leftTank));
    }

    public static Object leftTank$getLeftInputFilledPercentage(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.leftTank));
    }

    public static Object rightTank$getRightInput(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.rightTank));
    }

    public static Object rightTank$getRightInputCapacity(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.rightTank));
    }

    public static Object rightTank$getRightInputNeeded(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.rightTank));
    }

    public static Object rightTank$getRightInputFilledPercentage(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.rightTank));
    }

    public static Object leftInputSlot$getLeftInputItem(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.leftInputSlot));
    }

    public static Object outputSlot$getOutputItem(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object rightInputSlot$getRightInputItem(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.rightInputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsed_0(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }
}
