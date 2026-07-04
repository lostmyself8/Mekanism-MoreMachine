package com.jerry.mekmm.client.recipe_viewer.jei.machine;

import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;

import mekanism.client.gui.element.GuiUpArrow;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.recipe_viewer.RecipeViewerUtils;
import mekanism.client.recipe_viewer.jei.HolderRecipeCategory;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.inventory.container.slot.SlotOverlay;

import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.RecipeHolder;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.jetbrains.annotations.NotNull;

public class PresserRecipeCategory extends HolderRecipeCategory<TripleItemToItemRecipe> {

    private final GuiSlot primaryInput;
    private final GuiSlot secondaryInput;
    private final GuiSlot tertiaryInput;
    private final GuiSlot output;

    public PresserRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<TripleItemToItemRecipe> recipeType) {
        super(helper, recipeType);
        addElement(new GuiUpArrow(this, 68, 38));
        primaryInput = addSlot(SlotType.INPUT, 64, 16);
        secondaryInput = addSlot(SlotType.INPUT, 64, 35);
        tertiaryInput = addSlot(SlotType.EXTRA, 64, 54);
        output = addSlot(SlotType.OUTPUT, 116, 35);
        addSlot(SlotType.POWER, 41, 35).with(SlotOverlay.POWER);
        addElement(new GuiVerticalPowerBar(this, RecipeViewerUtils.FULL_BAR, 164, 15));
        addSimpleProgress(ProgressType.BAR, 86, 38);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, RecipeHolder<@NotNull TripleItemToItemRecipe> recipeHolder, @NotNull IFocusGroup focusGroup) {
        TripleItemToItemRecipe recipe = recipeHolder.value();
        ContextMap slotDisplayContext = getSlotDisplayContext();
        initItem(builder, RecipeIngredientRole.INPUT, primaryInput, recipe.getFirstInput().getRepresentations(slotDisplayContext));
        initItem(builder, RecipeIngredientRole.INPUT, secondaryInput, recipe.getSecondInput().getRepresentations(slotDisplayContext));
        initItem(builder, RecipeIngredientRole.INPUT, tertiaryInput, recipe.getThirdInput().getRepresentations(slotDisplayContext));
        initItem(builder, RecipeIngredientRole.OUTPUT, output, recipe.getOutputDefinition());
    }
}
