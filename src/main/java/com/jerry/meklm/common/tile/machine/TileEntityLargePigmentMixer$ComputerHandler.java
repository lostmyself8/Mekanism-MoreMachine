package com.jerry.meklm.common.tile.machine;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityLargePigmentMixer.class)
public class TileEntityLargePigmentMixer$ComputerHandler extends ComputerMethodFactory<TileEntityLargePigmentMixer> {

    public TileEntityLargePigmentMixer$ComputerHandler() {
        register(MethodData.builder("getLeftInput", TileEntityLargePigmentMixer$ComputerHandler::leftInputTank$getLeftInput).returnType(ChemicalStack.class).methodDescription("Get the contents of the left pigment tank."));
        register(MethodData.builder("getLeftInputCapacity", TileEntityLargePigmentMixer$ComputerHandler::leftInputTank$getLeftInputCapacity).returnType(long.class).methodDescription("Get the capacity of the left pigment tank."));
        register(MethodData.builder("getLeftInputNeeded", TileEntityLargePigmentMixer$ComputerHandler::leftInputTank$getLeftInputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the left pigment tank."));
        register(MethodData.builder("getLeftInputFilledPercentage", TileEntityLargePigmentMixer$ComputerHandler::leftInputTank$getLeftInputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the left pigment tank."));
        register(MethodData.builder("getRightInput", TileEntityLargePigmentMixer$ComputerHandler::rightInputTank$getRightInput).returnType(ChemicalStack.class).methodDescription("Get the contents of the right pigment tank."));
        register(MethodData.builder("getRightInputCapacity", TileEntityLargePigmentMixer$ComputerHandler::rightInputTank$getRightInputCapacity).returnType(long.class).methodDescription("Get the capacity of the right pigment tank."));
        register(MethodData.builder("getRightInputNeeded", TileEntityLargePigmentMixer$ComputerHandler::rightInputTank$getRightInputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the right pigment tank."));
        register(MethodData.builder("getRightInputFilledPercentage", TileEntityLargePigmentMixer$ComputerHandler::rightInputTank$getRightInputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the right pigment tank."));
        register(MethodData.builder("getOutput", TileEntityLargePigmentMixer$ComputerHandler::outputTank$getOutput).returnType(ChemicalStack.class).methodDescription("Get the contents of the output pigment tank."));
        register(MethodData.builder("getOutputCapacity", TileEntityLargePigmentMixer$ComputerHandler::outputTank$getOutputCapacity).returnType(long.class).methodDescription("Get the capacity of the output pigment tank."));
        register(MethodData.builder("getOutputNeeded", TileEntityLargePigmentMixer$ComputerHandler::outputTank$getOutputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the output pigment tank."));
        register(MethodData.builder("getOutputFilledPercentage", TileEntityLargePigmentMixer$ComputerHandler::outputTank$getOutputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the output pigment tank."));
        register(MethodData.builder("getLeftInputItem", TileEntityLargePigmentMixer$ComputerHandler::leftInputSlot$getLeftInputItem).returnType(ItemStack.class).methodDescription("Get the contents of the left input slot."));
        register(MethodData.builder("getOutputItem", TileEntityLargePigmentMixer$ComputerHandler::outputSlot$getOutputItem).returnType(ItemStack.class).methodDescription("Get the contents of the output slot."));
        register(MethodData.builder("getRightInputItem", TileEntityLargePigmentMixer$ComputerHandler::rightInputSlot$getRightInputItem).returnType(ItemStack.class).methodDescription("Get the contents of the right input slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargePigmentMixer$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityLargePigmentMixer$ComputerHandler::getEnergyUsage_0).returnType(long.class).methodDescription("Get the energy used in the last tick by the machine"));
    }

    public static Object leftInputTank$getLeftInput(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.leftInputTank));
    }

    public static Object leftInputTank$getLeftInputCapacity(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.leftInputTank));
    }

    public static Object leftInputTank$getLeftInputNeeded(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.leftInputTank));
    }

    public static Object leftInputTank$getLeftInputFilledPercentage(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.leftInputTank));
    }

    public static Object rightInputTank$getRightInput(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.rightInputTank));
    }

    public static Object rightInputTank$getRightInputCapacity(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.rightInputTank));
    }

    public static Object rightInputTank$getRightInputNeeded(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.rightInputTank));
    }

    public static Object rightInputTank$getRightInputFilledPercentage(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.rightInputTank));
    }

    public static Object outputTank$getOutput(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.outputTank));
    }

    public static Object outputTank$getOutputCapacity(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.outputTank));
    }

    public static Object outputTank$getOutputNeeded(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.outputTank));
    }

    public static Object outputTank$getOutputFilledPercentage(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.outputTank));
    }

    public static Object leftInputSlot$getLeftInputItem(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.leftInputSlot));
    }

    public static Object outputSlot$getOutputItem(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object rightInputSlot$getRightInputItem(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.rightInputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityLargePigmentMixer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }
}
