package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineRecipeSerializers;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

@NothingNullByDefault
public class RecyclerIRecipe extends RecyclerRecipe {

    public RecyclerIRecipe(ResourceLocation id, ItemStackIngredient input, ItemStack chanceOutput, double chance) {
        super(id, input, chanceOutput, chance);
    }

    @Override
    public RecipeSerializer<RecyclerRecipe> getSerializer() {
        return MoreMachineRecipeSerializers.RECYCLER.get();
    }

    @Override
    public RecipeType<RecyclerRecipe> getType() {
        return MoreMachineRecipeType.RECYCLING.get();
    }

    @Override
    public String getGroup() {
        return MoreMachineBlocks.RECYCLER.getName();
    }

    @Override
    public ItemStack getToastSymbol() {
        return MoreMachineBlocks.RECYCLER.getItemStack();
    }
}
