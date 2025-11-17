package com.jerry.mekmm.api.recipes;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@NothingNullByDefault
public abstract class RecyclerRecipe extends MekanismRecipe implements Predicate<@NotNull ItemStack> {

    protected static final RandomSource RANDOM = RandomSource.create();

    private final ItemStackIngredient input;
    private final ItemStack chanceOutput;
    private final double chance;

    public RecyclerRecipe(ResourceLocation id, ItemStackIngredient input, ItemStack chanceOutput, double chance) {
        super(id);
        this.input = Objects.requireNonNull(input, "Input cannot be null.");
        Objects.requireNonNull(chanceOutput, "Output cannot be null.");
        if (chanceOutput.isEmpty()) {
            throw new IllegalArgumentException("Output cannot be null.");
        } else if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Output chance must be at least zero and at most one.");
        }
        this.chanceOutput = chanceOutput.copy();
        this.chance = chance;
    }

    @Override
    public boolean test(ItemStack stack) {
        return input.test(stack);
    }

    /**
     * Gets a new chance output based on the given input.
     *
     * @param input Specific input.
     * @return New chance output.
     * @apiNote While Mekanism does not currently make use of the input, it is important to support it and pass the
     *          proper value in case any addons define input based
     *          outputs where things like NBT may be different.
     * @implNote The passed in input should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_ -> new")
    public ChanceOutput getOutput(ItemStack input) {
        return new ChanceOutput(chance > 0 ? RANDOM.nextDouble() : 0);
    }

    /**
     * For JEI, gets the chance output representations to display.
     *
     * @return Representation of the chance output, <strong>MUST NOT</strong> be modified.
     */
    public List<ItemStack> getChanceOutputDefinition() {
        return chanceOutput.isEmpty() ? Collections.emptyList() : Collections.singletonList(chanceOutput);
    }

    /**
     * Gets the chance (between 0 and 1) of the chance output being produced.
     */
    public double getOutputChance() {
        return chance;
    }

    /**
     * Gets the input ingredient.
     */
    public ItemStackIngredient getInput() {
        return input;
    }

    @Override
    public boolean isIncomplete() {
        return getInput().hasNoMatchingInstances();
    }

    @Override
    public void logMissingTags() {
        input.logMissingTags();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        input.write(buffer);
        buffer.writeItem(chanceOutput);
        buffer.writeDouble(chance);
    }

    /**
     * Represents a precalculated chance based output. This output keeps track of what random value was calculated for
     * use in comparing if the chance output should be
     * created.
     */
    public class ChanceOutput {

        protected final double rand;

        protected ChanceOutput(double rand) {
            this.rand = rand;
        }

        /**
         * Gets a copy of the chance output ignoring the random chance of it happening. This is mostly used for checking
         * the maximum amount we can get as a chance
         * output for purposes of seeing if we have space to process.
         *
         * @implNote return a new copy or ItemStack.EMPTY
         */
        public ItemStack getMaxChanceOutput() {
            return chance > 0 ? chanceOutput.copy() : ItemStack.EMPTY;
        }

        /**
         * Gets a copy of the chance output if the random number generated for this output matches the chance of a
         * secondary output being produced, otherwise returns
         * an empty stack.
         *
         * @implNote return a new copy or ItemStack.EMPTY
         */
        public ItemStack getChanceOutput() {
            if (rand <= chance) {
                return chanceOutput.copy();
            }
            return ItemStack.EMPTY;
        }

        /**
         * Similar to {@link #getChanceOutput()} except that this calculates a new random number to act as if this was
         * another chance output for purposes of handling
         * multiple operations at once.
         *
         * @implNote return a new copy or ItemStack.EMPTY
         */
        public ItemStack nextChanceOutput() {
            if (chance > 0) {
                double rand = RANDOM.nextDouble();
                if (rand <= chance) {
                    return chanceOutput.copy();
                }
            }
            return ItemStack.EMPTY;
        }
    }
}
