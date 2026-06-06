package com.jerry.meklg.common.tile;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

import java.util.*;

@MethodFactory(target = TileEntityLargeWindGenerator.class)
public class TileEntityLargeWindGenerator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeWindGenerator> {

    public TileEntityLargeWindGenerator$ComputerHandler() {
        register(MethodData.builder("getEnergyItem", TileEntityLargeWindGenerator$ComputerHandler::energySlot$getEnergyItem).returnType(Object.class).methodDescription("Get energy item slot."));
        register(MethodData.builder("isBlacklistedDimension", TileEntityLargeWindGenerator$ComputerHandler::isBlacklistDimension_0).returnType(Object.class));
        register(MethodData.builder("hasSameGeneratorNearby", TileEntityLargeWindGenerator$ComputerHandler::hasSameGeneratorNearby_0).returnType(Object.class));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeWindGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object isBlacklistDimension_0(TileEntityLargeWindGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.isBlacklistDimension());
    }

    public static Object hasSameGeneratorNearby_0(TileEntityLargeWindGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.hasSameGeneratorNearby());
    }
}
