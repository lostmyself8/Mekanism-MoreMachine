package com.jerry.mekaf.common.tile.factory;

import mekanism.api.chemical.ChemicalStack;
import mekanism.common.integration.computer.BaseComputerHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.ComputerMethodFactory;
import mekanism.common.integration.computer.MethodData;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityDissolvingFactory.class)
public class TileEntityDissolvingFactory$ComputerHandler extends ComputerMethodFactory<TileEntityDissolvingFactory> {

    public TileEntityDissolvingFactory$ComputerHandler() {
        register(MethodData.builder("getChemicalInput", TileEntityDissolvingFactory$ComputerHandler::injectTank$getChemicalInput).returnType(ChemicalStack.class).methodDescription("Get the contents of the chemical input tank."));
        register(MethodData.builder("getChemicalInputCapacity", TileEntityDissolvingFactory$ComputerHandler::injectTank$getChemicalInputCapacity).returnType(long.class).methodDescription("Get the capacity of the chemical input tank."));
        register(MethodData.builder("getChemicalInputNeeded", TileEntityDissolvingFactory$ComputerHandler::injectTank$getChemicalInputNeeded).returnType(long.class).methodDescription("Get the amount needed to fill the chemical input tank."));
        register(MethodData.builder("getChemicalInputFilledPercentage", TileEntityDissolvingFactory$ComputerHandler::injectTank$getChemicalInputFilledPercentage).returnType(double.class).methodDescription("Get the filled percentage of the chemical input tank."));
        register(MethodData.builder("getInputChemicalItem", TileEntityDissolvingFactory$ComputerHandler::gasInputSlot$getInputChemicalItem).returnType(ItemStack.class).methodDescription("Get the contents of the chemical input item slot."));
    }

    public static Object injectTank$getChemicalInput(TileEntityDissolvingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getStack(subject.chemicalTank));
    }

    public static Object injectTank$getChemicalInputCapacity(TileEntityDissolvingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getCapacity(subject.chemicalTank));
    }

    public static Object injectTank$getChemicalInputNeeded(TileEntityDissolvingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getNeeded(subject.chemicalTank));
    }

    public static Object injectTank$getChemicalInputFilledPercentage(TileEntityDissolvingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerChemicalTankWrapper.getFilledPercentage(subject.chemicalTank));
    }

    public static Object gasInputSlot$getInputChemicalItem(TileEntityDissolvingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.chemicalInputSlot));
    }
}
