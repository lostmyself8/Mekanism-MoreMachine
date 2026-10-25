package com.jerry.mekmm.api.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.*;

public final class PortRecipeDisplays {

    private PortRecipeDisplays() {}

    public static SlotDisplay items(List<ItemStack> stacks) {
        var displays = stacks.stream().filter(stack -> !stack.isEmpty()).<SlotDisplay>map(stack -> new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack))).toList();
        return displays.isEmpty() ? SlotDisplay.Empty.INSTANCE : new SlotDisplay.Composite(displays);
    }

    public static SlotDisplay station(ItemStack stack) {
        return new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack));
    }
}
