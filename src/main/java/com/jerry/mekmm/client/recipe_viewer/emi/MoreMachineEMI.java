// package com.jerry.mekmm.client.recipe_viewer.emi;
//
// import com.jerry.mekmm.Mekmm;
// import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
// import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerUtils;
// import com.jerry.mekmm.client.recipe_viewer.emi.recipe.*;
// import com.jerry.mekmm.common.MoreMachineLang;
// import com.jerry.mekmm.common.block.attribute.MoreMachineAttributeFactoryType;
// import com.jerry.mekmm.common.registries.MoreMachineBlocks;
// import com.jerry.mekmm.common.registries.MoreMachineChemicals;
// import com.jerry.mekmm.common.registries.MoreMachineItems;
// import com.jerry.mekmm.common.util.MoreMachineUtils;
//
// import mekanism.api.recipes.MekanismRecipe;
// import mekanism.client.recipe_viewer.emi.ChemicalEmiStack;
// import mekanism.client.recipe_viewer.emi.MekanismEmi;
// import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
// import mekanism.client.recipe_viewer.emi.recipe.ItemStackToItemStackEmiRecipe;
// import mekanism.client.recipe_viewer.emi.recipe.MekanismEmiRecipe;
// import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
// import mekanism.common.block.attribute.Attribute;
// import mekanism.common.recipe.IMekanismRecipeTypeProvider;
// import mekanism.common.tier.FactoryTier;
//
// import net.minecraft.resources.Identifier;
// import net.minecraft.world.item.BlockItem;
// import net.minecraft.world.item.Item;
// import net.minecraft.world.item.crafting.RecipeHolder;
// import net.minecraft.world.level.ItemLike;
// import net.neoforged.neoforge.fluids.FluidType;
//
// import dev.emi.emi.api.EmiEntrypoint;
// import dev.emi.emi.api.EmiPlugin;
// import dev.emi.emi.api.EmiRegistry;
// import dev.emi.emi.api.recipe.EmiInfoRecipe;
// import dev.emi.emi.api.recipe.EmiRecipeCategory;
// import dev.emi.emi.api.stack.EmiStack;
//
// import java.util.List;
// import java.util.Map;
// import java.util.function.BiFunction;
//
// import static mekanism.client.recipe_viewer.emi.MekanismEmi.registerItemSubtypes;
//
// @EmiEntrypoint
// public class MoreMachineEMI implements EmiPlugin {
//
// @Override
// public void register(EmiRegistry registry) {
// addCategories(registry);
//
// registerItemSubtypes(registry, MoreMachineItems.MM_ITEMS.getEntries());
// registerItemSubtypes(registry, MoreMachineBlocks.MM_BLOCKS.getSecondaryEntries());
// }
//
// private void addCategories(EmiRegistry registry) {
// addCategoryAndRecipes(registry, MMRecipeViewerRecipeType.RECYCLER, RecyclerEmiRecipe::new);
// addCategoryAndRecipes(registry, MMRecipeViewerRecipeType.PLANTING_STATION, PlantingEmiRecipe::new);
//
// addCategoryAndRecipes(registry, MMRecipeViewerRecipeType.REPLICATOR, ReplicatorEmiRecipe::new,
// MMRecipeViewerUtils.getItemReplicatorRecipes());
// addCategoryAndRecipes(registry, MMRecipeViewerRecipeType.FLUID_REPLICATOR, FluidReplicatorEmiRecipe::new,
// MMRecipeViewerUtils.getFluidReplicatorRecipes());
// addCategoryAndRecipes(registry, MMRecipeViewerRecipeType.CHEMICAL_REPLICATOR, ChemicalReplicatorEmiRecipe::new,
// MMRecipeViewerUtils.getChemicalReplicatorRecipes());
//
// addCategoryAndRecipes(registry, MMRecipeViewerRecipeType.STAMPING, StamperEmiRecipe::new);
// addCategoryAndRecipes(registry, MMRecipeViewerRecipeType.LATHE, ItemStackToItemStackEmiRecipe::new);
// addCategoryAndRecipes(registry, MMRecipeViewerRecipeType.ROLLING_MILL, ItemStackToItemStackEmiRecipe::new);
//
// registry.addRecipe(new EmiInfoRecipe(List.of(new
// ChemicalEmiStack(MoreMachineChemicals.UNSTABLE_DIMENSIONAL_GAS.asStack(FluidType.BUCKET_VOLUME))), List.of(
// MoreMachineLang.RECIPE_VIEWER_INFO_UNSTABLE_DIMENSIONAL_GAS.translate()),
// Mekmm.rl("info/unstable_dimensional_gas")));
// }
//
// public static <RECIPE extends MekanismRecipe<?>, TYPE extends IRecipeViewerRecipeType<RECIPE> &
// IMekanismRecipeTypeProvider<?, RECIPE, ?>> void addCategoryAndRecipes(
// EmiRegistry registry, TYPE recipeType, BiFunction<MekanismEmiRecipeCategory, RecipeHolder<RECIPE>,
// MekanismEmiRecipe<RECIPE>> recipeCreator) {
// MekanismEmiRecipeCategory category = addCategory(registry, recipeType);
// for (RecipeHolder<RECIPE> recipe : recipeType.getRecipes(registry.getRecipeManager())) {
// registry.addRecipe(recipeCreator.apply(category, recipe));
// }
// }
//
// public static <RECIPE> void addCategoryAndRecipes(EmiRegistry registry, IRecipeViewerRecipeType<RECIPE> recipeType,
// MekanismEmi.BasicRecipeCreator<RECIPE> recipeCreator,
// Map<Identifier, RECIPE> recipes) {
// MekanismEmiRecipeCategory category = addCategory(registry, recipeType);
// for (Map.Entry<Identifier, RECIPE> entry : recipes.entrySet()) {
// registry.addRecipe(recipeCreator.create(category, entry.getKey(), entry.getValue()));
// }
// }
//
// private static MekanismEmiRecipeCategory addCategory(EmiRegistry registry, IRecipeViewerRecipeType<?> recipeType) {
// MekanismEmiRecipeCategory category = MekanismEmiRecipeCategory.create(recipeType);
// registry.addCategory(category);
// addWorkstations(registry, category, recipeType.workstations());
// return category;
// }
//
// private static void addWorkstations(EmiRegistry registry, EmiRecipeCategory category, List<ItemLike> workstations) {
// for (ItemLike workstation : workstations) {
// Item item = workstation.asItem();
// registry.addWorkstation(category, EmiStack.of(item));
// if (item instanceof BlockItem blockItem) {
// MoreMachineAttributeFactoryType factoryType = Attribute.get(blockItem.getBlock(),
// MoreMachineAttributeFactoryType.class);
// if (factoryType != null) {
// for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
// registry.addWorkstation(category, EmiStack.of(MoreMachineBlocks.getMoreMachineFactory(tier,
// factoryType.getMoreMachineFactoryType())));
// }
// }
// }
// }
// }
// }
