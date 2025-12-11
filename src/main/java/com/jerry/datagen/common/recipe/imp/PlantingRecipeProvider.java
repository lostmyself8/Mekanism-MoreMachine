package com.jerry.datagen.common.recipe.imp;

import com.jerry.datagen.common.recipe.ISubRecipeProvider;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.datagen.recipe.builder.PlantingStationRecipeBuilder;
import com.jerry.mekmm.common.registries.MoreMachineChemicals;

import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PlantingRecipeProvider implements ISubRecipeProvider {

    @Override
    public void addRecipes(RecipeOutput consumer, HolderLookup.Provider registries) {
        String basePath = "planting/";
        // Sapling
        // Oak
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.OAK_SAPLING),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.OAK_LOG, 6),
                true).build(consumer, Mekmm.rl(basePath + "sapling/oak_sapling"));
        // Dark Oak
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.DARK_OAK_SAPLING),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.DARK_OAK_LOG, 6),
                true).build(consumer, Mekmm.rl(basePath + "sapling/dark_oak_sapling"));
        // Spruce
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.SPRUCE_SAPLING),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.SPRUCE_LOG, 6),
                true).build(consumer, Mekmm.rl(basePath + "sapling/spruce_sapling"));
        // Birch
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.BIRCH_SAPLING),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.BIRCH_LOG, 6),
                true).build(consumer, Mekmm.rl(basePath + "sapling/birch_sapling"));
        // Jungle
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.JUNGLE_SAPLING),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.JUNGLE_LOG, 6),
                true).build(consumer, Mekmm.rl(basePath + "sapling/jungle_sapling"));
        // Acacia
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.ACACIA_SAPLING),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.ACACIA_LOG, 6),
                true).build(consumer, Mekmm.rl(basePath + "sapling/acacia_sapling"));
        // Mangrove
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.MANGROVE_PROPAGULE),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.MANGROVE_LOG, 6),
                true).build(consumer, Mekmm.rl(basePath + "sapling/mangrove_propagule"));
        // Cherry
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.CHERRY_SAPLING),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.CHERRY_LOG, 6),
                true).build(consumer, Mekmm.rl(basePath + "sapling/cherry_sapling"));

        // Flower
        // 蒲公英
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.DANDELION),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.DANDELION, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/dandelion"));
        // 兰花
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.BLUE_ORCHID),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.BLUE_ORCHID, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/blue_orchid"));
        // 绒球葱
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.ALLIUM),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.ALLIUM, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/allium"));
        // 蓝花美耳草
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.AZURE_BLUET),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.AZURE_BLUET, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/azure_bluet"));
        // 红色郁金香
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.RED_TULIP),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.RED_TULIP, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/red_tulip"));
        // 橙色郁金香
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.ORANGE_TULIP),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.ORANGE_TULIP, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/orange_tulip"));
        // 白色郁金香
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.WHITE_TULIP),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.WHITE_TULIP, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/white_tulip"));
        // 滨菊
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.OXEYE_DAISY),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.OXEYE_DAISY, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/oxeye_daisy"));
        // 矢车菊
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.CORNFLOWER),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.CORNFLOWER, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/cornflower"));
        // 铃兰
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.LILY_OF_THE_VALLEY),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.LILY_OF_THE_VALLEY, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/lily_of_the_valley"));
        // 凋灵玫瑰
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.WITHER_ROSE),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.WITHER_ROSE, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/wither_rose"));
        // 火把花
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.TORCHFLOWER_SEEDS),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.TORCHFLOWER),
                true).build(consumer, Mekmm.rl(basePath + "flower/torchflower"));
        // 粉红色花簇
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.PINK_PETALS),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.PINK_PETALS, 3),
                true).build(consumer, Mekmm.rl(basePath + "flower/pink_petals"));
        // 高花丛
        // 向日葵
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.SUNFLOWER),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.SUNFLOWER, 2),
                true).build(consumer, Mekmm.rl(basePath + "flower/sunflower"));
        // 丁香
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.LILAC),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.LILAC, 2),
                true).build(consumer, Mekmm.rl(basePath + "flower/lilac"));
        // 玫瑰从
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.ROSE_BUSH),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.ROSE_BUSH, 2),
                true).build(consumer, Mekmm.rl(basePath + "flower/rose_bush"));
        // 牡丹
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.PEONY),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.PEONY, 2),
                true).build(consumer, Mekmm.rl(basePath + "flower/peony"));
        // 瓶子草
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.PITCHER_POD),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.PITCHER_PLANT),
                true).build(consumer, Mekmm.rl(basePath + "flower/pitcher_plant"));

        // Misc
        PlantingStationRecipeBuilder.planting(
                IngredientCreatorAccess.item().from(Items.MOSS_BLOCK),
                IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
                new ItemStack(Items.MOSS_BLOCK, 4),
                true).build(consumer, Mekmm.rl(basePath + "flower/moss_block"));
    }
}
