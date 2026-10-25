package com.jerry.mekmm.client.recipe_viewer.jei;

import com.jerry.mekmm.client.PortDisplays;

import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.recipe_viewer.jei.BaseRecipeCategory;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;

import net.minecraft.client.Minecraft;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;

import java.util.List;

public abstract class PortRecipeCategory<R> extends BaseRecipeCategory<R> {

    protected PortRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<R> type) {
        super(helper, type);
    }

    protected PortRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<?> type, mezz.jei.api.recipe.types.IRecipeType<R> jeiType) {
        super(helper, jeiType, type.getTextComponent(), createIcon(helper, type), type.xOffset(), type.yOffset(), type.width(), type.height());
    }

    protected ContextMap getSlotDisplayContext() {
        return SlotDisplayContext.fromLevel(java.util.Objects.requireNonNull(Minecraft.getInstance().level));
    }

    protected IRecipeSlotBuilder initItem(IRecipeLayoutBuilder b, RecipeIngredientRole r, GuiSlot s, List<?> v) {
        return super.initItem(b, r, s, PortDisplays.of(v));
    }

    protected IRecipeSlotBuilder initItem(IRecipeLayoutBuilder b, RecipeIngredientRole r, int x, int y, List<?> v) {
        return super.initItem(b, r, x, y, PortDisplays.of(v));
    }

    protected IRecipeSlotBuilder initChemical(IRecipeLayoutBuilder b, RecipeIngredientRole r, GuiElement s, List<?> v) {
        return super.initChemical(b, r, s, PortDisplays.of(v));
    }

    protected IRecipeSlotBuilder initChemical(IRecipeLayoutBuilder b, GuiElement s, List<?> v) {
        return super.initChemical(b, RecipeIngredientRole.OUTPUT, s, PortDisplays.of(v));
    }

    protected IRecipeSlotBuilder initFluid(IRecipeLayoutBuilder b, RecipeIngredientRole r, GuiGauge<?> s, List<?> v) {
        return super.initFluid(b, r, s, PortDisplays.of(v));
    }
}
