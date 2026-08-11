package com.jerry.meklm.common.inventory.slot;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.inventory.slot.InputInventorySlot;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.item.ItemResource;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class BigStackInputInventorySlot extends InputInventorySlot {

    private static final int STACK_MULTIPLIER = 8;

    public static BigStackInputInventorySlot at(BiPredicate<ItemResource, AutomationType> insertPredicate, Predicate<ItemResource> isItemValid,
                                                @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(insertPredicate, "Insertion check cannot be null");
        Objects.requireNonNull(isItemValid, "Item validity check cannot be null");
        return new BigStackInputInventorySlot(insertPredicate, isItemValid, listener, x, y);
    }

    public static BigStackInputInventorySlot at(Predicate<ItemResource> isItemValid, @Nullable IContentsListener listener, int x, int y) {
        return at(ConstantPredicates.alwaysTrueBi(), isItemValid, listener, x, y);
    }

    public static BigStackInputInventorySlot at(@Nullable IContentsListener listener, int x, int y) {
        return at(ConstantPredicates.alwaysTrue(), listener, x, y);
    }

    private BigStackInputInventorySlot(BiPredicate<ItemResource, AutomationType> insertPredicate, Predicate<ItemResource> isItemValid,
                                       @Nullable IContentsListener listener, int x, int y) {
        super(Item.ABSOLUTE_MAX_STACK_SIZE, insertPredicate, isItemValid, null, null, listener, x, y);
    }

    @Override
    @Range(from = 0, to = Long.MAX_VALUE)
    public long capacityAsLong(ItemResource resource) {
        return Math.multiplyExact(super.capacityAsLong(resource), STACK_MULTIPLIER);
    }
}
