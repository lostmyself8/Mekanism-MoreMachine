package com.jerry.mekmm.common.block.prefab;

import com.jerry.mekmm.common.tile.prefab.TileEntityDoll;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;

import java.util.function.UnaryOperator;

public class BlockDoll<TILE extends TileEntityDoll> extends BlockTile<TILE, BlockTypeTile<TILE>> {

    public BlockDoll(BlockTypeTile<TILE> tileEntityDollBlockTypeTile, UnaryOperator<Properties> propertiesModifier) {
        super(tileEntityDollBlockTypeTile, propertiesModifier);
    }
}
