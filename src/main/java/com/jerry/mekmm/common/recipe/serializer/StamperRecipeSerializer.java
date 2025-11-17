package com.jerry.mekmm.common.recipe.serializer;

import com.jerry.mekmm.api.recipes.StamperRecipe;

import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.Mekanism;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

public class StamperRecipeSerializer<RECIPE extends StamperRecipe> implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public StamperRecipeSerializer(IFactory<RECIPE> factory) {
        this.factory = factory;
    }

    @NotNull
    @Override
    public RECIPE fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
        JsonElement input = GsonHelper.isArrayNode(json, JsonConstants.INPUT) ? GsonHelper.getAsJsonArray(json, JsonConstants.INPUT) :
                GsonHelper.getAsJsonObject(json, JsonConstants.INPUT);
        ItemStackIngredient inputIngredient = IngredientCreatorAccess.item().deserialize(input);
        JsonElement mold = GsonHelper.isArrayNode(json, "mold") ? GsonHelper.getAsJsonArray(json, "mold") :
                GsonHelper.getAsJsonObject(json, "mold");
        ItemStackIngredient moldIngredient = IngredientCreatorAccess.item().deserialize(mold);
        ItemStack output = SerializerHelper.getItemStack(json, JsonConstants.OUTPUT);
        if (output.isEmpty()) {
            throw new JsonSyntaxException("Stamper recipe output must not be empty.");
        }
        return this.factory.create(recipeId, inputIngredient, moldIngredient, output);
    }

    @Override
    public RECIPE fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
        try {
            ItemStackIngredient input = IngredientCreatorAccess.item().read(buffer);
            ItemStackIngredient mold = IngredientCreatorAccess.item().read(buffer);
            ItemStack output = buffer.readItem();
            return this.factory.create(recipeId, input, mold, output);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading stamper recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing stamper recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends StamperRecipe> {

        RECIPE create(ResourceLocation id, ItemStackIngredient input, ItemStackIngredient mold, ItemStack output);
    }
}
