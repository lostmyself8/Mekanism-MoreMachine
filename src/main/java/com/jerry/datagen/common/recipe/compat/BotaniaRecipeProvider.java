package com.jerry.datagen.common.recipe.compat;

import mekanism.api.annotations.ParametersAreNotNullByDefault;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

@ParametersAreNotNullByDefault
public class BotaniaRecipeProvider extends CompatRecipeProvider {

    public BotaniaRecipeProvider(String modId) {
        super(modId);
    }

    @Override
    protected void registerRecipes(Consumer<FinishedRecipe> consumer, String basePath) {}
}
