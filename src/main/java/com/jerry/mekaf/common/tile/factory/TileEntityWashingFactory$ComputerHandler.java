package com.jerry.mekaf.common.tile.factory;

import mekanism.common.integration.computer.BaseComputerHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.ComputerMethodFactory;
import mekanism.common.integration.computer.MethodData;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

@MethodFactory(target = TileEntityWashingFactory.class)
public class TileEntityWashingFactory$ComputerHandler extends ComputerMethodFactory<TileEntityWashingFactory> {

    public TileEntityWashingFactory$ComputerHandler() {
        register(MethodData.builder("getFluid", TileEntityWashingFactory$ComputerHandler::fluidTank$getFluid).returnType(FluidStack.class).methodDescription("Get the contents of the fluid tank."));
        register(MethodData.builder("getFluidCapacity", TileEntityWashingFactory$ComputerHandler::fluidTank$getFluidCapacity).returnType(int.class).methodDescription("Get the capacity of the fluid tank."));
        register(MethodData.builder("getFluidNeeded", TileEntityWashingFactory$ComputerHandler::fluidTank$getFluidNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the fluid tank."));
        register(MethodData.builder("getFluidFilledPercentage", TileEntityWashingFactory$ComputerHandler::fluidTank$getFluidFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the fluid tank."));
        register(MethodData.builder("getFluidItemInput", TileEntityWashingFactory$ComputerHandler::fluidSlot$getFluidItemInput).returnType(ItemStack.class).methodDescription("Get the contents of the fluid item input slot."));
        register(MethodData.builder("getFluidItemOutput", TileEntityWashingFactory$ComputerHandler::fluidOutputSlot$getFluidItemOutput).returnType(ItemStack.class).methodDescription("Get the contents of the fluid item output slot."));
    }

    public static Object fluidTank$getFluid(TileEntityWashingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getStack(subject.fluidTank));
    }

    public static Object fluidTank$getFluidCapacity(TileEntityWashingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getCapacity(subject.fluidTank));
    }

    public static Object fluidTank$getFluidNeeded(TileEntityWashingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getNeeded(subject.fluidTank));
    }

    public static Object fluidTank$getFluidFilledPercentage(TileEntityWashingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getFilledPercentage(subject.fluidTank));
    }

    public static Object fluidSlot$getFluidItemInput(TileEntityWashingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidSlot));
    }

    public static Object fluidOutputSlot$getFluidItemOutput(TileEntityWashingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidOutputSlot));
    }
}
