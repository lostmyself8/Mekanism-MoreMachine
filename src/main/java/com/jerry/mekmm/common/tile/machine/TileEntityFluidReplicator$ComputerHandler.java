package com.jerry.mekmm.common.tile.machine;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityFluidReplicator.class)
public class TileEntityFluidReplicator$ComputerHandler extends ComputerMethodFactory<TileEntityFluidReplicator> {

    public TileEntityFluidReplicator$ComputerHandler() {
        register(MethodData.builder("getInputFluid", TileEntityFluidReplicator$ComputerHandler::fluidInputTank$getInputFluid).returnType(Object.class).methodDescription("Get input fluid tank."));
        register(MethodData.builder("getInputFluidCapacity", TileEntityFluidReplicator$ComputerHandler::fluidInputTank$getInputFluidCapacity).returnType(Object.class).methodDescription("Get input fluid tank."));
        register(MethodData.builder("getInputFluidNeeded", TileEntityFluidReplicator$ComputerHandler::fluidInputTank$getInputFluidNeeded).returnType(Object.class).methodDescription("Get input fluid tank."));
        register(MethodData.builder("getInputFluidFilledPercentage", TileEntityFluidReplicator$ComputerHandler::fluidInputTank$getInputFluidFilledPercentage).returnType(Object.class).methodDescription("Get input fluid tank."));
        register(MethodData.builder("getOutputFluid", TileEntityFluidReplicator$ComputerHandler::fluidOutputTank$getOutputFluid).returnType(Object.class).methodDescription("Get output fluid tank."));
        register(MethodData.builder("getOutputFluidCapacity", TileEntityFluidReplicator$ComputerHandler::fluidOutputTank$getOutputFluidCapacity).returnType(Object.class).methodDescription("Get output fluid tank."));
        register(MethodData.builder("getOutputFluidNeeded", TileEntityFluidReplicator$ComputerHandler::fluidOutputTank$getOutputFluidNeeded).returnType(Object.class).methodDescription("Get output fluid tank."));
        register(MethodData.builder("getOutputFluidFilledPercentage", TileEntityFluidReplicator$ComputerHandler::fluidOutputTank$getOutputFluidFilledPercentage).returnType(Object.class).methodDescription("Get output fluid tank."));
        register(MethodData.builder("getChemical", TileEntityFluidReplicator$ComputerHandler::gasTank$getChemical).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalCapacity", TileEntityFluidReplicator$ComputerHandler::gasTank$getChemicalCapacity).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalNeeded", TileEntityFluidReplicator$ComputerHandler::gasTank$getChemicalNeeded).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getChemicalFilledPercentage", TileEntityFluidReplicator$ComputerHandler::gasTank$getChemicalFilledPercentage).returnType(Object.class).methodDescription("Get chemical tank."));
        register(MethodData.builder("getInputTankOutputSlot", TileEntityFluidReplicator$ComputerHandler::lFluidInputSlot$getInputTankOutputSlot).returnType(Object.class).methodDescription("Get input tank output slot."));
        register(MethodData.builder("getOutputTankOutputSlot", TileEntityFluidReplicator$ComputerHandler::rFluidInputSlot$getOutputTankOutputSlot).returnType(Object.class).methodDescription("Get output tank output slot."));
        register(MethodData.builder("getInputSlot", TileEntityFluidReplicator$ComputerHandler::fluidInputSlot$getInputSlot).returnType(Object.class).methodDescription("Get input slot."));
        register(MethodData.builder("getOutputSlot", TileEntityFluidReplicator$ComputerHandler::fluidOutputSlot$getOutputSlot).returnType(Object.class).methodDescription("Get output slot."));
        register(MethodData.builder("getUUSlot", TileEntityFluidReplicator$ComputerHandler::chemicalSlot$getUUSlot).returnType(Object.class).methodDescription("Get uu slot."));
        register(MethodData.builder("getEnergyItem", TileEntityFluidReplicator$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityFluidReplicator$ComputerHandler::getEnergyUsage_0).returnType(Object.class));
    }

    public static Object fluidInputTank$getInputFluid(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getStack(subject.fluidInputTank));
    }

    public static Object fluidInputTank$getInputFluidCapacity(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getCapacity(subject.fluidInputTank));
    }

    public static Object fluidInputTank$getInputFluidNeeded(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getNeeded(subject.fluidInputTank));
    }

    public static Object fluidInputTank$getInputFluidFilledPercentage(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getFilledPercentage(subject.fluidInputTank));
    }

    public static Object fluidOutputTank$getOutputFluid(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getStack(subject.fluidOutputTank));
    }

    public static Object fluidOutputTank$getOutputFluidCapacity(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getCapacity(subject.fluidOutputTank));
    }

    public static Object fluidOutputTank$getOutputFluidNeeded(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getNeeded(subject.fluidOutputTank));
    }

    public static Object fluidOutputTank$getOutputFluidFilledPercentage(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getFilledPercentage(subject.fluidOutputTank));
    }

    public static Object gasTank$getChemical(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.gasTank));
    }

    public static Object gasTank$getChemicalCapacity(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.gasTank));
    }

    public static Object gasTank$getChemicalNeeded(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.gasTank));
    }

    public static Object gasTank$getChemicalFilledPercentage(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.gasTank));
    }

    public static Object lFluidInputSlot$getInputTankOutputSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.lFluidInputSlot));
    }

    public static Object rFluidInputSlot$getOutputTankOutputSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.rFluidInputSlot));
    }

    public static Object fluidInputSlot$getInputSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidInputSlot));
    }

    public static Object fluidOutputSlot$getOutputSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidOutputSlot));
    }

    public static Object chemicalSlot$getUUSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.chemicalSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsage());
    }
}
