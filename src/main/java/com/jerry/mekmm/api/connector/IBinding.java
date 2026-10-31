package com.jerry.mekmm.api.connector;

import mekanism.common.lib.transmitter.TransmissionType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * ItemConnector 绑定操作的接口，供普通右键和可选连锁集成复用。
 */
public interface IBinding {

    /**
     * 按 ItemConnector 的既有目标约束切换一条连接。
     *
     * @param player       执行绑定的服务端玩家，用于向普通单点操作报告结果
     * @param level        目标所在世界
     * @param source       连接源位置
     * @param target       待连接的目标位置
     * @param face         目标连接面
     * @param type         当前工具模式对应的传输类型
     * @param reportResult 是否向玩家发送单点结果消息；批量操作关闭此项并自行汇总
     * @return 目标校验或连接操作结果
     */
    ConnectResult bind(ServerPlayer player, Level level, GlobalPos source, BlockPos target, Direction face, TransmissionType type, boolean reportResult);
}
