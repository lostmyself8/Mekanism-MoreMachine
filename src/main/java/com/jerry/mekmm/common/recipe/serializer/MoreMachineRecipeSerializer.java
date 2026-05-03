package com.jerry.mekmm.common.recipe.serializer;

import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicStamperRecipe;

import mekanism.api.SerializationConstants;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MoreMachineRecipeSerializer {

    private MoreMachineRecipeSerializer() {}

    public static RecipeSerializer<BasicStamperRecipe> stamping(Function3<ItemStackIngredient, ItemStackIngredient, ItemStackTemplate, BasicStamperRecipe> factory) {
        return new RecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStackIngredient.CODEC.fieldOf(SerializationConstants.INPUT).forGetter(StamperRecipe::getInput),
                ItemStackIngredient.CODEC.fieldOf(MoreMachineSerializationConstants.MOLD).forGetter(StamperRecipe::getMold),
                ItemStackTemplate.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicStamperRecipe::getOutputRaw)).apply(instance, factory)), StreamCodec.composite(
                        ItemStackIngredient.STREAM_CODEC, BasicStamperRecipe::getInput,
                        ItemStackIngredient.STREAM_CODEC, BasicStamperRecipe::getMold,
                        ItemStackTemplate.STREAM_CODEC, BasicStamperRecipe::getOutputRaw,
                        factory));
    }
}
