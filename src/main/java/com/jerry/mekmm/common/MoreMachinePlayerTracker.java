package com.jerry.mekmm.common;

import com.jerry.mekmm.common.registries.MoreMachineDataComponents;

import net.minecraft.util.TriState;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class MoreMachinePlayerTracker {

    public MoreMachinePlayerTracker() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void rightClickBlock(RightClickBlock event) {
        // TODO:在考虑要不要把副手也算上
        ItemStack itemInHand = event.getEntity().getItemInHand(event.getHand());
        if (!itemInHand.isEmpty() && itemInHand.has(MoreMachineDataComponents.CONNECT_FROM)) {
            // 禁用掉与方块交互的操作
            event.setUseBlock(TriState.FALSE);
        }
    }
}
