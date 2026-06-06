package com.jerry.mekmm.common.tile.factory;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

import java.util.*;

@MethodFactory(target = TileEntityMoreMachineFactory.class)
public class TileEntityMoreMachineFactory$ComputerHandler extends ComputerMethodFactory<TileEntityMoreMachineFactory> {

    public TileEntityMoreMachineFactory$ComputerHandler() {
        register(MethodData.builder("getEnergyItem", TileEntityMoreMachineFactory$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("isAutoSortEnabled", TileEntityMoreMachineFactory$ComputerHandler::isSorting_0).returnType(Object.class));
        register(MethodData.builder("getEnergyUsage", TileEntityMoreMachineFactory$ComputerHandler::getLastUsage_0).returnType(Object.class));
        register(MethodData.builder("getTicksRequired", TileEntityMoreMachineFactory$ComputerHandler::getTicksRequired_0).returnType(Object.class).methodDescription("Total number of ticks it takes currently for the recipe to complete"));
        register(MethodData.builder("setAutoSort", TileEntityMoreMachineFactory$ComputerHandler::setAutoSort_1).returnType(Object.class).arguments(new String[] { "enabled" }, new Class[] { boolean.class }).requiresPublicSecurity());
        register(MethodData.builder("getRecipeProgress", TileEntityMoreMachineFactory$ComputerHandler::getRecipeProgress_1).returnType(Object.class).arguments(new String[] { "process" }, new Class[] { int.class }));
        register(MethodData.builder("getInput", TileEntityMoreMachineFactory$ComputerHandler::getInput_1).returnType(Object.class).arguments(new String[] { "process" }, new Class[] { int.class }));
        register(MethodData.builder("getOutput", TileEntityMoreMachineFactory$ComputerHandler::getOutput_1).returnType(Object.class).arguments(new String[] { "process" }, new Class[] { int.class }));
    }

    public static Object energySlot$getEnergyItem(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object isSorting_0(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.isSorting());
    }

    public static Object getLastUsage_0(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getLastUsage());
    }

    public static Object getTicksRequired_0(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getTicksRequired());
    }

    public static Object setAutoSort_1(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        subject.setAutoSort(helper.getBoolean(0));
        return helper.voidResult();
    }

    public static Object getRecipeProgress_1(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getRecipeProgress(helper.getInt(0)));
    }

    public static Object getInput_1(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getInput(helper.getInt(0)));
    }

    public static Object getOutput_1(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getOutput(helper.getInt(0)));
    }
}
