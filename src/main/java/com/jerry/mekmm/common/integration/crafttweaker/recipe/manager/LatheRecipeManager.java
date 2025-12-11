package com.jerry.mekmm.common.integration.crafttweaker.recipe.manager;

import com.jerry.mekmm.api.recipes.basic.BasicLatheRecipe;
import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.common.integration.crafttweaker.CrTUtils;
import mekanism.common.integration.crafttweaker.recipe.manager.ItemStackToItemStackRecipeManager;

import net.minecraft.world.item.ItemStack;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredientWithAmount;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name(CrTConstants.CLASS_RECIPE_MANAGER_LATHING)
public class LatheRecipeManager extends ItemStackToItemStackRecipeManager {

    public static final LatheRecipeManager INSTANCE = new LatheRecipeManager();

    private LatheRecipeManager() {
        super(MoreMachineRecipeType.LATHING);
    }

    @Override
    protected ItemStackToItemStackRecipe makeRecipe(IIngredientWithAmount input, ItemStack output) {
        return new BasicLatheRecipe(CrTUtils.fromCrT(input), output);
    }
}
