package com.jerry.meklm.common.inventory.slot;

import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.BasicInventorySlot;

import net.neoforged.neoforge.transfer.item.ItemResource;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class BigStackOutputInventorySlot extends BasicInventorySlot {

    private static final int STACK_MULTIPLIER = 8;

    public static BigStackOutputInventorySlot at(@Nullable IContentsListener listener, int x, int y) {
        return new BigStackOutputInventorySlot(listener, x, y);
    }

    private BigStackOutputInventorySlot(@Nullable IContentsListener listener, int x, int y) {
        super(ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue(), null, null, listener, x, y);
        setSlotType(ContainerSlotType.OUTPUT);
    }

    @Override
    @Range(from = 0, to = Long.MAX_VALUE)
    public long capacityAsLong(ItemResource resource) {
        return Math.multiplyExact(super.capacityAsLong(resource), STACK_MULTIPLIER);
    }
}
