package com.jerry.mekmm.client.gui.machine;

import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.tile.machine.TileEntityAmbientGasCollector;

import mekanism.api.chemical.ChemicalStack;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.common.util.text.TextUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuiAmbientGasCollector extends GuiMekanismTile<TileEntityAmbientGasCollector, MekanismTileContainer<TileEntityAmbientGasCollector>> {

    public GuiAmbientGasCollector(MekanismTileContainer<TileEntityAmbientGasCollector> container, Inventory inv, Component title) {
        super(container, inv, title);
        titleLabelY = 5;
        inventoryLabelY += 2;
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiInnerScreen(this, 54, 23, 80, 42, () -> {
            List<Component> list = new ArrayList<>();
            list.add(EnergyDisplay.of(tile.getEnergyContainer()).getTextComponent());
            if (tile.getNotBlocking()) {
                list.add(MoreMachineLang.NO_BLOCKING.translate());
            } else {
                list.add(MoreMachineLang.IS_BLOCKING.translate());
            }
            ChemicalStack chemicalStack = tile.chemicalTank.getStack();
            if (chemicalStack.isEmpty()) {
                list.add(MekanismLang.NO_CHEMICAL.translate());
            } else {
                list.add(MekanismLang.GENERIC_STORED_MB.translate(chemicalStack, TextUtils.format(chemicalStack.amount())));
            }
            return list;
        }));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15))
                .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, () -> {
                    MachineEnergyContainer<TileEntityAmbientGasCollector> energyContainer = tile.getEnergyContainer();
                    return energyContainer.getEnergyPerTick() > energyContainer.getEnergy();
                });
        addRenderableWidget(new GuiChemicalGauge(() -> tile.chemicalTank, () -> tile.getChemicalTanks(null), GaugeType.STANDARD, this, 6, 13))
                .warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, () -> tile.chemicalTank.getNeeded() < tile.estimateIncrementAmount());
        // TODO: Eventually we may want to consider showing a warning if the block under the pump is of the wrong type
        // or there wasn't a valid spot to suck
        addRenderableWidget(new GuiEnergyTab(this, tile.getEnergyContainer(), tile::usedEnergy));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
