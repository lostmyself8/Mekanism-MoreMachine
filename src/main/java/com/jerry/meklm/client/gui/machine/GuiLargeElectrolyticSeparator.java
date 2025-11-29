package com.jerry.meklm.client.gui.machine;

import com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator;

import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.button.GuiGasMode;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;

public class GuiLargeElectrolyticSeparator extends GuiMekanismTile<TileEntityLargeElectrolyticSeparator, MekanismTileContainer<TileEntityLargeElectrolyticSeparator>> {

    private GuiElement fluidGauge;

    public GuiLargeElectrolyticSeparator(MekanismTileContainer<TileEntityLargeElectrolyticSeparator> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiEnergyTab(this, tile.getEnergyContainer(), tile::getEnergyUsed));
        fluidGauge = addRenderableWidget(new GuiFluidGauge(() -> tile.fluidTank, () -> tile.getFluidTanks(null), GaugeType.STANDARD, this, 5, 10))
                .warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT));
        addRenderableWidget(new GuiChemicalGauge(() -> tile.leftTank, () -> tile.getChemicalTanks(null), GaugeType.SMALL, this, 58, 18))
                .warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, tile.getWarningCheck(TileEntityLargeElectrolyticSeparator.NOT_ENOUGH_SPACE_LEFT_OUTPUT_ERROR));
        addRenderableWidget(new GuiChemicalGauge(() -> tile.rightTank, () -> tile.getChemicalTanks(null), GaugeType.SMALL, this, 100, 18))
                .warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, tile.getWarningCheck(TileEntityLargeElectrolyticSeparator.NOT_ENOUGH_SPACE_RIGHT_OUTPUT_ERROR));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15))
                .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY))
                .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY_REDUCED_RATE, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE));
        addRenderableWidget(new GuiProgress(tile::getActive, ProgressType.BI, this, 80, 30).recipeViewerCategory(tile))
                .warning(WarningTracker.WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT));
        addRenderableWidget(new GuiGasMode(this, 7, 72, false, () -> tile.dumpLeft, tile.getBlockPos(), 0));
        addRenderableWidget(new GuiGasMode(this, 159, 72, true, () -> tile.dumpRight, tile.getBlockPos(), 1));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleTextWithOffset(guiGraphics, fluidGauge.getRelativeRight());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
