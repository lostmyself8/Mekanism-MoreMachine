package com.jerry.mekmm.client.gui;

import com.jerry.mekmm.client.gui.element.button.ConnectListButton;
import com.jerry.mekmm.common.attachments.component.ConnectionConfig;
import com.jerry.mekmm.common.attachments.component.WirelessConnectionManager;
import com.jerry.mekmm.common.tile.interfaces.ITileConnectHolder;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiElementHolder;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.scroll.GuiScrollBar;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.WorldUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class GuiConnectListHolder<TILE extends TileEntityMekanism & ITileConnectHolder, CONTAINER extends MekanismTileContainer<TILE>> extends GuiMekanismTile<TILE, CONTAINER> {

    /**
     * The number of lists that can be displayed
     */
    private static final int CONNECT_COUNT = 5;
    protected GuiInnerScreen leftScreen;
    private GuiScrollBar scrollBar;

    Level level;

    protected GuiConnectListHolder(CONTAINER container, Inventory inv, Component title) {
        // Height相比于矿机增加了7
        super(container, inv, title, DEFAULT_IMAGE_WIDTH + 100, DEFAULT_IMAGE_HEIGHT + 95);
        level = inv.player.level();
        // imageHeight += 95;// 相比于矿机增加了7
        // imageWidth += 100;
        inventoryLabelX += 50;
        inventoryLabelY = imageHeight - 94;
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        leftScreen = addRenderableWidget(new GuiInnerScreen(this, 9, 17, 110, 147));// width 85,height相比于矿机增加了7
        // List Holder 95
        addRenderableWidget(new GuiElementHolder(this, 120, 17, 133, 147));// width 158,height相比于矿机增加了29
        WirelessConnectionManager connectionManager = getConnectManager();
        scrollBar = addRenderableWidget(new GuiScrollBar(this, 253, 17, 147, connectionManager::getConnectionCount, () -> CONNECT_COUNT));
        for (int i = 0; i < CONNECT_COUNT; i++) {
            addRenderableWidget(new ConnectListButton(this, 121, 18 + i * 29, i, scrollBar::getCurrentSelection, connectionManager, this::onClick, this::getRenderStacks, level));
        }
    }

    protected void drawScreenText(GuiGraphicsExtractor guiGraphics, Component text, int y) {
        drawScreenText(guiGraphics, text, 0, y);
    }

    protected void drawScreenText(GuiGraphicsExtractor guiGraphics, Component text, int x, int y) {
        // TODO: Do we want to make usages of this method eventually set the text to be rendered within the gui element
        // for the screen?
        if (leftScreen != null) {// Validate it was properly set
            leftScreen.drawScaledScrollingString(guiGraphics, text, x, y, TextAlignment.LEFT, screenTextColor(), leftScreen.getXSize() - x, 5, false, 0.8F);
        }
    }

    // TODO:这里或许不需要List，mek这里是为了标签按钮和模组按钮
    private List<ItemStack> getRenderStacks(ConnectionConfig config) {
        if (config != null) {
            BlockEntity tile = WorldUtils.getTileEntity(level, config.pos());
            if (tile instanceof TileEntityBoundingBlock boundingBlock) {
                return List.of(level.getBlockState(boundingBlock.getMainPos()).getBlock().asItem().getDefaultInstance());
            }
            return List.of(level.getBlockState(config.pos()).getBlock().asItem().getDefaultInstance());
        }
        return Collections.emptyList();
    }

    protected WirelessConnectionManager getConnectManager() {
        return tile.getConnectManager();
    }

    protected abstract void onClick(ConnectionConfig config, int index);

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double xDelta, double yDelta) {
        return super.mouseScrolled(mouseX, mouseY, xDelta, yDelta) || scrollBar.adjustScroll(yDelta);
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
        renderInventoryText(guiGraphics);
    }
}
