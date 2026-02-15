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

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

@MethodFactory(target = TileEntityLargeRotaryCondensentrator.class)
public class TileEntityLargeRotaryCondensentrator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeRotaryCondensentrator> {

    private final String[] NAMES_value = new String[] { "value" };

    private final Class[] TYPES_3db6c47 = new Class[] { boolean.class };

    public TileEntityLargeRotaryCondensentrator$ComputerHandler() {
        register(MethodData.builder("getGas", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasTank$getGas).returnType(ChemicalStack.class).methodDescription("Get the contents of the gas tank."));
        register(MethodData.builder("getGasCapacity", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasTank$getGasCapacity).returnType(long.class).methodDescription("Get the capacity of the gas tank."));
        register(MethodData.builder("getGasNeeded", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasTank$getGasNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the gas tank."));
        register(MethodData.builder("getGasFilledPercentage", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasTank$getGasFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the gas tank."));
        register(MethodData.builder("getFluid", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidTank$getFluid).returnType(FluidStack.class).methodDescription("Get the contents of the fluid tank."));
        register(MethodData.builder("getFluidCapacity", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidTank$getFluidCapacity).returnType(int.class).methodDescription("Get the capacity of the fluid tank."));
        register(MethodData.builder("getFluidNeeded", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidTank$getFluidNeeded).returnType(int.class).methodDescription("Get the amount needed to fill the fluid tank."));
        register(MethodData.builder("getFluidFilledPercentage", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidTank$getFluidFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the fluid tank."));
        register(MethodData.builder("getGasItemInput", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasInputSlot$getGasItemInput).returnType(ItemStack.class).methodDescription("Get the contents of the gas item input slot."));
        register(MethodData.builder("getGasItemOutput", TileEntityLargeRotaryCondensentrator$ComputerHandler::gasOutputSlot$getGasItemOutput).returnType(ItemStack.class).methodDescription("Get the contents of the gas item output slot."));
        register(MethodData.builder("getFluidItemInput", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidInputSlot$getFluidItemInput).returnType(ItemStack.class).methodDescription("Get the contents of the fluid item input slot."));
        register(MethodData.builder("getFluidItemOutput", TileEntityLargeRotaryCondensentrator$ComputerHandler::fluidOutputSlot$getFluidItemOutput).returnType(ItemStack.class).methodDescription("Get the contents of the fluid item ouput slot."));
        register(MethodData.builder("getEnergyItem", TileEntityLargeRotaryCondensentrator$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityLargeRotaryCondensentrator$ComputerHandler::getEnergyUsage_0).returnType(long.class).methodDescription("Get the energy used in the last tick by the machine"));
        register(MethodData.builder("isCondensentrating", TileEntityLargeRotaryCondensentrator$ComputerHandler::isCondensentrating_0).returnType(boolean.class));
        register(MethodData.builder("setCondensentrating", TileEntityLargeRotaryCondensentrator$ComputerHandler::setCondensentrating_1).requiresPublicSecurity().arguments(NAMES_value, TYPES_3db6c47));
    }

    public static Object gasTank$getGas(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.chemicalTank));
    }

    public static Object gasTank$getGasCapacity(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.chemicalTank));
    }

    public static Object gasTank$getGasNeeded(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.chemicalTank));
    }

    public static Object gasTank$getGasFilledPercentage(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.chemicalTank));
    }

    public static Object fluidTank$getFluid(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getStack(subject.fluidTank));
    }

    public static Object fluidTank$getFluidCapacity(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getCapacity(subject.fluidTank));
    }

    public static Object fluidTank$getFluidNeeded(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getNeeded(subject.fluidTank));
    }

    public static Object fluidTank$getFluidFilledPercentage(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerFluidTankWrapper.getFilledPercentage(subject.fluidTank));
    }

    public static Object gasInputSlot$getGasItemInput(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.gasInputSlot));
    }

    public static Object gasOutputSlot$getGasItemOutput(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.gasOutputSlot));
    }

    public static Object fluidInputSlot$getFluidItemInput(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidInputSlot));
    }

    public static Object fluidOutputSlot$getFluidItemOutput(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fluidOutputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsed());
    }

    public static Object isCondensentrating_0(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.isCondensentrating());
    }

    public static Object setCondensentrating_1(TileEntityLargeRotaryCondensentrator subject, BaseComputerHelper helper) throws ComputerException {
        subject.setCondensentrating(helper.getBoolean(0));
        return helper.voidResult();
    }
}
