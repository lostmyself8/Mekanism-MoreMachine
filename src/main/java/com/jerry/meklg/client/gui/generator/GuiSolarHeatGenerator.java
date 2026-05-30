package com.jerry.meklg.client.gui.generator;

import mekanism.client.SpecialColors;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
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
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        addRenderableWidget(GuiSideHolder.create(this, -26, 6, 98, true, true, SpecialColors.TAB_ARMOR_SLOTS));
        super.addGuiElements();
        // addRenderableWidget(new GuiInnerScreen(this, 48, 21, 80, 44, () -> List.of(
        // EnergyDisplay.of(tile.getEnergyContainer()).getTextComponent(),
        // GeneratorsLang.PRODUCING_AMOUNT.translate(EnergyDisplay.ZERO),
        // MekanismLang.MAX_OUTPUT.translate(EnergyDisplay.of(tile.getMaxOutput())))));
        addRenderableWidget(new GuiEnergyTab(this, () -> List.of(
                GeneratorsLang.PRODUCING_AMOUNT.translate(EnergyDisplay.ZERO),
                MekanismLang.MAX_OUTPUT.translate(EnergyDisplay.of(tile.getMaxOutput())))));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
