package com.jerry.meklg.client.gui.generator;

import com.jerry.mekmm.common.MoreMachineLang;

import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.client.SpecialColors;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.generators.client.gui.element.GuiStateTexture;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.MekanismGenerators;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.jerry.meklg.common.tile.generator.TileEntityLargeWindGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuiLargeWindGenerator extends GuiMekanismTile<TileEntityLargeWindGenerator, MekanismTileContainer<TileEntityLargeWindGenerator>> {

    public GuiLargeWindGenerator(MekanismTileContainer<TileEntityLargeWindGenerator> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        // Add the side holder before the slots, as it holds a couple of the slots
        addRenderableWidget(GuiSideHolder.create(this, -26, 6, 98, true, true, SpecialColors.TAB_ARMOR_SLOTS));
        super.addGuiElements();
        addRenderableWidget(new GuiInnerScreen(this, 48, 21, 80, 44, () -> {
            List<Component> list = new ArrayList<>();
            list.add(EnergyDisplay.of(tile.getEnergyContainer()).getTextComponent());
            long amount = tile.getCurrentGeneration();
            list.add(GeneratorsLang.POWER.translate(MekanismUtils.getEnergyDisplayShort(amount)));
            list.add(GeneratorsLang.OUTPUT_RATE_SHORT.translate(EnergyDisplay.of(tile.getMaxOutput())));
            if (!tile.getActive()) {
                // 默认为未激活(mek这里统一为被方块遮挡，但我觉得应当做出区分)
                ILangEntry reason = MoreMachineLang.INACTIVE;
                if (tile.isBlacklistDimension()) {
                    reason = GeneratorsLang.NO_WIND;
                } else if (tile.hasSameGeneratorNearby()) {
                    reason = MoreMachineLang.SAME_BLOCK_NEARBY;
                } else if (!tile.canSeeSky(tile.getBlockPos().above(TileEntityLargeWindGenerator.TOP_Y))) {
                    reason = GeneratorsLang.SKY_BLOCKED;
                }
                list.add(reason.translateColored(EnumColor.DARK_RED));
            }
            return list;
        }));
        addRenderableWidget(new GuiEnergyTab(this, () -> List.of(
                GeneratorsLang.PRODUCING_AMOUNT.translate(tile.getActive() ? EnergyDisplay.of(tile.getCurrentGeneration()) : EnergyDisplay.ZERO),
                MekanismLang.MAX_OUTPUT.translate(EnergyDisplay.of(tile.getMaxOutput())))));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15));
        addRenderableWidget(new GuiStateTexture(this, 18, 35, tile::getActive, MekanismGenerators.rl(MekanismUtils.ResourceType.GUI.getPrefix() + "wind_on.png"),
                MekanismGenerators.rl(MekanismUtils.ResourceType.GUI.getPrefix() + "wind_off.png")));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
