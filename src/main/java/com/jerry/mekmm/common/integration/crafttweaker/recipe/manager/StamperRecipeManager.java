package com.jerry.mekmm.common.integration.crafttweaker.recipe.manager;

import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicStamperRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;

import mekanism.common.integration.crafttweaker.CrTUtils;
import mekanism.common.integration.crafttweaker.recipe.manager.MekanismRecipeManager;

import net.minecraft.world.item.crafting.RecipeInput;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredientWithAmount;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.util.ItemStackUtil;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name(CrTConstants.CLASS_RECIPE_MANAGER_STAMPING)
public class StamperRecipeManager extends MekanismRecipeManager<RecipeInput, StamperRecipe> {

    public static final StamperRecipeManager INSTANCE = new StamperRecipeManager();

    protected StamperRecipeManager() {
        super(MoreMachineRecipeType.STAMPING);
    }

    /**
     * Adds a stamping recipe that uses mold to convert an item into another item. Stampers and Stamping Factories can
     * process this recipe type.
     *
     * @param name   Name of the new recipe.
     * @param input  {@link IIngredientWithAmount} representing the item input of the recipe.
     * @param mold   {@link IIngredientWithAmount} representing the mold input of the recipe.
     * @param output {@link IItemStack} representing the output of the recipe.
     */
    @ZenCodeType.Method
    public void addRecipe(String name, IIngredientWithAmount input, IIngredientWithAmount mold, IItemStack output) {
        addRecipe(name, makeRecipe(input, mold, output));
    }

    /**
     * Creates a stamping recipe that combines multiple items into a new item.
     *
     * @param input  {@link IIngredientWithAmount} representing the item input of the recipe.
     * @param mold   {@link IIngredientWithAmount} representing the mold input of the recipe.
     * @param output {@link IItemStack} representing the output of the recipe. Will be validated as not empty.
     */
    public final StamperRecipe makeRecipe(IIngredientWithAmount input, IIngredientWithAmount mold, IItemStack output) {
        return new BasicStamperRecipe(CrTUtils.fromCrT(input), CrTUtils.fromCrT(mold), getAndValidateNotEmpty(output));
    }

    @Override
    protected String describeOutputs(StamperRecipe recipe) {
        return CrTUtils.describeOutputs(recipe.getOutputDefinition(), ItemStackUtil::getCommandString);
    }
}
