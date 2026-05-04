package com.jerry.datagen.common.recipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tags.MekanismTags;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;

import java.util.*;

@NothingNullByDefault
public abstract class BaseRecipeProvider extends RecipeProvider {

    protected BaseRecipeProvider(RecipeOutput output, HolderLookup.Provider registries) {
        super(registries, output);
    }

    @Override
    protected final void buildRecipes() {
        addRecipes(registries);
        for (ISubRecipeProvider subRecipeProvider : getSubRecipeProviders()) {
            subRecipeProvider.addRecipes(output, registries);
        }
    }

    protected abstract void addRecipes(HolderLookup.Provider registries);

    /**
     * Gets all the sub/offloaded recipe providers that this recipe provider has.
     *
     * @implNote This is only called once per provider so there is no need to bother caching the list that this returns
     */
    protected List<ISubRecipeProvider> getSubRecipeProviders() {
        return Collections.emptyList();
    }

    public static Ingredient createIngredient(HolderGetter<Item> lookup, TagKey<Item> itemTag, Item... items) {
        return Ingredient.of(new OrHolderSet<>(lookup.getOrThrow(itemTag), HolderSet.direct(Arrays.stream(items).map(Item::builtInRegistryHolder).toList())));
    }

    @SafeVarargs
    public static Ingredient createIngredient(Holder<Item>... items) {
        return Ingredient.of(HolderSet.direct(Arrays.stream(items).toList()));
    }

    public static Ingredient difference(HolderSet<Item> base, Holder<Item> subtracted) {
        return DifferenceIngredient.of(Ingredient.of(base), Ingredient.of(subtracted.value()));
    }

    public static HolderSet<Item> osmiumIngot(HolderGetter<Item> lookup) {
        TagKey<Item> tag = Objects.requireNonNull(MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM));
        return lookup.getOrThrow(tag);
    }

    public static HolderSet<Item> leadIngot(HolderGetter<Item> lookup) {
        TagKey<Item> tag = Objects.requireNonNull(MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.LEAD));
        return lookup.getOrThrow(tag);
    }

    public static HolderSet<Item> tinIngot(HolderGetter<Item> lookup) {
        TagKey<Item> tag = Objects.requireNonNull(MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.TIN));
        return lookup.getOrThrow(tag);
    }
}
