package com.jerry.mekmm.client.jei.machine;

import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiEnergyGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.tile.component.config.DataType;

import net.minecraft.world.item.ItemStack;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;

import java.util.ArrayList;
import java.util.List;

@NothingNullByDefault
public class ReplicatorRecipeCategory extends BaseRecipeCategory<ItemStackGasToItemStackRecipe> {

    private final GuiGauge<?> inputGauge;
    private final GuiSlot outputSlot;
    private final GuiSlot inputSlot;
    private final GuiSlot extra;

    public ReplicatorRecipeCategory(IGuiHelper helper, MekanismJEIRecipeType<ItemStackGasToItemStackRecipe> recipeType) {
        super(helper, recipeType, MoreMachineBlocks.REPLICATOR, 3, 3, 170, 79);
        GaugeType type1 = GaugeType.STANDARD.with(DataType.INPUT);
        inputGauge = addElement(GuiGasGauge.getDummy(type1, this, 7, 4));
        inputSlot = addSlot(SlotType.INPUT, 29, 32);
        outputSlot = addSlot(SlotType.OUTPUT, 131, 32);
        extra = addSlot(SlotType.EXTRA, 8, 65).with(SlotOverlay.MINUS);
        addSlot(SlotType.POWER, 152, 65).with(SlotOverlay.POWER);
        addSimpleProgress(ProgressType.LARGE_RIGHT, 64, 36);
        addElement(new GuiEnergyGauge(new GuiEnergyGauge.IEnergyInfoHandler() {

            @Override
            public FloatingLong getEnergy() {
                return FloatingLong.ONE;
            }

            @Override
            public FloatingLong getMaxEnergy() {
                return FloatingLong.ONE;
            }
        }, GaugeType.STANDARD, this, 151, 4));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ItemStackGasToItemStackRecipe recipe, IFocusGroup focuses) {
        initItem(builder, RecipeIngredientRole.INPUT, inputSlot, recipe.getItemInput().getRepresentations());
        List<GasStack> scaledChemicals = recipe.getChemicalInput().getRepresentations();
        initChemical(builder, MekanismJEI.TYPE_GAS, RecipeIngredientRole.INPUT, inputGauge, scaledChemicals);
        initItem(builder, RecipeIngredientRole.OUTPUT, outputSlot, recipe.getOutputDefinition());
        List<ItemStack> gasItemProviders = new ArrayList<>();
        for (GasStack gasStack : scaledChemicals) {
            gasItemProviders.addAll(MekanismJEI.GAS_STACK_HELPER.getStacksFor(gasStack.getType(), true));
        }
        initItem(builder, RecipeIngredientRole.CATALYST, extra, gasItemProviders);
    }
}
