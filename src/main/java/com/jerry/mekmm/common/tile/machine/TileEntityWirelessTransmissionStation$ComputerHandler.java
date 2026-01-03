package com.jerry.mekmm.common.tile.machine;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

@MethodFactory(target = TileEntityWirelessTransmissionStation.class)
public class TileEntityWirelessTransmissionStation$ComputerHandler extends ComputerMethodFactory<TileEntityWirelessTransmissionStation> {

    private final String[] NAMES_rate = new String[] { "rate" };

    private final Class[] TYPES_long = new Class[] { long.class };

    private final Class[] TYPES_int = new Class[] { int.class };

    private final Class[] TYPES_double = new Class[] { double.class };

    public TileEntityWirelessTransmissionStation$ComputerHandler() {
        register(MethodData.builder("getFluidDrainItem", TileEntityWirelessTransmissionStation$ComputerHandler::drainFluidSlot$getDrainItem).returnType(ItemStack.class).methodDescription("Get the contents of the fluid drain slot."));
        register(MethodData.builder("getFluidFillItem", TileEntityWirelessTransmissionStation$ComputerHandler::fillFluidSlot$getFillItem).returnType(ItemStack.class).methodDescription("Get the contents of the fluid fill slot."));
        register(MethodData.builder("getFluidItemOutput", TileEntityWirelessTransmissionStation$ComputerHandler::fluidOutputSlot$getFluidItemOutput).returnType(ItemStack.class).methodDescription("Get the contents of the fluid item output slot."));
        register(MethodData.builder("getFluidStored", TileEntityWirelessTransmissionStation$ComputerHandler::fluidTank$getFluid).returnType(FluidStack.class).methodDescription("Get the contents of the fluid tank."));
        register(MethodData.builder("getFluidCapacity", TileEntityWirelessTransmissionStation$ComputerHandler::fluidTank$getFluidCapacity).returnType(int.class).methodDescription("Get the capacity of the fluid tank."));
        register(MethodData.builder("getFluidNeeded", TileEntityWirelessTransmissionStation$ComputerHandler::fluidTank$getFluidNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the fluid tank."));
        register(MethodData.builder("getFluidPercentage", TileEntityWirelessTransmissionStation$ComputerHandler::fluidTank$getFluidFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the fluid tank."));
        register(MethodData.builder("getChemicalDrainItem", TileEntityWirelessTransmissionStation$ComputerHandler::drainChemicalSlot$getDrainItem).returnType(ItemStack.class).methodDescription("Get the contents of the chemical drain slot."));
        register(MethodData.builder("getChemicalFillItem", TileEntityWirelessTransmissionStation$ComputerHandler::fillChemicalSlot$getFillItem).returnType(ItemStack.class).methodDescription("Get the contents of the chemical fill slot."));
        register(MethodData.builder("getChemicalStored", TileEntityWirelessTransmissionStation$ComputerHandler::getChemicalTank$getStored).returnType(ChemicalStack.class).methodDescription("Get the contents of the chemical tank."));
        register(MethodData.builder("getChemicalCapacity", TileEntityWirelessTransmissionStation$ComputerHandler::getChemicalTank$getCapacity).returnType(long.class).methodDescription("Get the capacity of the chemical tank."));
        register(MethodData.builder("getChemicalNeeded", TileEntityWirelessTransmissionStation$ComputerHandler::getChemicalTank$getNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the chemical tank."));
        register(MethodData.builder("getChemicalPercentage", TileEntityWirelessTransmissionStation$ComputerHandler::getChemicalTank$getFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the chemical tank."));
        register(MethodData.builder("getItemSlot", TileEntityWirelessTransmissionStation$ComputerHandler::itemSlot$getItemSolt).returnType(ItemStack.class).methodDescription("Get the contents of the item slot."));
        register(MethodData.builder("getTemperature", TileEntityWirelessTransmissionStation$ComputerHandler::heatCapacitor$getTemperature).returnType(double.class).methodDescription("Get the temperature of the transmission in Kelvin."));
        register(MethodData.builder("setEnergyRate", TileEntityWirelessTransmissionStation$ComputerHandler::setEnergyRate).requiresPublicSecurity().methodDescription("Set energy transmission rate.").arguments(NAMES_rate, TYPES_long));
        register(MethodData.builder("setFluidsRate", TileEntityWirelessTransmissionStation$ComputerHandler::setFluidsRate).requiresPublicSecurity().methodDescription("Set fluids transmission rate").arguments(NAMES_rate, TYPES_int));
        register(MethodData.builder("setChemicalsRate", TileEntityWirelessTransmissionStation$ComputerHandler::setChemicalsRate).requiresPublicSecurity().methodDescription("Set chemicals transmission rate").arguments(NAMES_rate, TYPES_long));
        register(MethodData.builder("setItemsRate", TileEntityWirelessTransmissionStation$ComputerHandler::setItemsRate).requiresPublicSecurity().methodDescription("Set items transmission rate").arguments(NAMES_rate, TYPES_int));
        register(MethodData.builder("setHeatRate", TileEntityWirelessTransmissionStation$ComputerHandler::setHeatRate).requiresPublicSecurity().methodDescription("Set heat transmission rate").arguments(NAMES_rate, TYPES_double));
    }

    public static Object drainFluidSlot$getDrainItem(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.fluidDrainSlot));
    }

    public static Object fillFluidSlot$getFillItem(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.fluidFillSlot));
    }

    public static Object fluidOutputSlot$getFluidItemOutput(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.fluidOutputSlot));
    }

    public static Object fluidTank$getFluid(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getStack(subject.fluidTank));
    }

    public static Object fluidTank$getFluidCapacity(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getCapacity(subject.fluidTank));
    }

    public static Object fluidTank$getFluidNeeded(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getNeeded(subject.fluidTank));
    }

    public static Object fluidTank$getFluidFilledPercentage(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getFilledPercentage(subject.fluidTank));
    }

    public static Object drainChemicalSlot$getDrainItem(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.chemicalOutputSlot));
    }

    public static Object fillChemicalSlot$getFillItem(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.chemicalInputSlot));
    }

    public static Object getChemicalTank$getStored(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.chemicalTank));
    }

    public static Object getChemicalTank$getCapacity(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.chemicalTank));
    }

    public static Object getChemicalTank$getNeeded(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.chemicalTank));
    }

    public static Object getChemicalTank$getFilledPercentage(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.chemicalTank));
    }

    public static Object itemSlot$getItemSolt(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.inventorySlot));
    }

    public static Object heatCapacitor$getTemperature(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper.getTemperature(subject.heatCapacitor));
    }

    public static Object setEnergyRate(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        subject.computerSetEnergyRate(helper.getLong(0));
        return helper.voidResult();
    }

    public static Object setFluidsRate(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        subject.computerSetFluidsRate(helper.getInt(0));
        return helper.voidResult();
    }

    public static Object setChemicalsRate(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        subject.computerSetChemicalsRate(helper.getLong(0));
        return helper.voidResult();
    }

    public static Object setItemsRate(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        subject.computerSetItemsRate(helper.getInt(0));
        return helper.voidResult();
    }

    public static Object setHeatRate(TileEntityWirelessTransmissionStation subject, BaseComputerHelper helper) throws ComputerException {
        subject.computerSetHeatRate(helper.getDouble(0));
        return helper.voidResult();
    }
}
