package com.jerry.mekaf.common.tile.factory.base;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityAdvancedFactoryBase.class)
public class TileEntityAdvancedFactoryBase$ComputerHandler extends ComputerMethodFactory<TileEntityAdvancedFactoryBase> {

    private final String[] NAMES_process = new String[] { "process" };

    private final String[] NAMES_enabled = new String[] { "enabled" };

    private final Class[] TYPES_boolean = new Class[] { boolean.class };

    private final Class[] TYPES_int = new Class[] { int.class };

    public TileEntityAdvancedFactoryBase$ComputerHandler() {
        register(MethodData.builder("getEnergyItem", TileEntityAdvancedFactoryBase$ComputerHandler::energySlot$getEnergyItem).returnType(ItemStack.class).methodDescription("Get the contents of the energy slot."));
        register(MethodData.builder("isAutoSortEnabled", TileEntityAdvancedFactoryBase$ComputerHandler::isAutoSortEnabled_0).returnType(boolean.class));
        register(MethodData.builder("getEnergyUsage", TileEntityAdvancedFactoryBase$ComputerHandler::getEnergyUsage_0).returnType(long.class).methodDescription("Get the energy used in the last tick by the machine"));
        register(MethodData.builder("getTicksRequired", TileEntityAdvancedFactoryBase$ComputerHandler::getTicksRequired_0).returnType(int.class).methodDescription("Total number of ticks it takes currently for the recipe to complete"));
        register(MethodData.builder("setAutoSort", TileEntityAdvancedFactoryBase$ComputerHandler::setAutoSort_1).requiresPublicSecurity().arguments(NAMES_enabled, TYPES_boolean));
        register(MethodData.builder("getRecipeProgress", TileEntityAdvancedFactoryBase$ComputerHandler::getRecipeProgress_1).returnType(int.class).arguments(NAMES_process, TYPES_int));
    }

    public static Object energySlot$getEnergyItem(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.getStack(subject.energySlot));
    }

    public static Object isAutoSortEnabled_0(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.isSorting());
    }

    public static Object getEnergyUsage_0(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getLastUsage());
    }

    public static Object getTicksRequired_0(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getTicksRequired());
    }

    public static Object setAutoSort_1(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        subject.setAutoSort(helper.getBoolean(0));
        return helper.voidResult();
    }

    public static Object getRecipeProgress_1(TileEntityAdvancedFactoryBase subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getRecipeProgress(helper.getInt(0)));
    }
}
