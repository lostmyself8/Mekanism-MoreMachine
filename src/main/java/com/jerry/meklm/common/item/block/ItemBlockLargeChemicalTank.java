package com.jerry.meklm.common.item.block;

import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;

import mekanism.api.RelativeSide;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.component.component.AttachedSideConfig;
import mekanism.common.component.component.AttachedSideConfig.LightConfigInfo;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.util.StorageUtils;

import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class ItemBlockLargeChemicalTank<TILE extends TileEntityLargeChemicalTank<?>> extends ItemBlockTooltip<BlockTileModel<TILE, Machine<TILE>>> {

    public static final LightConfigInfo ITEM = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        sideConfig.put(RelativeSide.BACK, DataType.OUTPUT);
        return new LightConfigInfo(sideConfig, true);
    });

    public static final LightConfigInfo CHEMICAL = Util.make(() -> {
        Map<RelativeSide, DataType> sideConfig = new EnumMap<>(RelativeSide.class);
        sideConfig.put(RelativeSide.BACK, DataType.INPUT);
        return new LightConfigInfo(sideConfig, false);
    });

    private static final AttachedSideConfig SIDE_CONFIG = Util.make(() -> {
        Map<TransmissionType, LightConfigInfo> configInfo = new EnumMap<>(TransmissionType.class);
        configInfo.put(TransmissionType.ITEM, ITEM);
        configInfo.put(TransmissionType.CHEMICAL, CHEMICAL);
        return new AttachedSideConfig(configInfo);
    });

    public ItemBlockLargeChemicalTank(BlockTileModel<TILE, Machine<TILE>> block, Properties properties) {
        super(block, true, properties
                .component(MekanismDataComponents.DUMP_MODE, TileEntityChemicalTank.GasMode.IDLE)
                .component(MekanismDataComponents.SIDE_CONFIG, SIDE_CONFIG));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        // No bar for empty or stacked containers as bars are drawn on top of stack count number
        if (stack.getCount() > 1) {
            // Note: Technically this is handled by the below checks as the capability isn't exposed,
            // but we may as well short circuit it here
            return false;
        }
        return StorageUtils.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return ContainerType.CHEMICAL.getRGBDurabilityForDisplay(stack);
    }
}
