package com.jerry.mekmm.common.recipe.serializer;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.RecyclerRecipe;

import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecyclerRecipeSerializer<RECIPE extends RecyclerRecipe> implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public RecyclerRecipeSerializer(IFactory<RECIPE> factory) {
        this.factory = factory;
    }

    @Override
    @NotNull
    public RECIPE fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
        JsonElement input = GsonHelper.isArrayNode(json, JsonConstants.INPUT) ? GsonHelper.getAsJsonArray(json, JsonConstants.INPUT) :
                GsonHelper.getAsJsonObject(json, JsonConstants.INPUT);
        ItemStackIngredient inputIngredient = IngredientCreatorAccess.item().deserialize(input);
        double getChance;
        JsonElement chance = json.get("chance");
        // 判断有无chance字段
        if (!GsonHelper.isNumberValue(chance)) {
            throw new JsonSyntaxException("Expected chance to be a number greater than zero.");
        }
        // 获取chance字段的值
        getChance = chance.getAsJsonPrimitive().getAsDouble();
        if (getChance <= 0 || getChance > 1) {
            throw new JsonSyntaxException("Expected chance to be greater than zero, and less than or equal to  one.");
        }
        ItemStack output = SerializerHelper.getItemStack(json, JsonConstants.OUTPUT);
        if (output.isEmpty()) {
            throw new JsonSyntaxException("Recycler recipe output must not be empty, if it is defined.");
        }
        return factory.create(recipeId, inputIngredient, output, getChance);
    }

    @Override
    public @Nullable RECIPE fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
        try {
            ItemStackIngredient inputIngredient = IngredientCreatorAccess.item().read(buffer);
            ItemStack output = buffer.readItem();
            double chance = buffer.readDouble();
            return factory.create(recipeId, inputIngredient, output, chance);
        } catch (Exception e) {
            Mekmm.LOGGER.error("Error reading recycler recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekmm.LOGGER.error("Error writing recycler recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends RecyclerRecipe> {

        RECIPE create(ResourceLocation id, ItemStackIngredient input, ItemStack output, double chance);
    }
}
