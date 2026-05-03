// package com.jerry.mekmm.client.recipe_viewer.emi.recipe;
//
// import com.jerry.mekmm.api.recipes.RecyclerRecipe;
// import com.jerry.mekmm.common.tile.machine.TileEntityRecycler;
//
// import mekanism.client.gui.element.GuiUpArrow;
// import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
// import mekanism.client.gui.element.progress.ProgressType;
// import mekanism.client.gui.element.slot.SlotType;
// import mekanism.client.recipe_viewer.RecipeViewerUtils;
// import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
// import mekanism.client.recipe_viewer.emi.recipe.MekanismEmiHolderRecipe;
// import mekanism.common.inventory.container.slot.SlotOverlay;
//
// import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.crafting.RecipeHolder;
//
// import dev.emi.emi.api.stack.EmiStack;
// import dev.emi.emi.api.widget.WidgetHolder;
//
// import java.util.ArrayList;
// import java.util.List;
//
// public class RecyclerEmiRecipe extends MekanismEmiHolderRecipe<RecyclerRecipe> {
//
// public RecyclerEmiRecipe(MekanismEmiRecipeCategory category, RecipeHolder<RecyclerRecipe> recipeHolder) {
// super(category, recipeHolder);
// addInputDefinition(recipe.getInput());
// List<ItemStack> chanceOutputDefinition = recipe.getChanceOutputDefinition();
// List<EmiStack> list = new ArrayList<>(chanceOutputDefinition.size());
// for (ItemStack itemStack : chanceOutputDefinition) {
// list.add(EmiStack.of(itemStack).setChance((float) recipe.getOutputChance()));
// }
// addOutputDefinition(list);
// }
//
// @Override
// public void addWidgets(WidgetHolder widgets) {
// addElement(widgets, new GuiUpArrow(this, 68, 38));
// addSlot(widgets, SlotType.INPUT, 64, 17, input(0));
// addSlot(widgets, SlotType.OUTPUT, 116, 35, output(0)).recipeContext(this);
// addSlot(widgets, SlotType.POWER, 64, 53).with(SlotOverlay.POWER);
// addElement(widgets, new GuiVerticalPowerBar(this, RecipeViewerUtils.FULL_BAR, 164, 16));
// addSimpleProgress(widgets, ProgressType.BAR, 86, 38, TileEntityRecycler.BASE_TICKS_REQUIRED);
// }
// }
