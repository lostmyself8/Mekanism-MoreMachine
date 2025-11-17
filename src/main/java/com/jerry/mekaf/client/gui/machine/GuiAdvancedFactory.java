package com.jerry.mekaf.client.gui.machine;

import com.jerry.mekaf.client.gui.element.tab.AFGuiSortingTab;
import com.jerry.mekaf.common.tile.TileEntityPressurizedReactingFactory;
import com.jerry.mekaf.common.tile.TileEntityWashingFactory;
import com.jerry.mekaf.common.tile.base.*;

import com.jerry.mekmm.Mekmm;

import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.client.SpecialColors;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.GuiDumpButton;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiChemicalBar;
import mekanism.client.gui.element.bar.GuiFluidBar;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.*;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.interfaces.IHasDumpButton;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;
import org.jetbrains.annotations.NotNull;

public class GuiAdvancedFactory extends GuiConfigurableTile<TileEntityAdvancedFactoryBase<?>, MekanismTileContainer<TileEntityAdvancedFactoryBase<?>>> {

    public GuiAdvancedFactory(MekanismTileContainer<TileEntityAdvancedFactoryBase<?>> container, Inventory inv, Component title) {
        super(container, inv, title);
        imageHeight += tile instanceof TileEntityPressurizedReactingFactory ? 8 : 13;
        if (tile instanceof TileEntityGasToGasFactory<?> || tile instanceof TileEntitySlurryToSlurryFactory<?>) {
            imageHeight += 13;
        }
        if (tile.hasExtrasResourceBar()) {
            imageHeight += 11;
            if (tile instanceof TileEntityGasToGasFactory<?> || tile instanceof TileEntitySlurryToSlurryFactory<?>) {
                inventoryLabelY = 111;
            } else {
                inventoryLabelY = tile instanceof TileEntityPressurizedReactingFactory ? 93 : 98;
            }
        } else {
            if (tile instanceof TileEntityGasToGasFactory<?> || tile instanceof TileEntitySlurryToSlurryFactory<?>) {
                inventoryLabelY = 103;
            } else {
                inventoryLabelY = 88;
            }
        }

        if (tile.tier == FactoryTier.ULTIMATE) {
            imageWidth += 34;
            inventoryLabelX = 26;
        }
        // 想尝试使用Emek的gui布局，但似乎有点麻烦，还是采用原始布局吧
        if (isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的便宜，但未得到验证
            int index = tile.tier.ordinal() - 4;
            imageWidth += (36 * (index + 2)) + (2 * index);
            inventoryLabelX = (22 * (index + 2)) - (3 * index);
        }
        titleLabelY = 4;
        dynamicSlots = true;
    }

    private boolean isEMLoadAndTierOrdinalAboveOverLocked() {
        if (Mekmm.hooks.EMLoaded) {
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
        // 由于没有合适的API因此化学品输入的工厂不能自动整理
        if (tile instanceof TileEntityItemToGasFactory<?> || tile instanceof TileEntityItemToMergedFactory<?> || tile instanceof TileEntityItemToFluidFactory<?> || tile instanceof TileEntityPressurizedReactingFactory) {
            addRenderableWidget(new AFGuiSortingTab(this, tile));
        }
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), imageWidth - 12, 16, getEnergyHeight()))
                .warning(WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(RecipeError.NOT_ENOUGH_ENERGY, 0));
        // 左下角能量面板
        addRenderableWidget(new GuiEnergyTab(this, tile.getEnergyContainer(), tile::getLastUsage));

        if (tile.hasExtrasResourceBar()) {
            if (tile instanceof TileEntityWashingFactory factory) {
                addRenderableWidget(new GuiFluidBar(this, GuiFluidBar.getProvider(factory.getFluidTankBar(), factory.getFluidTanks(null)), 7, 102,
                        getBarWidth(), 4, true))
                        .warning(WarningType.NO_MATCHING_RECIPE, factory.getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                addRenderableWidget(new GuiDumpButton<>(this, factory, getButtonX(), 102));
            } else if (tile instanceof TileEntityPressurizedReactingFactory factory) {
                // 出输出化学储罐
                addRenderableWidget(new GuiGasGauge(() -> factory.outputGasTank, () -> factory.getGasTanks(null), GaugeType.SMALL, this, 6, 44))
                        .warning(WarningType.NO_SPACE_IN_OUTPUT, factory.getWarningCheck(TileEntityPressurizedReactingFactory.NOT_ENOUGH_SPACE_GAS_OUTPUT_ERROR, 0));
                // 化学储罐条
                addRenderableWidget(new GuiChemicalBar<>(this, GuiChemicalBar.getProvider(factory.getGasTankBar(), factory.getGasTanks(null)), 7, 76,
                        getBarWidth(), 4, true))
                        .warning(WarningType.NO_MATCHING_RECIPE, factory.getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                // 流体储罐条
                addRenderableWidget(new GuiFluidBar(this, GuiFluidBar.getProvider(factory.getFluidTankBar(), factory.getFluidTanks(null)), 7, 84,
                        getBarWidth(), 4, true))
                        .warning(WarningType.NO_MATCHING_RECIPE, factory.getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                // dump按钮
                addRenderableWidget(new GuiDumpButton<>(this, (TileEntityAdvancedFactoryBase<?> & IHasDumpButton) factory, getButtonX(), 76));
            } else {
                addRenderableWidget(new GuiChemicalBar<>(this, GuiChemicalBar.getProvider(tile.getGasTankBar(), tile.getGasTanks(null)),
                        7, tile instanceof TileEntityGasToGasFactory<?> ? 102 : 89,
                        getBarWidth(), 4, true))
                        .warning(WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                addRenderableWidget(new GuiDumpButton<>(this, (TileEntityAdvancedFactoryBase<?> & IHasDumpButton) tile, getButtonX(),
                        tile instanceof TileEntityGasToGasFactory<?> ? 102 : 89));
            }
        }

        // 物品到气体的工厂只需要一排储罐，物品槽位在TileEntity中被添加
        if (tile instanceof TileEntityItemToGasFactory<?> factory) {
            for (int i = 0; i < tile.tier.processes; i++) {
                int index = i;
                addRenderableWidget(new GuiGasGauge(() -> factory.outputGasTanks.get(index), () -> factory.getGasTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 57))
                        .warning(WarningType.NO_SPACE_IN_OUTPUT, factory.getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index));
            }
        }

        // 物品到混合化学品的工厂只需要一排储罐，物品槽位在TileEntity中被添加
        if (tile instanceof TileEntityItemToMergedFactory<?> factory) {
            for (int i = 0; i < tile.tier.processes; i++) {
                int index = i;
                addRenderableWidget(new GuiMergedChemicalTankGauge<>(() -> factory.outputChemicalTanks.get(index), () -> factory, GaugeType.SMALL, this, factory.getXPos(index) - 1, 57))
                        .warning(WarningType.NO_SPACE_IN_OUTPUT, factory.getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index));
            }
        }

        // 气体生产气体的工厂需要两排储罐
        if (tile instanceof TileEntityGasToGasFactory<?> factory) {
            for (int i = 0; i < tile.tier.processes; i++) {
                int index = i;
                addRenderableWidget(new GuiGasGauge(() -> factory.inputGasTanks.get(index), () -> tile.getGasTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 13))
                        .warning(WarningType.NO_MATCHING_RECIPE, factory.getWarningCheck(RecipeError.NOT_ENOUGH_LEFT_INPUT, index));
                addRenderableWidget(new GuiGasGauge(() -> factory.outputGasTanks.get(index), () -> tile.getGasTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 70))
                        .warning(WarningType.NO_SPACE_IN_OUTPUT, factory.getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index));
            }
        }

        // 浆液生产浆液的工厂需要两排储罐
        if (tile instanceof TileEntitySlurryToSlurryFactory<?> factory) {
            for (int i = 0; i < tile.tier.processes; i++) {
                int index = i;
                addRenderableWidget(new GuiSlurryGauge(() -> factory.inputSlurryTanks.get(index), () -> tile.getSlurryTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 13))
                        .warning(WarningType.NO_MATCHING_RECIPE, factory.getWarningCheck(RecipeError.NOT_ENOUGH_LEFT_INPUT, index));
                addRenderableWidget(new GuiSlurryGauge(() -> factory.outputSlurryTanks.get(index), () -> tile.getSlurryTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 70))
                        .warning(WarningType.NO_SPACE_IN_OUTPUT, factory.getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index));
            }
        }

        // 混合化学品到物品的工厂只需要一排储罐，但储罐在上面
        if (tile instanceof TileEntityMergedToItemFactory<?> factory) {
            for (int i = 0; i < tile.tier.processes; i++) {
                int index = i;
                addRenderableWidget(new GuiMergedChemicalTankGauge<>(() -> factory.inputChemicalTanks.get(index), () -> factory, GaugeType.SMALL, this, factory.getXPos(index) - 1, 13))
                        .warning(WarningType.NO_MATCHING_RECIPE, factory.getWarningCheck(RecipeError.NOT_ENOUGH_INPUT, index));
            }
        }

        // 物品到流体体的工厂只需要一排储罐，物品槽位在TileEntity中被添加
        if (tile instanceof TileEntityItemToFluidFactory<?> factory) {
            for (int i = 0; i < tile.tier.processes; i++) {
                int index = i;
                addRenderableWidget(new GuiFluidGauge(() -> factory.outputFluidTanks.get(index), () -> factory.getFluidTanks(null), GaugeType.SMALL, this, factory.getXPos(index) - 1, 57))
                        .warning(WarningType.NO_SPACE_IN_OUTPUT, factory.getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index));
            }
        }

        // 进度条
        for (int i = 0; i < tile.tier.processes; i++) {
            int cacheIndex = i;
            addProgress(new GuiProgress(() -> tile.getScaledProgress(1, cacheIndex), ProgressType.DOWN, this, 4 + tile.getXPos(i), getProgressYPos()))
                    // Only can happen if recipes change because inputs are sanitized in the factory based on the output
                    .warning(WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT, cacheIndex));
        }
    }

    private int getEnergyHeight() {
        if (tile instanceof TileEntityGasToGasFactory<?> || tile instanceof TileEntitySlurryToSlurryFactory<?>) {
            return 78;
        } else if (tile instanceof TileEntityMergedToItemFactory<?> || tile instanceof TileEntityItemToMergedFactory<?> || tile instanceof TileEntityItemToGasFactory<?> || tile instanceof TileEntityItemToFluidFactory<?>) {
            return 65;
        } else {
            return 52;
        }
    }

    private int getProgressYPos() {
        if (tile instanceof TileEntityGasToGasFactory<?> || tile instanceof TileEntitySlurryToSlurryFactory<?> || tile instanceof TileEntityMergedToItemFactory<?>) {
            return 46;
        } else {
            return 33;
        }
    }

    private GuiProgress addProgress(GuiProgress progressBar) {
        MekanismJEIRecipeType<?> jeiType = switch (tile.getAdvancedFactoryType()) {
            case OXIDIZING -> MekanismJEIRecipeType.OXIDIZING;
            case CHEMICAL_INFUSING -> MekanismJEIRecipeType.CHEMICAL_INFUSING;
            case DISSOLVING -> MekanismJEIRecipeType.DISSOLUTION;
            case WASHING -> MekanismJEIRecipeType.WASHING;
            case CRYSTALLIZING -> MekanismJEIRecipeType.CRYSTALLIZING;
            case PRESSURISED_REACTING -> MekanismJEIRecipeType.REACTION;
            case CENTRIFUGING -> MekanismJEIRecipeType.CENTRIFUGING;
            case LIQUIFYING -> MekanismJEIRecipeType.NUTRITIONAL_LIQUIFICATION;
        };
        return addRenderableWidget(progressBar.jeiCategories(jeiType));
    }

    private int getBarWidth() {
        if (isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的便宜，但未得到验证
            int index = tile.tier.ordinal() - 4;
            return 210 + 38 * index;
        }
        return tile.tier == FactoryTier.ULTIMATE ? 172 : 138;
    }

    private int getButtonX() {
        if (isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的便宜，但未得到验证
            int index = tile.tier.ordinal() - 4;
            return 220 + 38 * index;
        }
        return tile.tier == FactoryTier.ULTIMATE ? 182 : 148;
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        drawString(guiGraphics, playerInventoryTitle, inventoryLabelX, inventoryLabelY, titleTextColor());
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
