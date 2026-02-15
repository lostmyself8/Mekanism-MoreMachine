package com.jerry.mekmm.common.tile.prefab;

import com.jerry.mekmm.common.tile.interfaces.ITileConnect;

import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileEntityConnectableMachine extends TileEntityConfigurableMachine implements ITileConnect {

    public TileEntityConnectableMachine(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    public abstract ConnectStatus connectOrCut(BlockPos pos, Direction side, TransmissionType type);

    public enum ConnectStatus {
        CONNECT,
        DISCONNECT,
        CONNECT_FAIL
    }
}
