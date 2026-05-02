package com.jerry.mekmm.api.recipes;

import com.jerry.mekmm.Mekmm;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

@NothingNullByDefault
public abstract class RecyclerRecipe extends MekanismRecipe<SingleRecipeInput> implements Predicate<@NotNull ItemStack> {

    protected static final RandomSource RANDOM = RandomSource.create();
    private static final Holder<Item> RECYCLER = DeferredHolder.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, "recycler"));

    @Override
    public abstract boolean test(ItemStack stack);

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        // Don't match incomplete recipes or ones that don't match
        return !isIncomplete() && test(input.item());
    }

    /**
     * Gets a new chance output based on the given input.
     *
     * @param input Specific input.
     *
     * @return New chance output.
     *
     * @apiNote While Mekanism does not currently make use of the input, it is important to support it and pass the
     *          proper value in case any addons define input based
     *          outputs where things like NBT may be different.
     * @implNote The passed in input should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_ -> new")
    public abstract ChanceOutput getOutput(ItemStack input);

    /**
     * For JEI, gets the chance output representations to display.
     *
     * @return Representation of the chance output, <strong>MUST NOT</strong> be modified.
     */
    public abstract List<ItemStack> getChanceOutputDefinition();

    /**
     * Gets the chance (between 0 and 1) of the chance output being produced.
     */
    public abstract double getOutputChance();

    /**
     * Gets the input ingredient.
     */
    public abstract ItemStackIngredient getInput();

    @Override
    public boolean isIncomplete() {
        return getInput().hasNoMatchingInstances();
    }

    @Override
    public void logMissingTags() {
        getInput().logMissingTags();
    }

    @Override
    public final RecipeType<RecyclerRecipe> getType() {
        return MoreMachineRecipeTypes.TYPE_RECYCLER.value();
    }

    public String getGroup() {
        return "recycler";
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(RECYCLER);
    }

    /**
     * Represents a precalculated chance based output. This output keeps track of what random value was calculated for
     * use in comparing if the chance output should be
     * created.
     */
    public interface ChanceOutput {

        /**
         * Gets a copy of the chance output ignoring the random chance of it happening. This is mostly used for checking
         * the maximum amount we can get as a chance
         * output for purposes of seeing if we have space to process.
         *
         * @implNote return a new copy or ItemStack.EMPTY
         */
        ItemStack getMaxChanceOutput();

        /**
         * Gets a copy of the chance output if the random number generated for this output matches the chance of a
         * secondary output being produced, otherwise returns
         * an empty stack.
         *
         * @implNote return a new copy or ItemStack.EMPTY
         */
        ItemStack getChanceOutput();

        /**
         * Similar to {@link #getChanceOutput()} except that this calculates a new random number to act as if this was
         * another chance output for purposes of handling
         * multiple operations at once.
         *
         * @implNote return a new copy or ItemStack.EMPTY
         */
        ItemStack nextChanceOutput();
    }
}



