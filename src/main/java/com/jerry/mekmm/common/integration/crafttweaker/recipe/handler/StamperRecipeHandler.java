package com.jerry.mekmm.common.integration.crafttweaker.recipe.handler;

import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.recipe.manager.StamperRecipeManager;

import mekanism.common.integration.crafttweaker.CrTRecipeComponents;
import mekanism.common.integration.crafttweaker.CrTUtils;
import mekanism.common.integration.crafttweaker.CrTUtils.UnaryTypePair;
import mekanism.common.integration.crafttweaker.recipe.handler.MekanismRecipeHandler;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.blamejared.crafttweaker.api.ingredient.IIngredientWithAmount;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;

import java.util.Optional;

@IRecipeHandler.For(StamperRecipe.class)
public class StamperRecipeHandler extends MekanismRecipeHandler<StamperRecipe> {

    @Override
    public String dumpToCommandString(IRecipeManager<? super StamperRecipe> manager, RegistryAccess registryAccess, RecipeHolder<StamperRecipe> recipeHolder) {
        StamperRecipe recipe = recipeHolder.value();
        return buildCommandString(manager, recipeHolder, recipe.getInput(), recipe.getMold(), recipe.getOutputDefinition());
    }

    @Override
    public <U extends Recipe<?>> boolean doesConflict(IRecipeManager<? super StamperRecipe> manager, StamperRecipe recipe, U o) {
        // Only support if the other is a combiner recipe and don't bother checking the reverse as the recipe type's
        // generics
        // ensures that it is of the same type
        if (o instanceof StamperRecipe other) {
            return ingredientConflicts(recipe.getInput(), other.getInput()) &&
                    ingredientConflicts(recipe.getMold(), other.getMold());
        }
        return false;
    }

    @Override
    public Optional<IDecomposedRecipe> decompose(IRecipeManager<? super StamperRecipe> manager, RegistryAccess registryAccess, StamperRecipe recipe) {
        return decompose(recipe.getInput(), recipe.getMold(), recipe.getOutputDefinition());
    }

    @Override
    public Optional<StamperRecipe> recompose(IRecipeManager<? super StamperRecipe> m, RegistryAccess registryAccess, IDecomposedRecipe recipe) {
        if (m instanceof StamperRecipeManager manager) {
            UnaryTypePair<IIngredientWithAmount> inputs = CrTUtils.getPair(recipe, CrTRecipeComponents.ITEM.input());
            return Optional.of(manager.makeRecipe(
                    inputs.a(),
                    inputs.b(),
                    recipe.getOrThrowSingle(CrTRecipeComponents.ITEM.output())));
        }
        return Optional.empty();
    }
}
