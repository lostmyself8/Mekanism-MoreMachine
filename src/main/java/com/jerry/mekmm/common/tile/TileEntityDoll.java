package com.jerry.mekmm.common.tile;

import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.common.tile.base.TileEntityMekanism;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityDoll extends TileEntityMekanism {

    public TileEntityDoll(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.AUTHOR_DOLL, pos, state);
    }
}
