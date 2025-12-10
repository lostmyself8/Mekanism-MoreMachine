package com.jerry.mekmm.common.integration.crafttweaker.recipe.manager;

import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.impl.RecyclerIRecipe;

import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.integration.crafttweaker.recipe.manager.MekanismRecipeManager;

import net.minecraft.resources.ResourceLocation;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name(CrTConstants.CLASS_RECIPE_MANAGER_RECYCLING)
public class RecyclerRecipeManager extends MekanismRecipeManager<RecyclerRecipe> {

    public static final RecyclerRecipeManager INSTANCE = new RecyclerRecipeManager();

    protected RecyclerRecipeManager() {
        super(MoreMachineRecipeType.RECYCLING);
    }

    /**
     * Adds a recycling recipe that converts an item into another item. Recycler and Recycling Factories can process
     * this recipe type.
     *
     * @param name   Name of the new recipe.
     * @param input  {@link ItemStackIngredient} representing the input of the recipe.
     * @param output {@link IItemStack} representing the output of the recipe.
     * @param chance Chance of the output being produced. This must be a number greater than zero and at most one.
     */
    @ZenCodeType.Method
    public void addRecipe(String name, ItemStackIngredient input, IItemStack output, double chance) {
        addRecipe(makeRecipe(getAndValidateName(name), input, output, chance));
    }

    /**
     * Creates a recycling recipe that converts an item into another item and a chance based secondary item.
     *
     * @param id     Name of the new recipe.
     * @param input  {@link ItemStackIngredient} representing the input of the recipe.
     * @param output {@link IItemStack} representing the output of the recipe. Will be validated as not empty.
     * @param chance Chance of the output being produced. Will be validated to be a number greater than zero and at most
     *               one.
     */
    public final RecyclerRecipe makeRecipe(ResourceLocation id, ItemStackIngredient input, IItemStack output, double chance) {
        return new RecyclerIRecipe(id, input, getAndValidateNotEmpty(output), getAndValidateChance(chance));
    }

    private double getAndValidateChance(double chance) {
        if (chance <= 0 || chance > 1) {
            throw new IllegalArgumentException("This recycling recipe requires a output chance greater than zero and at most one.");
        }
        return chance;
    }

    @Override
    protected String describeOutputs(RecyclerRecipe recipe) {
        return "";
    }
}
