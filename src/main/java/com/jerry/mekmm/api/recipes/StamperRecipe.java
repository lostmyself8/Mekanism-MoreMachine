package com.jerry.mekmm.api.recipes;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

public abstract class StamperRecipe extends MekanismRecipe implements BiPredicate<@NotNull ItemStack, @NotNull ItemStack> {

    private final ItemStackIngredient input;
    private final ItemStackIngredient mold;
    private final ItemStack output;

    /**
     * @param id Recipe name.
     */
    protected StamperRecipe(ResourceLocation id, ItemStackIngredient input, ItemStackIngredient mold, ItemStack output) {
        super(id);
        this.input = Objects.requireNonNull(input, "Input cannot be null.");
        this.mold = Objects.requireNonNull(mold, "Mold cannot be null.");
        Objects.requireNonNull(output, "Output cannot be null.");
        if (output.isEmpty()) {
            throw new IllegalArgumentException("Output cannot be empty.");
        }
        this.output = output.copy();
    }

    @Override
    public boolean test(ItemStack input, ItemStack mold) {
        return this.input.test(input) && this.mold.test(mold);
    }

    /**
     * Gets the input ingredient.
     */
    public ItemStackIngredient getInput() {
        return input;
    }

    /**
     * Gets the mold input ingredient.
     */
    public ItemStackIngredient getMold() {
        return mold;
    }

    /**
     * Gets a new output based on the given inputs.
     *
     * @param input Specific input.
     * @param mold  Specific mold input.
     *
     * @return New output.
     *
     * @apiNote While Mekanism does not currently make use of the inputs, it is important to support it and pass the
     *          proper value in case any addons define input based
     *          outputs where things like NBT may be different.
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack mold) {
        return output.copy();
    }

    @NotNull
    @Override
    public ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return output.copy();
    }

    /**
     * For JEI, gets the output representations to display.
     *
     * @return Representation of the output, <strong>MUST NOT</strong> be modified.
     */
    public List<ItemStack> getOutputDefinition() {
        return Collections.singletonList(output);
    }

    @Override
    public boolean isIncomplete() {
        return input.hasNoMatchingInstances() || mold.hasNoMatchingInstances();
    }

    @Override
    public void logMissingTags() {
        input.logMissingTags();
        mold.logMissingTags();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        input.write(buffer);
        mold.write(buffer);
        buffer.writeItem(output);
    }
}
