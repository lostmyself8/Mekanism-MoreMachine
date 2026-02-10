package com.jerry.meklm.common.tile.machine;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.BaseComputerHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.ComputerMethodFactory;
import mekanism.common.integration.computer.MethodData;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityLargeChemicalInfuser.class)
public class TileEntityLargeChemicalInfuser$ComputerHandler extends ComputerMethodFactory<TileEntityLargeChemicalInfuser> {

    public TileEntityLargeChemicalInfuser$ComputerHandler() {
        register(MethodData.builder("getLeftInput", TileEntityLargeChemicalInfuser$ComputerHandler::leftTank$getLeftInput).returnType(ChemicalStack.class).methodDescription("Get the contents of the left input tank."));
        register(MethodData.builder("getLeftInputCapacity", TileEntityLargeChemicalInfuser$ComputerHandler::leftTank$getLeftInputCapacity).returnType(long.class).methodDescription("Get the capacity of the left input tank."));
        register(MethodData.builder("getLeftInputNeeded", TileEntityLargeChemicalInfuser$ComputerHandler::leftTank$getLeftInputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the left input tank."));
        register(MethodData.builder("getLeftInputFilledPercentage", TileEntityLargeChemicalInfuser$ComputerHandler::leftTank$getLeftInputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the left input tank."));
        register(MethodData.builder("getRightInput", TileEntityLargeChemicalInfuser$ComputerHandler::rightTank$getRightInput).returnType(ChemicalStack.class).methodDescription("Get the contents of the right input tank."));
        register(MethodData.builder("getRightInputCapacity", TileEntityLargeChemicalInfuser$ComputerHandler::rightTank$getRightInputCapacity).returnType(long.class).methodDescription("Get the capacity of the right input tank."));
        register(MethodData.builder("getRightInputNeeded", TileEntityLargeChemicalInfuser$ComputerHandler::rightTank$getRightInputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the right input tank."));
        register(MethodData.builder("getRightInputFilledPercentage", TileEntityLargeChemicalInfuser$ComputerHandler::rightTank$getRightInputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the right input tank."));
        register(MethodData.builder("getOutput", TileEntityLargeChemicalInfuser$ComputerHandler::centerTank$getOutput).returnType(ChemicalStack.class).methodDescription("Get the contents of the output (center) tank."));
        register(MethodData.builder("getOutputCapacity", TileEntityLargeChemicalInfuser$ComputerHandler::centerTank$getOutputCapacity).returnType(long.class).methodDescription("Get the capacity of the output (center) tank."));
        register(MethodData.builder("getOutputNeeded", TileEntityLargeChemicalInfuser$ComputerHandler::centerTank$getOutputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the output (center) tank."));
        register(MethodData.builder("getOutputFilledPercentage", TileEntityLargeChemicalInfuser$ComputerHandler::centerTank$getOutputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the output (center) tank."));
        register(MethodData.builder("getLeftInputItem", TileEntityLargeChemicalInfuser$ComputerHandler::leftInputSlot$getLeftInputItem).returnType(ItemStack.class).methodDescription("Get the contents of the left input item slot."));
        register(MethodData.builder("getOutputItem", TileEntityLargeChemicalInfuser$ComputerHandler::outputSlot$getOutputItem).returnType(ItemStack.class).methodDescription("Get the contents of the output item slot."));
        register(MethodData.builder("getRightInputItem", TileEntityLargeChemicalInfuser$ComputerHandler::rightInputSlot$getRightInputItem).returnType(ItemStack.class).methodDescription("Get the contents of the right input item slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeChemicalInfuser$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityLargeChemicalInfuser$ComputerHandler::getEnergyUsage_0).returnType(long.class).methodDescription("Get the energy used in the last tick by the machine"));
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

    public static Object centerTank$getOutput(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.centerTank));
    }

    public static Object centerTank$getOutputCapacity(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.centerTank));
    }

    public static Object centerTank$getOutputNeeded(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.centerTank));
    }

    public static Object centerTank$getOutputFilledPercentage(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.centerTank));
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

    public static Object getEnergyUsage_0(TileEntityLargeChemicalInfuser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }
}
