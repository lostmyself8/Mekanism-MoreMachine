package com.jerry.mekmm.api.recipes.basic;

import com.jerry.mekmm.api.recipes.MoreMachineRecipeSerializers;
import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NothingNullByDefault
public abstract class BasicTripleItemToItemRecipe extends TripleItemToItemRecipe {

    protected final RecipeType<TripleItemToItemRecipe> recipeType;
    protected final ItemStackIngredient first;
    protected final ItemStackIngredient second;
    protected final ItemStackIngredient third;
    protected final ItemStack output;

    public BasicTripleItemToItemRecipe(ItemStackIngredient first, ItemStackIngredient second, ItemStackIngredient third, ItemStack output, RecipeType<TripleItemToItemRecipe> recipeType) {
        this.recipeType = Objects.requireNonNull(recipeType, "Recipe type cannot be null");
        this.first = Objects.requireNonNull(first, "First input cannot be null.");
        this.second = Objects.requireNonNull(second, "Second input cannot be null.");
        this.third = Objects.requireNonNull(third, "Third input cannot be null.");
        Objects.requireNonNull(output, "Output cannot be null.");
        if (output.isEmpty()) {
            throw new IllegalArgumentException("Output cannot be empty.");
        }
        this.output = output.copy();
    }

    @Override
    public final RecipeType<TripleItemToItemRecipe> getType() {
        return recipeType;
    }

    @Override
    public ItemStackIngredient getFirstInput() {
        return first;
    }

    @Override
    public ItemStackIngredient getSecondInput() {
        return second;
    }

    @Override
    public ItemStackIngredient getThirdInput() {
        return third;
    }

    @Override
    public boolean test(ItemStack first, ItemStack second, ItemStack third) {
        return this.first.test(first) && this.second.test(second) && this.third.test(third);
    }

    @Override
    public List<ItemStack> getOutputDefinition() {
        return Collections.singletonList(output);
    }

    @Override
    @Contract(value = "_, _, _ -> new", pure = true)
    public ItemStack getOutput(ItemStack first, ItemStack second, ItemStack third) {
        return output.copy();
    }

    @Override
    public ItemStack getResultItem(Provider provider) {
        return output.copy();
    }

    public ItemStack getOutputRaw() {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MoreMachineRecipeSerializers.PRESSING.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicTripleItemToItemRecipe other = (BasicTripleItemToItemRecipe) o;
        return first.equals(other.first) && second.equals(other.second) && third.equals(other.third) && ItemStack.matches(output, other.output);
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(first, second, third);
        hash = 31 * hash + ItemStack.hashItemAndComponents(output);
        hash = 31 * hash + output.getCount();
        return hash;
    }
}
