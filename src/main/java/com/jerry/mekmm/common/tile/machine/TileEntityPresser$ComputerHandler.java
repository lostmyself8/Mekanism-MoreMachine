package com.jerry.mekmm.common.tile.machine;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityPresser.class)
public class TileEntityPresser$ComputerHandler extends ComputerMethodFactory<TileEntityPresser> {

    public TileEntityPresser$ComputerHandler() {
        register(MethodData.builder("getPrimaryInput", TileEntityPresser$ComputerHandler::primaryItemInputSlot$getPrimaryInput).returnType(ItemStack.class).methodDescription("Get the contents of the primary input slot."));
        register(MethodData.builder("getSecondaryInput", TileEntityPresser$ComputerHandler::secondaryItemInputSlot$getSecondaryInput).returnType(ItemStack.class).methodDescription("Get the contents of the secondary input slot."));
        register(MethodData.builder("getTertiaryInput", TileEntityPresser$ComputerHandler::tertiaryItemInputSlot$getTertiaryInput).returnType(ItemStack.class).methodDescription("Get the contents of the tertiary input slot."));
        register(MethodData.builder("getOutput", TileEntityPresser$ComputerHandler::outputSlot$getOutput).returnType(ItemStack.class).methodDescription("Get the contents of the output slot."));
        register(MethodData.builder("getEnergyItem", TileEntityPresser$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy slot."));
        register(MethodData.builder("getEnergyUsage", TileEntityPresser$ComputerHandler::getEnergyUsage_0).returnType(long.class).methodDescription("Get the energy used in the last tick by the machine"));
    }

    public static Object primaryItemInputSlot$getPrimaryInput(TileEntityPresser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.primaryItemInputSlot));
    }

    public static Object secondaryItemInputSlot$getSecondaryInput(TileEntityPresser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.secondaryItemInputSlot));
    }

    public static Object tertiaryItemInputSlot$getTertiaryInput(TileEntityPresser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.tertiaryItemInputSlot));
    }

    public static Object outputSlot$getOutput(TileEntityPresser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.outputSlot));
    }

    public static Object energySlot$getEnergyItem(TileEntityPresser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object getEnergyUsage_0(TileEntityPresser subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getEnergyUsage());
    }
}
