package com.jerry.mekaf.common.tile.factory;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import net.minecraft.world.item.ItemStack;

@MethodFactory(target = TileEntityItemToItemAdvancedFactory.class)
public class TileEntityItemToItemAdvancedFactory$ComputerHandler extends ComputerMethodFactory<TileEntityItemToItemAdvancedFactory> {

    private final String[] NAMES_process = new String[] { "process" };

    private final Class[] TYPES_1980e = new Class[] { int.class };

    public TileEntityItemToItemAdvancedFactory$ComputerHandler() {
        register(MethodData.builder("getInput", TileEntityItemToItemAdvancedFactory$ComputerHandler::getInput_1).returnType(ItemStack.class).arguments(NAMES_process, TYPES_1980e));
        register(MethodData.builder("getOutput", TileEntityItemToItemAdvancedFactory$ComputerHandler::getOutput_1).returnType(ItemStack.class).arguments(NAMES_process, TYPES_1980e));
    }

    public static Object getInput_1(TileEntityItemToItemAdvancedFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getInput(helper.getInt(0)));
    }

    public static Object getOutput_1(TileEntityItemToItemAdvancedFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getOutput(helper.getInt(0)));
    }
}
