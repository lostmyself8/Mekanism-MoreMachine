package com.jerry.meklm.common.tile.machine;

import mekanism.api.*;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityLargeAntiprotonicNucleosynthesizer.class)
public class TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler extends ComputerMethodFactory<TileEntityLargeAntiprotonicNucleosynthesizer> {

    public TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler() {
        register(MethodData.builder("getInputChemical", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasTank$getInputChemical).returnType(Object.class).methodDescription("Get input gas tank."));
        register(MethodData.builder("getInputChemicalCapacity", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasTank$getInputChemicalCapacity).returnType(Object.class).methodDescription("Get input gas tank."));
        register(MethodData.builder("getInputChemicalNeeded", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasTank$getInputChemicalNeeded).returnType(Object.class).methodDescription("Get input gas tank."));
        register(MethodData.builder("getInputChemicalFilledPercentage", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasTank$getInputChemicalFilledPercentage).returnType(Object.class).methodDescription("Get input gas tank."));
        register(MethodData.builder("getInputChemicalItem", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasInputSlot$getInputChemicalItem).returnType(Object.class).methodDescription("Get input gas item slot."));
        register(MethodData.builder("getInputItem", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::inputSlot$getInputItem).returnType(Object.class).methodDescription("Get input item slot."));
        register(MethodData.builder("getOutputItem", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::outputSlot$getOutputItem).returnType(Object.class).methodDescription("Get output slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::getEnergyUsed_0).returnType(Object.class));
    }

    public static Object gasTank$getInputChemical(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.gasTank));
    }

    public static Object gasTank$getInputChemicalCapacity(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.gasTank));
    }

    public static Object gasTank$getInputChemicalNeeded(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.gasTank));
    }

    public static Object gasTank$getInputChemicalFilledPercentage(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.gasTank));
    }

    public static Object gasInputSlot$getInputChemicalItem(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.gasInputSlot));
    }

    public static Object inputSlot$getInputItem(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.inputSlot));
    }

    public static Object outputSlot$getOutputItem(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsed_0(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }
}
