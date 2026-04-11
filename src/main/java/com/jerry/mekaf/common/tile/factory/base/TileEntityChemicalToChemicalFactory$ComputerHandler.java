package com.jerry.mekaf.common.tile.factory.base;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityChemicalToChemicalFactory.class)
public class TileEntityChemicalToChemicalFactory$ComputerHandler extends ComputerMethodFactory<TileEntityChemicalToChemicalFactory> {

    private final String[] NAMES_process = new String[] { "process" };

    private final Class[] TYPES_int = new Class[] { int.class };

    public TileEntityChemicalToChemicalFactory$ComputerHandler() {
        register(MethodData.builder("getInput", TileEntityChemicalToChemicalFactory$ComputerHandler::getInput_1).returnType(ChemicalStack.class).arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getInputCapacity", TileEntityChemicalToChemicalFactory$ComputerHandler::inputTank$getInputCapacity).returnType(int.class).methodDescription("Get the capacity of the input.").arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getInputNeeded", TileEntityChemicalToChemicalFactory$ComputerHandler::inputTank$getInputNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the input.").arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getInputFilledPercentage", TileEntityChemicalToChemicalFactory$ComputerHandler::inputTank$getInputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the input.").arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getOutput", TileEntityChemicalToChemicalFactory$ComputerHandler::getOutput_1).returnType(ChemicalStack.class).arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getOutputCapacity", TileEntityChemicalToChemicalFactory$ComputerHandler::outputTank$getOutputCapacity).returnType(long.class).methodDescription("Get the capacity of the output.").arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getOutputNeeded", TileEntityChemicalToChemicalFactory$ComputerHandler::outputTank$getOutputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the output.").arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getOutputFilledPercentage", TileEntityChemicalToChemicalFactory$ComputerHandler::outputTank$getOutputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the output.").arguments(NAMES_process, TYPES_int));
    }

    public static Object getInput_1(TileEntityChemicalToChemicalFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getInput(helper.getInt(0)));
    }

    public static Object inputTank$getInputCapacity(TileEntityChemicalToChemicalFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.getInputTank(helper.getInt(0))));
    }

    public static Object inputTank$getInputNeeded(TileEntityChemicalToChemicalFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.getInputTank(helper.getInt(0))));
    }

    public static Object inputTank$getInputFilledPercentage(TileEntityChemicalToChemicalFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.getInputTank(helper.getInt(0))));
    }

    public static Object getOutput_1(TileEntityChemicalToChemicalFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getOutput(helper.getInt(0)));
    }

    public static Object outputTank$getOutputCapacity(TileEntityChemicalToChemicalFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.getOutputTank(helper.getInt(0))));
    }

    public static Object outputTank$getOutputNeeded(TileEntityChemicalToChemicalFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.getOutputTank(helper.getInt(0))));
    }

    public static Object outputTank$getOutputFilledPercentage(TileEntityChemicalToChemicalFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.getOutputTank(helper.getInt(0))));
    }
}
