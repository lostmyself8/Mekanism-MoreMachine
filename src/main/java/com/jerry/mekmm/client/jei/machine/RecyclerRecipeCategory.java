package com.jerry.mekmm.client.jei.machine;

import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.SpecialColors;
import mekanism.client.gui.element.GuiUpArrow;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.util.text.TextUtils;

import net.minecraft.client.gui.GuiGraphics;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;

@NothingNullByDefault
public class RecyclerRecipeCategory extends BaseRecipeCategory<RecyclerRecipe> {

    private final GuiSlot input;
    private final GuiSlot output;

    public RecyclerRecipeCategory(IGuiHelper helper, MekanismJEIRecipeType<RecyclerRecipe> recipeType) {
        super(helper, recipeType, MoreMachineBlocks.RECYCLER, 28, 16, 144, 54);
        addElement(new GuiUpArrow(this, 68, 38));
        input = addSlot(SlotType.INPUT, 64, 17);
        output = addSlot(SlotType.OUTPUT, 116, 35);
        addSlot(SlotType.POWER, 64, 53).with(SlotOverlay.POWER);
        addElement(new GuiVerticalPowerBar(this, FULL_BAR, 164, 16));
        addSimpleProgress(ProgressType.BAR, 86, 38);
    }

    @Override
    public void draw(RecyclerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        double chance = recipe.getOutputChance();
        if (chance > 0) {
            guiGraphics.drawString(getFont(), TextUtils.getPercent(chance), 88, 37, SpecialColors.TEXT_TITLE.argb(), false);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecyclerRecipe recipe, IFocusGroup focuses) {
        initItem(builder, RecipeIngredientRole.INPUT, input, recipe.getInput().getRepresentations());
        initItem(builder, RecipeIngredientRole.OUTPUT, output, recipe.getChanceOutputDefinition());
    }
}
