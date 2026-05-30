package com.jerry.mekmm.mixin.recipe;

import com.jerry.mekmm.api.recipes.*;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache.TripleItem;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.DoubleItem;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.ItemChemical;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleItem;
import mekanism.common.registration.impl.RecipeTypeRegistryObject;

import net.minecraft.resources.ResourceLocation;
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
                                                                                                                                                                                                                  ResourceLocation name,
                                                                                                                                                                                                                  Function<MekanismRecipeType<VANILLA_INPUT, RECIPE, INPUT_CACHE>, INPUT_CACHE> inputCacheCreator) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void mekmm$initRecipe(CallbackInfo ci) {
        MoreMachineRecipeType.RECYCLING = register(MoreMachineRecipeTypes.NAME_RECYCLING, recipeType -> new SingleItem<>(recipeType, RecyclerRecipe::getInput));

        MoreMachineRecipeType.PLANTING_STATION = register(MoreMachineRecipeTypes.NAME_PLANTING, recipeType -> new ItemChemical<>(recipeType, PlantingRecipe::getItemInput, PlantingRecipe::getChemicalInput));

        MoreMachineRecipeType.STAMPING = register(MoreMachineRecipeTypes.NAME_STAMPING, recipeType -> new DoubleItem<>(recipeType, StamperRecipe::getInput, StamperRecipe::getMold));

        MoreMachineRecipeType.LATHING = register(MoreMachineRecipeTypes.NAME_LATHING, recipeType -> new SingleItem<>(recipeType, ItemStackToItemStackRecipe::getInput));

        MoreMachineRecipeType.ROLLING_MILL = register(MoreMachineRecipeTypes.NAME_ROLLING_MILL, recipeType -> new SingleItem<>(recipeType, ItemStackToItemStackRecipe::getInput));

        MoreMachineRecipeType.PRESSING = register(MoreMachineRecipeTypes.NAME_PRESSING, recipeType -> new TripleItem<>(recipeType, TripleItemToItemRecipe::getFirstInput, TripleItemToItemRecipe::getSecondInput, TripleItemToItemRecipe::getThirdInput));
    }
}
