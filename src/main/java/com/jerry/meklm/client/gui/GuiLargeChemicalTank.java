package com.jerry.meklm.client.gui;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.text.ILangEntry;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiMergedChemicalBar;
import mekanism.client.gui.element.button.GuiGasMode;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.util.text.TextUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuiLargeChemicalTank extends GuiConfigurableTile<TileEntityLargeChemicalTank<?>, MekanismTileContainer<TileEntityLargeChemicalTank<?>>> {

    public GuiLargeChemicalTank(MekanismTileContainer<TileEntityLargeChemicalTank<?>> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        // Add the side holder before the slots, as it holds a couple of the slots
        addRenderableWidget(GuiSideHolder.armorHolder(this));
        super.addGuiElements();
        addRenderableWidget(new GuiMergedChemicalBar<>(this, tile, tile.getChemicalTank(), 42, 16, 116, 10, true));
        addRenderableWidget(new GuiInnerScreen(this, 42, 37, 118, 28, () -> {
            List<Component> ret = new ArrayList<>();
            switch (tile.getChemicalTank().getCurrent()) {
                case EMPTY -> {
                    ret.add(MekanismLang.CHEMICAL.translate(MekanismLang.NONE));
                    ret.add(MekanismLang.GENERIC_FRACTION.translate(0, TextUtils.format(tile.getTier().getStorage())));
                }
                case GAS -> addStored(ret, tile.getChemicalTank().getGasTank(), MekanismLang.GAS);
                case INFUSION -> addStored(ret, tile.getChemicalTank().getInfusionTank(), MekanismLang.INFUSE_TYPE);
                case PIGMENT -> addStored(ret, tile.getChemicalTank().getPigmentTank(), MekanismLang.PIGMENT);
                case SLURRY -> addStored(ret, tile.getChemicalTank().getSlurryTank(), MekanismLang.SLURRY);
                default -> throw new IllegalStateException("Unknown current type");
            }
            return ret;
        }));
        addRenderableWidget(new GuiGasMode(this, 159, 72, true, () -> tile.dumping, tile.getBlockPos(), 0));
    }

    private void addStored(List<Component> ret, IChemicalTank<?, ?> tank, ILangEntry langKey) {
        ret.add(langKey.translate(tank.getStack()));
        ret.add(MekanismLang.GENERIC_FRACTION.translate(TextUtils.format(tank.getStored()), TextUtils.format(tank.getCapacity())));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        drawString(guiGraphics, playerInventoryTitle, inventoryLabelX, inventoryLabelY, titleTextColor());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
