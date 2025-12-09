package com.jerry.mekmm.api.recipes;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiPredicate;

/**
 * Represents a recipe that can be used in the Planting Station.
 *
 * @author Jerry
 */
@NothingNullByDefault
public abstract class PlantingRecipe extends MekanismRecipe implements BiPredicate<@NotNull ItemStack, @NotNull GasStack> {

    protected static final Random RANDOM = new Random();
    @Getter
    private final ItemStackIngredient itemInput;
    @Getter
    private final GasStackIngredient gasInput;
    @Getter
    public final ItemStack mainOutput;
    public final ItemStack secondaryOutput;
    @Getter
    private final double secondaryChance;

    /**
     * @param id Recipe name.
     */
    protected PlantingRecipe(ResourceLocation id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack mainOutput, ItemStack secondaryOutput, double secondaryChance) {
        super(id);
        this.itemInput = Objects.requireNonNull(itemInput, "Input cannot be null.");
        this.gasInput = Objects.requireNonNull(gasInput, "Gas input cannot be null.");
        Objects.requireNonNull(mainOutput, "Main output cannot be null.");
        Objects.requireNonNull(secondaryOutput, "Secondary output cannot be null.");
        if (mainOutput.isEmpty() && secondaryOutput.isEmpty()) {
            throw new IllegalArgumentException("At least one output must not be empty.");
        } else if (secondaryChance < 0 || secondaryChance > 1) {
            throw new IllegalArgumentException("Secondary output chance must be at least zero and at most one.");
        } else if (mainOutput.isEmpty()) {
            if (secondaryChance == 0 || secondaryChance == 1) {
                throw new IllegalArgumentException("Secondary output must have a chance greater than zero and less than one.");
            }
        } else if (secondaryOutput.isEmpty() && secondaryChance != 0) {
            throw new IllegalArgumentException("If there is no secondary output, the chance of getting the secondary output should be zero.");
        }
        this.mainOutput = mainOutput.copy();
        this.secondaryOutput = secondaryOutput.copy();
        this.secondaryChance = secondaryChance;
    }

    @Override
    public boolean test(ItemStack itemStack, GasStack gasStack) {
        return itemInput.test(itemStack) && gasInput.test(gasStack);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public PlantingStationRecipeOutput getOutput(ItemStack item, GasStack gas) {
        return new PlantingStationRecipeOutput(secondaryChance > 0 ? RANDOM.nextDouble() : 0);
    }

    /**
     * For JEI, gets the main output representations to display.
     *
     * @return Representation of the main output, <strong>MUST NOT</strong> be modified.
     */
    public List<ItemStack> getMainOutputDefinition() {
        return mainOutput.isEmpty() ? Collections.emptyList() : Collections.singletonList(mainOutput);
    }

    /**
     * For JEI, gets the secondary output representations to display.
     *
     * @return Representation of the secondary output, <strong>MUST NOT</strong> be modified.
     */
    public List<ItemStack> getSecondaryOutputDefinition() {
        return secondaryOutput.isEmpty() ? Collections.emptyList() : Collections.singletonList(secondaryOutput);
    }

    public ItemStack getSecondaryOutput() {
        return secondaryOutput.isEmpty() ? ItemStack.EMPTY : secondaryOutput;
    }

    @Override
    public boolean isIncomplete() {
        return getItemInput().hasNoMatchingInstances() && getGasInput().hasNoMatchingInstances();
    }

    @Override
    public void logMissingTags() {
        itemInput.logMissingTags();
        gasInput.logMissingTags();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        itemInput.write(buffer);
        gasInput.write(buffer);
        buffer.writeItem(mainOutput);
        buffer.writeItem(secondaryOutput);
        buffer.writeDouble(secondaryChance);
    }

    /**
     * Represents a precalculated chance based output. This output keeps track of what random value was calculated for
     * use in comparing if the secondary output should be
     * created.
     */
    public class PlantingStationRecipeOutput {

        protected final double rand;

        protected PlantingStationRecipeOutput(double rand) {
            this.rand = rand;
        }

        /**
         * Gets a copy of the main output of this recipe. This may be empty if there is only a secondary chance based
         * output.
         */
        public ItemStack getMainOutput() {
            return mainOutput.copy();
        }

        /**
         * Gets a copy of the secondary output ignoring the random chance of it happening. This is mostly used for
         * checking the maximum amount we can get as a secondary
         * output for purposes of seeing if we have space to process.
         */
        public ItemStack getMaxSecondaryOutput() {
            return secondaryChance > 0 ? secondaryOutput.copy() : ItemStack.EMPTY;
        }

        /**
         * Gets a copy of the secondary output if the random number generated for this output matches the chance of a
         * secondary output being produced, otherwise returns
         * an empty stack.
         */
        public ItemStack getSecondaryOutput() {
            if (rand <= secondaryChance) {
                return secondaryOutput.copy();
            }
            return ItemStack.EMPTY;
        }

        /**
         * Similar to {@link #getSecondaryOutput()} except that this calculates a new random number to act as if this
         * was another chance output for purposes of handling
         * multiple operations at once.
         */
        public ItemStack nextSecondaryOutput() {
            if (secondaryChance > 0) {
                double rand = RANDOM.nextDouble();
                if (rand <= secondaryChance) {
                    return secondaryOutput.copy();
                }
            }
            return ItemStack.EMPTY;
        }
    }
}
