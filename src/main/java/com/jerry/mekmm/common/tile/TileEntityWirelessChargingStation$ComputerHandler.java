package com.jerry.mekmm.common.tile;

import com.jerry.mekmm.common.integration.computer.ComputerEnergyContainerWrapper;

import mekanism.api.*;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.MethodFactory;

@MethodFactory(target = TileEntityWirelessChargingStation.class)
public class TileEntityWirelessChargingStation$ComputerHandler extends ComputerMethodFactory<TileEntityWirelessChargingStation> {

    public TileEntityWirelessChargingStation$ComputerHandler() {
        register(MethodData.builder("getEnergy", TileEntityWirelessChargingStation$ComputerHandler::energyContainer$getEnergy).returnType(Object.class).methodDescription("Get energy container."));
        register(MethodData.builder("getEnergyCapacity", TileEntityWirelessChargingStation$ComputerHandler::energyContainer$getEnergyCapacity).returnType(Object.class).methodDescription("Get energy container."));
        register(MethodData.builder("getEnergyNeeded", TileEntityWirelessChargingStation$ComputerHandler::energyContainer$getEnergyNeeded).returnType(Object.class).methodDescription("Get energy container."));
        register(MethodData.builder("getEnergyFilledPercentage", TileEntityWirelessChargingStation$ComputerHandler::energyContainer$getEnergyFilledPercentage).returnType(Object.class).methodDescription("Get energy container."));
        register(MethodData.builder("getChargeItem", TileEntityWirelessChargingStation$ComputerHandler::chargeSlot$getChargeItem).returnType(Object.class).methodDescription("Get charge slot."));
        register(MethodData.builder("getDischargeItem", TileEntityWirelessChargingStation$ComputerHandler::dischargeSlot$getDischargeItem).returnType(Object.class).methodDescription("Get discharge slot."));
        register(MethodData.builder("setChargeEquipment", TileEntityWirelessChargingStation$ComputerHandler::setChargeEquipment_1).returnType(Object.class).arguments(new String[] { "charge" }, new Class[] { boolean.class }).methodDescription("Set whether to charge equipment").requiresPublicSecurity());
        register(MethodData.builder("setChargeInventory", TileEntityWirelessChargingStation$ComputerHandler::setChargeInventory_1).returnType(Object.class).arguments(new String[] { "charge" }, new Class[] { boolean.class }).methodDescription("Set whether to charge inventory").requiresPublicSecurity());
        register(MethodData.builder("setChargeCurios", TileEntityWirelessChargingStation$ComputerHandler::setChargeCurios_1).returnType(Object.class).arguments(new String[] { "charge" }, new Class[] { boolean.class }).methodDescription("Set whether to charge curios").requiresPublicSecurity());
    }

    public static Object energyContainer$getEnergy(TileEntityWirelessChargingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerEnergyContainerWrapper.getEnergy(subject.getEnergyContainer()));
    }

    public static Object energyContainer$getEnergyCapacity(TileEntityWirelessChargingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerEnergyContainerWrapper.getCapacity(subject.getEnergyContainer()));
    }

    public static Object energyContainer$getEnergyNeeded(TileEntityWirelessChargingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerEnergyContainerWrapper.getNeeded(subject.getEnergyContainer()));
    }

    public static Object energyContainer$getEnergyFilledPercentage(TileEntityWirelessChargingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerEnergyContainerWrapper.getFilledPercentage(subject.getEnergyContainer()));
    }

    public static Object chargeSlot$getChargeItem(TileEntityWirelessChargingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.chargeSlot));
    }

    public static Object dischargeSlot$getDischargeItem(TileEntityWirelessChargingStation subject, BaseComputerHelper helper) throws ComputerException {
        return helper.convert(ComputerIInventorySlotWrapper.getStack(subject.dischargeSlot));
    }

    public static Object setChargeEquipment_1(TileEntityWirelessChargingStation subject, BaseComputerHelper helper) throws ComputerException {
        subject.setChargeEquipment(helper.getBoolean(0));
        return helper.voidResult();
    }

    public static Object setChargeInventory_1(TileEntityWirelessChargingStation subject, BaseComputerHelper helper) throws ComputerException {
        subject.setChargeInventory(helper.getBoolean(0));
        return helper.voidResult();
    }

    public static Object setChargeCurios_1(TileEntityWirelessChargingStation subject, BaseComputerHelper helper) throws ComputerException {
        subject.setChargeCurios(helper.getBoolean(0));
        return helper.voidResult();
    }
}
