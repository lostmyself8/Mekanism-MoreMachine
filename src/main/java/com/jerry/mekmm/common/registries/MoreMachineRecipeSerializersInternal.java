package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.MoreMachineRecipeSerializers;
import com.jerry.mekmm.api.recipes.basic.*;
import com.jerry.mekmm.common.recipe.serializer.MoreMachineRecipeSerializer;
import com.jerry.mekmm.common.recipe.serializer.PlantingRecipeSerializer;
import com.jerry.mekmm.common.recipe.serializer.RecyclerRecipeSerializer;

import mekanism.common.recipe.serializer.MekanismRecipeSerializer;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MoreMachineRecipeSerializersInternal {

    private MoreMachineRecipeSerializersInternal() {}

    public static final DeferredRegister<RecipeSerializer<?>> MM_RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Mekmm.MOD_ID);

    static {
        MoreMachineRecipeSerializers.RECYCLER = MM_RECIPE_SERIALIZERS.register("recycler", () -> RecyclerRecipeSerializer.create(BasicRecyclerRecipe::new));
        MoreMachineRecipeSerializers.PLANTING = MM_RECIPE_SERIALIZERS.register("planting", () -> PlantingRecipeSerializer.create(BasicPlantingRecipe::new));
        MoreMachineRecipeSerializers.STAMPING = MM_RECIPE_SERIALIZERS.register("stamper", () -> MoreMachineRecipeSerializer.stamping(BasicStamperRecipe::new));
        MoreMachineRecipeSerializers.LATHING = MM_RECIPE_SERIALIZERS.register("lathe", () -> MekanismRecipeSerializer.itemToItem(BasicLatheRecipe::new));
        MoreMachineRecipeSerializers.ROLLING_MILL = MM_RECIPE_SERIALIZERS.register("rolling_mill", () -> MekanismRecipeSerializer.itemToItem(BasicRollingMillRecipe::new));
        MoreMachineRecipeSerializers.PRESSING = MM_RECIPE_SERIALIZERS.register("pressing", () -> MoreMachineRecipeSerializer.pressing(BasicPresserRecipe::new));
    }
}
