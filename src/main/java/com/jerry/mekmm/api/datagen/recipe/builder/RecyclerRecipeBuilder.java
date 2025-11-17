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
public class RecyclerRecipeBuilder extends MekanismRecipeBuilder<RecyclerRecipeBuilder> {

    private final ItemStackIngredient input;
    private final ItemStack chanceOutput;
    private final double chance;

    protected RecyclerRecipeBuilder(ItemStackIngredient input, ItemStack chanceOutput, double chance) {
        super(ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "recycler"));
        this.input = input;
        this.chanceOutput = chanceOutput;
        this.chance = chance;
    }

    public static RecyclerRecipeBuilder recycler(ItemStackIngredient input, ItemStack chanceOutput, double chance) {
        if (chanceOutput.isEmpty()) {
            throw new IllegalArgumentException("This recycler recipe requires a non empty output.");
        }
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Output chance must be at least zero and at most one.");
        }
        return new RecyclerRecipeBuilder(input, chanceOutput, chance);
    }

    @Override
    protected MekanismRecipeBuilder<RecyclerRecipeBuilder>.RecipeResult getResult(ResourceLocation id) {
        return new RecyclerRecipeResult(id);
    }

    /**
     * Builds this recipe using the output item's name as the recipe name.
     *
     * @param consumer Finished Recipe Consumer.
     */
    public void build(Consumer<FinishedRecipe> consumer) {
        build(consumer, chanceOutput.getItem());
    }

    public class RecyclerRecipeResult extends RecipeResult {

        protected RecyclerRecipeResult(ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            json.add(JsonConstants.INPUT, input.serialize());
            json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(chanceOutput));
            json.addProperty("chance", chance);
        }
    }
}
