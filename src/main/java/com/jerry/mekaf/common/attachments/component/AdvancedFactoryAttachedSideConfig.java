package com.jerry.mekaf.common.attachments.component;

import mekanism.api.RelativeSide;
import mekanism.common.attachments.component.AttachedSideConfig;
import mekanism.common.attachments.component.AttachedSideConfig.LightConfigInfo;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.util.EnumUtils;

import net.minecraft.Util;

import java.util.EnumMap;
import java.util.Map;

public class AdvancedFactoryAttachedSideConfig {

    public static final LightConfigInfo SINGLE_INPUT_2 = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);

        sideConfig.put(RelativeSide.RIGHT, DataType.INPUT_2);
        sideConfig.put(RelativeSide.BACK, DataType.ENERGY);
        return new LightConfigInfo(sideConfig, false);
    });

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

    public static final AttachedSideConfig CHEMICAL_INFUSING = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, SINGLE_INPUT_2);
        configInfo.put(TransmissionType.CHEMICAL, LightConfigInfo.TWO_INPUT_AND_OUT);
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
