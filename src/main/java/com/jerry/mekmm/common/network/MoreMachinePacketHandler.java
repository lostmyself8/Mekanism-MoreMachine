package com.jerry.mekmm.common.network;

import com.jerry.mekmm.common.network.to_server.*;
import com.jerry.mekmm.common.network.to_server.button.MoreMachinePacketTileButtonPress;

import mekanism.common.lib.Version;
import mekanism.common.network.BasePacketHandler;
import mekanism.common.network.to_client.configuration.SyncAllSecurityData;

import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;

public class MoreMachinePacketHandler extends BasePacketHandler {

    public MoreMachinePacketHandler(IEventBus modEventBus, Version version) {
        super(modEventBus, version);
        modEventBus.addListener(RegisterConfigurationTasksEvent.class, event -> {
            ServerConfigurationPacketListener listener = event.getListener();
            event.register(new SyncAllSecurityData(listener));
        });
    }

    @Override
    protected void registerClientToServer(PacketRegistrar registrar) {
        registrar.play(PacketViewConnection.TYPE, PacketViewConnection.STREAM_CODEC);
        registrar.play(MoreMachinePacketGuiInteract.TYPE, MoreMachinePacketGuiInteract.STREAM_CODEC);
        registrar.play(PacketGuiSetDoubleValue.TYPE, PacketGuiSetDoubleValue.STREAM_CODEC);
        registrar.play(PacketGuiSetIntValue.TYPE, PacketGuiSetIntValue.STREAM_CODEC);
        registrar.play(PacketGuiSetLongValue.TYPE, PacketGuiSetLongValue.STREAM_CODEC);

        // Button Press
        registrar.play(MoreMachinePacketTileButtonPress.TYPE, MoreMachinePacketTileButtonPress.STREAM_CODEC);
    }

    @Override
    protected void registerServerToClient(PacketRegistrar registrar) {}
}
