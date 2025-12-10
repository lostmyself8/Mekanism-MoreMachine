package com.jerry.mekmm.client.jei.machine;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.GasStack;
import mekanism.client.SpecialColors;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.client.gui.element.bar.GuiEmptyBar;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import mekanism.common.util.text.TextUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;

import java.util.ArrayList;
import java.util.List;

@NothingNullByDefault
public class PlantingRecipeCategory extends BaseRecipeCategory<PlantingRecipe> {

    private final GuiBar<?> chemicalInput;
    private final GuiSlot input;
    private final GuiSlot extra;
    private final GuiSlot output;

    public PlantingRecipeCategory(IGuiHelper helper, MekanismJEIRecipeType<PlantingRecipe> recipeType) {
        super(helper, recipeType, MoreMachineBlocks.PLANTING_STATION, 28, 16, 144, 54);
        input = addSlot(SlotType.INPUT, 56, 17);
        extra = addSlot(SlotType.EXTRA, 56, 53);
        addSlot(SlotType.POWER, 31, 35).with(SlotOverlay.POWER);
        output = addSlot(SlotType.OUTPUT_WIDE, 112, 31);
        addElement(new GuiVerticalPowerBar(this, FULL_BAR, 164, 15));
        chemicalInput = addElement(new GuiEmptyBar(this, 60, 36, 6, 12));
        addSimpleProgress(ProgressType.BAR, 78, 38);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PlantingRecipe recipe, IFocusGroup focuses) {
        initItem(builder, RecipeIngredientRole.INPUT, input, recipe.getItemInput().getRepresentations());
        List<ItemStack> gasItemProviders = new ArrayList<>();
        List<GasStack> scaledGases = new ArrayList<>();
        for (GasStack gas : recipe.getGasInput().getRepresentations()) {
            // 额外
            gasItemProviders.addAll(MekanismJEI.GAS_STACK_HELPER.getStacksFor(gas.getType(), true));
            // While we are already looping the gases ensure we scale it to get the average amount that will get used
            // over all
            scaledGases.add(new GasStack(gas, gas.getAmount() * TileEntityAdvancedElectricMachine.BASE_TICKS_REQUIRED));
        }
        initChemical(builder, MekanismJEI.TYPE_GAS, RecipeIngredientRole.INPUT, chemicalInput, scaledGases);

        initItem(builder, RecipeIngredientRole.OUTPUT, output.getX() + 32, output.getY() + 20, recipe.getMainOutputDefinition());
        initItem(builder, RecipeIngredientRole.OUTPUT, output.getX() + 48, output.getY() + 20, recipe.getSecondaryOutputDefinition());

        initItem(builder, RecipeIngredientRole.CATALYST, extra, gasItemProviders);
    }

    @Override
    public void draw(PlantingRecipe recipe, IRecipeSlotsView recipeSlotView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotView, guiGraphics, mouseX, mouseY);
        double secondaryChance = recipe.getSecondaryChance();
        if (secondaryChance > 0) {
            guiGraphics.drawString(getFont(), TextUtils.getPercent(secondaryChance), 104, 41, SpecialColors.TEXT_TITLE.argb(), false);
        }
    }
}
