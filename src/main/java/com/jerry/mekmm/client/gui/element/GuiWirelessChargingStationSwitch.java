package com.jerry.mekmm.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiDigitalSwitch;
import mekanism.client.gui.element.GuiElement;
import mekanism.common.MekanismLang;
import mekanism.common.registries.MekanismSounds;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class GuiWirelessChargingStationSwitch extends GuiElement {

    public static final int BUTTON_SIZE_X = 15, BUTTON_SIZE_Y = 8;

    private final SwitchType type;
    private final Identifier icon;
    private final BooleanSupplier stateSupplier;
    private final IClickable onToggle;

    public GuiWirelessChargingStationSwitch(IGuiWrapper gui, int x, int y, Identifier icon, BooleanSupplier stateSupplier, IClickable onToggle, SwitchType type) {
        super(gui, x, y, type.width, type.height);
        this.type = type;
        this.icon = icon;
        this.stateSupplier = stateSupplier;
        this.onToggle = onToggle;
        this.clickSound = () -> this.stateSupplier.getAsBoolean() ? MekanismSounds.BEEP_OFF.get() : MekanismSounds.BEEP_ON.get();
        this.clickVolume = 1.0F;
    }

    @Override
    public void drawBackground(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);
        boolean state = stateSupplier.getAsBoolean();
        GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, state ? GuiDigitalSwitch.SELECTED : GuiDigitalSwitch.UNSELECTED, relativeX + type.switchX, relativeY + type.switchY, BUTTON_SIZE_X, BUTTON_SIZE_Y);
        GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, state ? GuiDigitalSwitch.UNSELECTED : GuiDigitalSwitch.SELECTED, relativeX + type.switchX, relativeY + type.switchY + BUTTON_SIZE_Y + 1, BUTTON_SIZE_X, BUTTON_SIZE_Y);
        GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, icon, relativeX + type.iconX, relativeY + type.iconY, 5, 5);
    }

    @Override
    public void renderForeground(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
        super.renderForeground(GuiGraphicsExtractor, mouseX, mouseY);
        drawScaledScrollingString(GuiGraphicsExtractor, MekanismLang.ON.translate(), type.switchX, type.switchY, TextAlignment.CENTER, 0xFF101010, BUTTON_SIZE_X, 1, false, 0.6F);
        drawScaledScrollingString(GuiGraphicsExtractor, MekanismLang.OFF.translate(), type.switchX, type.switchY + BUTTON_SIZE_Y + 1, TextAlignment.CENTER, 0xFF101010, BUTTON_SIZE_X, 1, false, 0.6F);
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent event, boolean doubleClick) {
        onToggle.onClick(this, event, doubleClick);
    }

    public enum SwitchType {

        LOWER_ICON(BUTTON_SIZE_X, BUTTON_SIZE_Y * 2 + 9, 0, 0, 5, 18);

        private final int iconX, iconY;
        private final int width, height;
        private final int switchX, switchY;

        SwitchType(int width, int height, int switchX, int switchY, int iconX, int iconY) {
            this.width = width;
            this.height = height;
            this.iconX = iconX;
            this.iconY = iconY;
            this.switchX = switchX;
            this.switchY = switchY;
        }
    }
}
