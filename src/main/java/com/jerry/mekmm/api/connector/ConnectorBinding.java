package com.jerry.mekmm.api.connector;

import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.tile.prefab.TileEntityConnectableMachine;
import com.jerry.mekmm.common.tile.prefab.TileEntityConnectableMachine.ConnectStatus;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ConnectorBinding implements IBinding {

    /**
     * 校验目标并执行连接切换；批量操作可关闭逐目标消息。
     * 使用ConnectResult避免FTB或别的模组判定时进行二次判断
     */
    @Override
    public ConnectResult bind(ServerPlayer player, Level level, GlobalPos source, BlockPos target, Direction face, TransmissionType type, boolean reportResult) {
        if (level.isClientSide) {
            throw new IllegalArgumentException("Connector binding requires a server level");
        }
        if (!level.dimension().equals(source.dimension())) {
            if (reportResult) {
                player.displayClientMessage(MoreMachineLang.CONNECTOR_ACROSS_DIMENSION.translate(EnumColor.DARK_RED), true);
            }
            return ConnectResult.ACROSS_DIMENSION;
        }

        BlockEntity targetTile = WorldUtils.getTileEntity(level, target);
        if (target.equals(source.pos()) || targetTile instanceof TileEntityBoundingBlock boundingBlock && boundingBlock.getMainPos().equals(source.pos())) {
            if (reportResult) {
                player.displayClientMessage(MoreMachineLang.CONNECTOR_SELF.translate(EnumColor.DARK_RED), true);
            }
            return ConnectResult.CONNECTOR_SELF;
        }

        TileEntityConnectableMachine sourceTile = WorldUtils.getTileEntity(TileEntityConnectableMachine.class, level, source.pos(), true);
        if (sourceTile == null) {
            if (reportResult) {
                player.displayClientMessage(MoreMachineLang.CONNECTOR_LOSE.translate(EnumColor.DARK_RED, MoreMachineUtils.formatPos(source.pos())), true);
            }
            return ConnectResult.SOURCE_LOST;
        }

        ConnectStatus status = sourceTile.connectOrCut(target, face, type);
        if (reportResult) {
            BlockState state = level.getBlockState(targetTile instanceof TileEntityBoundingBlock boundingBlock ? boundingBlock.getMainPos() : target);
            MutableComponent targetName = TextComponentUtil.translate(state.getBlock().getDescriptionId());
            switch (status) {
                case CONNECT -> player.displayClientMessage(MoreMachineLang.CONNECTOR_TO.translate(EnumColor.INDIGO, targetName, EnumColor.INDIGO, face), true);
                case DISCONNECT -> player.displayClientMessage(MoreMachineLang.CONNECTOR_DISCONNECT.translate(EnumColor.INDIGO, targetName, EnumColor.INDIGO, face), true);
                case CONNECT_FAIL -> player.displayClientMessage(MoreMachineLang.CONNECTOR_FAIL.translate(EnumColor.DARK_RED, targetName, EnumColor.DARK_RED, face), true);
            }
        }
        return ConnectResult.toResult(status);
    }
}
