package com.jerry.mekmm.api.datagen.recipe.builder;

import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicPresserRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;

@NothingNullByDefault
public class TripleItemToItemRecipeBuilder extends MekanismRecipeBuilder<TripleItemToItemRecipeBuilder> {

    private final Factory factory;
    private final ItemStackIngredient first;
    private final ItemStackIngredient second;
    private final ItemStackIngredient third;
    private final ItemStack output;

    protected TripleItemToItemRecipeBuilder(ItemStackIngredient first, ItemStackIngredient second, ItemStackIngredient third, ItemStack output, Factory factory) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.output = output;
        this.factory = factory;
    }

    /**
     * Creates a Pressing recipe builder.
     *
     * @param first  First input.
     * @param second Second input.
     * @param third  Third input.
     * @param output Output.
     */
    public static TripleItemToItemRecipeBuilder pressing(ItemStackIngredient first, ItemStackIngredient second, ItemStackIngredient third, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This Presser recipe requires a non empty item output.");
        }
        return new TripleItemToItemRecipeBuilder(first, second, third, output, BasicPresserRecipe::new);
    }

    @Override
    protected TripleItemToItemRecipe asRecipe() {
        return factory.create(first, second, third, output);
    }

    /**
     * Builds this recipe using the output item's name as the recipe name.
     *
     * @param recipeOutput Finished Recipe Consumer.
     */
    public void build(RecipeOutput recipeOutput) {
        build(recipeOutput, output.getItemHolder());
    }

    @FunctionalInterface
    public interface Factory {

        TripleItemToItemRecipe create(ItemStackIngredient first, ItemStackIngredient second, ItemStackIngredient third, ItemStack output);
    }
}
