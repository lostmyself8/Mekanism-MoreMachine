package com.jerry.mekmm.client.gui.element.button;

import com.jerry.mekmm.common.attachments.component.ConnectionConfig;
import com.jerry.mekmm.common.attachments.component.WirelessConnectionManager;

import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiUtils;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.button.MekanismButton;
import mekanism.client.gui.element.slot.GuiSequencedSlotDisplay;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.ObjIntConsumer;

public class ConnectListButton extends MekanismButton {

    private static final Identifier TEXTURE = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_BUTTON, "lists_holder.png");

    protected static final int TEXTURE_WIDTH = 131;// 156
    protected static final int TEXTURE_HEIGHT = 58;

    private final GuiSequencedSlotDisplay slotDisplay;
    private final GuiSlot slot;
    private final IntSupplier connectIndex;
    private final WirelessConnectionManager connectManager;
    private final ObjIntConsumer<ConnectionConfig> onPress;
    private final int index;
    private ConnectionConfig prevConnection;

    private final Level level;

    private static ConnectionConfig getConnection(WirelessConnectionManager connectManager, int index) {
        if (index >= 0 && index < connectManager.getConnectionCount()) {
            return connectManager.getConnections().get(index);
        }
        return null;
    }

    public ConnectListButton(IGuiWrapper gui, int x, int y, int index, IntSupplier connectIndex, WirelessConnectionManager connectManager, ObjIntConsumer<ConnectionConfig> onPress, Function<ConnectionConfig, List<ItemStack>> renderStackSupplier, Level level) {
        super(gui, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT / 2, CommonComponents.EMPTY, (element, mouseX, mouseY) -> {
            ConnectListButton button = (ConnectListButton) element;
            int actualIndex = button.connectIndex.getAsInt() + button.index;
            button.onPress.accept(getConnection(button.connectManager, actualIndex), actualIndex);
            return true;
        });
        this.index = index;
        this.connectIndex = connectIndex;
        this.connectManager = connectManager;
        this.onPress = onPress;
        this.level = level;
        slot = addChild(new GuiSlot(SlotType.NORMAL, gui, relativeX + 2, relativeY + 2));
        slotDisplay = addChild(new GuiSequencedSlotDisplay(gui, relativeX + 3, relativeY + 3, () -> renderStackSupplier.apply(getConnection())));
        setButtonBackground(ButtonBackground.NONE);
    }

    protected void setVisibility(boolean visible) {
        // TODO: Should we check visibility before passing things like tooltip to children? That way we don't have to
        // manually hide the children as well
        this.visible = visible;
        this.slot.visible = visible;
        this.slotDisplay.visible = visible;
    }

    protected int getActualIndex() {
        return connectIndex.getAsInt() + index;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        setVisibility(getConnection() != null);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Nullable
    protected ConnectionConfig getConnection() {
        return getConnection(connectManager, getActualIndex());
    }

    @Override
    public void drawBackground(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.blit(TEXTURE, getButtonX(), getButtonY(), getButtonWidth(), getButtonHeight(), 0, isMouseOverCheckWindows(mouseX, mouseY) ? 0 : 29, TEXTURE_WIDTH, 29, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void renderForeground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.renderForeground(guiGraphics, mouseX, mouseY);
        ConnectionConfig connection = getConnection();
        if (connection != prevConnection) {
            slotDisplay.updateStackList();
            prevConnection = connection;
        }
        // 似乎不太可能为null
        EnumColor color = switch (connection.type()) {
            case ENERGY -> EnumColor.BRIGHT_GREEN;
            case FLUID -> EnumColor.AQUA;
            case CHEMICAL -> EnumColor.YELLOW;
            case ITEM -> EnumColor.GRAY;
            case HEAT -> EnumColor.ORANGE;
        };
        GuiUtils.fill(guiGraphics, getButtonX(), getButtonY(), getButtonWidth(), getButtonHeight(), MekanismRenderer.getColorARGB(color, 0.3F));
        Component connectionDescriptor = level.getBlockState(connection.pos()).getBlock().asItem().getDefaultInstance().getHoverName();
        // 这里width代替了FilterButton里面的textWidth，但似乎可以更长
        drawScrollingString(guiGraphics, connectionDescriptor, 19, 3, TextAlignment.LEFT, titleTextColor(), 227, 3, false);
    }
}
