package com.jerry.mekmm.mixin.recipe;

import com.jerry.mekmm.api.recipes.MoreMachineRecipeTypes;
import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeInput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = MekanismRecipeType.class, remap = false)
public class MixinMekanismRecipeType {

    @Shadow
    private static <VANILLA_INPUT extends RecipeInput, RECIPE extends MekanismRecipe<VANILLA_INPUT>, INPUT_CACHE extends IInputRecipeCache> RecipeTypeRegistryObject<VANILLA_INPUT, RECIPE, INPUT_CACHE> register(
                                                                                                                                                                                                                  Identifier name,
                                                                                                                                                                                                                  Function<MekanismRecipeType<VANILLA_INPUT, RECIPE, INPUT_CACHE>, INPUT_CACHE> inputCacheCreator) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void mekmm$initRecipe(CallbackInfo ci) {
        MoreMachineRecipeType.RECYCLING = register(MoreMachineRecipeTypes.NAME_RECYCLING, recipeType -> new InputRecipeCache.SingleItem<>(recipeType, RecyclerRecipe::getInput));

        MoreMachineRecipeType.PLANTING_STATION = register(MoreMachineRecipeTypes.NAME_PLANTING, recipeType -> new InputRecipeCache.ItemChemical<>(recipeType, PlantingRecipe::getItemInput, PlantingRecipe::getChemicalInput));

        MoreMachineRecipeType.STAMPING = register(MoreMachineRecipeTypes.NAME_STAMPING, recipeType -> new InputRecipeCache.DoubleItem<>(recipeType, StamperRecipe::getInput, StamperRecipe::getMold));

        MoreMachineRecipeType.LATHING = register(MoreMachineRecipeTypes.NAME_LATHING, recipeType -> new InputRecipeCache.SingleItem<>(recipeType, ItemStackToItemStackRecipe::getInput));

        MoreMachineRecipeType.ROLLING_MILL = register(MoreMachineRecipeTypes.NAME_ROLLING_MILL, recipeType -> new InputRecipeCache.SingleItem<>(recipeType, ItemStackToItemStackRecipe::getInput));

        MoreMachineRecipeType.PRESSING = register(MoreMachineRecipeTypes.NAME_PRESSING, MoreMachineInputRecipeCache.Pressing::new);
    }
}
