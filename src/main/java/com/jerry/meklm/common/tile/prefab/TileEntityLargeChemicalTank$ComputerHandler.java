package com.jerry.meklm.common.tile.prefab;

import mekanism.api.*;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;
import mekanism.common.tile.TileEntityChemicalTank.GasMode;
import mekanism.common.util.*;

@MethodFactory(target = TileEntityLargeChemicalTank.class)
public class TileEntityLargeChemicalTank$ComputerHandler extends ComputerMethodFactory<TileEntityLargeChemicalTank> {

    public TileEntityLargeChemicalTank$ComputerHandler() {
        register(MethodData.builder("getDrainItem", TileEntityLargeChemicalTank$ComputerHandler::drainSlot$getDrainItem).returnType(Object.class).methodDescription("Get drain slot."));
        register(MethodData.builder("getFillItem", TileEntityLargeChemicalTank$ComputerHandler::fillSlot$getFillItem).returnType(Object.class).methodDescription("Get fill slot."));
        register(MethodData.builder("getDumpingMode", TileEntityLargeChemicalTank$ComputerHandler::dumping$getDumpingMode).returnType(Object.class).methodDescription("Get the current Dumping configuration"));
        register(MethodData.builder("setDumpingMode", TileEntityLargeChemicalTank$ComputerHandler::setDumpingMode_1).returnType(Object.class).arguments(new String[] { "mode" }, new Class[] { GasMode.class }).methodDescription("Set the Dumping mode of the tank").requiresPublicSecurity());
        register(MethodData.builder("incrementDumpingMode", TileEntityLargeChemicalTank$ComputerHandler::incrementDumpingMode_0).returnType(Object.class).methodDescription("Advance the Dumping mode to the next configuration in the list").requiresPublicSecurity());
        register(MethodData.builder("decrementDumpingMode", TileEntityLargeChemicalTank$ComputerHandler::decrementDumpingMode_0).returnType(Object.class).methodDescription("Descend the Dumping mode to the previous configuration in the list").requiresPublicSecurity());
    }

    public static Object drainSlot$getDrainItem(TileEntityLargeChemicalTank subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.drainSlot));
    }

    public static Object fillSlot$getFillItem(TileEntityLargeChemicalTank subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.fillSlot));
    }

    public static Object dumping$getDumpingMode(TileEntityLargeChemicalTank subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.dumping);
    }

    public static Object setDumpingMode_1(TileEntityLargeChemicalTank subject, BaseComputerHelper helper) throws ComputerException {
        subject.setDumpingMode(helper.getEnum(0, GasMode.class));
        return helper.voidResult();
    }

    public static Object incrementDumpingMode_0(TileEntityLargeChemicalTank subject, BaseComputerHelper helper) throws ComputerException {
        subject.incrementDumpingMode();
        return helper.voidResult();
    }

    public static Object decrementDumpingMode_0(TileEntityLargeChemicalTank subject, BaseComputerHelper helper) throws ComputerException {
        subject.decrementDumpingMode();
        return helper.voidResult();
    }
}
