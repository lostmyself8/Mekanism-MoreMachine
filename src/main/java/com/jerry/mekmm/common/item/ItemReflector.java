package com.jerry.mekmm.common.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import org.jetbrains.annotations.NotNull;

public class ItemReflector extends Item {

    public ItemReflector(Properties properties) {
        this(250, properties);
    }

    public ItemReflector(int use, Properties properties) {
        super(properties.durability(use));
    }

    @Override
    public int getEnchantmentValue() {
        return 5;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean isPrimaryItemFor(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return isAllowedEnchantment(enchantment);
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return isAllowedEnchantment(enchantment);
    }

    private boolean isAllowedEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING) || enchantment.is(Enchantments.MENDING);
    }
}
