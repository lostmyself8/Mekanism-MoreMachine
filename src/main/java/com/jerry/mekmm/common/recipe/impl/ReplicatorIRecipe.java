package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.util.RegistryUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

@NothingNullByDefault
public class ReplicatorIRecipe extends ItemStackGasToItemStackRecipe {

    public ReplicatorIRecipe(Item item, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack output) {
        this(Mekmm.rl("item_replicator/" + RegistryUtils.getName(item).toString().replace(':', '/')), itemInput, gasInput, output);
    }

    public ReplicatorIRecipe(ResourceLocation id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack output) {
        super(id, itemInput, gasInput, output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    @Override
    public String getGroup() {
        return MoreMachineBlocks.REPLICATOR.getName();
    }

    @Override
    public ItemStack getToastSymbol() {
        return MoreMachineBlocks.REPLICATOR.getItemStack();
    }
}
