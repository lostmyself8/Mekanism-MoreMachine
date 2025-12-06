package com.jerry.mekmm.common.item.block;

import com.jerry.mekmm.common.block.prefab.BlockDoll;

import mekanism.common.item.block.ItemBlockTooltip;

public class ItemBlockDoll<BLOCK extends BlockDoll<?>> extends ItemBlockTooltip<BLOCK> {

    public ItemBlockDoll(BLOCK block, Properties properties) {
        super(block, properties);
    }
}
