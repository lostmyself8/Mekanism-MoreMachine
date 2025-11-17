package com.jerry.mekmm.common.recipe.impl;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.FluidStackGasToFluidStackRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.common.util.RegistryUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

@NothingNullByDefault
public class FluidReplicatorIRecipe extends FluidStackGasToFluidStackRecipe {

    public FluidReplicatorIRecipe(Fluid fluid, FluidStackIngredient fluidInput, GasStackIngredient gasInput, FluidStack output) {
        this(Mekmm.rl("fluid_replicator/" + RegistryUtils.getName(fluid).toString().replace(':', '/')), fluidInput, gasInput, output);
    }

    public FluidReplicatorIRecipe(ResourceLocation id, FluidStackIngredient itemInput, GasStackIngredient gasInput, FluidStack output) {
        super(id, itemInput, gasInput, output);
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
        return MoreMachineBlocks.FLUID_REPLICATOR.getName();
    }

    @Override
    public ItemStack getToastSymbol() {
        return MoreMachineBlocks.FLUID_REPLICATOR.getItemStack();
    }
}
