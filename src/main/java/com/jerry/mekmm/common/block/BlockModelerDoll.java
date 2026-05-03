package com.jerry.mekmm.common.block;

import com.jerry.mekmm.common.block.prefab.BlockDoll;
import com.jerry.mekmm.common.tile.TileEntityModelerDoll;

import mekanism.common.content.blocktype.BlockTypeTile;

public class BlockModelerDoll extends BlockDoll<TileEntityModelerDoll> {

    public BlockModelerDoll(BlockTypeTile<TileEntityModelerDoll> tileEntityDollBlockTypeTile, Properties properties) {
        super(tileEntityDollBlockTypeTile, properties);
    }
}
