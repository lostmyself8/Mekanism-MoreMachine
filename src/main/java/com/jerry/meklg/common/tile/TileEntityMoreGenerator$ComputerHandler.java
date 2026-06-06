package com.jerry.meklg.common.tile;

import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.MethodFactory;

import java.util.*;

@MethodFactory(target = TileEntityMoreGenerator.class)
public class TileEntityMoreGenerator$ComputerHandler extends ComputerMethodFactory<TileEntityMoreGenerator> {

    public TileEntityMoreGenerator$ComputerHandler() {
        register(MethodData.builder("getMaxOutput", TileEntityMoreGenerator$ComputerHandler::getMaxOutput_0).returnType(Object.class));
        register(MethodData.builder("getProductionRate", TileEntityMoreGenerator$ComputerHandler::getProductionRate_0).returnType(Object.class).methodDescription("Get the amount of energy produced by this generator in the last tick."));
    }

    public static Object getMaxOutput_0(TileEntityMoreGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getMaxOutput());
    }

    public static Object getProductionRate_0(TileEntityMoreGenerator subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(subject.getProductionRate());
    }
}
