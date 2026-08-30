package com.jerry.mekmm.client.recipe_viewer.emi.recipe;

import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.common.tile.machine.TileEntityStamper;

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

public class StamperEmiRecipe extends MekanismEmiHolderRecipe<StamperRecipe> {

    public StamperEmiRecipe(MekanismEmiRecipeCategory category, RecipeHolder<StamperRecipe> recipeHolder) {
        super(category, recipeHolder);
        addInputDefinition(recipe.getInput());
        addInputDefinition(recipe.getMold());
        addItemOutputDefinition(recipe.getOutputDefinition());
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        addElement(widgetHolder, new GuiUpArrow(this, 68, 38));
        addSlot(widgetHolder, SlotType.INPUT, 64, 17, input(0));
        addSlot(widgetHolder, SlotType.EXTRA, 64, 53, input(1));
        addSlot(widgetHolder, SlotType.OUTPUT, 116, 35, output(0)).recipeContext(this);
        addSlot(widgetHolder, SlotType.POWER, 39, 35).with(SlotOverlay.POWER);
        addElement(widgetHolder, new GuiVerticalPowerBar(this, RecipeViewerUtils.FULL_BAR, 164, 15));
        addSimpleProgress(widgetHolder, ProgressType.BAR, 86, 38, TileEntityStamper.BASE_TICKS_REQUIRED);
    }
}
