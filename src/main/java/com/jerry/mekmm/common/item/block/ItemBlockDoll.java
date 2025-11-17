package com.jerry.mekmm.common.item.block;

import com.jerry.mekmm.common.block.BlockDoll;

import mekanism.common.item.block.ItemBlockTooltip;

public class ItemBlockDoll extends ItemBlockTooltip<BlockDoll> {

    public ItemBlockDoll(BlockDoll block) {
        super(block, new Properties().stacksTo(64));
    }
}
