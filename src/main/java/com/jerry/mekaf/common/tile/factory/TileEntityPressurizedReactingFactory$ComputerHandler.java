package com.jerry.mekaf.common.tile.factory;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

@MethodFactory(target = TileEntityPressurizedReactingFactory.class)
public class TileEntityPressurizedReactingFactory$ComputerHandler extends ComputerMethodFactory<TileEntityPressurizedReactingFactory> {

    private final String[] NAMES_process = new String[] { "process" };

    private final Class[] TYPES_int = new Class[] { int.class };

    public TileEntityPressurizedReactingFactory$ComputerHandler() {
        register(MethodData.builder("getInputFluid", TileEntityPressurizedReactingFactory$ComputerHandler::inputFluidTank$getInputFluid).returnType(FluidStack.class).methodDescription("Get the contents of the fluid input."));
        register(MethodData.builder("getInputFluidCapacity", TileEntityPressurizedReactingFactory$ComputerHandler::inputFluidTank$getInputFluidCapacity).returnType(int.class).methodDescription("Get the capacity of the fluid input."));
        register(MethodData.builder("getInputFluidNeeded", TileEntityPressurizedReactingFactory$ComputerHandler::inputFluidTank$getInputFluidNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the fluid input."));
        register(MethodData.builder("getInputFluidFilledPercentage", TileEntityPressurizedReactingFactory$ComputerHandler::inputFluidTank$getInputFluidFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the fluid input."));
        register(MethodData.builder("getInputGas", TileEntityPressurizedReactingFactory$ComputerHandler::inputGasTank$getInputGas).returnType(ChemicalStack.class).methodDescription("Get the contents of the gas input."));
        register(MethodData.builder("getInputGasCapacity", TileEntityPressurizedReactingFactory$ComputerHandler::inputGasTank$getInputGasCapacity).returnType(long.class).methodDescription("Get the capacity of the gas input."));
        register(MethodData.builder("getInputGasNeeded", TileEntityPressurizedReactingFactory$ComputerHandler::inputGasTank$getInputGasNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the gas input."));
        register(MethodData.builder("getInputGasFilledPercentage", TileEntityPressurizedReactingFactory$ComputerHandler::inputGasTank$getInputGasFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the gas input."));
        register(MethodData.builder("getOutputGas", TileEntityPressurizedReactingFactory$ComputerHandler::outputGasTank$getOutputGas).returnType(ChemicalStack.class).methodDescription("Get the contents of the gas output."));
        register(MethodData.builder("getOutputGasCapacity", TileEntityPressurizedReactingFactory$ComputerHandler::outputGasTank$getOutputGasCapacity).returnType(long.class).methodDescription("Get the capacity of the gas output."));
        register(MethodData.builder("getOutputGasNeeded", TileEntityPressurizedReactingFactory$ComputerHandler::outputGasTank$getOutputGasNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the gas output."));
        register(MethodData.builder("getOutputGasFilledPercentage", TileEntityPressurizedReactingFactory$ComputerHandler::outputGasTank$getOutputGasFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the gas output."));
        register(MethodData.builder("getInput", TileEntityPressurizedReactingFactory$ComputerHandler::getInput_1).returnType(ItemStack.class).arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getOutput", TileEntityPressurizedReactingFactory$ComputerHandler::getOutput_1).returnType(ItemStack.class).arguments(NAMES_process, TYPES_int));
    }

    public static Object inputFluidTank$getInputFluid(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getStack(subject.inputFluidTank));
    }

    public static Object inputFluidTank$getInputFluidCapacity(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getCapacity(subject.inputFluidTank));
    }

    public static Object inputFluidTank$getInputFluidNeeded(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getNeeded(subject.inputFluidTank));
    }

    public static Object inputFluidTank$getInputFluidFilledPercentage(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getFilledPercentage(subject.inputFluidTank));
    }

    public static Object inputGasTank$getInputGas(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.inputChemicalTank));
    }

    public static Object inputGasTank$getInputGasCapacity(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.inputChemicalTank));
    }

    public static Object inputGasTank$getInputGasNeeded(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.inputChemicalTank));
    }

    public static Object inputGasTank$getInputGasFilledPercentage(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.inputChemicalTank));
    }

    public static Object outputGasTank$getOutputGas(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.outputChemicalTank));
    }

    public static Object outputGasTank$getOutputGasCapacity(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.outputChemicalTank));
    }

    public static Object outputGasTank$getOutputGasNeeded(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.outputChemicalTank));
    }

    public static Object outputGasTank$getOutputGasFilledPercentage(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.outputChemicalTank));
    }

    public static Object getInput_1(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getInput(helper.getInt(0)));
    }

    public static Object getOutput_1(TileEntityPressurizedReactingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getOutput(helper.getInt(0)));
    }
}
