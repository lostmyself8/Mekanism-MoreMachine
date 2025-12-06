package com.jerry.mekmm.common.block;

import com.jerry.mekmm.common.block.prefab.BlockDoll;
import com.jerry.mekmm.common.tile.TileEntityModelerDoll;

import mekanism.common.content.blocktype.BlockTypeTile;

import java.util.function.UnaryOperator;

public class BlockModelerDoll extends BlockDoll<TileEntityModelerDoll> {

    public BlockModelerDoll(BlockTypeTile<TileEntityModelerDoll> tileEntityDollBlockTypeTile, UnaryOperator<Properties> propertiesModifier) {
        super(tileEntityDollBlockTypeTile, propertiesModifier);
    }
}
