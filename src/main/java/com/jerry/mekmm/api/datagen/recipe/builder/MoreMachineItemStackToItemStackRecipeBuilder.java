package com.jerry.mekmm.api.datagen.recipe.builder;

import com.jerry.mekmm.api.recipes.basic.BasicLatheRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicRollingMillRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.datagen.recipe.builder.ItemStackToItemStackRecipeBuilder;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

@NothingNullByDefault
public class MoreMachineItemStackToItemStackRecipeBuilder extends ItemStackToItemStackRecipeBuilder {

    protected MoreMachineItemStackToItemStackRecipeBuilder(ItemStackIngredient input, ItemStackTemplate output, Factory factory) {
        super(input, output, factory);
    }

    /**
     * Creates a Lathing recipe builder.
     *
     * @param input  Input.
     * @param output Output.
     */
    public static ItemStackToItemStackRecipeBuilder lathing(ItemStackIngredient input, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This lathing recipe requires a non empty item output.");
        }
        return new MoreMachineItemStackToItemStackRecipeBuilder(input, ItemStackTemplate.fromNonEmptyStack(output), BasicLatheRecipe::new);
    }

    /**
     * Creates a Rolling Mill recipe builder.
     *
     * @param input  Input.
     * @param output Output.
     */
    public static ItemStackToItemStackRecipeBuilder rollingMill(ItemStackIngredient input, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This rolling mill recipe requires a non empty item output.");
        }
        return new MoreMachineItemStackToItemStackRecipeBuilder(input, ItemStackTemplate.fromNonEmptyStack(output), BasicRollingMillRecipe::new);
    }
}
