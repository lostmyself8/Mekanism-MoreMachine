package com.jerry.meklm.common.inventory.slot;

import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.inventory.slot.InputInventorySlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class BigStackInputInventorySlot extends InputInventorySlot {

    public static BigStackInputInventorySlot at(@Nullable IContentsListener listener, int x, int y) {
        return at(ConstantPredicates.alwaysTrue(), listener, x, y);
    }

    public static BigStackInputInventorySlot at(Predicate<@NotNull ItemStack> isItemValid, @Nullable IContentsListener listener, int x, int y) {
        return at(ConstantPredicates.alwaysTrue(), isItemValid, listener, x, y);
    }

    public static BigStackInputInventorySlot at(Predicate<@NotNull ItemStack> insertPredicate, Predicate<@NotNull ItemStack> isItemValid, @Nullable IContentsListener listener,
                                        int x, int y) {
        Objects.requireNonNull(insertPredicate, "Insertion check cannot be null");
        Objects.requireNonNull(isItemValid, "Item validity check cannot be null");
        return new BigStackInputInventorySlot(insertPredicate, isItemValid, listener, x, y);
    }

    protected BigStackInputInventorySlot(Predicate<@NotNull ItemStack> insertPredicate, Predicate<@NotNull ItemStack> isItemValid, @Nullable IContentsListener listener, int x, int y) {
        super(insertPredicate, isItemValid, listener, x, y);
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
