package com.jerry.mekmm.api.recipes;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.common.Mekanism;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MoreMachineRecipeTypes {

    // 由于MekanismRecipeType的限制，这里只能用Mekanism.MODID，这非常狗屎！！！
    public static final ResourceLocation NAME_RECYCLING = ResourceLocation.fromNamespaceAndPath(Mekanism.MODID, "recycling");
    public static final ResourceLocation NAME_PLANTING = ResourceLocation.fromNamespaceAndPath(Mekanism.MODID, "planting");
    public static final ResourceLocation NAME_STAMPING = ResourceLocation.fromNamespaceAndPath(Mekanism.MODID, "stamping");
    public static final ResourceLocation NAME_LATHING = ResourceLocation.fromNamespaceAndPath(Mekanism.MODID, "lathing");
    public static final ResourceLocation NAME_ROLLING_MILL = ResourceLocation.fromNamespaceAndPath(Mekanism.MODID, "rolling_mill");
    public static final ResourceLocation NAME_PRESSING = ResourceLocation.fromNamespaceAndPath(Mekanism.MODID, "pressing");

    public static final DeferredHolder<RecipeType<?>, RecipeType<RecyclerRecipe>> TYPE_RECYCLER = DeferredHolder.create(Registries.RECIPE_TYPE, NAME_RECYCLING);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PlantingRecipe>> TYPE_PLANTING = DeferredHolder.create(Registries.RECIPE_TYPE, NAME_PLANTING);
    public static final DeferredHolder<RecipeType<?>, RecipeType<StamperRecipe>> TYPE_STAMPING = DeferredHolder.create(Registries.RECIPE_TYPE, NAME_STAMPING);
    public static final DeferredHolder<RecipeType<?>, RecipeType<ItemStackToItemStackRecipe>> TYPE_LATHING = DeferredHolder.create(Registries.RECIPE_TYPE, NAME_LATHING);
    public static final DeferredHolder<RecipeType<?>, RecipeType<ItemStackToItemStackRecipe>> TYPE_ROLLING_MILL = DeferredHolder.create(Registries.RECIPE_TYPE, NAME_ROLLING_MILL);
    public static final DeferredHolder<RecipeType<?>, RecipeType<TripleItemToItemRecipe>> TYPE_PRESSING = DeferredHolder.create(Registries.RECIPE_TYPE, NAME_PRESSING);
}
