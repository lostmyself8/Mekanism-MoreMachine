//package com.jerry.mekmm.client.recipe_viewer.emi.recipe;
//
//import com.jerry.mekmm.api.recipes.basic.MMBasicChemicalChemicalToChemicalRecipe;
//import com.jerry.mekmm.common.tile.machine.TileEntityReplicator;
//
//import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
//import mekanism.client.gui.element.gauge.GaugeType;
//import mekanism.client.gui.element.gauge.GuiChemicalGauge;
//import mekanism.client.gui.element.gauge.GuiEnergyGauge;
//import mekanism.client.gui.element.progress.ProgressType;
//import mekanism.client.gui.element.slot.SlotType;
//import mekanism.client.recipe_viewer.emi.MekanismEmiRecipeCategory;
//import mekanism.client.recipe_viewer.emi.recipe.MekanismEmiRecipe;
//import mekanism.common.inventory.container.slot.SlotOverlay;
//import mekanism.common.tile.component.config.DataType;
//
//import net.minecraft.resources.Identifier;
//
//import dev.emi.emi.api.widget.WidgetHolder;
//import org.jetbrains.annotations.NotNull;
//
//public class ChemicalReplicatorEmiRecipe extends MekanismEmiRecipe<MMBasicChemicalChemicalToChemicalRecipe> {
//
//    public ChemicalReplicatorEmiRecipe(MekanismEmiRecipeCategory category, Identifier id, MMBasicChemicalChemicalToChemicalRecipe recipes) {
//        super(category, id, recipes);
//        super.addInputDefinition(recipe.getRightInput());
//        addInputDefinition(recipe.getLeftInput());
//        addChemicalOutputDefinition(recipe.getOutputDefinition());
//        addCatalsyst(recipe.getRightInput());
//    }
//
//    // 重写，消耗变为0
//    @Override
//    protected void addInputDefinition(@NotNull ChemicalStackIngredient ingredient) {
//        getInputs().add(chemicalIngredient(ingredient).setChance(0));
//    }
//
//    @Override
//    public void addWidgets(WidgetHolder widgetHolder) {
//        GaugeType chemical_right = GaugeType.STANDARD.with(DataType.INPUT);
//        initTank(widgetHolder, GuiChemicalGauge.getDummy(chemical_right, this, 7, 4), input(0));
//        GaugeType chemical_left = GaugeType.STANDARD.with(DataType.INPUT);
//        initTank(widgetHolder, GuiChemicalGauge.getDummy(chemical_left, this, 28, 4), input(1));
//        GaugeType chemical_output = GaugeType.STANDARD.with(DataType.INPUT);
//        initTank(widgetHolder, GuiChemicalGauge.getDummy(chemical_output, this, 131, 4), output(0)).recipeContext(this);
//        addSlot(widgetHolder, SlotType.EXTRA, 8, 65, catalyst(0)).catalyst(true);
//        addSlot(widgetHolder, SlotType.INPUT, 29, 65).with(SlotOverlay.PLUS);
//        addSlot(widgetHolder, SlotType.OUTPUT, 132, 65).with(SlotOverlay.PLUS);
//        addSlot(widgetHolder, SlotType.POWER, 152, 65).with(SlotOverlay.POWER);
//        addSimpleProgress(widgetHolder, ProgressType.LARGE_RIGHT, 64, 36, TileEntityReplicator.BASE_TICKS_REQUIRED);
//        addElement(widgetHolder, new GuiEnergyGauge(new GuiEnergyGauge.IEnergyInfoHandler() {
//
//            @Override
//            public long getEnergy() {
//                return 1L;
//            }
//
//            @Override
//            public long getMaxEnergy() {
//                return 1L;
//            }
//        }, GaugeType.STANDARD, this, 151, 4));
//    }
//}
