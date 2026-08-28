package com.jerry.mekmm.client.recipe_viewer.jei.machine;

import com.jerry.mekmm.api.recipes.RecyclerRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.gui.element.GuiUpArrow;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.recipe_viewer.RecipeViewerUtils;
import mekanism.client.recipe_viewer.jei.HolderRecipeCategory;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.util.text.TextUtils;

import net.minecraft.world.item.crafting.RecipeHolder;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;

@NothingNullByDefault
public class RecyclerRecipeCategory extends HolderRecipeCategory<RecyclerRecipe> {

    private final GuiSlot input;
    private final GuiSlot output;

    public RecyclerRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<RecyclerRecipe> recipeType) {
        super(helper, recipeType);
        addElement(new GuiUpArrow(this, 68, 38));
        input = addSlot(SlotType.INPUT, 64, 17);
        output = addSlot(SlotType.OUTPUT, 116, 35);
        addSlot(SlotType.POWER, 64, 53).with(SlotOverlay.POWER);
        addElement(new GuiVerticalPowerBar(this, RecipeViewerUtils.FULL_BAR, 164, 16));
        addSimpleProgress(ProgressType.BAR, 86, 38);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<RecyclerRecipe> recipeHolder, IFocusGroup focuses) {
        super.createRecipeExtras(builder, recipeHolder, focuses);
        double secondaryChance = recipeHolder.value().getOutputChance();
        if (secondaryChance > 0) {
            builder.addText(TextUtils.getPercent(secondaryChance), output.getWidth() + 6, font().lineHeight)
                    .setPosition(getGuiLeft() + output.getRelativeX() - 2, getGuiTop() + output.getRelativeBottom() + 1)
                    .setTextAlignment(HorizontalAlignment.CENTER)
                    .setColor(titleTextColor());
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<RecyclerRecipe> recipeHolder, IFocusGroup focuses) {
        RecyclerRecipe recipe = recipeHolder.value();
        initItem(builder, RecipeIngredientRole.INPUT, input, recipe.getInput().getRepresentations());
        initItem(builder, RecipeIngredientRole.OUTPUT, output, recipe.getChanceOutputDefinition());
    }
}
