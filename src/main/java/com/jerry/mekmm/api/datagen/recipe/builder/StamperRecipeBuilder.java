package com.jerry.mekmm.api.datagen.recipe.builder;

import com.jerry.mekmm.Mekmm;

import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@NothingNullByDefault
public class StamperRecipeBuilder extends MekanismRecipeBuilder<StamperRecipeBuilder> {

    private final ItemStackIngredient input;
    private final ItemStackIngredient mold;
    private final ItemStack output;

    protected StamperRecipeBuilder(ItemStackIngredient input, ItemStackIngredient mold, ItemStack output) {
        super(ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "stamper"));
        this.input = input;
        this.mold = mold;
        this.output = output;
    }

    /**
     * Creates a Stamping recipe builder.
     *
     * @param input  Main Input.
     * @param mold   Mold Input.
     * @param output Output.
     */
    public static StamperRecipeBuilder stamping(ItemStackIngredient input, ItemStackIngredient mold, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This stamping recipe requires a non empty item output.");
        }
        return new StamperRecipeBuilder(input, mold, output);
    }

    @Override
    protected StamperRecipeResult getResult(ResourceLocation id) {
        return new StamperRecipeResult(id);
    }

    /**
     * Builds this recipe using the output item's name as the recipe name.
     *
     * @param consumer Finished Recipe Consumer.
     */
    public void build(Consumer<FinishedRecipe> consumer) {
        build(consumer, output.getItem());
    }

    public class StamperRecipeResult extends RecipeResult {

        protected StamperRecipeResult(ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            json.add(JsonConstants.INPUT, input.serialize());
            json.add("mold", mold.serialize());
            json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(output));
        }
    }
}
