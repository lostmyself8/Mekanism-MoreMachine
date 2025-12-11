package com.jerry.mekmm.common.integration.crafttweaker.recipe.handler;

import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.recipe.manager.RecyclerRecipeManager;

import mekanism.common.integration.crafttweaker.CrTRecipeComponents;
import mekanism.common.integration.crafttweaker.CrTUtils;
import mekanism.common.integration.crafttweaker.recipe.handler.MekanismRecipeHandler;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;

import java.util.Optional;

@IRecipeHandler.For(RecyclerRecipe.class)
public class RecyclerRecipeHandler extends MekanismRecipeHandler<RecyclerRecipe> {

    @Override
    public String dumpToCommandString(IRecipeManager<? super RecyclerRecipe> manager, RegistryAccess registryAccess, RecipeHolder<RecyclerRecipe> recipeHolder) {
        RecyclerRecipe recipe = recipeHolder.value();
        return buildCommandString(manager, recipeHolder, recipe.getInput(), recipe.getChanceOutputDefinition(), recipe.getOutputChance());
    }

    @Override
    public <U extends Recipe<?>> boolean doesConflict(IRecipeManager<? super RecyclerRecipe> manager, RecyclerRecipe recipe, U o) {
        return o instanceof RecyclerRecipe other && ingredientConflicts(recipe.getInput(), other.getInput());
    }

    @Override
    public Optional<IDecomposedRecipe> decompose(IRecipeManager<? super RecyclerRecipe> manager, RegistryAccess registryAccess, RecyclerRecipe recipe) {
        return decompose(recipe.getInput(), recipe.getChanceOutputDefinition(), recipe.getOutputChance());
    }

    @Override
    public Optional<RecyclerRecipe> recompose(IRecipeManager<? super RecyclerRecipe> m, RegistryAccess registryAccess, IDecomposedRecipe recipe) {
        if (m instanceof RecyclerRecipeManager manager) {
            double chance = CrTUtils.getSingleIfPresent(recipe, CrTRecipeComponents.CHANCE).orElse(0D);
            return Optional.of(manager.makeRecipe(
                    recipe.getOrThrowSingle(CrTRecipeComponents.ITEM.input()),
                    recipe.getOrThrowSingle(CrTRecipeComponents.ITEM.output()),
                    chance));
        }
        return Optional.empty();
    }
}
