package com.jerry.mekmm.common.item.block;

import com.jerry.mekmm.common.registries.MoreMachineDataComponents;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessChargingStation;

import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.component.component.AttachedEjector;
import mekanism.common.component.component.AttachedSideConfig;
import mekanism.common.component.component.AttachedSideConfig.LightConfigInfo;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismDataComponents;

import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.Map;

public class ItemBlockWirelessChargingStation extends ItemBlockTooltip<BlockTileModel<TileEntityWirelessChargingStation, Machine<TileEntityWirelessChargingStation>>> {

    public static final AttachedSideConfig SIDE_CONFIG = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, LightConfigInfo.FRONT_OUT_NO_EJECT);
        configInfo.put(TransmissionType.ENERGY, LightConfigInfo.FRONT_OUT_NO_EJECT);
        return new AttachedSideConfig(configInfo);
    });

    public ItemBlockWirelessChargingStation(BlockTileModel<TileEntityWirelessChargingStation, Machine<TileEntityWirelessChargingStation>> block, Properties properties) {
        super(block, true, properties
                .component(MoreMachineDataComponents.CHARGE_EQUIPMENT, false)
                .component(MoreMachineDataComponents.CHARGE_INVENTORY, false)
                .component(MoreMachineDataComponents.CHARGE_CURIOS, false)
                .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                .component(MekanismDataComponents.SIDE_CONFIG, SIDE_CONFIG));
    }
}
