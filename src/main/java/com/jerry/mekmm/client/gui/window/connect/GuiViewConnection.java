package com.jerry.mekmm.client.gui.window.connect;

import com.jerry.mekmm.client.render.BlockHighlightManager;
import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.attachments.component.ConnectionConfig;
import com.jerry.mekmm.common.network.to_server.PacketViewConnection;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessTransmissionStation;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.text.EnumColor;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.button.TranslationButton;
import mekanism.client.gui.element.slot.GuiSequencedSlotDisplay;
import mekanism.client.gui.element.window.GuiWindow;
import mekanism.common.inventory.container.SelectedWindowData;
import mekanism.common.network.PacketUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO:所有有关无线连接的gui都应该优化，就像mek的过滤器gui那样
public class GuiViewConnection extends GuiWindow {

    static int MINER_FILTER_WIDTH = 173;

    protected GuiSequencedSlotDisplay slotDisplay;
    protected final ConnectionConfig config;

    private final Level level;

    public static GuiViewConnection create(IGuiWrapper gui, TileEntityWirelessTransmissionStation tile, Level level, ConnectionConfig config) {
        return new GuiViewConnection(gui, (gui.getXSize() - MINER_FILTER_WIDTH) / 2, 30, tile, level, config);
    }

    private GuiViewConnection(IGuiWrapper gui, int x, int y, TileEntityWirelessTransmissionStation tile, Level level, ConnectionConfig config) {
        super(gui, x, y, MINER_FILTER_WIDTH, 100, SelectedWindowData.UNSPECIFIED);
        this.level = level;
        this.config = config;
        addChild(new GuiInnerScreen(gui(), relativeX + 29, relativeY + 18, getScreenWidth(), getScreenHeight(), this::getScreenText).clearFormat());
        // 删除连接
        addChild(new TranslationButton(gui(), getLeftButtonX(), relativeY + 20 + getScreenHeight(), 60, 20, MoreMachineLang.BUTTON_DISCONNECT, (element, mouseX, mouseY) -> {
            if (config != null) {
                // TODO:删除连接但目前有bug
                PacketUtils.sendToServer(new PacketViewConnection(tile.getBlockPos(), config));
            }
            return close(element, mouseX, mouseY);
        }));
        // 高亮位置
        addChild(new TranslationButton(gui(), getLeftButtonX() + 62, relativeY + 20 + getScreenHeight(), 60, 20, MoreMachineLang.BUTTON_HIGHLIGHT, (element, mouseX, mouseY) -> {
            if (config != null) {
                // 高亮显示
                float r = 0, g = 0, b = 0;
                switch (config.type()) {
                    // 红
                    case ENERGY -> {
                        r = 1.0f;
                        g = 0.0f;
                        b = 0.0f;
                    }
                    // 蓝
                    case FLUID -> {
                        r = 0.0f;
                        g = 0.4f;
                        b = 1.0f;
                    }
                    // 黄
                    case CHEMICAL -> {
                        r = 1.0f;
                        g = 1.0f;
                        b = 0.0f;
                    }
                    // 灰
                    case ITEM -> {
                        r = 0.0f;
                        g = 1.0f;
                        b = 0.0f;
                    }
                    // 橙
                    case HEAT -> {
                        r = 1.0f;
                        g = 0.5f;
                        b = 0.0f;
                    }
                }
                BlockHighlightManager.getInstance().addHighlight(config.pos(), 400, r, g, b);
            }
            return close(element, mouseX, mouseY);
        }));
        slotDisplay = addChild(new GuiSequencedSlotDisplay(gui(), relativeX + 8, relativeY + getSlotOffset() + 1, this::getRenderStacks));
        slotDisplay.updateStackList();
    }

    protected int getSlotOffset() {
        return 18;
    }

    protected int getScreenHeight() {
        return 52;
    }

    public int getScreenWidth() {
        return 116;
    }

    protected int getLeftButtonX() {
        return relativeX + width / 2 - 61;
    }

    private List<ItemStack> getRenderStacks() {
        if (config != null) {
            return List.of(level.getBlockState(config.pos()).getBlock().asItem().getDefaultInstance());
        }
        return Collections.emptyList();
    }

    protected List<Component> getScreenText() {
        List<Component> list = new ArrayList<>();
        BlockPos pos = config.pos();
        Component connectionDescriptor = level.getBlockState(pos).getBlock().asItem().getDefaultInstance().getHoverName();
        list.add(MoreMachineLang.LIST_NAME.translate(connectionDescriptor));
        list.add(MoreMachineLang.LIST_POS.translate(EnumColor.ORANGE, MoreMachineUtils.formatPos(pos)));
        list.add(MoreMachineLang.LIST_DIRECTION.translate(EnumColor.PINK, config.direction()));
        list.add(MoreMachineLang.LIST_TYPE.translate(MoreMachineUtils.getColorByTransmitterType(config.type()), config.type()));
        return list;
    }

    @Override
    public void renderForeground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.renderForeground(guiGraphics, mouseX, mouseY);
        drawTitleText(guiGraphics, MoreMachineLang.VIEW_CONNECTION.translate(), 6);
    }
}
