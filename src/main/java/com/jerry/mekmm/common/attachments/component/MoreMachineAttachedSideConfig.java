package com.jerry.mekmm.common.attachments.component;

import mekanism.api.RelativeSide;
import mekanism.common.attachments.component.AttachedSideConfig;
import mekanism.common.attachments.component.AttachedSideConfig.LightConfigInfo;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.config.DataType;

import net.minecraft.Util;

import java.util.EnumMap;
import java.util.Map;

public class MoreMachineAttachedSideConfig {

    public static final AttachedSideConfig FLUID_REPLICATOR = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, LightConfigInfo.EXTRA_MACHINE);
        configInfo.put(TransmissionType.CHEMICAL, LightConfigInfo.INPUT_ONLY);
        configInfo.put(TransmissionType.FLUID, LightConfigInfo.OUT_EJECT);
        configInfo.put(TransmissionType.ENERGY, LightConfigInfo.INPUT_ONLY);
        return new AttachedSideConfig(configInfo);
    });

    public static final AttachedSideConfig CHEMICAL_REPLICATOR = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, LightConfigInfo.EXTRA_MACHINE);
        configInfo.put(TransmissionType.CHEMICAL, LightConfigInfo.TWO_INPUT_AND_OUT);
        configInfo.put(TransmissionType.ENERGY, LightConfigInfo.INPUT_ONLY);
        return new AttachedSideConfig(configInfo);
    });

    public static final LightConfigInfo TWO_INPUT_AND_OUT = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        sideConfig.put(RelativeSide.RIGHT, DataType.INPUT_1);
        sideConfig.put(RelativeSide.LEFT, DataType.INPUT_2);
        sideConfig.put(RelativeSide.FRONT, DataType.OUTPUT);
        return new LightConfigInfo(sideConfig, true);
    });

    public static final LightConfigInfo THREE_INPUT_AND_OUT = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        sideConfig.put(RelativeSide.RIGHT, DataType.INPUT_1);
        sideConfig.put(RelativeSide.LEFT, DataType.INPUT_2);
        sideConfig.put(RelativeSide.BACK, DataType.EXTRA);
        sideConfig.put(RelativeSide.FRONT, DataType.OUTPUT);
        return new LightConfigInfo(sideConfig, true);
    });

    public static final AttachedSideConfig PRESSER = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, THREE_INPUT_AND_OUT);
        configInfo.put(TransmissionType.ENERGY, LightConfigInfo.INPUT_ONLY);
        return new AttachedSideConfig(configInfo);
    });
}
