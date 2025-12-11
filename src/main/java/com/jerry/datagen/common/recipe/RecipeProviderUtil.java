package com.jerry.datagen.common.recipe;

import com.jerry.mekmm.api.datagen.recipe.builder.PlantingStationRecipeBuilder;
import com.jerry.mekmm.common.registries.MoreMachineChemicals;

import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.Mekanism;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;

import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.blakebr0.mysticalagriculture.registry.CropRegistry;
import org.jetbrains.annotations.Nullable;

public class RecipeProviderUtil {

    private RecipeProviderUtil() {}

    public static void addPlantingStationTypeRecipes(RecipeOutput consumer, String basePath, ItemLike input, ItemLike main, ItemLike second, ICondition condition) {
        for (Crop crop : CropRegistry.getInstance().getCrops()) {
            build(consumer, PlantingStationRecipeBuilder.planting(
                    IngredientCreatorAccess.item().from(crop.getSeedsItem()),
                    IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.UU_MATTER.asStack(1)),
                    new ItemStack(crop.getEssenceItem()),
                    true), basePath + "compat/myth" + crop.getName(), condition);
        }
    }

    private static void build(RecipeOutput consumer, MekanismRecipeBuilder<?> builder, String path, @Nullable ICondition condition) {
        if (condition != null) {
            // If there is a condition, add it to the recipe builder
            builder.addCondition(condition);
        }
        builder.build(consumer, Mekanism.rl(path));
    }
}
