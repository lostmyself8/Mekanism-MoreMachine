package com.jerry.meklm.common.inventory.slot;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.BasicInventorySlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class BigStackOutputInventorySlot extends BasicInventorySlot {

    public static BigStackOutputInventorySlot at(@Nullable IContentsListener listener, int x, int y) {
        return new BigStackOutputInventorySlot(listener, x, y);
    }

    protected BigStackOutputInventorySlot(@Nullable IContentsListener listener, int x, int y) {
        super(ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue(), listener, x, y);
        setSlotType(ContainerSlotType.OUTPUT);
    }

    @Override
    public int getLimit(ItemStack stack) {
        try {
            return Math.multiplyExact(super.getLimit(stack), 8);
        } catch (ArithmeticException ignored) {
            return Integer.MAX_VALUE;
        }
    }
}
