package com.jerry.datagen.common.recipe;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class MekmmRecipeRunner extends RecipeProvider.Runner {

    private final BiFunction<Provider, RecipeOutput, RecipeProvider> factory;
    private final String modId;

    public MekmmRecipeRunner(PackOutput packOutput, CompletableFuture<Provider> registries, BiFunction<Provider, RecipeOutput, RecipeProvider> factory, String modId) {
        super(packOutput, registries);
        this.factory = factory;
        this.modId = modId;
    }

    @Override
    protected @NotNull RecipeProvider createRecipeProvider(@NotNull Provider registries, @NotNull RecipeOutput output) {
        return factory.apply(registries, output);
    }

    @Override
    public @NotNull String getName() {
        return "Recipes: " + modId;
    }
}
