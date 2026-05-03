package com.jerry.mekmm.api.recipes.basic;

import com.jerry.mekmm.api.recipes.MoreMachineRecipeSerializers;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NothingNullByDefault
public class BasicRecyclerRecipe extends RecyclerRecipe {

    private final ItemStackIngredient input;
    private final ItemStack chanceOutput;
    private final double chance;

    public BasicRecyclerRecipe(ItemStackIngredient input, ItemStack chanceOutput, double chance) {
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
        return this.input.test(stack);
    }

    @Override
    @Contract(value = "_ -> new")
    public ChanceOutput getOutput(ItemStack input) {
        return new BasicChanceOutput(chance > 0 ? RANDOM.nextDouble() : 0);
    }

    /**
     * For Serializer use. DO NOT MODIFY RETURN VALUE.
     *
     * @return the uncopied basic output
     */
    public ItemStack getChanceOutputRaw() {
        return this.chanceOutput;
    }

    @Override
    public List<ItemStack> getChanceOutputDefinition() {
        return chanceOutput.isEmpty() ? Collections.emptyList() : Collections.singletonList(chanceOutput);
    }

    @Override
    public double getOutputChance() {
        return chance;
    }

    @Override
    public ItemStackIngredient getInput() {
        return input;
    }

    @Override
    public RecipeSerializer<BasicRecyclerRecipe> getSerializer() {
        return MoreMachineRecipeSerializers.RECYCLER.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicRecyclerRecipe other = (BasicRecyclerRecipe) o;
        return input.equals(other.input) && ItemStack.matches(chanceOutput, other.chanceOutput) && chance == other.chance;
    }

    @Override
    public int hashCode() {
        int hash = input.hashCode();
        hash = 31 * hash + ItemStack.hashItemAndComponents(chanceOutput);
        hash = 31 * hash + chanceOutput.getCount();
        hash = 31 * hash + Double.hashCode(chance);
        return hash;
    }

    /**
     * Represents a precalculated chance based output. This output keeps track of what random value was calculated for
     * use in comparing if the secondary output should be
     * created.
     */
    public class BasicChanceOutput implements ChanceOutput {

        protected final double rand;

        protected BasicChanceOutput(double rand) {
            this.rand = rand;
        }

        public ItemStack getMaxChanceOutput() {
            return chance > 0 ? chanceOutput.copy() : ItemStack.EMPTY;
        }

        public ItemStack getChanceOutput() {
            if (rand <= chance) {
                return chanceOutput.copy();
            }
            return ItemStack.EMPTY;
        }

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
