package com.jerry.datagen.common.recipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tags.MekanismTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;

import java.util.*;

@NothingNullByDefault
public abstract class BaseRecipeProvider extends RecipeProvider {

    protected BaseRecipeProvider(RecipeOutput output, HolderLookup.Provider registries) {
        super(registries, output);
    }

    @Override
    protected final void buildRecipes() {
        addRecipes(output, registries);
        for (ISubRecipeProvider subRecipeProvider : getSubRecipeProviders()) {
            subRecipeProvider.addRecipes(output, registries);
        }
    }

    protected abstract void addRecipes(RecipeOutput output, HolderLookup.Provider registries);

    /**
     * Gets all the sub/offloaded recipe providers that this recipe provider has.
     *
     * @implNote This is only called once per provider so there is no need to bother caching the list that this returns
     */
    protected List<ISubRecipeProvider> getSubRecipeProviders() {
        return Collections.emptyList();
    }

    public static Ingredient createIngredient(TagKey<Item> itemTag, ItemLike... items) {
        return createIngredient(Collections.singleton(itemTag), items);
    }

    public static Ingredient createIngredient(Collection<TagKey<Item>> itemTags, ItemLike... items) {
        return Ingredient.of(Arrays.stream(items));
    }

    @SafeVarargs
    public static Ingredient createIngredient(TagKey<Item>... tags) {
        throw new UnsupportedOperationException("Tag ingredients require registry lookups in 26.1.2.");
    }

    public static Ingredient difference(TagKey<Item> base, ItemLike subtracted) {
        throw new UnsupportedOperationException("Tag ingredients require registry lookups in 26.1.2.");
    }

    protected HolderSet<Item> itemTag(TagKey<Item> tag) {
        return items.getOrThrow(tag);
    }

    public static TagKey<Item> osmiumIngot() {
        return Objects.requireNonNull(MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM));
    }

    public static TagKey<Item> leadIngot() {
        return Objects.requireNonNull(MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.LEAD));
    }

    public static TagKey<Item> tinIngot() {
        return Objects.requireNonNull(MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.TIN));
    }
}

