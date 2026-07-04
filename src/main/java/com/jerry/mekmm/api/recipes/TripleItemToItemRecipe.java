package com.jerry.mekmm.api.recipes;

import com.jerry.mekmm.Mekmm;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public abstract class TripleItemToItemRecipe extends MekanismRecipe<RecipeInput> implements TriPredicate<ItemStack, ItemStack, ItemStack> {

    private static final Holder<Item> PRESSER = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, "presser"));

    public abstract ItemStackIngredient getFirstInput();

    public abstract ItemStackIngredient getSecondInput();

    public abstract ItemStackIngredient getThirdInput();

    @Override
    public abstract boolean test(ItemStack first, ItemStack second, ItemStack third);

    @Override
    public ItemStack assemble(RecipeInput input) {
        if (!isIncomplete() && input.size() == 3) {
            ItemStack first = input.getItem(0);
            ItemStack second = input.getItem(1);
            ItemStack third = input.getItem(2);
            if (test(first, second, third)) {
                return getOutput(first, second, third).create();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return !isIncomplete() && input.size() == 3 && test(input.getItem(0), input.getItem(1), input.getItem(2));
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public abstract ItemStackTemplate getOutput(ItemStack first, ItemStack second, ItemStack third);

    public abstract List<ItemStack> getOutputDefinition();

    @Override
    public boolean isIncomplete() {
        return getFirstInput().hasNoMatchingInstances() || getSecondInput().hasNoMatchingInstances() || getThirdInput().hasNoMatchingInstances();
    }

    @Override
    public void logMissingTags() {
        getFirstInput().logMissingTags();
        getSecondInput().logMissingTags();
        getThirdInput().logMissingTags();
    }

    @Override
    public final RecipeType<TripleItemToItemRecipe> getType() {
        return MoreMachineRecipeTypes.TYPE_PRESSING.value();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(PRESSER);
    }
}
