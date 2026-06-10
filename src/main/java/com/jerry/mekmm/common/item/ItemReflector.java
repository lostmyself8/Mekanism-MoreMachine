package com.jerry.mekmm.common.item;

import net.minecraft.world.item.Item;

public class ItemReflector extends Item {

    public ItemReflector(Properties properties) {
        this(250, properties);
    }

    public ItemReflector(int use, Properties properties) {
        super(properties.durability(use));
    }
}
