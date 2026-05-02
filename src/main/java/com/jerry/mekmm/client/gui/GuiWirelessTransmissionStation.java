package com.jerry.mekmm.client.gui;

import com.jerry.mekmm.client.gui.element.bar.GuiFlexibleHorizontalRateBar;
import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.inventory.container.tile.WirelessTransmissionStationContainer;
import com.jerry.mekmm.common.network.to_server.button.MoreMachinePacketTileButtonPress;
import com.jerry.mekmm.common.network.to_server.button.MoreMachinePacketTileButtonPress.MoreMachineClickedTileButton;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessTransmissionStation;

import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiEnergyGauge;
import mekanism.client.gui.element.gauge.GuiEnergyGauge.IEnergyInfoHandler;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.client.gui.tooltip.TooltipUtils;
import mekanism.common.MekanismLang;
import mekanism.common.network.PacketUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils;
import mekanism.common.util.text.EnergyDisplay;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiWirelessTransmissionStation extends GuiConfigurableTile<TileEntityWirelessTransmissionStation, WirelessTransmissionStationContainer> {

    public GuiWirelessTransmissionStation(WirelessTransmissionStationContainer container, Inventory inv, Component title) {
        super(container, inv, title, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT + 14);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiChemicalGauge(() -> tile.chemicalTank, () -> tile.getChemicalTanks(null), GaugeType.STANDARD, this, 7, 14));
        addRenderableWidget(new GuiFluidGauge(() -> tile.fluidTank, () -> tile.getFluidTanks(null), GaugeType.STANDARD, this, 151, 14));
        addRenderableWidget(new GuiEnergyGauge(new IEnergyInfoHandler() {

            @Override
            public long getEnergy() {
                return tile.getEnergyContainer().getEnergy();
            }

            @Override
            public long getMaxEnergy() {
                return tile.getEnergyContainer().getMaxEnergy();
            }
        }, GaugeType.MEDIUM, this, 53, 14, 68, 60));
        addRenderableWidget(new GuiEnergyTab(this, () -> List.of(MekanismLang.MATRIX_INPUT_RATE.translate(EnergyDisplay.of(tile.getInputRate())),
                MekanismLang.MAX_OUTPUT.translate(EnergyDisplay.of(tile.getEnergyRate())))));
        // 热量条
        addRenderableWidget(new GuiFlexibleHorizontalRateBar(this, new IBarInfoHandler() {

            @Override
            public Component getTooltip() {
                return MekanismUtils.getTemperatureDisplay(tile.getTemperature(), UnitDisplayUtils.TemperatureUnit.KELVIN, true);
            }

            @Override
            public double getLevel() {
                return Math.min(1, tile.getTemperature() / TileEntityWirelessTransmissionStation.MAX_MULTIPLIER_TEMP);
            }
        }, 27, 80, 119, 9));
        // 配置按钮
        addRenderableWidget(new MekanismImageButton(this, 151, 77, 18, 18, getButtonLocation("config"),
                (element, mouseX, mouseY) -> PacketUtils.sendToServer(new MoreMachinePacketTileButtonPress(MoreMachineClickedTileButton.WIRELESS_TRANSMISSION_STATION_CONFIG, ((GuiWirelessTransmissionStation) element.gui()).tile))))
                .setTooltip(TooltipUtils.create(MoreMachineLang.CONFIGURATION));
        addRenderableWidget(new GuiHeatTab(this, () -> {
            Component temp = MekanismUtils.getTemperatureDisplay(tile.getTotalTemperature(), UnitDisplayUtils.TemperatureUnit.KELVIN, true);
            Component transfer = MekanismUtils.getTemperatureDisplay(tile.getLastTransferLoss(), UnitDisplayUtils.TemperatureUnit.KELVIN, false);
            Component environment = MekanismUtils.getTemperatureDisplay(tile.getLastEnvironmentLoss(), UnitDisplayUtils.TemperatureUnit.KELVIN, false);
            return List.of(MekanismLang.TEMPERATURE.translate(temp), MekanismLang.TRANSFERRED_RATE.translate(transfer), MekanismLang.DISSIPATED_RATE.translate(environment));
        }));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
        renderTitleText(GuiGraphicsExtractor);
        // renderInventoryText(GuiGraphicsExtractor);
        super.drawForegroundText(GuiGraphicsExtractor, mouseX, mouseY);
    }
}
