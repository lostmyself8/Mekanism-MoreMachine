package com.jerry.mekmm.common.block;

import com.jerry.mekmm.common.tile.TileEntityDoll;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;

public class BlockDoll extends BlockTile<TileEntityDoll, BlockTypeTile<TileEntityDoll>> {

    public BlockDoll(BlockTypeTile<TileEntityDoll> tileEntityDollBlockTypeTile, Properties properties) {
        super(tileEntityDollBlockTypeTile, properties);
    }
}
