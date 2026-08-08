package com.jerry.mekmm.common.tile;

import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.tile.prefab.TileEntityDoll;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityLexEdenFumo extends TileEntityDoll {

    public TileEntityLexEdenFumo(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.LEX_EDEN_FUMO, pos, state);
    }
}
