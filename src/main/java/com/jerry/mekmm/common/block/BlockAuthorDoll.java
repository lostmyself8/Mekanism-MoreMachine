package com.jerry.mekmm.common.block;

import com.jerry.mekmm.common.block.prefab.BlockDoll;
import com.jerry.mekmm.common.tile.TileEntityAuthorDoll;

import mekanism.common.content.blocktype.BlockTypeTile;

import java.util.function.UnaryOperator;

public class BlockAuthorDoll extends BlockDoll<TileEntityAuthorDoll> {

    public BlockAuthorDoll(BlockTypeTile<TileEntityAuthorDoll> tileEntityDollBlockTypeTile, UnaryOperator<Properties> propertiesModifier) {
        super(tileEntityDollBlockTypeTile, propertiesModifier);
    }
}
