package com.jerry.mekmm.api.datagen.recipe.builder;

import com.jerry.mekmm.api.recipes.basic.BasicRecyclerRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

@NothingNullByDefault
public class RecyclerRecipeBuilder extends MekanismRecipeBuilder<RecyclerRecipeBuilder> {

    private final ItemStackIngredient input;
    private final ItemStack chanceOutput;
    private final double chance;

    protected RecyclerRecipeBuilder(ItemStackIngredient input, ItemStack chanceOutput, double chance) {
        this.input = input;
        this.chanceOutput = chanceOutput;
        this.chance = chance;
    }

    public static RecyclerRecipeBuilder recycler(ItemStackIngredient input, ItemStack chanceOutput, double chance) {
        if (chanceOutput.isEmpty()) {
            throw new IllegalArgumentException("This recycler recipe requires a non empty output.");
        }
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Output chance must be at least zero and at most one.");
        }
        return new RecyclerRecipeBuilder(input, chanceOutput, chance);
    }

    @Override
    protected Recipe<?> asRecipe() {
        return new BasicRecyclerRecipe(input, chanceOutput, chance);
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(ItemStackTemplate.fromNonEmptyStack(chanceOutput));
    }
}
