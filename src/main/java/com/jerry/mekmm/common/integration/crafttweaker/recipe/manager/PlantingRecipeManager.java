package com.jerry.mekmm.common.integration.crafttweaker.recipe.manager;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.impl.PlantingIRecipe;

import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.integration.crafttweaker.CrTUtils;
import mekanism.common.integration.crafttweaker.recipe.manager.MekanismRecipeManager;
import mekanism.common.util.text.TextUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.util.ItemStackUtil;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;

@ZenRegister
@ZenCodeType.Name(CrTConstants.CLASS_RECIPE_MANAGER_PLANTING)
public class PlantingRecipeManager extends MekanismRecipeManager<PlantingRecipe> {

    public static final PlantingRecipeManager INSTANCE = new PlantingRecipeManager();

    protected PlantingRecipeManager() {
        super(MoreMachineRecipeType.PLANTING);
    }

    /**
     * Adds a planting recipe that converts an item into another item and a chance based secondary item. Planting
     * Station and Planting Factories can process this recipe
     * type.
     *
     * @param name       Name of the new recipe.
     * @param itemInput  {@link ItemStackIngredient} representing the itemInput of the recipe.
     * @param gasInput   {@link GasStackIngredient} representing the gasInput of the recipe.
     * @param mainOutput {@link IItemStack} representing the main output of the recipe.
     */
    @ZenCodeType.Method
    public void addRecipe(String name, ItemStackIngredient itemInput, GasStackIngredient gasInput, IItemStack mainOutput) {
        addRecipe(makeRecipe(getAndValidateName(name), itemInput, gasInput, mainOutput));
    }

    /**
     * Adds a planting recipe that converts an item into another item and a chance based secondary item. Planting
     * Station and Planting Factories can process this recipe
     * type.
     *
     * @param name            Name of the new recipe.
     * @param itemInput       {@link ItemStackIngredient} representing the itemInput of the recipe.
     * @param gasInput        {@link GasStackIngredient} representing the gasInput of the recipe.
     * @param secondaryOutput {@link IItemStack} representing the secondary chance based output of the recipe.
     * @param secondaryChance Chance of the secondary output being produced. This must be a number greater than zero and
     *                        at most one.
     */
    @ZenCodeType.Method
    public void addRecipe(String name, ItemStackIngredient itemInput, GasStackIngredient gasInput, IItemStack secondaryOutput, double secondaryChance) {
        addRecipe(makeRecipe(getAndValidateName(name), itemInput, gasInput, secondaryOutput, secondaryChance));
    }

    /**
     * Adds a planting recipe that converts an item into another item and a chance based secondary item. Planting
     * Station and Planting Factories can process this recipe
     * type.
     *
     * @param name            Name of the new recipe.
     * @param itemInput       {@link ItemStackIngredient} representing the itemInput of the recipe.
     * @param gasInput        {@link GasStackIngredient} representing the gasInput of the recipe.
     * @param mainOutput      {@link IItemStack} representing the main output of the recipe.
     * @param secondaryOutput {@link IItemStack} representing the secondary chance based output of the recipe.
     * @param secondaryChance Chance of the secondary output being produced. This must be a number greater than zero and
     *                        at most one.
     */
    @ZenCodeType.Method
    public void addRecipe(String name, ItemStackIngredient itemInput, GasStackIngredient gasInput, IItemStack mainOutput, IItemStack secondaryOutput, double secondaryChance) {
        addRecipe(makeRecipe(getAndValidateName(name), itemInput, gasInput, mainOutput, secondaryOutput, secondaryChance));
    }

    /**
     * Creates a planting recipe that converts an item into another item.
     *
     * @param id         Name of the new recipe.
     * @param itemInput  {@link ItemStackIngredient} representing the itemInput of the recipe.
     * @param gasInput   {@link GasStackIngredient} representing the gasInput of the recipe.
     * @param mainOutput {@link IItemStack} representing the main output of the recipe. Will be validated as not empty.
     */
    public final PlantingRecipe makeRecipe(ResourceLocation id, ItemStackIngredient itemInput, GasStackIngredient gasInput, IItemStack mainOutput) {
        return new PlantingIRecipe(id, itemInput, gasInput, getAndValidateNotEmpty(mainOutput), ItemStack.EMPTY, 0);
    }

    /**
     * Creates a planting recipe that converts an item into a chance based secondary item.
     *
     * @param id              Name of the new recipe.
     * @param itemInput       {@link ItemStackIngredient} representing the itemInput of the recipe.
     * @param gasInput        {@link GasStackIngredient} representing the gasInput of the recipe.
     * @param secondaryOutput {@link IItemStack} representing the secondary chance based output of the recipe. Will be
     *                        validated as not empty.
     * @param secondaryChance Chance of the secondary output being produced. Will be validated to be a number greater
     *                        than zero and at most one.
     */
    public final PlantingRecipe makeRecipe(ResourceLocation id, ItemStackIngredient itemInput, GasStackIngredient gasInput, IItemStack secondaryOutput, double secondaryChance) {
        return new PlantingIRecipe(id, itemInput, gasInput, ItemStack.EMPTY, getAndValidateNotEmpty(secondaryOutput), getAndValidateSecondaryChance(secondaryChance));
    }

    /**
     * Creates a planting recipe that converts an item into another item and a chance based secondary item.
     *
     * @param id              Name of the new recipe.
     * @param itemInput       {@link ItemStackIngredient} representing the itemInput of the recipe.
     * @param gasInput        {@link GasStackIngredient} representing the gasInput of the recipe.
     * @param mainOutput      {@link IItemStack} representing the main output of the recipe. Will be validated as not
     *                        empty.
     * @param secondaryOutput {@link IItemStack} representing the secondary chance based output of the recipe. Will be
     *                        validated as not empty.
     * @param secondaryChance Chance of the secondary output being produced. Will be validated to be a number greater
     *                        than zero and at most one.
     */
    public final PlantingRecipe makeRecipe(ResourceLocation id, ItemStackIngredient itemInput, GasStackIngredient gasInput, IItemStack mainOutput, IItemStack secondaryOutput, double secondaryChance) {
        return new PlantingIRecipe(id, itemInput, gasInput, getAndValidateNotEmpty(mainOutput), getAndValidateNotEmpty(secondaryOutput), getAndValidateSecondaryChance(secondaryChance));
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
