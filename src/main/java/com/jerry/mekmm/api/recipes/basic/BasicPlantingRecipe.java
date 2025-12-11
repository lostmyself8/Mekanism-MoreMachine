package com.jerry.mekmm.api.recipes.basic;

import com.jerry.mekmm.api.recipes.MoreMachineRecipeSerializers;
import com.jerry.mekmm.api.recipes.PlantingRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.SawmillRecipe.ChanceOutput;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@NothingNullByDefault
public class BasicPlantingRecipe extends PlantingRecipe {

    public final ItemStackIngredient itemInput;
    public final ChemicalStackIngredient chemicalInput;
    public final ItemStack mainOutput;
    public final ItemStack secondaryOutput;
    protected final double secondaryChance;

    private final boolean perTickUsage;

    public BasicPlantingRecipe(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStack mainOutput, ItemStack secondaryOutput, double secondaryChance, boolean perTickUsage) {
        this.itemInput = Objects.requireNonNull(itemInput, "Input cannot be null.");
        this.chemicalInput = Objects.requireNonNull(chemicalInput, "Chemical input cannot be null.");
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

        this.perTickUsage = perTickUsage;
    }

    @Override
    public boolean test(@NotNull ItemStack itemStack, ChemicalStack chemicalStack) {
        return this.itemInput.test(itemStack) && this.chemicalInput.test(chemicalStack);
    }

    @Override
    public boolean perTickUsage() {
        return perTickUsage;
    }

    @Override
    public ItemStackIngredient getItemInput() {
        return itemInput;
    }

    @Override
    public ChemicalStackIngredient getChemicalInput() {
        return chemicalInput;
    }

    @Override
    public List<ItemStack> getMainOutputDefinition() {
        return mainOutput.isEmpty() ? Collections.emptyList() : Collections.singletonList(mainOutput);
    }

    @Override
    public List<ItemStack> getSecondaryOutputDefinition() {
        return secondaryOutput.isEmpty() ? Collections.emptyList() : Collections.singletonList(secondaryOutput);
    }

    @Override
    public double getSecondaryChance() {
        return secondaryChance;
    }

    /**
     * For Serializer use. DO NOT MODIFY RETURN VALUE.
     *
     * @return the uncopied basic output, or empty if the value is ItemStack.EMPTY
     */
    public Optional<ItemStack> getMainOutputRaw() {
        return this.mainOutput.isEmpty() ? Optional.empty() : Optional.of(this.mainOutput);
    }

    /**
     * For Serializer use. DO NOT MODIFY RETURN VALUE.
     *
     * @return the uncopied basic output
     */
    public Optional<ItemStack> getSecondaryOutputRaw() {
        return this.secondaryOutput.isEmpty() ? Optional.empty() : Optional.of(this.secondaryOutput);
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public ChanceOutput getOutput(ItemStack solid, ChemicalStack chemical) {
        return new BasicChanceOutput(secondaryChance > 0 ? RANDOM.nextDouble() : 0);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MoreMachineRecipeSerializers.PLANTING.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicPlantingRecipe other = (BasicPlantingRecipe) o;
        return secondaryChance == other.secondaryChance && itemInput.equals(other.itemInput) && chemicalInput.equals(other.chemicalInput) && ItemStack.matches(mainOutput, other.mainOutput) && ItemStack.matches(secondaryOutput, other.secondaryOutput);
    }

    @Override
    public int hashCode() {
        int hash = 31 * itemInput.hashCode() + chemicalInput.hashCode();
        hash = 31 * hash + Double.hashCode(secondaryChance);
        hash = 31 * hash + ItemStack.hashItemAndComponents(mainOutput);
        hash = 31 * hash + mainOutput.getCount();
        if (!secondaryOutput.isEmpty()) {
            hash = 31 * hash + ItemStack.hashItemAndComponents(secondaryOutput);
            hash = 31 * hash + secondaryOutput.getCount();
        }
        return hash;
    }

    public class BasicChanceOutput implements ChanceOutput {

        protected final double rand;

        protected BasicChanceOutput(double rand) {
            this.rand = rand;
        }

        @Override
        public ItemStack getMainOutput() {
            return mainOutput.copy();
        }

        @Override
        public ItemStack getMaxSecondaryOutput() {
            return secondaryChance > 0 ? secondaryOutput.copy() : ItemStack.EMPTY;
        }

        @Override
        public ItemStack getSecondaryOutput() {
            if (rand <= secondaryChance) {
                return secondaryOutput.copy();
            }
            return ItemStack.EMPTY;
        }

        @Override
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
