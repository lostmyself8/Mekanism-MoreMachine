package com.jerry.mekmm.common.recipe.serializer;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.PlantingRecipe;

import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
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
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class PlantingRecipeSerializer<RECIPE extends PlantingRecipe> implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public PlantingRecipeSerializer(IFactory<RECIPE> factory) {
        this.factory = factory;
    }

    @Override
    public RECIPE fromJson(ResourceLocation recipeId, JsonObject json) {
        JsonElement itemInput = GsonHelper.isArrayNode(json, JsonConstants.ITEM_INPUT) ? GsonHelper.getAsJsonArray(json, JsonConstants.ITEM_INPUT) :
                GsonHelper.getAsJsonObject(json, JsonConstants.ITEM_INPUT);
        ItemStackIngredient itemInputIngredient = IngredientCreatorAccess.item().deserialize(itemInput);
        JsonElement gasInput = GsonHelper.isArrayNode(json, JsonConstants.GAS_INPUT) ? GsonHelper.getAsJsonArray(json, JsonConstants.GAS_INPUT) :
                GsonHelper.getAsJsonObject(json, JsonConstants.GAS_INPUT);
        GasStackIngredient gasInputIngredient = IngredientCreatorAccess.gas().deserialize(gasInput);

        ItemStack mainOutput = SerializerHelper.getItemStack(json, JsonConstants.MAIN_OUTPUT);
        // 第二输出可能为空物品，但不能为null。因此先将其设定为空物品状态。待读取到之后再进行更改，若没读取到则直接使用空物品。
        ItemStack secondaryOutput = ItemStack.EMPTY;
        if (json.has(JsonConstants.SECONDARY_OUTPUT)) {
            if (mainOutput.isEmpty()) {
                throw new JsonSyntaxException("Planting main recipe output must not be empty, if it is defined.");
            }
            secondaryOutput = SerializerHelper.getItemStack(json, JsonConstants.SECONDARY_OUTPUT);
        } else {
            if (mainOutput.isEmpty()) {
                throw new JsonSyntaxException("Planting main recipe output must not be empty, if it is defined.");
            }
        }
        return factory.create(recipeId, itemInputIngredient, gasInputIngredient, mainOutput, secondaryOutput);
    }

    @Override
    public @Nullable RECIPE fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        try {
            ItemStackIngredient itemInputIngredient = IngredientCreatorAccess.item().read(buffer);
            GasStackIngredient gasStackIngredient = IngredientCreatorAccess.gas().read(buffer);
            ItemStack mainOutput = buffer.readItem();
            ItemStack secondaryOutput = buffer.readItem();
            return factory.create(recipeId, itemInputIngredient, gasStackIngredient, mainOutput, secondaryOutput);
        } catch (Exception e) {
            Mekmm.LOGGER.error("Error reading planting recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekmm.LOGGER.error("Error writing planting recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends PlantingRecipe> {

        RECIPE create(ResourceLocation id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack mainOutput, ItemStack secondaryOutput);
    }
}
