package com.jerry.mekmm.client.gui.element.bar;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class GuiFlexibleHorizontalRateBar extends GuiBar<IBarInfoHandler> {

    private static final Identifier RATE_BAR = MekanismUtils.getResource(ResourceType.GUI_BAR, "horizontal_rate.png");
    // 默认78
    private final int texWidth;
    // 默认8
    private final int texHeight;

    public GuiFlexibleHorizontalRateBar(IGuiWrapper gui, IBarInfoHandler handler, int x, int y, int texWidth, int texHeight) {
        super(RATE_BAR, gui, handler, x, y, texWidth, texHeight, true);
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    @Override
    protected void renderBarOverlay(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks, double handlerLevel) {
        int displayInt = (int) (handlerLevel * texWidth);
        if (displayInt > 0) {
            GuiGraphicsExtractor.blit(getResource(), relativeX + 1, relativeY + 1, 0, 0, displayInt, texHeight, texWidth, texHeight);
        }
    }
}
