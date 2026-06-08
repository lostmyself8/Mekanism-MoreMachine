package com.jerry.meklm.client.gui.machine.base;

import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;

import mekanism.api.chemical.IChemicalTank;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiChemicalBar;
import mekanism.client.gui.element.button.GuiGasMode;
import mekanism.client.gui.element.tab.GuiWarningTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.IWarningTracker;
import mekanism.common.util.text.TextUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuiLargeChemicalTank extends GuiMekanismTile<TileEntityLargeChemicalTank<?>, MekanismTileContainer<TileEntityLargeChemicalTank<?>>> {

    public GuiLargeChemicalTank(MekanismTileContainer<TileEntityLargeChemicalTank<?>> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        addRenderableWidget(GuiSideHolder.armorHolder(this));
        super.addGuiElements();
        addRenderableWidget(new GuiChemicalBar(this, GuiChemicalBar.getProvider(tile.getChemicalTank(), tile.getChemicalTanks()), 42, 16, 116, 10, true));
        addRenderableWidget(new GuiInnerScreen(this, 42, 37, 118, 28, () -> {
            List<Component> ret = new ArrayList<>();
            IChemicalTank tank = tile.getChemicalTank();
            if (tank.isEmpty()) {
                ret.add(MekanismLang.CHEMICAL.translate(MekanismLang.NONE));
                ret.add(MekanismLang.GENERIC_FRACTION.translate(0, TextUtils.format(tile.getTier().getStorage())));
            } else {
                ret.add(MekanismLang.CHEMICAL.translate(tank.asStack()));
                ret.add(MekanismLang.GENERIC_FRACTION.translate(TextUtils.format(tank.amountAsLong()), TextUtils.format(tank.capacityAsLong(tank.resource()))));
            }
            return ret;
        }));
        addRenderableWidget(new GuiGasMode(this, 159, 72, true, () -> tile.dumping, tile.getBlockPos(), 0));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
        renderTitleText(GuiGraphicsExtractor);
        renderInventoryText(GuiGraphicsExtractor, 85);
        super.drawForegroundText(GuiGraphicsExtractor, mouseX, mouseY);
    }

    @Override
    protected void addWarningTab(IWarningTracker warningTracker) {
        // Move the tab to the right side of the gui so it doesn't overlap the equipped items
        addRenderableWidget(new GuiWarningTab(this, warningTracker, false));
    }
}
