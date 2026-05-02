package com.jerry.datagen.common.recipe.builder;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public abstract class BaseRecipeBuilder<BUILDER extends BaseRecipeBuilder<BUILDER>> extends MekanismRecipeBuilder<BUILDER> {

    protected final Item result;
    private final ItemStackTemplate resultStack;
    protected final int count;
    protected RecipeCategory category = RecipeCategory.MISC;
    @Nullable
    protected String group;

    protected BaseRecipeBuilder(ItemLike result, int count) {
        this.result = result.asItem();
        this.resultStack = new ItemStackTemplate(this.result.builtInRegistryHolder(), count);
        this.count = count;
    }

    @SuppressWarnings("unchecked")
    private BUILDER self() {
        return (BUILDER) this;
    }

    public BUILDER group(String group) {
        this.group = group;
        return self();
    }

    public BUILDER category(RecipeCategory category) {
        this.category = category;
        return self();
    }

    public void build(RecipeOutput recipeOutput) {
        save(recipeOutput);
    }

    public void build(RecipeOutput recipeOutput, Identifier id) {
        save(recipeOutput, id);
    }

    protected ItemStackTemplate resultStack() {
        return resultStack;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(resultStack);
    }
}
