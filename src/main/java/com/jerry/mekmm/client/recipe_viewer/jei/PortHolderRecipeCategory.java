package com.jerry.mekmm.client.recipe_viewer.jei;

import mekanism.client.recipe_viewer.jei.MekanismJEI;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.mojang.serialization.Codec;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IRecipeManager;

public abstract class PortHolderRecipeCategory<R extends Recipe<?>> extends PortRecipeCategory<RecipeHolder<R>> {

    protected PortHolderRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<R> type) {
        super(helper, type, MekanismJEI.holderRecipeType(type));
    }

    @Override
    public Identifier getIdentifier(RecipeHolder<R> recipe) {
        return recipe.id().identifier();
    }

    @Override
    public Codec<RecipeHolder<R>> getCodec(ICodecHelper helper, IRecipeManager manager) {
        return helper.getRecipeHolderCodec();
    }
}
