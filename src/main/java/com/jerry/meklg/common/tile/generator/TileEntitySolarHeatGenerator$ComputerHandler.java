package com.jerry.meklg.common.tile.generator;

import mekanism.common.integration.computer.BaseComputerHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.ComputerMethodFactory;
import mekanism.common.integration.computer.MethodData;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntitySolarHeatGenerator.class)
public class TileEntitySolarHeatGenerator$ComputerHandler extends ComputerMethodFactory<TileEntitySolarHeatGenerator> {

    public TileEntitySolarHeatGenerator$ComputerHandler() {
        register(MethodData.builder("getEnergyItem", TileEntitySolarHeatGenerator$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy item slot."));
    }

    public static Object energySlot$getEnergyItem(TileEntitySolarHeatGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }
}
