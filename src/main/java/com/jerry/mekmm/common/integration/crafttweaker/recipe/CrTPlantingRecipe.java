package com.jerry.mekmm.common.integration.crafttweaker.recipe;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;

import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.integration.crafttweaker.CrTUtils;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;

@ZenRegister
@NativeTypeRegistration(value = PlantingRecipe.class, zenCodeName = CrTConstants.CLASS_RECIPE_PLANTING)
public class CrTPlantingRecipe {

    private CrTPlantingRecipe() {}

    /**
     * Gets the item input ingredient.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("itemInput")
    public static ItemStackIngredient getItemInput(PlantingRecipe _this) {
        return _this.getItemInput();
    }

    /**
     * Gets the chemical input ingredient.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("chemicalInput")
    public static ChemicalStackIngredient getChemicalInput(PlantingRecipe _this) {
        return _this.getChemicalInput();
    }

    /**
     * Main output representations, this list may or may not be complete and likely only contains one element, but has
     * the possibility of containing multiple.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("mainOutputs")
    public static List<IItemStack> getMainOutputs(PlantingRecipe _this) {
        return CrTUtils.convertItems(_this.getMainOutputDefinition());
    }

    /**
     * Secondary output representations, this list may or may not be complete and likely only contains one element, but
     * has the possibility of containing multiple.
     * Secondary outputs have a chance of {@link #getSecondaryChance(PlantingRecipe)} to be produced.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("secondaryOutputs")
    public static List<IItemStack> getSecondaryOutputs(PlantingRecipe _this) {
        return CrTUtils.convertItems(_this.getSecondaryOutputDefinition());
    }

    /**
     * Gets the chance (between 0 and 1) of the secondary output being produced.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("secondaryChance")
    public static double getSecondaryChance(PlantingRecipe _this) {
        return _this.getSecondaryChance();
    }
}
