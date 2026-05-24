package com.jerry.mekmm.api.recipes;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriPredicate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NothingNullByDefault
public abstract class TripleItemToItemRecipe extends MekanismRecipe<RecipeInput> implements TriPredicate<@NotNull ItemStack, @NotNull ItemStack, @NotNull ItemStack> {

    /**
     * Gets the first item input ingredient.
     */
    public abstract ItemStackIngredient getFirstInput();

    /**
     * Gets the second item input ingredient.
     */
    public abstract ItemStackIngredient getSecondInput();

    /**
     * Gets the third item input ingredient.
     */
    public abstract ItemStackIngredient getThirdInput();

    @Override
    public abstract boolean test(ItemStack first, ItemStack second, ItemStack third);

    @Override
    public boolean matches(RecipeInput input, Level level) {
        // Don't match incomplete recipes or ones that don't match
        return !isIncomplete() && test(input.getItem(0), input.getItem(1), input.getItem(2));
    }

    /**
     * For JEI, gets the output representations to display.
     *
     * @return Representation of the output, <strong>MUST NOT</strong> be modified.
     */
    public abstract List<ItemStack> getOutputDefinition();

    /**
     * Gets a new output based on the given inputs.
     *
     * @param first  Specific first item input.
     * @param second Specific second item input.
     * @param third  Specific third item input.
     *
     * @return New output.
     *
     * @apiNote While Mekanism does not currently make use of the inputs, it is important to support it and pass the
     *          proper value in case any addons define input based
     *          outputs where things like NBT may be different.
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public abstract ItemStack getOutput(ItemStack first, ItemStack second, ItemStack third);

    @Override
    public abstract ItemStack getResultItem(Provider provider);

    @Override
    public boolean isIncomplete() {
        return getFirstInput().hasNoMatchingInstances() || getSecondInput().hasNoMatchingInstances() || getThirdInput().hasNoMatchingInstances();
    }
}
