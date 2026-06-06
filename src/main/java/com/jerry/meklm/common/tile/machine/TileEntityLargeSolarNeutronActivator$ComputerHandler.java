package com.jerry.meklm.common.tile.machine;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityLargeSolarNeutronActivator.class)
public class TileEntityLargeSolarNeutronActivator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeSolarNeutronActivator> {

    public TileEntityLargeSolarNeutronActivator$ComputerHandler() {
        register(MethodData.builder("getInput", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputTank$getInput).returnType(Object.class).methodDescription("Get input tank."));
        register(MethodData.builder("getInputCapacity", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputTank$getInputCapacity).returnType(Object.class).methodDescription("Get input tank."));
        register(MethodData.builder("getInputNeeded", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputTank$getInputNeeded).returnType(Object.class).methodDescription("Get input tank."));
        register(MethodData.builder("getInputFilledPercentage", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputTank$getInputFilledPercentage).returnType(Object.class).methodDescription("Get input tank."));
        register(MethodData.builder("getOutput", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputTank$getOutput).returnType(Object.class).methodDescription("Get output tank."));
        register(MethodData.builder("getOutputCapacity", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputTank$getOutputCapacity).returnType(Object.class).methodDescription("Get output tank."));
        register(MethodData.builder("getOutputNeeded", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputTank$getOutputNeeded).returnType(Object.class).methodDescription("Get output tank."));
        register(MethodData.builder("getOutputFilledPercentage", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputTank$getOutputFilledPercentage).returnType(Object.class).methodDescription("Get output tank."));
        register(MethodData.builder("getInputItem", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputSlot$getInputItem).returnType(Object.class).methodDescription("Get input slot."));
        register(MethodData.builder("getOutputItem", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputSlot$getOutputItem).returnType(Object.class).methodDescription("Get output slot."));
        register(MethodData.builder("getProductionRate", TileEntityLargeSolarNeutronActivator$ComputerHandler::productionRate$getProductionRate).returnType(Object.class));
        register(MethodData.builder("canSeeSun", TileEntityLargeSolarNeutronActivator$ComputerHandler::canSeeSun_0).returnType(Object.class));
    }

    public static Object inputTank$getInput(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.inputTank));
    }

    public static Object inputTank$getInputCapacity(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.inputTank));
    }

    public static Object inputTank$getInputNeeded(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.inputTank));
    }

    public static Object inputTank$getInputFilledPercentage(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.inputTank));
    }

    public static Object outputTank$getOutput(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.outputTank));
    }

    public static Object outputTank$getOutputCapacity(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.outputTank));
    }

    public static Object outputTank$getOutputNeeded(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.outputTank));
    }

    public static Object outputTank$getOutputFilledPercentage(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.outputTank));
    }

    public static Object inputSlot$getInputItem(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.inputSlot));
    }

    public static Object outputSlot$getOutputItem(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object productionRate$getProductionRate(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getProductionRate());
    }

    public static Object canSeeSun_0(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.canSeeSun());
    }
}
