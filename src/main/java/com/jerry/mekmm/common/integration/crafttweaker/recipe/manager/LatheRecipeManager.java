package com.jerry.mekmm.common.integration.crafttweaker.recipe.manager;

import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.impl.LatheIRecipe;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.integration.crafttweaker.recipe.manager.ItemStackToItemStackRecipeManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name(CrTConstants.CLASS_RECIPE_MANAGER_LATHING)
public class LatheRecipeManager extends ItemStackToItemStackRecipeManager {

    public static final LatheRecipeManager INSTANCE = new LatheRecipeManager();

    private LatheRecipeManager() {
        super(MoreMachineRecipeType.LATHING);
    }

    @Override
    protected ItemStackToItemStackRecipe makeRecipe(ResourceLocation id, ItemStackIngredient input, ItemStack output) {
        return new LatheIRecipe(id, input, output);
    }
}
