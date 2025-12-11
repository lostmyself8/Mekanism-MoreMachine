package com.jerry.mekmm.common.integration.crafttweaker.recipe;

import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;

import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.integration.crafttweaker.CrTUtils;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;

@ZenRegister
@NativeTypeRegistration(value = StamperRecipe.class, zenCodeName = CrTConstants.CLASS_RECIPE_STAMPING)
public class CrTStamperRecipe {

    private CrTStamperRecipe() {}

    /**
     * Gets the input ingredient.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("input")
    public static ItemStackIngredient getInput(StamperRecipe _this) {
        return _this.getInput();
    }

    /**
     * Gets the mold ingredient.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("mod")
    public static ItemStackIngredient getMold(StamperRecipe _this) {
        return _this.getMold();
    }

    /**
     * Output representations, this list may or may not be complete and likely only contains one element, but has the
     * possibility of containing multiple.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("outputs")
    public static List<IItemStack> getOutputs(StamperRecipe _this) {
        return CrTUtils.convertItems(_this.getOutputDefinition());
    }
}
