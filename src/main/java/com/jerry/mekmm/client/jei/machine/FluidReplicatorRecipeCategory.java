package com.jerry.mekmm.client.jei.machine;

import com.jerry.mekmm.api.recipes.FluidStackGasToFluidStackRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.math.FloatingLong;
import mekanism.client.gui.element.gauge.*;
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
public class FluidReplicatorRecipeCategory extends BaseRecipeCategory<FluidStackGasToFluidStackRecipe> {

    private final GuiGauge<?> gasInputGauge;
    private final GuiGauge<?> fluidInputGauge;
    private final GuiGauge<?> outputGauge;
    private final GuiSlot extra;

    public FluidReplicatorRecipeCategory(IGuiHelper helper, MekanismJEIRecipeType<FluidStackGasToFluidStackRecipe> recipeType) {
        super(helper, recipeType, MoreMachineBlocks.FLUID_REPLICATOR, 3, 3, 170, 79);
        GaugeType type1 = GaugeType.STANDARD.with(DataType.INPUT);
        GaugeType output = GaugeType.STANDARD.with(DataType.OUTPUT);
        gasInputGauge = addElement(GuiGasGauge.getDummy(type1, this, 7, 4));
        fluidInputGauge = addElement(GuiFluidGauge.getDummy(type1, this, 28, 4));
        outputGauge = addElement(GuiGasGauge.getDummy(output, this, 131, 4));
        extra = addSlot(SlotType.EXTRA, 8, 65).with(SlotOverlay.MINUS);
        addSlot(SlotType.INPUT, 29, 65).with(SlotOverlay.PLUS);
        addSlot(SlotType.OUTPUT, 132, 65).with(SlotOverlay.PLUS);
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
    public void setRecipe(IRecipeLayoutBuilder builder, FluidStackGasToFluidStackRecipe recipe, IFocusGroup focuses) {
        initFluid(builder, RecipeIngredientRole.INPUT, fluidInputGauge, recipe.getFluidInput().getRepresentations());
        List<GasStack> scaledChemicals = recipe.getChemicalInput().getRepresentations();
        initChemical(builder, MekanismJEI.TYPE_GAS, RecipeIngredientRole.INPUT, gasInputGauge, scaledChemicals);
        initFluid(builder, RecipeIngredientRole.OUTPUT, outputGauge, recipe.getOutputDefinition());
        List<ItemStack> gasItemProviders = new ArrayList<>();
        for (GasStack gasStack : scaledChemicals) {
            gasItemProviders.addAll(MekanismJEI.GAS_STACK_HELPER.getStacksFor(gasStack.getType(), true));
        }
        initItem(builder, RecipeIngredientRole.CATALYST, extra, gasItemProviders);
    }
}
