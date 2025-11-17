package com.jerry.mekmm.client.jei;

import com.jerry.mekmm.api.recipes.FluidStackGasToFluidStackRecipe;
import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.client.jei.MekanismJEIRecipeType;

public class MoreMachineJEIRecipeType {

    private MoreMachineJEIRecipeType() {}

    public static final MekanismJEIRecipeType<RecyclerRecipe> RECYCLING = new MekanismJEIRecipeType<>(MoreMachineBlocks.RECYCLER, RecyclerRecipe.class);
    public static final MekanismJEIRecipeType<PlantingRecipe> PLANTING = new MekanismJEIRecipeType<>(MoreMachineBlocks.PLANTING_STATION, PlantingRecipe.class);

    public static final MekanismJEIRecipeType<StamperRecipe> CNC_STAMPING = new MekanismJEIRecipeType<>(MoreMachineBlocks.CNC_STAMPER, StamperRecipe.class);
    public static final MekanismJEIRecipeType<ItemStackToItemStackRecipe> CNC_LATHING = new MekanismJEIRecipeType<>(MoreMachineBlocks.CNC_LATHE, ItemStackToItemStackRecipe.class);
    public static final MekanismJEIRecipeType<ItemStackToItemStackRecipe> CNC_ROLLING_MILL = new MekanismJEIRecipeType<>(MoreMachineBlocks.CNC_ROLLING_MILL, ItemStackToItemStackRecipe.class);

    public static final MekanismJEIRecipeType<ItemStackGasToItemStackRecipe> REPLICATOR = new MekanismJEIRecipeType<>(MoreMachineBlocks.REPLICATOR, ItemStackGasToItemStackRecipe.class);
    public static final MekanismJEIRecipeType<FluidStackGasToFluidStackRecipe> FLUID_REPLICATOR = new MekanismJEIRecipeType<>(MoreMachineBlocks.FLUID_REPLICATOR, FluidStackGasToFluidStackRecipe.class);
}
