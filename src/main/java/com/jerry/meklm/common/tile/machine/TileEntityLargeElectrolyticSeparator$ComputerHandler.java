package com.jerry.meklm.common.tile.machine;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.BaseComputerHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.ComputerMethodFactory;
import mekanism.common.integration.computer.MethodData;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;
import mekanism.common.tile.TileEntityChemicalTank;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

@MethodFactory(target = TileEntityLargeElectrolyticSeparator.class)
public class TileEntityLargeElectrolyticSeparator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeElectrolyticSeparator> {

    private final String[] NAMES_mode = new String[] { "mode" };

    private final Class[] TYPES_ef806282 = new Class[] { TileEntityChemicalTank.GasMode.class };

    public TileEntityLargeElectrolyticSeparator$ComputerHandler() {
        register(MethodData.builder("getInput", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidTank$getInput).returnType(FluidStack.class).methodDescription("Get the contents of the input tank."));
        register(MethodData.builder("getInputCapacity", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidTank$getInputCapacity).returnType(int.class).methodDescription("Get the capacity of the input tank."));
        register(MethodData.builder("getInputNeeded", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidTank$getInputNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the input tank."));
        register(MethodData.builder("getInputFilledPercentage", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidTank$getInputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the input tank."));
        register(MethodData.builder("getLeftOutput", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftTank$getLeftOutput).returnType(ChemicalStack.class).methodDescription("Get the contents of the left output tank."));
        register(MethodData.builder("getLeftOutputCapacity", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftTank$getLeftOutputCapacity).returnType(long.class).methodDescription("Get the capacity of the left output tank."));
        register(MethodData.builder("getLeftOutputNeeded", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftTank$getLeftOutputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the left output tank."));
        register(MethodData.builder("getLeftOutputFilledPercentage", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftTank$getLeftOutputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the left output tank."));
        register(MethodData.builder("getRightOutput", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightTank$getRightOutput).returnType(ChemicalStack.class).methodDescription("Get the contents of the right output tank."));
        register(MethodData.builder("getRightOutputCapacity", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightTank$getRightOutputCapacity).returnType(long.class).methodDescription("Get the capacity of the right output tank."));
        register(MethodData.builder("getRightOutputNeeded", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightTank$getRightOutputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the right output tank."));
        register(MethodData.builder("getRightOutputFilledPercentage", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightTank$getRightOutputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the right output tank."));
        register(MethodData.builder("getLeftOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::getLeftOutputDumpingMode_0).returnType(TileEntityChemicalTank.GasMode.class));
        register(MethodData.builder("getRightOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::getRightOutputDumpingMode_0).returnType(TileEntityChemicalTank.GasMode.class));
        register(MethodData.builder("getInputItem", TileEntityLargeElectrolyticSeparator$ComputerHandler::fluidSlot$getInputItem).returnType(ItemStack.class).methodDescription("Get the contents of the input item slot."));
        register(MethodData.builder("getLeftOutputItem", TileEntityLargeElectrolyticSeparator$ComputerHandler::leftOutputSlot$getLeftOutputItem).returnType(ItemStack.class).methodDescription("Get the contents of the left output item slot."));
        register(MethodData.builder("getRightOutputItem", TileEntityLargeElectrolyticSeparator$ComputerHandler::rightOutputSlot$getRightOutputItem).returnType(ItemStack.class).methodDescription("Get the contents of the right output item slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeElectrolyticSeparator$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityLargeElectrolyticSeparator$ComputerHandler::getEnergyUsage_0).returnType(long.class).methodDescription("Get the energy used in the last tick by the machine"));
        register(MethodData.builder("setLeftOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::setLeftOutputDumpingMode_1).requiresPublicSecurity().arguments(NAMES_mode, TYPES_ef806282));
        register(MethodData.builder("incrementLeftOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::incrementLeftOutputDumpingMode_0).requiresPublicSecurity());
        register(MethodData.builder("decrementLeftOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::decrementLeftOutputDumpingMode_0).requiresPublicSecurity());
        register(MethodData.builder("setRightOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::setRightOutputDumpingMode_1).requiresPublicSecurity().arguments(NAMES_mode, TYPES_ef806282));
        register(MethodData.builder("incrementRightOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::incrementRightOutputDumpingMode_0).requiresPublicSecurity());
        register(MethodData.builder("decrementRightOutputDumpingMode", TileEntityLargeElectrolyticSeparator$ComputerHandler::decrementRightOutputDumpingMode_0).requiresPublicSecurity());
    }

    public static Object fluidTank$getInput(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getStack(subject.fluidTank));
    }

    public static Object fluidTank$getInputCapacity(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getCapacity(subject.fluidTank));
    }

    public static Object fluidTank$getInputNeeded(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getNeeded(subject.fluidTank));
    }

    public static Object fluidTank$getInputFilledPercentage(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getFilledPercentage(subject.fluidTank));
    }

    public static Object leftTank$getLeftOutput(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.leftTank));
    }

    public static Object leftTank$getLeftOutputCapacity(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.leftTank));
    }

    public static Object leftTank$getLeftOutputNeeded(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.leftTank));
    }

    public static Object leftTank$getLeftOutputFilledPercentage(
                                                                TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.leftTank));
    }

    public static Object rightTank$getRightOutput(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.rightTank));
    }

    public static Object rightTank$getRightOutputCapacity(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.rightTank));
    }

    public static Object rightTank$getRightOutputNeeded(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.rightTank));
    }

    public static Object rightTank$getRightOutputFilledPercentage(
                                                                  TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.rightTank));
    }

    public static Object getLeftOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.dumpLeft);
    }

    public static Object getRightOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.dumpRight);
    }

    public static Object fluidSlot$getInputItem(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidSlot));
    }

    public static Object leftOutputSlot$getLeftOutputItem(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.leftOutputSlot));
    }

    public static Object rightOutputSlot$getRightOutputItem(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.rightOutputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }

    public static Object setLeftOutputDumpingMode_1(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.setLeftOutputDumpingMode(helper.getEnum(0, TileEntityChemicalTank.GasMode.class));
        return helper.voidResult();
    }

    public static Object incrementLeftOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.incrementLeftOutputDumpingMode();
        return helper.voidResult();
    }

    public static Object decrementLeftOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.decrementLeftOutputDumpingMode();
        return helper.voidResult();
    }

    public static Object setRightOutputDumpingMode_1(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.setRightOutputDumpingMode(helper.getEnum(0, TileEntityChemicalTank.GasMode.class));
        return helper.voidResult();
    }

    public static Object incrementRightOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.incrementRightOutputDumpingMode();
        return helper.voidResult();
    }

    public static Object decrementRightOutputDumpingMode_0(TileEntityLargeElectrolyticSeparator subject, BaseComputerHelper helper) throws ComputerException {
        subject.decrementRightOutputDumpingMode();
        return helper.voidResult();
    }
}
