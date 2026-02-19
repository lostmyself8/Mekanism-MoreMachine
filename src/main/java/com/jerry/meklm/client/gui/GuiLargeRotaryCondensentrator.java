package com.jerry.meklm.client.gui;

import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.GuiDownArrow;
import mekanism.client.gui.element.bar.GuiHorizontalPowerBar;
import mekanism.client.gui.element.button.ToggleButton;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.IProgressInfoHandler;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.network.to_server.PacketGuiInteract;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator;
import org.jetbrains.annotations.NotNull;

public class GuiLargeRotaryCondensentrator extends GuiConfigurableTile<TileEntityLargeRotaryCondensentrator, MekanismTileContainer<TileEntityLargeRotaryCondensentrator>> {

    public GuiLargeRotaryCondensentrator(MekanismTileContainer<TileEntityLargeRotaryCondensentrator> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
        titleLabelY = 4;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiDownArrow(this, 159, 44));
        addRenderableWidget(new GuiHorizontalPowerBar(this, tile.getEnergyContainer(), 115, 75))
                .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY))
                .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY_REDUCED_RATE, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE));
        addRenderableWidget(new GuiEnergyTab(this, tile.getEnergyContainer(), tile::getEnergyUsed));
        addRenderableWidget(new GuiFluidGauge(() -> tile.fluidTank, () -> tile.getFluidTanks(null), GaugeType.STANDARD, this, 133, 13))
                .warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(TileEntityLargeRotaryCondensentrator.NOT_ENOUGH_FLUID_INPUT_ERROR))
                .warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, tile.getWarningCheck(TileEntityLargeRotaryCondensentrator.NOT_ENOUGH_SPACE_FLUID_OUTPUT_ERROR));
        addRenderableWidget(new GuiGasGauge(() -> tile.gasTank, () -> tile.getGasTanks(null), GaugeType.STANDARD, this, 25, 13))
                .warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(TileEntityLargeRotaryCondensentrator.NOT_ENOUGH_GAS_INPUT_ERROR))
                .warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, tile.getWarningCheck(TileEntityLargeRotaryCondensentrator.NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR));
        addRenderableWidget(new GuiProgress(new IProgressInfoHandler.IBooleanProgressInfoHandler() {

            @Override
            public boolean fillProgressBar() {
                return tile.getActive();
            }

            @Override
            public boolean isActive() {
                return !tile.mode;
            }
        }, ProgressType.LARGE_RIGHT, this, 64, 39).jeiCategories(MekanismJEIRecipeType.CONDENSENTRATING))
                .warning(WarningTracker.WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT));
        addRenderableWidget(new GuiProgress(new IProgressInfoHandler.IBooleanProgressInfoHandler() {

            @Override
            public boolean fillProgressBar() {
                return tile.getActive();
            }

            @Override
            public boolean isActive() {
                return tile.mode;
            }
        }, ProgressType.LARGE_LEFT, this, 64, 39).jeiCategories(MekanismJEIRecipeType.DECONDENSENTRATING))
                .warning(WarningTracker.WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT));
        addRenderableWidget(new ToggleButton(this, 4, 4, () -> tile.mode, () -> Mekanism.packetHandler().sendToServer(new PacketGuiInteract(PacketGuiInteract.GuiInteraction.NEXT_MODE, tile)),
                getOnHover(MekanismLang.CONDENSENTRATOR_TOGGLE)));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        drawString(guiGraphics, (tile.mode ? MekanismLang.DECONDENSENTRATING : MekanismLang.CONDENSENTRATING).translate(), 6, imageHeight - 92, titleTextColor());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
