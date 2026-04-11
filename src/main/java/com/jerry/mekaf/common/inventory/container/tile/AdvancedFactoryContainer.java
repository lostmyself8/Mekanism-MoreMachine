package com.jerry.mekaf.common.inventory.container.tile;

import com.jerry.mekaf.common.registries.AdvancedFactoryContainerTypes;
import com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.Mekmm;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tier.FactoryTier;

import net.minecraft.world.entity.player.Inventory;

import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;

public class AdvancedFactoryContainer extends MekanismTileContainer<TileEntityAdvancedFactoryBase<?>> {

    public AdvancedFactoryContainer(int id, Inventory inv, TileEntityAdvancedFactoryBase<?> tile) {
        super(AdvancedFactoryContainerTypes.ADVANCED_FACTORY, id, inv, tile);
    }

    @Override
    protected int getInventoryYOffset() {
        int invY = 85 + 13 * tile.getTankCount();
        if (tile.hasExtraResourceBar()) {
            invY += 10 + 8 * (tile.getBarCount() - 1);
        }
        return invY;
    }

    @Override
    protected int getInventoryXOffset() {
        // 想尝试使用Emek的gui布局，但似乎有点麻烦，还是采用原始布局吧
        if (Mekmm.hooks.evolvedMekanism.isLoaded()) {
            if (tile.tier.ordinal() >= EMFactoryTier.OVERCLOCKED.ordinal()) {
                // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
                // 这个公式似乎并非完美，在index过大时可能会导致有细微的便宜，但未得到验证
                int index = tile.tier.ordinal() - 4;
                return (22 * (index + 2)) - (3 * index);
            }
        }
        return tile.tier == FactoryTier.ULTIMATE ? 26 : 8;
    }
}
