package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.api.recipes.StamperRecipe;
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
public class StamperIRecipe extends StamperRecipe {

    /**
     * @param id     Recipe name.
     * @param input  Input.
     * @param mold   Mold Input.
     * @param output Output.
     */
    public StamperIRecipe(ResourceLocation id, ItemStackIngredient input, ItemStackIngredient mold, ItemStack output) {
        super(id, input, mold, output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MoreMachineRecipeSerializers.STAMPING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MoreMachineRecipeType.STAMPING.get();
    }

    @Override
    public String getGroup() {
        return MoreMachineBlocks.CNC_STAMPER.getName();
    }

    @Override
    public ItemStack getToastSymbol() {
        return MoreMachineBlocks.CNC_STAMPER.getItemStack();
    }
}
