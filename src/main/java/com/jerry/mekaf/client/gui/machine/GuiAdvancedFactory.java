package com.jerry.mekaf.client.gui.machine;

import com.jerry.mekaf.client.gui.element.tab.AdvancedFactoryGuiSortingTab;
import com.jerry.mekaf.common.tile.factory.*;
import com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase;
import com.jerry.mekaf.common.tile.factory.base.TileEntityChemicalToChemicalFactory;
import com.jerry.mekaf.common.tile.factory.base.TileEntityChemicalToItemFactory;
import com.jerry.mekaf.common.tile.factory.base.TileEntityItemToChemicalFactory;

import com.jerry.mekmm.Mekmm;

import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.client.SpecialColors;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.GuiDownArrow;
import mekanism.client.gui.element.GuiDumpButton;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiChemicalBar;
import mekanism.client.gui.element.bar.GuiFluidBar;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.interfaces.IHasDumpButton;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiAdvancedFactory extends GuiConfigurableTile<TileEntityAdvancedFactoryBase<?, ?>, MekanismTileContainer<TileEntityAdvancedFactoryBase<?, ?>>> {

    @Nullable
    private GuiDumpButton<?> dumpButton;
    private final int tankCount;

    public GuiAdvancedFactory(MekanismTileContainer<TileEntityAdvancedFactoryBase<?, ?>> container, Inventory inv, Component title) {
        super(container, inv, title);
        tankCount = tile.getTankCount();
        // 根据储罐数量决定gui布局
        imageHeight += 13 * tankCount;
        inventoryLabelY = 75 + 13 * tankCount;
        if (tile.hasExtraResourceBar()) {
            int num = tile.getBarCount() - 1;
            imageHeight += 11 + 8 * num;
            // 第一个额外资源槽加10像素，后面的加8像素
            inventoryLabelY += 10 + 8 * num;
        }

        if (tile.tier == FactoryTier.ULTIMATE) {
            imageWidth += 34;
            inventoryLabelX = 26;
        }
        // 想尝试使用Emek的gui布局，但似乎有点麻烦，还是采用原始布局吧
        if (isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的偏移，但未得到验证
            int index = tile.tier.ordinal() - 4;
            imageWidth += (36 * (index + 2)) + (2 * index);
            inventoryLabelX = (22 * (index + 2)) - (3 * index);
        }
        titleLabelY = 4;
        dynamicSlots = true;
    }

    private boolean isEMLoadAndTierOrdinalAboveOverLocked() {
        if (Mekmm.hooks.evolvedMekanism.isLoaded()) {
            return tile.tier.ordinal() >= EMFactoryTier.OVERCLOCKED.ordinal();
        }
        return false;
    }

    @Override
    protected void addGuiElements() {
        if (tile instanceof TileEntityWashingFactory) {
            addRenderableWidget(GuiSideHolder.create(this, imageWidth, 66, 57, false, true, SpecialColors.TAB_CHEMICAL_WASHER));
        }
        super.addGuiElements();
        if (tile instanceof TileEntityWashingFactory) {
            addRenderableWidget(new GuiDownArrow(this, imageWidth + 8, 90));
        }
        addRenderableWidget(new AdvancedFactoryGuiSortingTab(this, tile));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), imageWidth - 12, 16, 13 * tankCount + 52))
                .warning(WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(RecipeError.NOT_ENOUGH_ENERGY, 0));
        addRenderableWidget(new GuiEnergyTab(this, tile.getEnergyContainer(), tile::getLastUsage));

        if (tile.hasExtraResourceBar()) {
            if (tile instanceof TileEntityWashingFactory factory) {
                addRenderableWidget(new GuiFluidBar(this, GuiFluidBar.getProvider(factory.getFluidTankBar(), tile.getFluidTanks(null)), 7, 102,
                        getBarWidth(), 4, true))
                        .warning(WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                dumpButton = addRenderableWidget(new GuiDumpButton<>(this, (TileEntityAdvancedFactoryBase<?, ?> & IHasDumpButton) tile, getButtonX(), 102));
            } else if (tile instanceof TileEntityPressurizedReactingFactory factory) {
                // 出输出化学储罐
                addRenderableWidget(new GuiChemicalGauge(() -> factory.outputChemicalTank, () -> tile.getChemicalTanks(null), GaugeType.SMALL, this, 6, 44))
                        .warning(WarningType.NO_SPACE_IN_OUTPUT, factory.getWarningCheck(TileEntityPressurizedReactingFactory.NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR, 0));
                // 化学储罐条
                addRenderableWidget(new GuiChemicalBar(this, GuiChemicalBar.getProvider(factory.getChemicalTankBar(), tile.getChemicalTanks(null)), 7, 76,
                        getBarWidth(), 4, true))
                        .warning(WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                // 流体储罐条
                addRenderableWidget(new GuiFluidBar(this, GuiFluidBar.getProvider(factory.getFluidTankBar(), tile.getFluidTanks(null)), 7, 84,
                        getBarWidth(), 4, true))
                        .warning(WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                // dump按钮
                dumpButton = addRenderableWidget(new GuiDumpButton<>(this, (TileEntityAdvancedFactoryBase<?, ?> & IHasDumpButton) tile, getButtonX(), 76));
            } else {
                addRenderableWidget(new GuiChemicalBar(this, GuiChemicalBar.getProvider(tile.getChemicalTankBar(), tile.getChemicalTanks(null)),
                        7, 13 * tankCount + 76, getBarWidth(), 4, true))
                        .warning(WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                dumpButton = addRenderableWidget(new GuiDumpButton<>(this, (TileEntityAdvancedFactoryBase<?, ?> & IHasDumpButton) tile, getButtonX(), 13 * tankCount + 76));
            }
        }

        if (tile instanceof TileEntityLiquifyingFactory factory) {
            addRenderableWidget(new GuiFluidGauge(() -> factory.fluidTank, () -> factory.getFluidTanks(null), GaugeType.SMALL, this, 6, 44))
                    .warning(WarningType.NO_MATCHING_RECIPE, factory.getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, 0));
        }

        // 物品到气体的工厂只需要一排储罐，物品槽位在TileEntity中被添加
        if (tile instanceof TileEntityItemToChemicalFactory<?, ?> factory) {
            for (int i = 0; i < tile.tier.processes; i++) {
                int index = i;
                addRenderableWidget(new GuiChemicalGauge(() -> factory.outputChemicalTanks.get(index), () -> tile.getChemicalTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 57))
                        .warning(WarningType.NO_SPACE_IN_OUTPUT, factory.getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index));
            }
        }

        // 气体到物品的工厂只需要一排储罐，但储罐在上面
        if (tile instanceof TileEntityChemicalToItemFactory<?, ?> factory) {
            for (int i = 0; i < tile.tier.processes; i++) {
                int index = i;
                addRenderableWidget(new GuiChemicalGauge(() -> factory.inputChemicalTanks.get(index), () -> tile.getChemicalTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 13))
                        .warning(WarningType.NO_MATCHING_RECIPE, factory.getWarningCheck(RecipeError.NOT_ENOUGH_INPUT, index));
            }
        }

        // 气体生产气体的工厂需要两排储罐
        if (tile instanceof TileEntityChemicalToChemicalFactory<?, ?> factory) {
            for (int i = 0; i < tile.tier.processes; i++) {
                int index = i;
                addRenderableWidget(new GuiChemicalGauge(() -> factory.inputChemicalTanks.get(index), () -> tile.getChemicalTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 13))
                        .warning(WarningType.NO_MATCHING_RECIPE, factory.getWarningCheck(RecipeError.NOT_ENOUGH_LEFT_INPUT, index));
                addRenderableWidget(new GuiChemicalGauge(() -> factory.outputChemicalTanks.get(index), () -> tile.getChemicalTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 70))
                        .warning(WarningType.NO_SPACE_IN_OUTPUT, factory.getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index));
            }
        }

        // 所有工厂都有的进度条
        for (int i = 0; i < tile.tier.processes; i++) {
            int cacheIndex = i;
            addRenderableWidget(new GuiProgress(() -> tile.getScaledProgress(1, cacheIndex), ProgressType.DOWN, this, 4 + tile.getXPos(i), 13 * tile.UpperTankCount() + 33))
                    .recipeViewerCategory(tile)
                    .warning(WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT, cacheIndex));
        }
    }

    private int getBarWidth() {
        if (isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的偏移，但未得到验证
            int index = tile.tier.ordinal() - 4;
            return 210 + 38 * index;
        }
        return tile.tier == FactoryTier.ULTIMATE ? 172 : 138;
    }

    private int getButtonX() {
        if (isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的偏移，但未得到验证
            int index = tile.tier.ordinal() - 4;
            return 220 + 38 * index;
        }
        return tile.tier == FactoryTier.ULTIMATE ? 182 : 148;
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics, dumpButton == null ? getXSize() : dumpButton.getRelativeX());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
