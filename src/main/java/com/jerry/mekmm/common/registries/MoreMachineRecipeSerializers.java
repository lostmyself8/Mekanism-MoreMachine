package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.common.recipe.impl.*;
import com.jerry.mekmm.common.recipe.serializer.PlantingRecipeSerializer;
import com.jerry.mekmm.common.recipe.serializer.RecyclerRecipeSerializer;
import com.jerry.mekmm.common.recipe.serializer.StamperRecipeSerializer;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.common.recipe.serializer.ItemStackToItemStackRecipeSerializer;
import mekanism.common.registration.impl.RecipeSerializerDeferredRegister;
import mekanism.common.registration.impl.RecipeSerializerRegistryObject;

public class MoreMachineRecipeSerializers {

    private MoreMachineRecipeSerializers() {}

    public static final RecipeSerializerDeferredRegister MM_RECIPE_SERIALIZERS = new RecipeSerializerDeferredRegister(Mekmm.MOD_ID);

    public static final RecipeSerializerRegistryObject<RecyclerRecipe> RECYCLER = MM_RECIPE_SERIALIZERS.register("recycler", () -> new RecyclerRecipeSerializer<>(RecyclerIRecipe::new));
    public static final RecipeSerializerRegistryObject<PlantingRecipe> PLANTING = MM_RECIPE_SERIALIZERS.register("planting", () -> new PlantingRecipeSerializer<>(PlantingIRecipe::new));
    public static final RecipeSerializerRegistryObject<StamperRecipe> STAMPING = MM_RECIPE_SERIALIZERS.register("stamper", () -> new StamperRecipeSerializer<>(StamperIRecipe::new));
    public static final RecipeSerializerRegistryObject<ItemStackToItemStackRecipe> LATHING = MM_RECIPE_SERIALIZERS.register("lathe", () -> new ItemStackToItemStackRecipeSerializer<>(LatheIRecipe::new));
    public static final RecipeSerializerRegistryObject<ItemStackToItemStackRecipe> ROLLING_MILL = MM_RECIPE_SERIALIZERS.register("rolling_mill", () -> new ItemStackToItemStackRecipeSerializer<>(RollingMillIRecipe::new));
}
