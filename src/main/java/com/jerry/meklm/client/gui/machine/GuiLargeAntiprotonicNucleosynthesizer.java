package com.jerry.meklm.client.gui.machine;

import com.jerry.meklm.common.tile.machine.TileEntityLargeAntiprotonicNucleosynthesizer;

import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.client.gui.element.bar.GuiDynamicHorizontalRateBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiEnergyGauge;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.render.lib.effect.BoltRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.lib.Color;
import mekanism.common.lib.effect.BoltEffect;
import mekanism.common.util.text.TextUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class GuiLargeAntiprotonicNucleosynthesizer extends GuiConfigurableTile<TileEntityLargeAntiprotonicNucleosynthesizer, MekanismTileContainer<TileEntityLargeAntiprotonicNucleosynthesizer>> {

    private static final Vec3 from = new Vec3(47, 50, 0), to = new Vec3(147, 50, 0);
    private static final BoltEffect.BoltRenderInfo boltRenderInfo = new BoltEffect.BoltRenderInfo().color(Color.rgbad(0.45F, 0.45F, 0.5F, 1));

    private final BoltRenderer bolt = new BoltRenderer();
    private final Supplier<BoltEffect> boltSupplier = () -> new BoltEffect(boltRenderInfo, from, to, 15)
            .count(Math.min(Mth.ceil(tile.getProcessRate() / 8F), 20))
            .size(1)
            .lifespan(1)
            .spawn(BoltEffect.SpawnFunction.CONSECUTIVE)
            .fade(BoltEffect.FadeFunction.NONE);
    private GuiInnerScreen screen;

    public GuiLargeAntiprotonicNucleosynthesizer(MekanismTileContainer<TileEntityLargeAntiprotonicNucleosynthesizer> container, Inventory inv, Component title) {
        super(container, inv, title, DEFAULT_IMAGE_WIDTH + 20, DEFAULT_IMAGE_HEIGHT + 27);
        dynamicSlots = true;
        inventoryLabelY = imageHeight - 93;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        screen = addRenderableWidget(new GuiInnerScreen(this, 45, 18, 104, 68).recipeViewerCategory(tile));
        addRenderableWidget(new GuiEnergyTab(this, tile.getEnergyContainer(), tile::getEnergyUsed));
        addRenderableWidget(new GuiChemicalGauge(() -> tile.gasTank, () -> tile.getChemicalTanks(null), GaugeType.SMALL_MED, this, 5, 18))
                .warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_SECONDARY_INPUT));
        addRenderableWidget(new GuiEnergyGauge(tile.getEnergyContainer(), GaugeType.SMALL_MED, this, 172, 18))
                .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY));
        addRenderableWidget(new GuiDynamicHorizontalRateBar(this, new GuiBar.IBarInfoHandler() {

            @Override
            public Component getTooltip() {
                return MekanismLang.PROGRESS.translate(TextUtils.getPercent(tile.getScaledProgress()));
            }

            @Override
            public double getLevel() {
                return Math.min(1, tile.getScaledProgress());
            }
        }, 5, 88, 183, Color.ColorFunction.scale(Color.rgbi(60, 45, 74), Color.rgbi(100, 30, 170))))
                .warning(WarningTracker.WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
        renderTitleText(GuiGraphicsExtractor);
        renderInventoryText(GuiGraphicsExtractor);
        screen.drawScrollingString(GuiGraphicsExtractor, MekanismLang.PROCESS_RATE.translate(TextUtils.getPercent(tile.getProcessRate())), 0,
                screen.getHeight() - font().lineHeight - 2, TextAlignment.CENTER, screenTextColor(), 2, false);
        super.drawForegroundText(GuiGraphicsExtractor, mouseX, mouseY);
        // PoseStack pose = GuiGraphicsExtractor.pose();
        // pose.pushPose();
        // pose.translate(0, 0, 100);
        // MultiBufferSource.BufferSource renderer = GuiGraphicsExtractor.bufferSource();
        // float partialTicks = MekanismRenderer.getPartialTick();
        // bolt.update(this, boltSupplier.get(), partialTicks);
        // bolt.render(partialTicks, pose, renderer);
        // renderer.endBatch(MekanismRenderType.MEK_LIGHTNING);
        // pose.popPose();
    }
}
