package com.jerry.mekaf.client.gui.element.tab;

import com.jerry.mekaf.common.tile.factory.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.common.network.to_server.MoreMachinePacketGuiInteract;

import mekanism.client.SpecialColors;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiInsetElement;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.network.PacketUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.text.BooleanStateDisplay;

import net.minecraft.client.gui.GuiGraphics;

import org.jetbrains.annotations.NotNull;

public class AdvancedFactoryGuiSortingTab extends GuiInsetElement<TileEntityAdvancedFactoryBase<?>> {

    public AdvancedFactoryGuiSortingTab(IGuiWrapper gui, TileEntityAdvancedFactoryBase<?> tile) {
        super(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "sorting.png"), gui, tile, -26, 62, 35, 18, true);
        setTooltip(MekanismLang.AUTO_SORT);
    }

    @Override
    public void drawBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(guiGraphics, mouseX, mouseY, partialTicks);
        drawScrollingString(guiGraphics, BooleanStateDisplay.OnOff.of(dataSource.isSorting()).getTextComponent(), 0, 24, TextAlignment.CENTER, titleTextColor(), 3, false);
    }

    @Override
    protected void colorTab(GuiGraphics guiGraphics) {
        MekanismRenderer.color(guiGraphics, SpecialColors.TAB_FACTORY_SORT);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        PacketUtils.sendToServer(new MoreMachinePacketGuiInteract(MoreMachinePacketGuiInteract.MoreMachineGuiInteraction.AUTO_SORT_BUTTON, dataSource));
    }
}
