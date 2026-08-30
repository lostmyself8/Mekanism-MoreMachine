package com.jerry.mekmm.client.recipe_viewer.emi.recipe;

import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;
import com.jerry.mekmm.common.tile.machine.TileEntityPresser;

import mekanism.client.gui.element.GuiUpArrow;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.recipe_viewer.RecipeViewerUtils;
import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
import mekanism.client.recipe_viewer.emi.recipe.MekanismEmiHolderRecipe;
import mekanism.common.inventory.container.slot.SlotOverlay;

import net.minecraft.world.item.crafting.RecipeHolder;

import dev.emi.emi.api.widget.WidgetHolder;

public class PresserEmiRecipe extends MekanismEmiHolderRecipe<TripleItemToItemRecipe> {

    public PresserEmiRecipe(MekanismEmiRecipeCategory category, RecipeHolder<TripleItemToItemRecipe> recipeHolder) {
        super(category, recipeHolder);
        addInputDefinition(recipe.getFirstInput());
        addInputDefinition(recipe.getSecondInput());
        addInputDefinition(recipe.getThirdInput());
        addItemOutputDefinition(recipe.getOutputDefinition());
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        addElement(widgetHolder, new GuiUpArrow(this, 68, 38));
        addSlot(widgetHolder, SlotType.INPUT, 64, 16, input(0));
        addSlot(widgetHolder, SlotType.INPUT_2, 64, 35, input(1));
        addSlot(widgetHolder, SlotType.EXTRA, 64, 54, input(2));
        addSlot(widgetHolder, SlotType.OUTPUT, 116, 35, output(0)).recipeContext(this);
        addSlot(widgetHolder, SlotType.POWER, 41, 35).with(SlotOverlay.POWER);
        addElement(widgetHolder, new GuiVerticalPowerBar(this, RecipeViewerUtils.FULL_BAR, 164, 15));
        addSimpleProgress(widgetHolder, ProgressType.BAR, 86, 38, TileEntityPresser.BASE_TICKS_REQUIRED);
    }
}
