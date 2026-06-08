package com.jerry.meklg.client.gui.generator;

import mekanism.api.math.MathUtils;
import mekanism.client.SpecialColors;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.generators.common.GeneratorsLang;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.jerry.meklg.common.tile.generator.TileEntityLargeGasGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiLargeGasGenerator extends GuiMekanismTile<TileEntityLargeGasGenerator, MekanismTileContainer<TileEntityLargeGasGenerator>> {

    public GuiLargeGasGenerator(MekanismTileContainer<TileEntityLargeGasGenerator> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        // Add the side holder before the slots, as it holds a couple of the slots
        addRenderableWidget(GuiSideHolder.create(this, -26, 6, 98, true, true, SpecialColors.TAB_ARMOR_SLOTS));
        super.addGuiElements();
        addRenderableWidget(new GuiEnergyTab(this, () -> {
            // 燃料消耗和发电量都受效率乘数影响
            long productionAmount = MathUtils.clampToLong(tile.getUsed());
            return List.of(
                    GeneratorsLang.PRODUCING_AMOUNT.translate(EnergyDisplay.of(productionAmount)),
                    MekanismLang.MAX_OUTPUT.translate(EnergyDisplay.of(tile.getMaxOutput())));
        }));
        addRenderableWidget(new GuiChemicalGauge(() -> tile.fuelTank, tile::getChemicalTanks, GaugeType.WIDE, this, 55, 18));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
        renderTitleText(GuiGraphicsExtractor);
        renderInventoryTextAndOther(GuiGraphicsExtractor, GeneratorsLang.GAS_BURN_RATE.translate(tile.getUsed()));
        super.drawForegroundText(GuiGraphicsExtractor, mouseX, mouseY);
    }
}
