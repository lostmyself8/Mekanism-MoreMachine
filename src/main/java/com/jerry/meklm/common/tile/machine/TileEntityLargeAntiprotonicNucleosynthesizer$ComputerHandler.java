package com.jerry.meklm.common.tile.machine;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityLargeAntiprotonicNucleosynthesizer.class)
public class TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler extends ComputerMethodFactory<TileEntityLargeAntiprotonicNucleosynthesizer> {

    public TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler() {
        register(MethodData.builder("getInputChemical", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasTank$getInputChemical).returnType(ChemicalStack.class).methodDescription("Get the contents of the input gas tank."));
        register(MethodData.builder("getInputChemicalCapacity", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasTank$getInputChemicalCapacity).returnType(long.class).methodDescription("Get the capacity of the input gas tank."));
        register(MethodData.builder("getInputChemicalNeeded", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasTank$getInputChemicalNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the input gas tank."));
        register(MethodData.builder("getInputChemicalFilledPercentage", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasTank$getInputChemicalFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the input gas tank."));
        register(MethodData.builder("getInputChemicalItem", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::gasInputSlot$getInputChemicalItem).returnType(ItemStack.class).methodDescription("Get the contents of the input gas item slot."));
        register(MethodData.builder("getInputItem", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::inputSlot$getInputItem).returnType(ItemStack.class).methodDescription("Get the contents of the input item slot."));
        register(MethodData.builder("getOutputItem", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::outputSlot$getOutputItem).returnType(ItemStack.class).methodDescription("Get the contents of the output slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityLargeAntiprotonicNucleosynthesizer$ComputerHandler::getEnergyUsage_0).returnType(long.class).methodDescription("Get the energy used in the last tick by the machine"));
    }

    public static Object gasTank$getInputChemical(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.gasTank));
    }

    public static Object gasTank$getInputChemicalCapacity(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.gasTank));
    }

    public static Object gasTank$getInputChemicalNeeded(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.gasTank));
    }

    public static Object gasTank$getInputChemicalFilledPercentage(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.gasTank));
    }

    public static Object gasInputSlot$getInputChemicalItem(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.gasInputSlot));
    }

    public static Object inputSlot$getInputItem(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.inputSlot));
    }

    public static Object outputSlot$getOutputItem(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityLargeAntiprotonicNucleosynthesizer subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }
}
