package com.jerry.mekmm.common.tile;

import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.tile.prefab.TileEntityDoll;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityAuthorDoll extends TileEntityDoll {

    public TileEntityAuthorDoll(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.AUTHOR_DOLL, pos, state);
    }
}
