package com.jerry.mekmm.client.gui;

import com.jerry.mekmm.client.gui.window.connect.GuiViewConnection;
import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.attachments.component.ConnectionConfig;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.network.to_server.PacketGuiSetDoubleValue;
import com.jerry.mekmm.common.network.to_server.PacketGuiSetDoubleValue.GuiDoubleValue;
import com.jerry.mekmm.common.network.to_server.PacketGuiSetIntValue;
import com.jerry.mekmm.common.network.to_server.PacketGuiSetIntValue.GuiIntValue;
import com.jerry.mekmm.common.network.to_server.PacketGuiSetLongValue;
import com.jerry.mekmm.common.network.to_server.PacketGuiSetLongValue.GuiLongValue;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessTransmissionStation;

import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.client.gui.element.text.GuiTextField;
import mekanism.client.gui.tooltip.TooltipUtils;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.network.PacketUtils;
import mekanism.common.network.to_server.button.PacketTileButtonPress;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.common.util.text.InputValidator;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;

public class GuiWirelessTransmissionStationConfig extends GuiConnectListHolder<TileEntityWirelessTransmissionStation, MekanismTileContainer<TileEntityWirelessTransmissionStation>> {

    private GuiTextField energyRateField, fluidsRateField, chemicalsRateField, itemsRateField, heatRateField;

    public GuiWirelessTransmissionStationConfig(MekanismTileContainer<TileEntityWirelessTransmissionStation> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new MekanismImageButton(this, 5, 5, 11, 14, getButtonLocation("back"),
                (element, mouseX, mouseY) -> PacketUtils.sendToServer(new PacketTileButtonPress(PacketTileButtonPress.ClickedTileButton.BACK_BUTTON, ((GuiWirelessTransmissionStationConfig) element.gui()).tile))))
                .setTooltip(TooltipUtils.BACK);

        energyRateField = addRenderableWidget(new GuiTextField(this, 13, 34, 60, 11));
        energyRateField.setMaxLength(Long.toString(MoreMachineConfig.general.energyRate.get()).length());
        // 都是输入0-9的数字，直接借用Mek现有的
        energyRateField.setInputValidator(InputValidator.DIGIT);
        energyRateField.configureDigitalBorderInput(() -> setLongText(energyRateField, GuiLongValue.SET_ENERGY_RATE));
        fluidsRateField = addRenderableWidget(new GuiTextField(this, 13, 61, 60, 11));
        fluidsRateField.setMaxLength(Integer.toString(MoreMachineConfig.general.fluidsRate.get()).length());
        fluidsRateField.setInputValidator(InputValidator.DIGIT);
        fluidsRateField.configureDigitalBorderInput(() -> setText(fluidsRateField, GuiIntValue.SET_FLUIDS_RATE));
        chemicalsRateField = addRenderableWidget(new GuiTextField(this, 13, 88, 60, 11));
        chemicalsRateField.setMaxLength(Long.toString(MoreMachineConfig.general.chemicalsRate.get()).length());
        chemicalsRateField.setInputValidator(InputValidator.DIGIT);
        chemicalsRateField.configureDigitalBorderInput(() -> setLongText(chemicalsRateField, GuiLongValue.SET_CHEMICALS_RATE));
        itemsRateField = addRenderableWidget(new GuiTextField(this, 13, 115, 60, 11));
        itemsRateField.setMaxLength(Integer.toString(MoreMachineConfig.general.itemsRate.get()).length());
        itemsRateField.setInputValidator(InputValidator.DIGIT);
        itemsRateField.configureDigitalBorderInput(() -> setText(itemsRateField, GuiIntValue.SET_ITEMS_RATE));
        heatRateField = addRenderableWidget(new GuiTextField(this, 13, 142, 60, 11));
        heatRateField.setMaxLength(Double.toString(MoreMachineConfig.general.heatRate.get() * 10_000).length());
        heatRateField.setInputValidator(c -> c >= '0' && c <= '9' || c == '.');
        heatRateField.configureDigitalBorderInput(() -> setDoubleText(heatRateField, GuiDoubleValue.SET_HEAT_RATE));
    }

    // 覆写，以免绘制SecurityTab和RedstoneControl
    @Override
    protected void addGenericTabs() {
        // Don't add the generic tabs when we are in the config
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
        renderTitleTextWithOffset(guiGraphics, 14);// Adjust spacing for back button
        drawScreenText(guiGraphics, MoreMachineLang.WTS_ENERGY_RATE.translate(EnergyDisplay.of(tile.getEnergyRate())), 7);
        drawScreenText(guiGraphics, MoreMachineLang.WTS_FLUIDS_RATE.translate(tile.getFluidsRate()), 34);
        drawScreenText(guiGraphics, MoreMachineLang.WTS_CHEMICALS_RATE.translate(tile.getChemicalsRate()), 61);
        drawScreenText(guiGraphics, MoreMachineLang.WTS_ITEMS_RATE.translate(tile.getItemsRate()), 88);
        drawScreenText(guiGraphics, MoreMachineLang.WTS_HEAT_RATE.translate(MekanismUtils.getTemperatureDisplay(tile.getHeatRate(), UnitDisplayUtils.TemperatureUnit.KELVIN, false)), 115);
    }

    @Override
    protected void onClick(ConnectionConfig config, int index) {
        // 点击右侧按钮后执行
        addWindow(GuiViewConnection.create(this, tile, level, config));
    }

    private void setLongText(GuiTextField field, GuiLongValue interaction) {
        if (!field.getText().isEmpty()) {
            try {
                PacketUtils.sendToServer(new PacketGuiSetLongValue(interaction, tile.getBlockPos(), Long.parseLong(field.getText())));
            } catch (NumberFormatException ignored) {// Might not be valid if multiple negative signs
            }
            field.setText("");
        }
    }

    private void setText(GuiTextField field, GuiIntValue interaction) {
        if (!field.getText().isEmpty()) {
            try {
                PacketUtils.sendToServer(new PacketGuiSetIntValue(interaction, tile.getBlockPos(), Integer.parseInt(field.getText())));
            } catch (NumberFormatException ignored) {// Might not be valid if multiple negative signs
            }
            field.setText("");
        }
    }

    private void setDoubleText(GuiTextField field, GuiDoubleValue interaction) {
        if (!field.getText().isEmpty()) {
            try {
                PacketUtils.sendToServer(new PacketGuiSetDoubleValue(interaction, tile.getBlockPos(), Double.parseDouble(field.getText())));
            } catch (NumberFormatException ignored) {// Might not be valid if multiple negative signs
            }
            field.setText("");
        }
    }
}
