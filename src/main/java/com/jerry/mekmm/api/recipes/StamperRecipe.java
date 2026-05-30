package com.jerry.mekmm.api.recipes;

import com.jerry.mekmm.Mekmm;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiPredicate;

@NothingNullByDefault
public abstract class StamperRecipe extends MekanismRecipe<RecipeInput> implements BiPredicate<@NotNull ItemStack, @NotNull ItemStack> {

    private static final Holder<Item> STAMPER = DeferredHolder.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "stamper"));

    @Override
    public abstract boolean test(ItemStack input, ItemStack mold);

    /**
     * Gets the main input ingredient.
     */
    public abstract ItemStackIngredient getInput();

    /**
     * Gets the secondary input ingredient.
     */
    public abstract ItemStackIngredient getMold();

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) {
        if (!isIncomplete() && input.size() == 2) {
            ItemStack mainInput = input.getItem(0);
            ItemStack extraInput = input.getItem(1);
            if (test(mainInput, extraInput)) {
                return getOutput(mainInput, extraInput);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        // Don't match incomplete recipes or ones that don't match
        return !isIncomplete() && input.size() == 2 && test(input.getItem(0), input.getItem(1));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 1;
    }

    /**
     * Gets a new output based on the given inputs.
     *
     * @param input Specific input.
     * @param extra Specific secondary/extra input.
     *
     * @return New output.
     *
     * @apiNote While Mekanism does not currently make use of the inputs, it is important to support it and pass the
     *          proper value in case any addons define input based
     *          outputs where things like NBT may be different.
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public abstract ItemStack getOutput(ItemStack input, ItemStack extra);

    @Override
    public abstract ItemStack getResultItem(HolderLookup.Provider provider);

    /**
     * For JEI, gets the output representations to display.
     *
     * @return Representation of the output, <strong>MUST NOT</strong> be modified.
     */
    public abstract List<ItemStack> getOutputDefinition();

    @Override
    public boolean isIncomplete() {
        return getInput().hasNoMatchingInstances() || getMold().hasNoMatchingInstances();
    }

    @Override
    public final RecipeType<StamperRecipe> getType() {
        return MoreMachineRecipeTypes.TYPE_STAMPING.value();
    }

    @Override
    public String getGroup() {
        return "stamper";
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(STAMPER);
    }
}
