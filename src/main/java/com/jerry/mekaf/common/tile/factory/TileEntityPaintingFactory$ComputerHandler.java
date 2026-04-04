package com.jerry.mekaf.common.tile.factory;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityPaintingFactory.class)
public class TileEntityPaintingFactory$ComputerHandler extends ComputerMethodFactory<TileEntityPaintingFactory> {

    public TileEntityPaintingFactory$ComputerHandler() {
        register(MethodData.builder("getChemicalInput", TileEntityPaintingFactory$ComputerHandler::pigmentTank$getChemicalInput).returnType(ChemicalStack.class).methodDescription("Get the contents of the chemical tank."));
        register(MethodData.builder("getChemicalInputCapacity", TileEntityPaintingFactory$ComputerHandler::pigmentTank$getChemicalInputCapacity).returnType(long.class).methodDescription("Get the capacity of the chemical tank."));
        register(MethodData.builder("getChemicalInputNeeded", TileEntityPaintingFactory$ComputerHandler::pigmentTank$getChemicalInputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the chemical tank."));
        register(MethodData.builder("getChemicalInputFilledPercentage", TileEntityPaintingFactory$ComputerHandler::pigmentTank$getChemicalInputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the chemical tank."));
        register(MethodData.builder("getInputChemicalItem", TileEntityPaintingFactory$ComputerHandler::pigmentInputSlot$getInputChemicalItem).returnType(ItemStack.class).methodDescription("Get the contents of the chemical slot."));
    }

    public static Object pigmentTank$getChemicalInput(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.chemicalTank));
    }

    public static Object pigmentTank$getChemicalInputCapacity(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.chemicalTank));
    }

    public static Object pigmentTank$getChemicalInputNeeded(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.chemicalTank));
    }

    public static Object pigmentTank$getChemicalInputFilledPercentage(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.chemicalTank));
    }

    public static Object pigmentInputSlot$getInputChemicalItem(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.chemicalInputSlot));
    }
}
