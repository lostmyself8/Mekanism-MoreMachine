package com.jerry.meklg.common.tile.generator;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

@MethodFactory(target = TileEntitySolarHeatGenerator.class)
public class TileEntitySolarHeatGenerator$ComputerHandler extends ComputerMethodFactory<TileEntitySolarHeatGenerator> {

    public TileEntitySolarHeatGenerator$ComputerHandler() {
        register(MethodData.builder("getHeatedCoolant", TileEntitySolarHeatGenerator$ComputerHandler::superheatedCoolantTank$getHeatedCoolant).returnType(ChemicalStack.class).methodDescription("Get the contents of the heated coolant tank."));
        register(MethodData.builder("getHeatedCoolantCapacity", TileEntitySolarHeatGenerator$ComputerHandler::superheatedCoolantTank$getHeatedCoolantCapacity).returnType(long.class).methodDescription("Get the capacity of the heated coolant tank."));
        register(MethodData.builder("getHeatedCoolantNeeded", TileEntitySolarHeatGenerator$ComputerHandler::superheatedCoolantTank$getHeatedCoolantNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the heated coolant tank."));
        register(MethodData.builder("getHeatedCoolantFilledPercentage", TileEntitySolarHeatGenerator$ComputerHandler::superheatedCoolantTank$getHeatedCoolantFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the heated coolant tank."));
        register(MethodData.builder("getCooledCoolant", TileEntitySolarHeatGenerator$ComputerHandler::cooledCoolantTank$getCooledCoolant).returnType(ChemicalStack.class).methodDescription("Get the contents of the cooled coolant tank."));
        register(MethodData.builder("getCooledCoolantCapacity", TileEntitySolarHeatGenerator$ComputerHandler::cooledCoolantTank$getCooledCoolantCapacity).returnType(long.class).methodDescription("Get the capacity of the cooled coolant tank."));
        register(MethodData.builder("getCooledCoolantNeeded", TileEntitySolarHeatGenerator$ComputerHandler::cooledCoolantTank$getCooledCoolantNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the cooled coolant tank."));
        register(MethodData.builder("getCooledCoolantFilledPercentage", TileEntitySolarHeatGenerator$ComputerHandler::cooledCoolantTank$getCooledCoolantFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the cooled coolant tank."));
        register(MethodData.builder("getFluid", TileEntitySolarHeatGenerator$ComputerHandler::fluidTank$getFluid).returnType(FluidStack.class).methodDescription("Get the contents of the work fluid tank."));
        register(MethodData.builder("getFluidCapacity", TileEntitySolarHeatGenerator$ComputerHandler::fluidTank$getFluidCapacity).returnType(int.class).methodDescription("Get the capacity of the work fluid tank."));
        register(MethodData.builder("getFluidNeeded", TileEntitySolarHeatGenerator$ComputerHandler::fluidTank$getFluidNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the work fluid tank."));
        register(MethodData.builder("getFluidFilledPercentage", TileEntitySolarHeatGenerator$ComputerHandler::fluidTank$getFluidFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the work fluid tank."));
        register(MethodData.builder("getTemperature", TileEntitySolarHeatGenerator$ComputerHandler::heatCapacitor$getTemperature).returnType(double.class).methodDescription("Get the temperature of the solar heat generator in Kelvin."));
        register(MethodData.builder("getEnergyItem", TileEntitySolarHeatGenerator$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy item slot."));
    }

    public static Object superheatedCoolantTank$getHeatedCoolant(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.superheatedCoolantTank));
    }

    public static Object superheatedCoolantTank$getHeatedCoolantCapacity(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.superheatedCoolantTank));
    }

    public static Object superheatedCoolantTank$getHeatedCoolantNeeded(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.superheatedCoolantTank));
    }

    public static Object superheatedCoolantTank$getHeatedCoolantFilledPercentage(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.superheatedCoolantTank));
    }

    public static Object cooledCoolantTank$getCooledCoolant(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.cooledCoolantTank));
    }

    public static Object cooledCoolantTank$getCooledCoolantCapacity(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.cooledCoolantTank));
    }

    public static Object cooledCoolantTank$getCooledCoolantNeeded(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.cooledCoolantTank));
    }

    public static Object cooledCoolantTank$getCooledCoolantFilledPercentage(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.cooledCoolantTank));
    }

    public static Object fluidTank$getFluid(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getStack(subject.fluidTank));
    }

    public static Object fluidTank$getFluidCapacity(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getCapacity(subject.fluidTank));
    }

    public static Object fluidTank$getFluidNeeded(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getNeeded(subject.fluidTank));
    }

    public static Object fluidTank$getFluidFilledPercentage(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getFilledPercentage(subject.fluidTank));
    }

    public static Object heatCapacitor$getTemperature(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper.getTemperature(subject.heatCapacitor));
    }

    public static Object energySlot$getEnergyItem(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }
}
