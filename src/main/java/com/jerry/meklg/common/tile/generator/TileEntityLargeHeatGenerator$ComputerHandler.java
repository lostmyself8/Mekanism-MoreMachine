package com.jerry.meklg.common.tile.generator;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

@MethodFactory(target = TileEntityLargeHeatGenerator.class)
public class TileEntityLargeHeatGenerator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeHeatGenerator> {

    public TileEntityLargeHeatGenerator$ComputerHandler() {
        register(MethodData.builder("getLava", TileEntityLargeHeatGenerator$ComputerHandler::lavaTank$getLava).returnType(FluidStack.class).methodDescription("Get the contents of the lava tank."));
        register(MethodData.builder("getLavaCapacity", TileEntityLargeHeatGenerator$ComputerHandler::lavaTank$getLavaCapacity).returnType(int.class).methodDescription("Get the capacity of the lava tank."));
        register(MethodData.builder("getLavaNeeded", TileEntityLargeHeatGenerator$ComputerHandler::lavaTank$getLavaNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the lava tank."));
        register(MethodData.builder("getLavaFilledPercentage", TileEntityLargeHeatGenerator$ComputerHandler::lavaTank$getLavaFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the lava tank."));
        register(MethodData.builder("getTemperature", TileEntityLargeHeatGenerator$ComputerHandler::heatCapacitor$getTemperature).returnType(double.class).methodDescription("Get the temperature of the generator in Kelvin."));
        register(MethodData.builder("getFuelItem", TileEntityLargeHeatGenerator$ComputerHandler::fuelSlot$getFuelItem).returnType(ItemStack.class).methodDescription("Get the contents of the fuel item slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeHeatGenerator$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy item slot."));
        register(MethodData.builder("getTransferLoss", TileEntityLargeHeatGenerator$ComputerHandler::getTransferLoss_0).returnType(double.class));
        register(MethodData.builder("getEnvironmentalLoss", TileEntityLargeHeatGenerator$ComputerHandler::getEnvironmentalLoss_0).returnType(double.class));
    }

    public static Object lavaTank$getLava(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getStack(subject.lavaTank));
    }

    public static Object lavaTank$getLavaCapacity(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getCapacity(subject.lavaTank));
    }

    public static Object lavaTank$getLavaNeeded(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getNeeded(subject.lavaTank));
    }

    public static Object lavaTank$getLavaFilledPercentage(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getFilledPercentage(subject.lavaTank));
    }

    public static Object heatCapacitor$getTemperature(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper.getTemperature(subject.heatCapacitor));
    }

    public static Object fuelSlot$getFuelItem(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.fuelSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getTransferLoss_0(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getLastTransferLoss());
    }

    public static Object getEnvironmentalLoss_0(TileEntityLargeHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getLastEnvironmentLoss());
    }
}
