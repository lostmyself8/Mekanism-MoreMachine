package com.jerry.mekmm.common.tile.factory;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityMoreMachineFactory.class)
public class TileEntityMoreMachineFactory$ComputerHandler extends ComputerMethodFactory<TileEntityMoreMachineFactory> {

    private final String[] NAMES_process = new String[] { "process" };

    private final String[] NAMES_enabled = new String[] { "enabled" };

    private final Class[] TYPES_boolean = new Class[] { boolean.class };

    private final Class[] TYPES_int = new Class[] { int.class };

    public TileEntityMoreMachineFactory$ComputerHandler() {
        register(MethodData.builder("getEnergyItem", TileEntityMoreMachineFactory$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy slot."));
        register(MethodData.builder("isAutoSortEnabled", TileEntityMoreMachineFactory$ComputerHandler::isAutoSortEnabled_0).returnType(boolean.class));
        register(MethodData.builder("getEnergyUsage", TileEntityMoreMachineFactory$ComputerHandler::getEnergyUsage_0).returnType(long.class).methodDescription("Get the energy used in the last tick by the machine"));
        register(MethodData.builder("getTicksRequired", TileEntityMoreMachineFactory$ComputerHandler::getTicksRequired_0).returnType(int.class).methodDescription("Total number of ticks it takes currently for the recipe to complete"));
        register(MethodData.builder("setAutoSort", TileEntityMoreMachineFactory$ComputerHandler::setAutoSort_1).requiresPublicSecurity().arguments(NAMES_enabled, TYPES_boolean));
        register(MethodData.builder("getRecipeProgress", TileEntityMoreMachineFactory$ComputerHandler::getRecipeProgress_1).returnType(int.class).arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getInput", TileEntityMoreMachineFactory$ComputerHandler::getInput_1).returnType(ItemStack.class).arguments(NAMES_process, TYPES_int));
        register(MethodData.builder("getOutput", TileEntityMoreMachineFactory$ComputerHandler::getOutput_1).returnType(ItemStack.class).arguments(NAMES_process, TYPES_int));
    }

    public static Object energySlot$getEnergyItem(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object isAutoSortEnabled_0(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.isSorting());
    }

    public static Object getEnergyUsage_0(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
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
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.getInputSlot(helper.getInt(0))));
    }

    public static Object getOutput_1(TileEntityMoreMachineFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.getOutputSlot(helper.getInt(0))));
    }
}
