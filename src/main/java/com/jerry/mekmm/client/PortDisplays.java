package com.jerry.mekmm.client;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalStackTemplate;
import mekanism.api.recipes.ingredients.chemical.display.ChemicalStackSlotDisplay;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackSlotDisplay;

import java.util.ArrayList;
import java.util.List;

public final class PortDisplays {

    private PortDisplays() {}

    public static SlotDisplay of(List<?> stacks) {
        List<SlotDisplay> entries = new ArrayList<>();
        for (Object value : stacks) {
            if (value instanceof ItemStack stack && !stack.isEmpty()) entries.add(new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack)));
            else if (value instanceof ItemStackTemplate stack) entries.add(new SlotDisplay.ItemStackSlotDisplay(stack));
            else if (value instanceof ChemicalStack stack && !stack.isEmpty()) entries.add(new ChemicalStackSlotDisplay(ChemicalStackTemplate.fromNonEmptyStack(stack)));
            else if (value instanceof ChemicalStackTemplate stack) entries.add(new ChemicalStackSlotDisplay(stack));
            else if (value instanceof FluidStack stack && !stack.isEmpty()) entries.add(new FluidStackSlotDisplay(FluidStackTemplate.fromNonEmptyStack(stack)));
            else if (value instanceof FluidStackTemplate stack) entries.add(new FluidStackSlotDisplay(stack));
        }
        return entries.isEmpty() ? SlotDisplay.Empty.INSTANCE : new SlotDisplay.Composite(entries);
    }
}
