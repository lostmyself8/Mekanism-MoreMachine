package com.jerry.mekaf.common.tile.factory;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

@MethodFactory(target = TileEntityLiquifyingFactory.class)
public class TileEntityLiquifyingFactory$ComputerHandler extends ComputerMethodFactory<TileEntityLiquifyingFactory> {

    private final String[] NAMES_process = new String[] { "process" };

    private final Class[] TYPES_int = new Class[] { int.class };

    public TileEntityLiquifyingFactory$ComputerHandler() {
        register(MethodData.builder("getFluidOutput", TileEntityLiquifyingFactory$ComputerHandler::fluidTank$getOutput).returnType(FluidStack.class).methodDescription("Get the contents of the output tank."));
        register(MethodData.builder("getFluidOutputCapacity", TileEntityLiquifyingFactory$ComputerHandler::fluidTank$getOutputCapacity).returnType(int.class).methodDescription("Get the capacity of the output tank."));
        register(MethodData.builder("getFluidOutputNeeded", TileEntityLiquifyingFactory$ComputerHandler::fluidTank$getOutputNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the output tank."));
        register(MethodData.builder("getFluidOutputFilledPercentage", TileEntityLiquifyingFactory$ComputerHandler::fluidTank$getOutputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the output tank."));
        register(MethodData.builder("getInput", TileEntityLiquifyingFactory$ComputerHandler::getInput_1).returnType(ItemStack.class).arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getOutput", TileEntityLiquifyingFactory$ComputerHandler::getOutput_1).returnType(ItemStack.class).arguments(NAMES_process, TYPES_int));
    }

    public static Object fluidTank$getOutput(TileEntityLiquifyingFactory subject,
                                             BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getStack(subject.fluidTank));
    }

    public static Object fluidTank$getOutputCapacity(TileEntityLiquifyingFactory subject,
                                                     BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getCapacity(subject.fluidTank));
    }

    public static Object fluidTank$getOutputNeeded(TileEntityLiquifyingFactory subject,
                                                   BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getNeeded(subject.fluidTank));
    }

    public static Object fluidTank$getOutputFilledPercentage(TileEntityLiquifyingFactory subject,
                                                             BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerFluidTankWrapper.getFilledPercentage(subject.fluidTank));
    }

    public static Object getInput_1(TileEntityLiquifyingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getInput(helper.getInt(0)));
    }

    public static Object getOutput_1(TileEntityLiquifyingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getOutput(helper.getInt(0)));
    }
}
