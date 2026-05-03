package com.jerry.mekmm.common.block;

import com.jerry.mekmm.common.block.prefab.BlockDoll;
import com.jerry.mekmm.common.tile.TileEntityAuthorDoll;

import mekanism.common.content.blocktype.BlockTypeTile;

public class BlockAuthorDoll extends BlockDoll<TileEntityAuthorDoll> {

    public BlockAuthorDoll(BlockTypeTile<TileEntityAuthorDoll> tileEntityDollBlockTypeTile, Properties properties) {
        super(tileEntityDollBlockTypeTile, properties);
    }
}
