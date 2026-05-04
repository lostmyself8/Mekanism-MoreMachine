package com.jerry.meklg.common.tile.generator;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityLargeWindGenerator.class)
public class TileEntityLargeWindGenerator$ComputerHandler extends ComputerMethodFactory<TileEntityLargeWindGenerator> {

    public TileEntityLargeWindGenerator$ComputerHandler() {
        register(MethodData.builder("getEnergyItem", TileEntityLargeWindGenerator$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy item slot."));
        register(MethodData.builder("isBlacklistedDimension", TileEntityLargeWindGenerator$ComputerHandler::isBlacklistedDimension_0).returnType(boolean.class));
    }

    public static Object energySlot$getEnergyItem(TileEntityLargeWindGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object isBlacklistedDimension_0(TileEntityLargeWindGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.isBlacklistDimension());
    }
}
