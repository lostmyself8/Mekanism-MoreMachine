package com.jerry.mekmm.client.gui.element.tab;

import com.jerry.mekmm.common.network.to_server.MoreMachinePacketGuiInteract;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;

import mekanism.client.SpecialColors;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiInsetElement;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.text.BooleanStateDisplay;

import net.minecraft.client.gui.GuiGraphics;

import org.jetbrains.annotations.NotNull;

public class MoreMachineGuiSortingTab extends GuiInsetElement<TileEntityMoreMachineFactory<?>> {

    public MoreMachineGuiSortingTab(IGuiWrapper gui, TileEntityMoreMachineFactory<?> tile) {
        super(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "sorting.png"), gui, tile, -26, 62, 35, 18, true);
    }

    @Override
    public void drawBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(guiGraphics, mouseX, mouseY, partialTicks);
        drawTextScaledBound(guiGraphics, BooleanStateDisplay.OnOff.of(dataSource.isSorting()).getTextComponent(), relativeX + 10, relativeY + 24, titleTextColor(), 21);
    }

    @Override
    public void renderToolTip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderToolTip(guiGraphics, mouseX, mouseY);
        displayTooltips(guiGraphics, mouseX, mouseY, MekanismLang.AUTO_SORT.translate());
    }

    @Override
    protected void colorTab(GuiGraphics guiGraphics) {
        MekanismRenderer.color(guiGraphics, SpecialColors.TAB_FACTORY_SORT);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        Mekanism.packetHandler().sendToServer(new MoreMachinePacketGuiInteract(MoreMachinePacketGuiInteract.GuiInteraction.AUTO_SORT_BUTTON, dataSource));
    }
}
