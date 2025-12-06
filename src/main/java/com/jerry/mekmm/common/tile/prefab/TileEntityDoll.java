package com.jerry.mekmm.common.tile.prefab;

import mekanism.api.providers.IBlockProvider;
import mekanism.common.tile.base.TileEntityMekanism;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

// 留着，或许之后会给玩偶加一些功能
public class TileEntityDoll extends TileEntityMekanism {

    public TileEntityDoll(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }
}
