package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.api.recipes.basic.MMBasicChemicalChemicalToChemicalRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

@NothingNullByDefault
public class ChemicalReplicatorIRecipeSingle extends MMBasicChemicalChemicalToChemicalRecipe {

    /**
     * @param leftInput  Left input.
     * @param rightInput Right input.
     * @param output     Output.
     * @apiNote The order of the inputs does not matter.
     */
    public ChemicalReplicatorIRecipeSingle(ChemicalStackIngredient leftInput, ChemicalStackIngredient rightInput, ChemicalStack output) {
        super(leftInput, rightInput, output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    @Override
    public String getGroup() {
        return "duplicator";
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(MoreMachineBlocks.CHEMICAL_REPLICATOR);
    }
}
