package com.jerry.mekmm.api.recipes.basic;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.basic.IBasicItemStackOutput;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NothingNullByDefault
public abstract class MMBasicItemStackChemicalToItemStackRecipe extends ItemStackChemicalToItemStackRecipe implements IBasicItemStackOutput {

    protected final ItemStackIngredient itemInput;
    protected final ChemicalStackIngredient chemicalInput;
    protected final ItemStackTemplate output;

    /**
     * @param itemInput     Item input.
     * @param chemicalInput Chemical input.
     * @param output        Output.
     */
    public MMBasicItemStackChemicalToItemStackRecipe(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStackTemplate output) {
        this.itemInput = Objects.requireNonNull(itemInput, "Item input cannot be null.");
        this.chemicalInput = Objects.requireNonNull(chemicalInput, "Chemical input cannot be null.");
        Objects.requireNonNull(output, "Output cannot be null.");
        this.output = output;
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
    @Contract(value = "_, _ -> new", pure = true)
    public ItemStackTemplate getOutput(ItemStack inputItem, ChemicalStack inputChemical) {
        return output;
    }

    @Override
    public final boolean perTickUsage() {
        return true;
    }

    @Override
    public boolean test(ItemStack itemStack, ChemicalStack chemicalStack) {
        return itemInput.test(itemStack) && chemicalInput.test(chemicalStack);
    }

    @Override
    public List<@NotNull ItemStackTemplate> getOutputDefinition() {
        return Collections.singletonList(output);
    }

    @Override
    public ItemStackTemplate getOutputRaw() {
        return output;
    }
}
