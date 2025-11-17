package com.jerry.datagen.common.recipe;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

/**
 * Interface for helping split the recipe provider over multiple classes to make it a bit easier to interact with
 */
public interface ISubRecipeProvider {

    void addRecipes(Consumer<FinishedRecipe> consumer);
}
