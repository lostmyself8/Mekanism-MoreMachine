package com.jerry.mekmm.common.item.block;

import com.jerry.mekmm.common.registries.MoreMachineDataComponents;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessTransmissionStation;

import mekanism.api.RelativeSide;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.component.component.AttachedEjector;
import mekanism.common.component.component.AttachedSideConfig;
import mekanism.common.component.component.AttachedSideConfig.LightConfigInfo;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.util.EnumUtils;

import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.Map;

public class ItemBlockWirelessTransmissionStation extends ItemBlockTooltip<BlockTileModel<TileEntityWirelessTransmissionStation, Machine<TileEntityWirelessTransmissionStation>>> {

    public static final LightConfigInfo HEAT = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        for (RelativeSide side : EnumUtils.SIDES) {
            sideConfig.put(side, DataType.INPUT_OUTPUT);
        }
        return new LightConfigInfo(sideConfig, false);
    });

    public static final AttachedSideConfig SIDE_CONFIG = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ENERGY, create(RelativeSide.BOTTOM));
        configInfo.put(TransmissionType.FLUID, create(RelativeSide.RIGHT));
        configInfo.put(TransmissionType.CHEMICAL, create(RelativeSide.LEFT));
        configInfo.put(TransmissionType.ITEM, create(RelativeSide.FRONT));
        configInfo.put(TransmissionType.HEAT, HEAT);
        return new AttachedSideConfig(configInfo);
    });

    public ItemBlockWirelessTransmissionStation(BlockTileModel<TileEntityWirelessTransmissionStation, Machine<TileEntityWirelessTransmissionStation>> block, Properties properties) {
        super(block, true, properties
                .component(MoreMachineDataComponents.ENERGY_RATE, TileEntityWirelessTransmissionStation.DEFAULT_ENERGY_RATE)
                .component(MoreMachineDataComponents.FLUIDS_RATE, TileEntityWirelessTransmissionStation.DEFAULT_FLUIDS_RATE)
                .component(MoreMachineDataComponents.CHEMICALS_RATE, TileEntityWirelessTransmissionStation.DEFAULT_CHEMICALS_RATE)
                .component(MoreMachineDataComponents.ITEMS_RATE, TileEntityWirelessTransmissionStation.DEFAULT_ITEMS_RATE)
                .component(MoreMachineDataComponents.HEAT_RATE, TileEntityWirelessTransmissionStation.DEFAULT_HEAT_RATE)
                .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                .component(MekanismDataComponents.SIDE_CONFIG, SIDE_CONFIG));
    }

    private static LightConfigInfo create(RelativeSide relativeSide) {
        return Util.make(() -> {
            Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
            for (RelativeSide side : EnumUtils.SIDES) {
                if (side == relativeSide) {
                    sideConfig.put(side, DataType.INPUT);
                } else {
                    sideConfig.put(side, DataType.OUTPUT);
                }
            }
            return new LightConfigInfo(sideConfig, false);
        });
    }
}
