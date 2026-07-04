package com.jerry.mekmm.api.recipes.basic;

import com.jerry.mekmm.api.recipes.MoreMachineRecipeSerializers;
import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;

import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NullMarked
public class BasicPresserRecipe extends TripleItemToItemRecipe {

    protected final ItemStackIngredient first;
    protected final ItemStackIngredient second;
    protected final ItemStackIngredient third;
    protected final ItemStackTemplate output;

    public BasicPresserRecipe(ItemStackIngredient first, ItemStackIngredient second, ItemStackIngredient third, ItemStackTemplate output) {
        this.first = Objects.requireNonNull(first, "First input cannot be null.");
        this.second = Objects.requireNonNull(second, "Second input cannot be null.");
        this.third = Objects.requireNonNull(third, "Third input cannot be null.");
        this.output = Objects.requireNonNull(output, "Output cannot be null.");
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
    @Contract(value = "_, _, _ -> new", pure = true)
    public ItemStackTemplate getOutput(ItemStack first, ItemStack second, ItemStack third) {
        return output;
    }

    @Override
    public List<ItemStack> getOutputDefinition() {
        return Collections.singletonList(output.create());
    }

    public ItemStackTemplate getOutputRaw() {
        return output;
    }

    @Override
    public RecipeSerializer<BasicPresserRecipe> getSerializer() {
        return MoreMachineRecipeSerializers.PRESSING.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (getClass() != o.getClass()) {
            return false;
        }
        BasicPresserRecipe other = (BasicPresserRecipe) o;
        return first.equals(other.first) && second.equals(other.second) && third.equals(other.third) && ItemStack.isSameItemSameComponents(output.create(), other.output.create());
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(first, second, third);
        hash = 31 * hash + ItemStack.hashItemAndComponents(output.create());
        hash = 31 * hash + output.count();
        return hash;
    }
}
