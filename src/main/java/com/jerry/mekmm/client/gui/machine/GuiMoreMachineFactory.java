package com.jerry.mekmm.client.gui.machine;

import com.jerry.mekmm.client.gui.element.tab.MoreMachineGuiSortingTab;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;
import com.jerry.mekmm.common.tile.factory.TileEntityPlantingFactory;
import com.jerry.mekmm.common.tile.factory.TileEntityReplicatingFactory;

import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.GuiDumpButton;
import mekanism.client.gui.element.bar.GuiChemicalBar;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.interfaces.IHasDumpButton;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiMoreMachineFactory extends GuiConfigurableTile<TileEntityMoreMachineFactory<?>, MekanismTileContainer<TileEntityMoreMachineFactory<?>>> {

    @Nullable
    private GuiDumpButton<?> dumpButton;

    private static int getImageHeight(TileEntityMoreMachineFactory<?> tile) {
        int imageHeight = DEFAULT_IMAGE_HEIGHT;
        if (tile.hasSecondaryResourceBar()) {
            imageHeight += 11;
        } else if (tile instanceof TileEntityPlantingFactory) {
            imageHeight += 20;
        }
        return imageHeight;
    }

    private static int getImageWidth(TileEntityMoreMachineFactory<?> tile) {
        int imageWidth = DEFAULT_IMAGE_WIDTH;
        if (tile.tier == FactoryTier.ULTIMATE) {
            imageWidth += 34;
        }
        if (tile.isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的便宜，但未得到验证
            int index = tile.tier.ordinal() - 4;
            imageWidth += (36 * (index + 2)) + (2 * index);
        }
        return imageWidth;
    }

    public GuiMoreMachineFactory(MekanismTileContainer<TileEntityMoreMachineFactory<?>> container, Inventory inv, Component title) {
        super(container, inv, title, getImageWidth(container.getTileEntity()), getImageHeight(container.getTileEntity()));
        if (tile.hasSecondaryResourceBar()) {
            inventoryLabelY = 85;
            if (tile instanceof TileEntityPlantingFactory) {
                inventoryLabelY = 105;
            }
        } else {
            inventoryLabelY = 75;
        }

        if (tile.tier == FactoryTier.ULTIMATE) {
            inventoryLabelX = 26;
        }
        // 想尝试使用Emek的gui布局，但似乎有点麻烦，还是采用原始布局吧
        if (tile.isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的便宜，但未得到验证
            int index = tile.tier.ordinal() - 4;
            inventoryLabelX = (22 * (index + 2)) - (3 * index);
        }
        titleLabelY = 4;
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new MoreMachineGuiSortingTab(this, tile));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), imageWidth - 12, 16, tile instanceof TileEntityPlantingFactory ? 73 : 52))
                .warning(WarningTracker.WarningType.NOT_ENOUGH_ENERGY, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY, 0));

        addRenderableWidget(new GuiEnergyTab(this, tile.getEnergyContainer(), tile::getLastUsage));
        if (tile.hasSecondaryResourceBar()) {
            if (tile instanceof TileEntityPlantingFactory factory) {
                addRenderableWidget(new GuiChemicalBar(this, GuiChemicalBar.getProvider(factory.getChemicalTank(), tile.getChemicalTanks(null)), 7, 96,
                        getBarWidth(), 4, true))
                        .warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                dumpButton = addRenderableWidget(new GuiDumpButton<>(this, (TileEntityMoreMachineFactory<?> & IHasDumpButton) tile, getButtonX(), 96));
            }
            if (tile instanceof TileEntityReplicatingFactory factory) {
                addRenderableWidget(new GuiChemicalBar(this, GuiChemicalBar.getProvider(factory.getChemicalTank(), tile.getChemicalTanks(null)), 7, 76,
                        getBarWidth(), 4, true))
                        .warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_SECONDARY_INPUT, 0));
                dumpButton = addRenderableWidget(new GuiDumpButton<>(this, (TileEntityMoreMachineFactory<?> & IHasDumpButton) tile, getButtonX(), 76));
            }
        }

        int baseX = tile.tier == FactoryTier.BASIC ? 55 : tile.tier == FactoryTier.ADVANCED ? 35 : tile.tier == FactoryTier.ELITE ? 29 : 27;
        int baseXMult = tile.tier == FactoryTier.BASIC ? 38 : tile.tier == FactoryTier.ADVANCED ? 26 : 19;
        for (int i = 0; i < tile.tier.processes; i++) {
            int cacheIndex = i;
            addRenderableWidget(new GuiProgress(() -> tile.getScaledProgress(1, cacheIndex), ProgressType.DOWN, this, 4 + baseX + (i * baseXMult), 33))
                    .recipeViewerCategory(tile)
                    // Only can happen if recipes change because inputs are sanitized in the factory based on the output
                    .warning(WarningTracker.WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(CachedRecipe.OperationTracker.RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT, cacheIndex));
        }
    }

    private int getBarWidth() {
        if (tile.isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的便宜，但未得到验证
            int index = tile.tier.ordinal() - 4;
            return 210 + 38 * index;
        }
        return tile.tier == FactoryTier.ULTIMATE ? 172 : 138;
    }

    private int getButtonX() {
        if (tile.isEMLoadAndTierOrdinalAboveOverLocked()) {
            // 这里采用mekE的布局公式，但要记得减去4，因为mekE是从0开始的
            // 这两个公式似乎并非完美，在index过大时可能会导致有细微的便宜，但未得到验证
            int index = tile.tier.ordinal() - 4;
            return 220 + 38 * index;
        }
        return tile.tier == FactoryTier.ULTIMATE ? 182 : 148;
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
        renderTitleText(GuiGraphicsExtractor);
        renderInventoryText(GuiGraphicsExtractor, dumpButton == null ? getImageWidth() : dumpButton.getRelativeX());
        super.drawForegroundText(GuiGraphicsExtractor, mouseX, mouseY);
    }
}
