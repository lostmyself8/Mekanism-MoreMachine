package com.jerry.mekmm.common.integration.crafttweaker.recipe.manager;

import com.jerry.mekmm.common.integration.crafttweaker.CrTConstants;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.impl.RollingMillIRecipe;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.integration.crafttweaker.recipe.manager.ItemStackToItemStackRecipeManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name(CrTConstants.CLASS_RECIPE_MANAGER_ROLLING_MILL)
public class RollingMillRecipeManager extends ItemStackToItemStackRecipeManager {

    public static final RollingMillRecipeManager INSTANCE = new RollingMillRecipeManager();

    private RollingMillRecipeManager() {
        super(MoreMachineRecipeType.ROLLING_MILL);
    }

    @Override
    protected ItemStackToItemStackRecipe makeRecipe(ResourceLocation id, ItemStackIngredient input, ItemStack output) {
        return new RollingMillIRecipe(id, input, output);
    }
}
