package com.jerry.mekmm.api.recipes.basic;

import com.jerry.mekmm.api.recipes.MoreMachineRecipeSerializers;
import com.jerry.mekmm.api.recipes.StamperRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.core.TypedInstance;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NothingNullByDefault
public class BasicStamperRecipe extends StamperRecipe {

    protected final ItemStackIngredient input;
    protected final ItemStackIngredient mold;
    protected final ItemStackTemplate output;

    /**
     * @param input  Input.
     * @param mold   Mold.
     * @param output Output.
     */
    public BasicStamperRecipe(ItemStackIngredient input, ItemStackIngredient mold, ItemStackTemplate output) {
        this.input = Objects.requireNonNull(input, "Input cannot be null.");
        this.mold = Objects.requireNonNull(mold, "Mold cannot be null.");
        Objects.requireNonNull(output, "Output cannot be null.");
        this.output = output;
    }

    @Override
    public boolean test(ItemStack input, ItemStack extra) {
        return this.input.test(input) && mold.test(extra);
    }

    @Override
    public ItemStackIngredient getInput() {
        return input;
    }

    @Override
    public ItemStackIngredient getMold() {
        return mold;
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public <INPUT extends TypedInstance<Item> & DataComponentHolder> ItemStackTemplate getOutput(@NotNull INPUT input, @NotNull INPUT extra) {
        return output;
    }

    @Override
    public List<ItemStack> getOutputDefinition() {
        return Collections.singletonList(output.create());
    }

    public ItemStackTemplate getOutputRaw() {
        return output;
    }

    @Override
    public RecipeSerializer<@NotNull BasicStamperRecipe> getSerializer() {
        return MoreMachineRecipeSerializers.STAMPING.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicStamperRecipe other = (BasicStamperRecipe) o;
        return input.equals(other.input) && mold.equals(other.mold) && itemStackTemplateMatches(output, other.output);
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(input, mold);
        hash = 31 * hash + itemStackTemplateHash(output);
        hash = 31 * hash + output.count();
        return hash;
    }

    private static boolean itemStackTemplateMatches(ItemStackTemplate a, ItemStackTemplate b) {
        return ItemStack.isSameItemSameComponents(a.create(), b.create());
    }

    private static int itemStackTemplateHash(ItemStackTemplate template) {
        return ItemStack.hashItemAndComponents(template.create());
    }
}
