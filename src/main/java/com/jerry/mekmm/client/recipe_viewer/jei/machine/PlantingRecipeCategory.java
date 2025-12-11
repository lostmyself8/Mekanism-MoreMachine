package com.jerry.mekmm.client.recipe_viewer.jei.machine;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.common.tile.machine.TileEntityPlantingStation;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.client.gui.element.bar.GuiEmptyBar;
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

import java.util.List;

@NothingNullByDefault
public class PlantingRecipeCategory extends HolderRecipeCategory<PlantingRecipe> {

    private final GuiBar<?> chemicalInput;
    private final GuiSlot input;
    private final GuiSlot extra;
    private final GuiSlot output;

    public PlantingRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<PlantingRecipe> recipeType) {
        super(helper, recipeType);
        input = addSlot(SlotType.INPUT, 56, 17);
        extra = addSlot(SlotType.EXTRA, 56, 53);
        addSlot(SlotType.POWER, 31, 35).with(SlotOverlay.POWER);
        output = addSlot(SlotType.OUTPUT_WIDE, 112, 31);
        addElement(new GuiVerticalPowerBar(this, RecipeViewerUtils.FULL_BAR, 164, 15));
        chemicalInput = addElement(new GuiEmptyBar(this, 60, 36, 6, 12));
        addSimpleProgress(ProgressType.BAR, 78, 38);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<PlantingRecipe> recipeHolder, IFocusGroup focuses) {
        super.createRecipeExtras(builder, recipeHolder, focuses);
        double secondaryChance = recipeHolder.value().getSecondaryChance();
        if (secondaryChance > 0) {
            builder.addText(TextUtils.getPercent(secondaryChance), output.getWidth() - 2, font().lineHeight)
                    // Perform the same translations as super does
                    .setPosition(getGuiLeft() + output.getRelativeX() + 1, getGuiTop() + output.getRelativeBottom() + 1)
                    .setTextAlignment(HorizontalAlignment.RIGHT)
                    .setColor(titleTextColor());
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<PlantingRecipe> recipeHolder, IFocusGroup focusGroup) {
        PlantingRecipe recipe = recipeHolder.value();
        initItem(builder, RecipeIngredientRole.INPUT, input, recipe.getItemInput().getRepresentations());
        List<ChemicalStack> scaledChemicals = recipe.getChemicalInput().getRepresentations();
        if (recipe.perTickUsage()) {
            scaledChemicals = scaledChemicals.stream()
                    .map(chemical -> chemical.copyWithAmount(chemical.getAmount() * TileEntityPlantingStation.BASE_TICKS_REQUIRED))
                    .toList();
        }
        initChemical(builder, RecipeIngredientRole.INPUT, chemicalInput, scaledChemicals);
        initItem(builder, RecipeIngredientRole.OUTPUT, output.getX() + 4, output.getY() + 4, recipe.getMainOutputDefinition());
        initItem(builder, RecipeIngredientRole.OUTPUT, output.getX() + 20, output.getY() + 4, recipe.getSecondaryOutputDefinition());
        initItem(builder, RecipeIngredientRole.CATALYST, extra, RecipeViewerUtils.getStacksFor(recipe.getChemicalInput(), true));
    }
}
