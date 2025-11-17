package com.jerry.mekmm.client.jei.machine;

import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.client.gui.element.GuiDownArrow;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.common.inventory.container.slot.SlotOverlay;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.jetbrains.annotations.NotNull;

public class StamperRecipeCategory extends BaseRecipeCategory<StamperRecipe> {

    private final GuiSlot input;
    private final GuiSlot extra;
    private final GuiSlot output;

    public StamperRecipeCategory(IGuiHelper helper, MekanismJEIRecipeType<StamperRecipe> recipeType) {
        super(helper, recipeType, MoreMachineBlocks.CNC_STAMPER, 28, 16, 144, 54);
        addElement(new GuiDownArrow(this, 68, 38));
        input = addSlot(SlotType.INPUT, 64, 17);
        extra = addSlot(SlotType.EXTRA, 64, 53);
        output = addSlot(SlotType.OUTPUT, 116, 35);
        addSlot(SlotType.POWER, 39, 35).with(SlotOverlay.POWER);
        addElement(new GuiVerticalPowerBar(this, FULL_BAR, 164, 15));
        addSimpleProgress(ProgressType.BAR, 86, 38);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, StamperRecipe recipe, @NotNull IFocusGroup focusGroup) {
        initItem(builder, RecipeIngredientRole.INPUT, input, recipe.getInput().getRepresentations());
        initItem(builder, RecipeIngredientRole.INPUT, extra, recipe.getMold().getRepresentations());
        initItem(builder, RecipeIngredientRole.OUTPUT, output, recipe.getOutputDefinition());
    }
}
