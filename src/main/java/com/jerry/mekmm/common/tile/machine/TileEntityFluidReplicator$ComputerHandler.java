package com.jerry.mekmm.common.tile.machine;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

@MethodFactory(target = TileEntityFluidReplicator.class)
public class TileEntityFluidReplicator$ComputerHandler extends ComputerMethodFactory<TileEntityFluidReplicator> {

    public TileEntityFluidReplicator$ComputerHandler() {
        register(MethodData.builder("getInput", TileEntityFluidReplicator$ComputerHandler::inputTank$getFluid).returnType(FluidStack.class).methodDescription("Get the contents of the input tank."));
        register(MethodData.builder("getInputCapacity", TileEntityFluidReplicator$ComputerHandler::inputTank$getFluidCapacity).returnType(int.class).methodDescription("Get the capacity of the input tank."));
        register(MethodData.builder("getInputNeeded", TileEntityFluidReplicator$ComputerHandler::inputTank$getFluidNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the input tank."));
        register(MethodData.builder("getInputFilledPercentage", TileEntityFluidReplicator$ComputerHandler::inputTank$getFluidFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the input tank."));
        register(MethodData.builder("getOutput", TileEntityFluidReplicator$ComputerHandler::outputTank$getFluid).returnType(FluidStack.class).methodDescription("Get the contents of the output tank."));
        register(MethodData.builder("getOutputCapacity", TileEntityFluidReplicator$ComputerHandler::outputTank$getFluidCapacity).returnType(int.class).methodDescription("Get the capacity of the output tank."));
        register(MethodData.builder("getOutputNeeded", TileEntityFluidReplicator$ComputerHandler::outputTank$getFluidNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the output tank."));
        register(MethodData.builder("getOutputFilledPercentage", TileEntityFluidReplicator$ComputerHandler::outputTank$getFluidFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the output tank."));
        register(MethodData.builder("getUU", TileEntityFluidReplicator$ComputerHandler::uuTank$getChemical).returnType(ChemicalStack.class).methodDescription("Get the contents of the uu tank."));
        register(MethodData.builder("getUUCapacity", TileEntityFluidReplicator$ComputerHandler::uuTank$getChemicalCapacity).returnType(long.class).methodDescription("Get the capacity of the uu tank."));
        register(MethodData.builder("getUUNeeded", TileEntityFluidReplicator$ComputerHandler::uuTank$getChemicalNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the uu tank."));
        register(MethodData.builder("getUUFilledPercentage", TileEntityFluidReplicator$ComputerHandler::uuTank$getChemicalFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the uu tank."));
        register(MethodData.builder("getInputTankDrainSlot", TileEntityFluidReplicator$ComputerHandler::inputTankOutputSlot$getInputTankDrainSlot).returnType(ItemStack.class).methodDescription("Get the contents of the input tank drain slot."));
        register(MethodData.builder("getOutputTankDrainSlot", TileEntityFluidReplicator$ComputerHandler::outputTankOutputSlot$getOutputTankDrainSlot).returnType(ItemStack.class).methodDescription("Get the contents of the output tank drain slot."));
        register(MethodData.builder("getInputSlot", TileEntityFluidReplicator$ComputerHandler::inputSlot$getInputSlot).returnType(ItemStack.class).methodDescription("Get the contents of the input slot."));
        register(MethodData.builder("getOutputSlot", TileEntityFluidReplicator$ComputerHandler::outputSlot$getOutputSlot).returnType(ItemStack.class).methodDescription("Get the contents of the output slot."));
        register(MethodData.builder("getUUSlot", TileEntityFluidReplicator$ComputerHandler::uuSlot$getUUSlot).returnType(ItemStack.class).methodDescription("Get the contents of the uu slot."));
        register(MethodData.builder("getEnergyItem", TileEntityFluidReplicator$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityFluidReplicator$ComputerHandler::getEnergyUsage_0).returnType(long.class).methodDescription("Get the energy used in the last tick by the machine"));
    }

    public static Object inputTank$getFluid(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getStack(subject.inputTank));
    }

    public static Object inputTank$getFluidCapacity(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getCapacity(subject.inputTank));
    }

    public static Object inputTank$getFluidNeeded(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getNeeded(subject.inputTank));
    }

    public static Object inputTank$getFluidFilledPercentage(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getFilledPercentage(subject.inputTank));
    }

    public static Object outputTank$getFluid(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getStack(subject.outputTank));
    }

    public static Object outputTank$getFluidCapacity(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getCapacity(subject.outputTank));
    }

    public static Object outputTank$getFluidNeeded(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getNeeded(subject.outputTank));
    }

    public static Object outputTank$getFluidFilledPercentage(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getFilledPercentage(subject.outputTank));
    }

    public static Object uuTank$getChemical(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.uuTank));
    }

    public static Object uuTank$getChemicalCapacity(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.uuTank));
    }

    public static Object uuTank$getChemicalNeeded(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.uuTank));
    }

    public static Object uuTank$getChemicalFilledPercentage(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.uuTank));
    }

    public static Object inputTankOutputSlot$getInputTankDrainSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.inputTankDrainSlot));
    }

    public static Object outputTankOutputSlot$getOutputTankDrainSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.outputTankDrainSlot));
    }

    public static Object inputSlot$getInputSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.fluidInputSlot));
    }

    public static Object outputSlot$getOutputSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.fluidOutputSlot));
    }

    public static Object uuSlot$getUUSlot(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.uuSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityFluidReplicator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsage());
    }
}
