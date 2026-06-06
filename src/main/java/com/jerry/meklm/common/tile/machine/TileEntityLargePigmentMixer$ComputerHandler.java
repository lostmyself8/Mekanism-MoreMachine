package com.jerry.meklm.common.tile.machine;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityLargePigmentMixer.class)
public class TileEntityLargePigmentMixer$ComputerHandler extends ComputerMethodFactory<TileEntityLargePigmentMixer> {

    public TileEntityLargePigmentMixer$ComputerHandler() {
        register(MethodData.builder("getLeftInput", TileEntityLargePigmentMixer$ComputerHandler::leftInputTank$getLeftInput).returnType(Object.class).methodDescription("Get left pigment tank."));
        register(MethodData.builder("getLeftInputCapacity", TileEntityLargePigmentMixer$ComputerHandler::leftInputTank$getLeftInputCapacity).returnType(Object.class).methodDescription("Get left pigment tank."));
        register(MethodData.builder("getLeftInputNeeded", TileEntityLargePigmentMixer$ComputerHandler::leftInputTank$getLeftInputNeeded).returnType(Object.class).methodDescription("Get left pigment tank."));
        register(MethodData.builder("getLeftInputFilledPercentage", TileEntityLargePigmentMixer$ComputerHandler::leftInputTank$getLeftInputFilledPercentage).returnType(Object.class).methodDescription("Get left pigment tank."));
        register(MethodData.builder("getRightInput", TileEntityLargePigmentMixer$ComputerHandler::rightInputTank$getRightInput).returnType(Object.class).methodDescription("Get right pigment tank."));
        register(MethodData.builder("getRightInputCapacity", TileEntityLargePigmentMixer$ComputerHandler::rightInputTank$getRightInputCapacity).returnType(Object.class).methodDescription("Get right pigment tank."));
        register(MethodData.builder("getRightInputNeeded", TileEntityLargePigmentMixer$ComputerHandler::rightInputTank$getRightInputNeeded).returnType(Object.class).methodDescription("Get right pigment tank."));
        register(MethodData.builder("getRightInputFilledPercentage", TileEntityLargePigmentMixer$ComputerHandler::rightInputTank$getRightInputFilledPercentage).returnType(Object.class).methodDescription("Get right pigment tank."));
        register(MethodData.builder("getOutput", TileEntityLargePigmentMixer$ComputerHandler::outputTank$getOutput).returnType(Object.class).methodDescription("Get output pigment tank."));
        register(MethodData.builder("getOutputCapacity", TileEntityLargePigmentMixer$ComputerHandler::outputTank$getOutputCapacity).returnType(Object.class).methodDescription("Get output pigment tank."));
        register(MethodData.builder("getOutputNeeded", TileEntityLargePigmentMixer$ComputerHandler::outputTank$getOutputNeeded).returnType(Object.class).methodDescription("Get output pigment tank."));
        register(MethodData.builder("getOutputFilledPercentage", TileEntityLargePigmentMixer$ComputerHandler::outputTank$getOutputFilledPercentage).returnType(Object.class).methodDescription("Get output pigment tank."));
        register(MethodData.builder("getLeftInputItem", TileEntityLargePigmentMixer$ComputerHandler::leftInputSlot$getLeftInputItem).returnType(Object.class).methodDescription("Get left input slot."));
        register(MethodData.builder("getOutputItem", TileEntityLargePigmentMixer$ComputerHandler::outputSlot$getOutputItem).returnType(Object.class).methodDescription("Get output slot."));
        register(MethodData.builder("getRightInputItem", TileEntityLargePigmentMixer$ComputerHandler::rightInputSlot$getRightInputItem).returnType(Object.class).methodDescription("Get right input slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargePigmentMixer$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityLargePigmentMixer$ComputerHandler::getEnergyUsed_0).returnType(Object.class));
    }

    public static Object leftInputTank$getLeftInput(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.leftInputTank));
    }

    public static Object leftInputTank$getLeftInputCapacity(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.leftInputTank));
    }

    public static Object leftInputTank$getLeftInputNeeded(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.leftInputTank));
    }

    public static Object leftInputTank$getLeftInputFilledPercentage(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.leftInputTank));
    }

    public static Object rightInputTank$getRightInput(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.rightInputTank));
    }

    public static Object rightInputTank$getRightInputCapacity(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.rightInputTank));
    }

    public static Object rightInputTank$getRightInputNeeded(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.rightInputTank));
    }

    public static Object rightInputTank$getRightInputFilledPercentage(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.rightInputTank));
    }

    public static Object outputTank$getOutput(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.outputTank));
    }

    public static Object outputTank$getOutputCapacity(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.outputTank));
    }

    public static Object outputTank$getOutputNeeded(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.outputTank));
    }

    public static Object outputTank$getOutputFilledPercentage(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.outputTank));
    }

    public static Object leftInputSlot$getLeftInputItem(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.leftInputSlot));
    }

    public static Object outputSlot$getOutputItem(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object rightInputSlot$getRightInputItem(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.rightInputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsed_0(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }
}
