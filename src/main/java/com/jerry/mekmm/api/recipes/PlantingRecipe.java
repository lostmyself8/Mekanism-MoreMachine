package com.jerry.mekmm.api.recipes;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Represents a recipe that can be used in the Planting Station.
 *
 * @author Jerry
 */
@NothingNullByDefault
public abstract class PlantingRecipe extends MekanismRecipe implements BiPredicate<@NotNull ItemStack, @NotNull GasStack> {

    private final ItemStackIngredient itemInput;
    private final GasStackIngredient gasInput;
    public final ItemStack mainOutput;
    public final ItemStack secondaryOutput;

    /**
     * @param id Recipe name.
     */
    protected PlantingRecipe(ResourceLocation id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack mainOutput, ItemStack secondaryOutput) {
        super(id);
        this.itemInput = Objects.requireNonNull(itemInput, "Input cannot be null.");
        this.gasInput = Objects.requireNonNull(gasInput, "Gas input cannot be null.");
        Objects.requireNonNull(mainOutput, "Main output cannot be null.");
        Objects.requireNonNull(secondaryOutput, "Secondary output cannot be null.");
        if (mainOutput.isEmpty()) {
            throw new IllegalArgumentException("Main output cannot be null.");
        }
        this.mainOutput = mainOutput.copy();
        this.secondaryOutput = secondaryOutput.copy();
    }

    @Override
    public boolean test(ItemStack itemStack, GasStack gasStack) {
        return itemInput.test(itemStack) && gasInput.test(gasStack);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public PlantingStationRecipeOutput getOutput(ItemStack item, GasStack gas) {
        return new PlantingStationRecipeOutput(mainOutput.copy(), secondaryOutput.copy());
    }

    public List<PlantingStationRecipeOutput> getOutputDefinition() {
        return Collections.singletonList(new PlantingStationRecipeOutput(mainOutput, secondaryOutput));
    }

    public ItemStackIngredient getItemInput() {
        return itemInput;
    }

    public GasStackIngredient getGasInput() {
        return gasInput;
    }

    public ItemStack getMainOutput() {
        return mainOutput;
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
    }

    /**
     * @apiNote Main item cannot be null, secondary item can be null.
     */
    public record PlantingStationRecipeOutput(@NotNull ItemStack first, @NotNull ItemStack second) {

        public PlantingStationRecipeOutput {
            Objects.requireNonNull(first, "First output cannot be null.");
            if (first.isEmpty()) {
                throw new IllegalArgumentException("First output cannot be null");
            }
        }
    }
}
