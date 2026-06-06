package com.jerry.mekmm.common.tile.factory;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityStampingFactory.class)
public class TileEntityStampingFactory$ComputerHandler extends ComputerMethodFactory<TileEntityStampingFactory> {

    public TileEntityStampingFactory$ComputerHandler() {
        register(MethodData.builder("getSecondaryInput", TileEntityStampingFactory$ComputerHandler::moldSlot$getSecondaryInput).returnType(Object.class).methodDescription("Get secondary input slot."));
    }

    public static Object moldSlot$getSecondaryInput(TileEntityStampingFactory subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.moldSlot));
    }
}
