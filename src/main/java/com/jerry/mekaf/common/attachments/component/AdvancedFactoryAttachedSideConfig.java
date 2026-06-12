package com.jerry.mekaf.common.attachments.component;

import mekanism.api.RelativeSide;
import mekanism.common.component.component.AttachedSideConfig;
import mekanism.common.component.component.AttachedSideConfig.LightConfigInfo;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.util.EnumUtils;

import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.Map;

public class AdvancedFactoryAttachedSideConfig {

    public static final LightConfigInfo ENERGY_ONLY_NO_TOP = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        for (RelativeSide side : EnumUtils.SIDES) {
            if (side != RelativeSide.TOP) sideConfig.put(side, DataType.ENERGY);
        }
        return new LightConfigInfo(sideConfig, false);
    });

    public static final LightConfigInfo INPUT_ONLY_NO_TOP = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        for (RelativeSide side : EnumUtils.SIDES) {
            if (side != RelativeSide.TOP) sideConfig.put(side, DataType.INPUT);
        }
        return new LightConfigInfo(sideConfig, false);
    });

    public static final LightConfigInfo INPUT_ENERGY_ONLY = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        for (RelativeSide side : EnumUtils.SIDES) {
            sideConfig.put(side, DataType.INPUT);
        }
        sideConfig.put(RelativeSide.BACK, DataType.ENERGY);
        return new LightConfigInfo(sideConfig, false);
    });

    public static final LightConfigInfo EXTRA_MACHINE_NO_OUT = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        for (RelativeSide side : EnumUtils.SIDES) {
            sideConfig.put(side, DataType.INPUT);
        }
        sideConfig.put(RelativeSide.BOTTOM, DataType.EXTRA);
        sideConfig.put(RelativeSide.BACK, DataType.ENERGY);
        return new LightConfigInfo(sideConfig, false);
    });

    public static final LightConfigInfo OUTPUT_ENERGY_ONLY = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        for (RelativeSide side : EnumUtils.SIDES) {
            sideConfig.put(side, DataType.OUTPUT);
        }
        sideConfig.put(RelativeSide.BACK, DataType.ENERGY);
        return new LightConfigInfo(sideConfig, false);
    });

    public static final AttachedSideConfig CHEMICAL_OUT_NO_ITEM_OUT_MACHINE = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, INPUT_ENERGY_ONLY);
        configInfo.put(TransmissionType.CHEMICAL, LightConfigInfo.RIGHT_OUTPUT);
        configInfo.put(TransmissionType.ENERGY, LightConfigInfo.INPUT_ONLY);
        return new AttachedSideConfig(configInfo);
    });

    public static final AttachedSideConfig DISSOLUTION = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, EXTRA_MACHINE_NO_OUT);
        configInfo.put(TransmissionType.CHEMICAL, LightConfigInfo.OUT_EJECT);
        configInfo.put(TransmissionType.ENERGY, LightConfigInfo.INPUT_ONLY);
        return new AttachedSideConfig(configInfo);
    });

    public static final AttachedSideConfig CRYSTALLIZER = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, OUTPUT_ENERGY_ONLY);
        configInfo.put(TransmissionType.CHEMICAL, LightConfigInfo.INPUT_ONLY);
        configInfo.put(TransmissionType.ENERGY, LightConfigInfo.INPUT_ONLY);
        return new AttachedSideConfig(configInfo);
    });

    public static final AttachedSideConfig CENTRIFUGE = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, ENERGY_ONLY_NO_TOP);
        configInfo.put(TransmissionType.CHEMICAL, LightConfigInfo.FRONT_OUT_EJECT_NO_TOP);
        configInfo.put(TransmissionType.ENERGY, INPUT_ONLY_NO_TOP);
        return new AttachedSideConfig(configInfo);
    });
}
