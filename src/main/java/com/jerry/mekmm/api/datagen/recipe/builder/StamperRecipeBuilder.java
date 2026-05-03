package com.jerry.mekmm.api.datagen.recipe.builder;

import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicStamperRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;

@NothingNullByDefault
public class StamperRecipeBuilder extends MekanismRecipeBuilder<StamperRecipeBuilder> {

    private final ItemStackIngredient input;
    private final ItemStackIngredient mold;
    private final ItemStack output;

    protected StamperRecipeBuilder(ItemStackIngredient input, ItemStackIngredient mold, ItemStack output) {
        this.input = input;
        this.mold = mold;
        this.output = output;
    }

    /**
     * Creates a Stamping recipe builder.
     *
     * @param input  Input.
     * @param mold   Mold Input.
     * @param output Output.
     */
    public static StamperRecipeBuilder stamping(ItemStackIngredient input, ItemStackIngredient mold, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This stamping recipe requires a non empty item output.");
        }
        return new StamperRecipeBuilder(input, mold, output);
    }

    @Override
    protected StamperRecipe asRecipe() {
        return new BasicStamperRecipe(input, mold, output);
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(ItemStackTemplate.fromNonEmptyStack(output));
    }

    /**
     * Builds this recipe using the output item's name as the recipe name.
     *
     * @param recipeOutput Finished Recipe Consumer.
     */
    public void build(RecipeOutput recipeOutput) {
        save(recipeOutput);
    }
}
