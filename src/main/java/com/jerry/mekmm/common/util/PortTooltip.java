package com.jerry.mekmm.common.util;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.ItemAccessUtils;
import mekanism.common.util.text.TextUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public final class PortTooltip {

    private PortTooltip() {}

    public static void addStoredChemical(ItemStack stack, Consumer<Component> output) {
        var tanks = Capabilities.CHEMICAL.getCapability(ItemAccessUtils.sideEffectFreeAccess(stack));
        if (tanks != null) for (int i = 0; i < tanks.size(); i++) {
            var resource = tanks.getResource(i);
            if (!resource.isEmpty()) output.accept(Component.literal(TextUtils.format(tanks.getAmountAsLong(i)) + " mB " + resource.typeHolder().getRegisteredName()));
        }
    }
}
