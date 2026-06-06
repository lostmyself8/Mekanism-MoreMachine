package com.jerry.mekaf.common.tile;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityPaintingFactory.class)
public class TileEntityPaintingFactory$ComputerHandler extends ComputerMethodFactory<TileEntityPaintingFactory> {

    public TileEntityPaintingFactory$ComputerHandler() {
        register(MethodData.builder("getPigmentInput", TileEntityPaintingFactory$ComputerHandler::pigmentTank$getPigmentInput).returnType(Object.class).methodDescription("Get pigment tank."));
        register(MethodData.builder("getPigmentInputCapacity", TileEntityPaintingFactory$ComputerHandler::pigmentTank$getPigmentInputCapacity).returnType(Object.class).methodDescription("Get pigment tank."));
        register(MethodData.builder("getPigmentInputNeeded", TileEntityPaintingFactory$ComputerHandler::pigmentTank$getPigmentInputNeeded).returnType(Object.class).methodDescription("Get pigment tank."));
        register(MethodData.builder("getPigmentInputFilledPercentage", TileEntityPaintingFactory$ComputerHandler::pigmentTank$getPigmentInputFilledPercentage).returnType(Object.class).methodDescription("Get pigment tank."));
        register(MethodData.builder("getInputPigmentItem", TileEntityPaintingFactory$ComputerHandler::pigmentInputSlot$getInputPigmentItem).returnType(Object.class).methodDescription("Get pigment slot."));
    }

    public static Object pigmentTank$getPigmentInput(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getStack(subject.pigmentTank));
    }

    public static Object pigmentTank$getPigmentInputCapacity(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getCapacity(subject.pigmentTank));
    }

    public static Object pigmentTank$getPigmentInputNeeded(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getNeeded(subject.pigmentTank));
    }

    public static Object pigmentTank$getPigmentInputFilledPercentage(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.getFilledPercentage(subject.pigmentTank));
    }

    public static Object pigmentInputSlot$getInputPigmentItem(TileEntityPaintingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.pigmentInputSlot));
    }
}
