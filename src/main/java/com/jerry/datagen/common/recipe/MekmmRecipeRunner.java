package com.jerry.datagen.common.recipe;

import com.jerry.mekmm.Mekmm;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class MekmmRecipeRunner extends RecipeProvider.Runner {

    private final BiFunction<HolderLookup.Provider, RecipeOutput, RecipeProvider> factory;

    public MekmmRecipeRunner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries,
                             BiFunction<HolderLookup.Provider, RecipeOutput, RecipeProvider> factory) {
        super(packOutput, registries);
        this.factory = factory;
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return factory.apply(registries, output);
    }

    @Override
    public String getName() {
        return "Recipes: " + Mekmm.MOD_ID;
    }
}
