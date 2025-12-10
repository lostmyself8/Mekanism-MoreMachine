package com.jerry.datagen.common.recipe.compat;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.datagen.recipe.builder.PlantingStationRecipeBuilder;
import com.jerry.mekmm.api.datagen.recipe.builder.StamperRecipeBuilder;
import com.jerry.mekmm.common.registries.MoreMachineGas;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.common.register.IEItems;

import java.util.function.Consumer;

@ParametersAreNotNullByDefault
public class IERecipeProvider extends CompatRecipeProvider {

    public IERecipeProvider(String modId) {
        super(modId);
    }

    @Override
    protected void registerRecipes(Consumer<FinishedRecipe> consumer, String basePath) {
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(IEItems.Misc.HEMP_SEEDS),
                IngredientCreatorAccess.gas().from(MoreMachineGas.NUTRIENT_SOLUTION, 1),
                new ItemStack(IEItems.Ingredients.HEMP_FIBER)).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + "planting/hemp_seed"));

        StamperRecipeBuilder.stamping(
                IngredientCreatorAccess.item().from(Tags.Items.INGOTS_GOLD),
                IngredientCreatorAccess.item().from(IEItems.Molds.MOLD_PLATE),
                new ItemStack(IEItems.Metals.PLATES.get(EnumMetals.GOLD))).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + "stamper/gold_plate"));

        StamperRecipeBuilder.stamping(
                IngredientCreatorAccess.item().from(Tags.Items.INGOTS_COPPER),
                IngredientCreatorAccess.item().from(IEItems.Molds.MOLD_PLATE),
                new ItemStack(IEItems.Metals.PLATES.get(EnumMetals.COPPER))).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + "stamper/copper_plate"));

        StamperRecipeBuilder.stamping(
                IngredientCreatorAccess.item().from(Tags.Items.INGOTS_IRON),
                IngredientCreatorAccess.item().from(IEItems.Molds.MOLD_PLATE),
                new ItemStack(IEItems.Metals.PLATES.get(EnumMetals.IRON))).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + "stamper/iron_plate"));

        StamperRecipeBuilder.stamping(
                IngredientCreatorAccess.item().from(Tags.Items.INGOTS_IRON),
                IngredientCreatorAccess.item().from(IEItems.Molds.MOLD_ROD),
                new ItemStack(IEItems.Ingredients.STICK_IRON, 2)).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + "stamper/iron_stick"));

        // 1.20.1 IE还没有加入下界合金棒
        // StamperRecipeBuilder.stamping(
        // IngredientCreatorAccess.item().from(Tags.Items.INGOTS_NETHERITE),
        // IngredientCreatorAccess.item().from(IEItems.Molds.MOLD_ROD),
        // new ItemStack(IEItems.Ingredients.STICK_NETHERITE, 2)
        // ).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + "stamper/netherite_stick"));

        StamperRecipeBuilder.stamping(
                IngredientCreatorAccess.item().from(Tags.Items.INGOTS_COPPER),
                IngredientCreatorAccess.item().from(IEItems.Molds.MOLD_BULLET_CASING),
                new ItemStack(IEItems.Ingredients.EMPTY_CASING, 2)).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + "stamper/empty_casing"));

        StamperRecipeBuilder.stamping(
                IngredientCreatorAccess.item().from(Tags.Items.INGOTS_COPPER),
                IngredientCreatorAccess.item().from(IEItems.Molds.MOLD_WIRE),
                new ItemStack(IEItems.Ingredients.WIRE_COPPER, 2)).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + "stamper/copper_wire"));

        StamperRecipeBuilder.stamping(
                IngredientCreatorAccess.item().from(Blocks.MELON),
                IngredientCreatorAccess.item().from(IEItems.Molds.MOLD_UNPACKING),
                new ItemStack(Items.MELON_SLICE, 9)).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + "stamper/melon"));
    }
}
