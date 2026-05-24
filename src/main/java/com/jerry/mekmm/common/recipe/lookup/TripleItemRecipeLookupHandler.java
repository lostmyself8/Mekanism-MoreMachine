package com.jerry.mekmm.common.recipe.lookup;

import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache.TripleItem;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.lookup.ITripleRecipeLookupHandler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriPredicate;

public interface TripleItemRecipeLookupHandler<RECIPE extends MekanismRecipe<?> & TriPredicate<ItemStack, ItemStack, ItemStack>> extends
                                              ITripleRecipeLookupHandler<ItemStack, ItemStack, ItemStack, RECIPE, TripleItem<RECIPE>> {}
