package com.jerry.mekmm.api.recipes.basic;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.MoreMachineRecipeSerializers;
import com.jerry.mekmm.api.recipes.MoreMachineRecipeTypes;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.basic.BasicItemStackToItemStackRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

@NothingNullByDefault
public class BasicRollingMillRecipe extends BasicItemStackToItemStackRecipe {

    private static final Holder<Item> CNC_ROLLING_MILL = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, "cnc_rolling_mill"));

    /**
     * @param input  Input.
     * @param output Output.
     */
    public BasicRollingMillRecipe(ItemStackIngredient input, ItemStackTemplate output) {
        super(input, output, MoreMachineRecipeTypes.TYPE_ROLLING_MILL.value());
    }

    @Override
    public RecipeSerializer<BasicRollingMillRecipe> getSerializer() {
        return MoreMachineRecipeSerializers.ROLLING_MILL.value();
    }

    public String getGroup() {
        return "cnc_rolling_mill";
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(CNC_ROLLING_MILL);
    }
}

