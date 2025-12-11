package com.jerry.mekmm.common.integration.crafttweaker.recipe;

import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;

import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.integration.crafttweaker.CrTUtils;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;

@ZenRegister
@NativeTypeRegistration(value = RecyclerRecipe.class, zenCodeName = CrTConstants.CLASS_RECIPE_RECYCLING)
public class CrtRecyclerRecipe {

    private CrtRecyclerRecipe() {}

    /**
     * Gets the input ingredient.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("input")
    public static ItemStackIngredient getInput(RecyclerRecipe _this) {
        return _this.getInput();
    }

    /**
     * Output representations, this list may or may not be complete and likely only contains one element, but has the
     * possibility of containing multiple.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("outputs")
    public static List<IItemStack> getOutputs(RecyclerRecipe _this) {
        return CrTUtils.convertItems(_this.getChanceOutputDefinition());
    }

    /**
     * Gets the chance (between 0 and 1) of the secondary output being produced.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("chance")
    public static double getChance(RecyclerRecipe _this) {
        return _this.getOutputChance();
    }
}
