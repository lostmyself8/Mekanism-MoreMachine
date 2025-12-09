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

        // 输出可能为空物品，但不能为null。因此先将其设定为空物品状态。待读取到之后再进行更改，若没读取到则直接使用空物品。
        ItemStack mainOutput = ItemStack.EMPTY;
        ItemStack secondaryOutput = ItemStack.EMPTY;
        double secondaryChance = 0;
        if (json.has(JsonConstants.SECONDARY_OUTPUT) || json.has(JsonConstants.SECONDARY_CHANCE)) {
            if (json.has(JsonConstants.MAIN_OUTPUT)) {
                // Allow for the main output to be optional if we have a secondary output
                mainOutput = SerializerHelper.getItemStack(json, JsonConstants.MAIN_OUTPUT);
                if (mainOutput.isEmpty()) {
                    throw new JsonSyntaxException("planting main recipe output must not be empty, if it is defined.");
                }
            }
            // If we have either json element for secondary information, assume we have both and fail if we can't get
            // one of them
            JsonElement chance = json.get(JsonConstants.SECONDARY_CHANCE);
            if (!GsonHelper.isNumberValue(chance)) {
                throw new JsonSyntaxException("Expected secondaryChance to be a number greater than zero.");
            }
            secondaryChance = chance.getAsJsonPrimitive().getAsDouble();
            if (secondaryChance <= 0 || secondaryChance > 1) {
                throw new JsonSyntaxException("Expected secondaryChance to be greater than zero, and less than or equal to one.");
            }
            secondaryOutput = SerializerHelper.getItemStack(json, JsonConstants.SECONDARY_OUTPUT);
            if (secondaryOutput.isEmpty()) {
                throw new JsonSyntaxException("planting secondary recipe output must not be empty, if there is no main output.");
            }
        } else {
            // If we don't have a secondary output require a main output
            mainOutput = SerializerHelper.getItemStack(json, JsonConstants.MAIN_OUTPUT);
            if (mainOutput.isEmpty()) {
                throw new JsonSyntaxException("planting main recipe output must not be empty, if there is no secondary output.");
            }
        }
        return factory.create(recipeId, itemInputIngredient, gasInputIngredient, mainOutput, secondaryOutput, secondaryChance);
    }

    @Override
    public @Nullable RECIPE fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        try {
            ItemStackIngredient itemInputIngredient = IngredientCreatorAccess.item().read(buffer);
            GasStackIngredient gasStackIngredient = IngredientCreatorAccess.gas().read(buffer);
            ItemStack mainOutput = buffer.readItem();
            ItemStack secondaryOutput = buffer.readItem();
            double secondaryChance = buffer.readDouble();
            return this.factory.create(recipeId, itemInputIngredient, gasStackIngredient, mainOutput, secondaryOutput, secondaryChance);
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

        RECIPE create(ResourceLocation id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack mainOutput, ItemStack secondaryOutput, double secondaryChance);
    }
}
