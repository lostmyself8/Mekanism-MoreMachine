package com.jerry.mekmm.common.integration.crafttweaker.recipe.manager;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicPlantingRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;

import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput;
import mekanism.common.integration.crafttweaker.CrTUtils;
import mekanism.common.integration.crafttweaker.recipe.manager.MekanismRecipeManager;
import mekanism.common.util.text.TextUtils;

import net.minecraft.world.item.ItemStack;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredientWithAmount;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.util.ItemStackUtil;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;

@ZenRegister
@ZenCodeType.Name(CrTConstants.CLASS_RECIPE_MANAGER_PLANTING)
public class PlantingRecipeManager extends MekanismRecipeManager<SingleItemChemicalRecipeInput, PlantingRecipe> {

    public static final PlantingRecipeManager INSTANCE = new PlantingRecipeManager();

    protected PlantingRecipeManager() {
        super(MoreMachineRecipeType.PLANTING_STATION);
    }

    /**
     * Adds a planting recipe that converts an item into another item and a chance based secondary item. Planting
     * Station and Planting Factories can process this recipe
     * type.
     *
     * @param name          Name of the new recipe.
     * @param itemInput     {@link IIngredientWithAmount} representing the itemInput of the recipe.
     * @param chemicalInput {@link ChemicalStackIngredient} representing the chemicalInput of the recipe.
     * @param mainOutput    {@link IItemStack} representing the main output of the recipe.
     * @param perTickUsage  Should the recipe consume the chemical input each tick it is processing.
     */
    @ZenCodeType.Method
    public void addRecipe(String name, IIngredientWithAmount itemInput, ChemicalStackIngredient chemicalInput, IItemStack mainOutput, boolean perTickUsage) {
        addRecipe(name, makeRecipe(itemInput, chemicalInput, mainOutput, perTickUsage));
    }

    /**
     * Adds a planting recipe that converts an item into another item and a chance based secondary item. Planting
     * Station and Planting Factories can process this recipe
     * type.
     *
     * @param name            Name of the new recipe.
     * @param itemInput       {@link IIngredientWithAmount} representing the itemInput of the recipe.
     * @param chemicalInput   {@link ChemicalStackIngredient} representing the chemicalInput of the recipe.
     * @param secondaryOutput {@link IItemStack} representing the secondary chance based output of the recipe.
     * @param secondaryChance Chance of the secondary output being produced. This must be a number greater than zero and
     *                        at most one.
     * @param perTickUsage    Should the recipe consume the chemical input each tick it is processing.
     */
    @ZenCodeType.Method
    public void addRecipe(String name, IIngredientWithAmount itemInput, ChemicalStackIngredient chemicalInput, IItemStack secondaryOutput, double secondaryChance, boolean perTickUsage) {
        addRecipe(name, makeRecipe(itemInput, chemicalInput, secondaryOutput, secondaryChance, perTickUsage));
    }

    /**
     * Adds a planting recipe that converts an item into another item and a chance based secondary item. Planting
     * Station and Planting Factories can process this recipe
     * type.
     *
     * @param name            Name of the new recipe.
     * @param itemInput       {@link IIngredientWithAmount} representing the itemInput of the recipe.
     * @param chemicalInput   {@link ChemicalStackIngredient} representing the chemicalInput of the recipe.
     * @param mainOutput      {@link IItemStack} representing the main output of the recipe.
     * @param secondaryOutput {@link IItemStack} representing the secondary chance based output of the recipe.
     * @param secondaryChance Chance of the secondary output being produced. This must be a number greater than zero and
     *                        at most one.
     * @param perTickUsage    Should the recipe consume the chemical input each tick it is processing.
     */
    @ZenCodeType.Method
    public void addRecipe(String name, IIngredientWithAmount itemInput, ChemicalStackIngredient chemicalInput, IItemStack mainOutput, IItemStack secondaryOutput, double secondaryChance, boolean perTickUsage) {
        addRecipe(name, makeRecipe(itemInput, chemicalInput, mainOutput, secondaryOutput, secondaryChance, perTickUsage));
    }

    /**
     * Creates a planting recipe that converts an item into another item.
     *
     * @param itemInput     {@link IIngredientWithAmount} representing the itemInput of the recipe.
     * @param chemicalInput {@link ChemicalStackIngredient} representing the chemicalInput of the recipe.
     * @param mainOutput    {@link IItemStack} representing the main output of the recipe. Will be validated as not
     *                      empty.
     * @param perTickUsage  Should the recipe consume the chemical input each tick it is processing.
     */
    public final PlantingRecipe makeRecipe(IIngredientWithAmount itemInput, ChemicalStackIngredient chemicalInput, IItemStack mainOutput, boolean perTickUsage) {
        return new BasicPlantingRecipe(CrTUtils.fromCrT(itemInput), chemicalInput, getAndValidateNotEmpty(mainOutput), ItemStack.EMPTY, 0, perTickUsage);
    }

    /**
     * Creates a planting recipe that converts an item into a chance based secondary item.
     *
     * @param itemInput       {@link IIngredientWithAmount} representing the itemInput of the recipe.
     * @param chemicalInput   {@link ChemicalStackIngredient} representing the chemicalInput of the recipe.
     * @param secondaryOutput {@link IItemStack} representing the secondary chance based output of the recipe. Will be
     *                        validated as not empty.
     * @param secondaryChance Chance of the secondary output being produced. Will be validated to be a number greater
     *                        than zero and at most one.
     * @param perTickUsage    Should the recipe consume the chemical input each tick it is processing.
     */
    public final PlantingRecipe makeRecipe(IIngredientWithAmount itemInput, ChemicalStackIngredient chemicalInput, IItemStack secondaryOutput, double secondaryChance, boolean perTickUsage) {
        return new BasicPlantingRecipe(CrTUtils.fromCrT(itemInput), chemicalInput, ItemStack.EMPTY, getAndValidateNotEmpty(secondaryOutput), getAndValidateSecondaryChance(secondaryChance), perTickUsage);
    }

    /**
     * Creates a planting recipe that converts an item into another item and a chance based secondary item.
     *
     * @param itemInput       {@link IIngredientWithAmount} representing the itemInput of the recipe.
     * @param chemicalInput   {@link ChemicalStackIngredient} representing the chemicalInput of the recipe.
     * @param mainOutput      {@link IItemStack} representing the main output of the recipe. Will be validated as not
     *                        empty.
     * @param secondaryOutput {@link IItemStack} representing the secondary chance based output of the recipe. Will be
     *                        validated as not empty.
     * @param secondaryChance Chance of the secondary output being produced. Will be validated to be a number greater
     *                        than zero and at most one.
     * @param perTickUsage    Should the recipe consume the chemical input each tick it is processing.
     */
    public final PlantingRecipe makeRecipe(IIngredientWithAmount itemInput, ChemicalStackIngredient chemicalInput, IItemStack mainOutput, IItemStack secondaryOutput, double secondaryChance, boolean perTickUsage) {
        return new BasicPlantingRecipe(CrTUtils.fromCrT(itemInput), chemicalInput, getAndValidateNotEmpty(mainOutput), getAndValidateNotEmpty(secondaryOutput), getAndValidateSecondaryChance(secondaryChance), perTickUsage);
    }

    private double getAndValidateSecondaryChance(double secondaryChance) {
        if (secondaryChance <= 0 || secondaryChance > 1) {
            throw new IllegalArgumentException("This planting recipe requires a secondary output chance greater than zero and at most one.");
        }
        return secondaryChance;
    }

    @Override
    protected String describeOutputs(PlantingRecipe recipe) {
        StringBuilder builder = new StringBuilder();
        List<ItemStack> mainOutputs = recipe.getMainOutputDefinition();
        if (!mainOutputs.isEmpty()) {
            builder.append("main: ").append(CrTUtils.describeOutputs(mainOutputs, ItemStackUtil::getCommandString));
        }
        if (recipe.getSecondaryChance() > 0) {
            if (!mainOutputs.isEmpty()) {
                builder.append("; ");
            }
            if (recipe.getSecondaryChance() == 1) {
                builder.append("secondary: ");
            } else {
                builder.append("secondary with chance ")
                        .append(TextUtils.getPercent(recipe.getSecondaryChance()))
                        .append(": ");
            }
            builder.append(CrTUtils.describeOutputs(recipe.getSecondaryOutputDefinition(), ItemStackUtil::getCommandString));
        }
        return builder.toString();
    }
}
