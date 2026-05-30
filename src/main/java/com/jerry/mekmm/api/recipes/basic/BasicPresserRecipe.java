package com.jerry.mekmm.api.recipes.basic;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.MoreMachineRecipeSerializers;
import com.jerry.mekmm.api.recipes.MoreMachineRecipeTypes;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

@NothingNullByDefault
public class BasicPresserRecipe extends BasicTripleItemToItemRecipe {

    private static final Holder<Item> PRESSER = DeferredHolder.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "presser"));

    public BasicPresserRecipe(ItemStackIngredient first, ItemStackIngredient second, ItemStackIngredient third, ItemStack output) {
        super(first, second, third, output, MoreMachineRecipeTypes.TYPE_PRESSING.value());
    }

    @Override
    public RecipeSerializer<BasicPresserRecipe> getSerializer() {
        return MoreMachineRecipeSerializers.PRESSING.value();
    }

    @Override
    public String getGroup() {
        return "presser";
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(PRESSER);
    }
}
