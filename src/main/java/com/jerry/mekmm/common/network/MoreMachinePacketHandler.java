package com.jerry.mekmm.common.network;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.network.to_server.MoreMachinePacketGuiInteract;

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
        registerClientToServer(MoreMachinePacketGuiInteract.class, MoreMachinePacketGuiInteract::decode);
    }
}
