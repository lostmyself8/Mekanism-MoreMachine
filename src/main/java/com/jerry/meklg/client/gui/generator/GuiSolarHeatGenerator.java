package com.jerry.meklg.client.gui.generator;

import mekanism.client.SpecialColors;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiEnergyGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.client.gui.element.tab.GuiWarningTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.IWarningTracker;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.generators.common.GeneratorsLang;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.jerry.meklg.common.tile.generator.TileEntitySolarHeatGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiSolarHeatGenerator extends GuiMekanismTile<TileEntitySolarHeatGenerator, MekanismTileContainer<TileEntitySolarHeatGenerator>> {

    public GuiSolarHeatGenerator(MekanismTileContainer<TileEntitySolarHeatGenerator> container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth += 28;
        imageHeight += 3;
        inventoryLabelX += 14;
        inventoryLabelY += 4;
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        addRenderableWidget(GuiSideHolder.create(this, -26, 6, 98, true, true, SpecialColors.TAB_ARMOR_SLOTS));
        super.addGuiElements();
        addRenderableWidget(new GuiInnerScreen(this, 47, 14, 110, 40, () -> List.of(
                MekanismLang.TEMPERATURE.translate(MekanismUtils.getTemperatureDisplay(tile.getTotalTemperature(), UnitDisplayUtils.TemperatureUnit.KELVIN, true)),
                MekanismLang.TEMPERATURE.translate(tile.getCoolantConversionEfficiency()))));
        addRenderableWidget(new GuiChemicalGauge(() -> tile.cooledCoolantTank, () -> tile.getChemicalTanks(null), GaugeType.STANDARD, this, 27, 14));
        addRenderableWidget(new GuiChemicalGauge(() -> tile.superheatedCoolantTank, () -> tile.getChemicalTanks(null), GaugeType.STANDARD, this, 159, 14));
        addRenderableWidget(new GuiFluidGauge(() -> tile.fluidTank, () -> tile.getFluidTanks(null), GaugeType.STANDARD, this, 7, 14));
        addRenderableWidget(new GuiEnergyTab(this, () -> List.of(
                GeneratorsLang.PRODUCING_AMOUNT.translate(EnergyDisplay.of(tile.getProductionRate())),
                MekanismLang.MAX_OUTPUT.translate(EnergyDisplay.of(tile.getMaxOutput())))));
        // addRenderableWidget(new GuiEnergyGauge(tile.getEnergyContainer(), GaugeType.SMALL_MED, this, 179, 14));
        addRenderableWidget(new GuiEnergyGauge(new GuiEnergyGauge.IEnergyInfoHandler() {

            @Override
            public long getEnergy() {
                return tile.getEnergyContainer().getEnergy();
            }

            @Override
            public long getMaxEnergy() {
                return tile.getEnergyContainer().getMaxEnergy();
            }
        }, GaugeType.SMALL_MED, this, 179, 14, 18, 40));
        addRenderableWidget(new GuiHeatTab(this, () -> {
            Component temp = MekanismUtils.getTemperatureDisplay(tile.getTotalTemperature(), UnitDisplayUtils.TemperatureUnit.KELVIN, true);
            Component transfer = MekanismUtils.getTemperatureDisplay(tile.getLastTransferLoss(), UnitDisplayUtils.TemperatureUnit.KELVIN, false);
            Component environment = MekanismUtils.getTemperatureDisplay(tile.getLastEnvironmentLoss(), UnitDisplayUtils.TemperatureUnit.KELVIN, false);
            return List.of(MekanismLang.TEMPERATURE.translate(temp), MekanismLang.TRANSFERRED_RATE.translate(transfer), MekanismLang.DISSIPATED_RATE.translate(environment));
        }));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void addWarningTab(IWarningTracker warningTracker) {
        addRenderableWidget(new GuiWarningTab(this, warningTracker, false));
    }
}
