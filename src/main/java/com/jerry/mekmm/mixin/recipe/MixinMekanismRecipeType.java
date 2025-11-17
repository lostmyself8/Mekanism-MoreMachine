package com.jerry.mekmm.mixin.recipe;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;

import net.minecraft.world.item.crafting.RecipeType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = MekanismRecipeType.class, remap = false)
public abstract class MixinMekanismRecipeType<RECIPE extends MekanismRecipe, INPUT_CACHE extends IInputRecipeCache> implements RecipeType<RECIPE>,
                                             IMekanismRecipeTypeProvider<RECIPE, INPUT_CACHE> {

    @Shadow
    private static <RECIPE extends MekanismRecipe, INPUT_CACHE extends IInputRecipeCache> RecipeTypeRegistryObject<RECIPE, INPUT_CACHE> register(String name,
                                                                                                                                                 Function<MekanismRecipeType<RECIPE, INPUT_CACHE>, INPUT_CACHE> inputCacheCreator) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void mekmm$initRecipe(CallbackInfo ci) {
        MoreMachineRecipeType.RECYCLING = register("recycling", recipeType -> new InputRecipeCache.SingleItem<>(recipeType, RecyclerRecipe::getInput));

        MoreMachineRecipeType.PLANTING = register("planting", recipeType -> new InputRecipeCache.ItemChemical<>(recipeType, PlantingRecipe::getItemInput, PlantingRecipe::getGasInput));

        MoreMachineRecipeType.STAMPING = register("stamping", recipeType -> new InputRecipeCache.DoubleItem<>(recipeType, StamperRecipe::getInput, StamperRecipe::getMold));

        MoreMachineRecipeType.LATHING = register("lathing", recipeType -> new InputRecipeCache.SingleItem<>(recipeType, ItemStackToItemStackRecipe::getInput));

        MoreMachineRecipeType.ROLLING_MILL = register("rolling_mill", recipeType -> new InputRecipeCache.SingleItem<>(recipeType, ItemStackToItemStackRecipe::getInput));
    }
}
