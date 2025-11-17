package com.jerry.mekmm.client.gui.machine;

import com.jerry.mekmm.common.tile.machine.TileEntityPlantingStation;

import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.bar.GuiChemicalBar;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;

public class GuiPlantingStation extends GuiConfigurableTile<TileEntityPlantingStation, MekanismTileContainer<TileEntityPlantingStation>> {

    public GuiPlantingStation(MekanismTileContainer<TileEntityPlantingStation> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();

        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15))
                .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY));

        addRenderableWidget(new GuiEnergyTab(this, tile.getEnergyContainer(), tile::getActive));

        addRenderableWidget(new GuiSlot(SlotType.OUTPUT_WIDE, this, 111, 30))
                .warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE))
                .warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, tile.getWarningCheck(TileEntityPlantingStation.NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR));

        addRenderableWidget(new GuiChemicalBar<>(this, GuiChemicalBar.getProvider(tile.gasTank, tile.getGasTanks(null)), 60, 36, 6, 12, false))
                .warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_SECONDARY_INPUT));

        addRenderableWidget(new GuiProgress(tile::getScaledProgress, ProgressType.BAR, this, 78, 38).jeiCategory(tile))
                .warning(WarningTracker.WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        drawString(guiGraphics, playerInventoryTitle, inventoryLabelX, inventoryLabelY, titleTextColor());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
