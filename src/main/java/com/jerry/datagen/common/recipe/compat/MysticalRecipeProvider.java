// package com.jerry.datagen.common.recipe.compat;
//
// import com.jerry.mekmm.Mekmm;
// import com.jerry.mekmm.api.datagen.recipe.builder.PlantingStationRecipeBuilder;
// import com.jerry.mekmm.common.registries.MoreMachineChemicals;
//
// import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
//
// import net.minecraft.core.HolderLookup;
// import net.minecraft.data.recipes.RecipeOutput;
// import net.minecraft.world.item.ItemStack;
//
// import com.blakebr0.mysticalagriculture.api.crop.Crop;
// import com.blakebr0.mysticalagriculture.registry.CropRegistry;
//
// import javax.annotation.ParametersAreNonnullByDefault;
//
// @ParametersAreNonnullByDefault
// public class MysticalRecipeProvider extends CompatRecipeProvider {
//
// public MysticalRecipeProvider(String modId) {
// super(modId);
// }
//
// @Override
// protected void registerRecipes(RecipeOutput consumer, String basePath, HolderLookup.Provider registries) {
// for (Crop crop : CropRegistry.getInstance().getCrops()) {
// PlantingStationRecipeBuilder.planting(
// IngredientCreatorAccess.item().from(crop.getSeedsItem()),
// IngredientCreatorAccess.chemicalStack().from(MoreMachineChemicals.NUTRIENT_SOLUTION.asStack(1)),
// new ItemStack(crop.getEssenceItem(), switch (crop.getTier().getValue()) {
// case 1 -> 5;
// case 2 -> 4;
// case 3 -> 3;
// case 4 -> 2;
// // 5级或者更高级，0级或者更低级默认为1
// default -> 1;
// }),
// true).addCondition(modLoaded).build(consumer, Mekmm.rl(basePath + crop.getName()));
// }
// }
// }
