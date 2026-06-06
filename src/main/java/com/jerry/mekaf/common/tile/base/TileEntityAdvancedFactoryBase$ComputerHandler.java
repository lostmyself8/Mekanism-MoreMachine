package com.jerry.mekaf.common.tile.base;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

import java.util.*;

@MethodFactory(target = TileEntityAdvancedFactoryBase.class)
public class TileEntityAdvancedFactoryBase$ComputerHandler extends ComputerMethodFactory<TileEntityAdvancedFactoryBase> {

    public TileEntityAdvancedFactoryBase$ComputerHandler() {
        register(MethodData.builder("getEnergyItem", TileEntityAdvancedFactoryBase$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy slot."));
        register(MethodData.builder("isAutoSortEnabled", TileEntityAdvancedFactoryBase$ComputerHandler::isSorting_0).returnType(Object.class));
        register(MethodData.builder("getEnergyUsage", TileEntityAdvancedFactoryBase$ComputerHandler::getLastUsage_0).returnType(Object.class));
        register(MethodData.builder("getTicksRequired", TileEntityAdvancedFactoryBase$ComputerHandler::getTicksRequired_0).returnType(Object.class).methodDescription("Total number of ticks it takes currently for the recipe to complete"));
    }

    public static Object energySlot$getEnergyItem(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object isSorting_0(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.isSorting());
    }

    public static Object getLastUsage_0(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getLastUsage());
    }

    public static Object getTicksRequired_0(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getTicksRequired());
    }
}
