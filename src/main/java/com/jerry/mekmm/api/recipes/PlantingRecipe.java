package com.jerry.mekmm.api.recipes;

import com.jerry.mekmm.Mekmm;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.SawmillRecipe.ChanceOutput;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Represents a recipe that can be used in the Planting Station.
 *
 * @author Jerry
 */
@NothingNullByDefault
public abstract class PlantingRecipe extends MekanismRecipe<SingleItemChemicalRecipeInput> implements BiPredicate<@NotNull ItemStack, ChemicalStack> {

    protected static final RandomSource RANDOM = RandomSource.create();
    private static final Holder<Item> PLANTING_STATION = DeferredHolder.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "plating_station"));

    @Override
    public abstract boolean test(ItemStack itemStack, ChemicalStack chemicalStack);

    @Override
    public boolean matches(SingleItemChemicalRecipeInput input, Level level) {
        // Don't match incomplete recipes or ones that don't match
        return !isIncomplete() && test(input.item(), input.chemical());
    }

    /**
     * Represents whether this recipe consumes the chemical each tick.
     *
     * @since 10.7.0
     */
    public abstract boolean perTickUsage();

    /**
     * Gets the input item ingredient.
     */
    public abstract ItemStackIngredient getItemInput();

    /**
     * Gets the input chemical ingredient.
     */
    public abstract ChemicalStackIngredient getChemicalInput();

    /**
     * Gets a new output based on the given inputs.
     *
     * @param inputItem     Specific item input.
     * @param inputChemical Specific chemical input.
     *
     * @return New output.
     *
     * @apiNote While Mekanism does not currently make use of the inputs, it is important to support it and pass the
     *          proper value in case any addons define input based
     *          outputs where things like NBT may be different.
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public abstract ChanceOutput getOutput(ItemStack inputItem, ChemicalStack inputChemical);

    /**
     * For JEI, gets the main output representations to display.
     *
     * @return Representation of the main output, <strong>MUST NOT</strong> be modified.
     */
    public abstract List<ItemStack> getMainOutputDefinition();

    /**
     * For JEI, gets the secondary output representations to display.
     *
     * @return Representation of the secondary output, <strong>MUST NOT</strong> be modified.
     */
    public abstract List<ItemStack> getSecondaryOutputDefinition();

    /**
     * Gets the chance (between 0 and 1) of the secondary output being produced.
     */
    public abstract double getSecondaryChance();

    @Override
    public boolean isIncomplete() {
        return getItemInput().hasNoMatchingInstances() || getChemicalInput().hasNoMatchingInstances();
    }

    @Override
    public void logMissingTags() {
        getItemInput().logMissingTags();
        getChemicalInput().logMissingTags();
    }

    @Override
    public RecipeType<?> getType() {
        return MoreMachineRecipeTypes.TYPE_PLANTING.value();
    }

    @Override
    public String getGroup() {
        return "planting_station";
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(PLANTING_STATION);
    }
}
