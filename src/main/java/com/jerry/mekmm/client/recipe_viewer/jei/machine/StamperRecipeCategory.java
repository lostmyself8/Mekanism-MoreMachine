package com.jerry.mekmm.client.recipe_viewer.jei.machine;

import com.jerry.mekmm.api.recipes.StamperRecipe;

import mekanism.client.gui.element.GuiDownArrow;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.recipe_viewer.RecipeViewerUtils;
import mekanism.client.recipe_viewer.jei.HolderRecipeCategory;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.inventory.container.slot.SlotOverlay;

import net.minecraft.world.item.crafting.RecipeHolder;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.jetbrains.annotations.NotNull;

// 虽然可以直接使用融合机的RecipeCategory，但复制一份应该更好
public class StamperRecipeCategory extends HolderRecipeCategory<StamperRecipe> {

    private final GuiSlot input;
    private final GuiSlot extra;
    private final GuiSlot output;

    public StamperRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<StamperRecipe> recipeType) {
        super(helper, recipeType);
        addElement(new GuiDownArrow(this, 68, 38));
        input = addSlot(SlotType.INPUT, 64, 17);
        extra = addSlot(SlotType.EXTRA, 64, 53);
        output = addSlot(SlotType.OUTPUT, 116, 35);
        addSlot(SlotType.POWER, 39, 35).with(SlotOverlay.POWER);
        addElement(new GuiVerticalPowerBar(this, RecipeViewerUtils.FULL_BAR, 164, 15));
        addSimpleProgress(ProgressType.BAR, 86, 38);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, RecipeHolder<StamperRecipe> recipeHolder, @NotNull IFocusGroup focusGroup) {
        StamperRecipe recipe = recipeHolder.value();
        initItem(builder, RecipeIngredientRole.INPUT, input, recipe.getInput().getRepresentations());
        initItem(builder, RecipeIngredientRole.INPUT, extra, recipe.getMold().getRepresentations());
        initItem(builder, RecipeIngredientRole.OUTPUT, output, recipe.getOutputDefinition());
    }
}
