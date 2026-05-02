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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.WrongMethodTypeException;

@MethodFactory(target = TileEntityLargeSolarNeutronActivator.class)
public class TileEntityLargeSolarNeutronActivator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeSolarNeutronActivator> {

    private static MethodHandle fieldGetter$productionRate = getGetterHandle(TileEntityLargeSolarNeutronActivator.class, "productionRate");

    public TileEntityLargeSolarNeutronActivator$ComputerHandler() {
        register(MethodData.builder("getInput", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputTank$getInput).returnType(ChemicalStack.class).methodDescription("Get the contents of the input tank."));
        register(MethodData.builder("getInputCapacity", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputTank$getInputCapacity).returnType(long.class).methodDescription("Get the capacity of the input tank."));
        register(MethodData.builder("getInputNeeded", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputTank$getInputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the input tank."));
        register(MethodData.builder("getInputFilledPercentage", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputTank$getInputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the input tank."));
        register(MethodData.builder("getOutput", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputTank$getOutput).returnType(ChemicalStack.class).methodDescription("Get the contents of the output tank."));
        register(MethodData.builder("getOutputCapacity", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputTank$getOutputCapacity).returnType(long.class).methodDescription("Get the capacity of the output tank."));
        register(MethodData.builder("getOutputNeeded", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputTank$getOutputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the output tank."));
        register(MethodData.builder("getOutputFilledPercentage", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputTank$getOutputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the output tank."));
        register(MethodData.builder("getPeakProductionRate", TileEntityLargeSolarNeutronActivator$ComputerHandler::getPeakProductionRate_0).returnType(float.class));
        register(MethodData.builder("getProductionRate", TileEntityLargeSolarNeutronActivator$ComputerHandler::getProductionRate_0).returnType(float.class));
        register(MethodData.builder("getInputItem", TileEntityLargeSolarNeutronActivator$ComputerHandler::inputSlot$getInputItem).returnType(ItemStack.class).methodDescription("Get the contents of the input slot."));
        register(MethodData.builder("getOutputItem", TileEntityLargeSolarNeutronActivator$ComputerHandler::outputSlot$getOutputItem).returnType(ItemStack.class).methodDescription("Get the contents of the output slot."));
        register(MethodData.builder("canSeeSun", TileEntityLargeSolarNeutronActivator$ComputerHandler::canSeeSun_0).returnType(boolean.class));
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

    public static Object getPeakProductionRate_0(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getPeakProductionRate());
    }

    private static float getter$productionRate(TileEntityLargeSolarNeutronActivator subject) {
        try {
            return (float) fieldGetter$productionRate.invokeExact(subject);
        } catch (WrongMethodTypeException wmte) {
            throw new RuntimeException("Getter not bound correctly", wmte);
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }

    public static Object getProductionRate_0(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(getter$productionRate(subject));
    }

    public static Object inputSlot$getInputItem(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.inputSlot));
    }

    public static Object outputSlot$getOutputItem(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object canSeeSun_0(TileEntityLargeSolarNeutronActivator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.canSeeSun());
    }
}
