package com.jerry.mekmm.common.network;

import com.jerry.mekmm.Mekmm;

import mekanism.common.network.BasePacketHandler;

import net.minecraftforge.network.simple.SimpleChannel;

public class MoreMachinePacketHandler extends BasePacketHandler {

    private final SimpleChannel netHandler = createChannel(Mekmm.rl(Mekmm.MOD_ID), Mekmm.instance.versionNumber);

    @Override
    protected SimpleChannel getChannel() {
        return netHandler;
    }

    @Override
    public void initialize() {
        // 1.20.1自行发包会导致错误信息，应该使用mixin注入mekanism的PacketHandler
        // registerClientToServer(MoreMachinePacketGuiInteract.class, MoreMachinePacketGuiInteract::decode);
    }
}
