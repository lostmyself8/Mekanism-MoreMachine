package com.jerry.mekmm.common.recipe.serializer;

import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicPresserRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicStamperRecipe;

import com.mojang.datafixers.util.Function4;
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

    public static RecipeSerializer<BasicPresserRecipe> pressing(Function4<ItemStackIngredient, ItemStackIngredient, ItemStackIngredient, ItemStackTemplate, BasicPresserRecipe> factory) {
        return new RecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStackIngredient.CODEC.fieldOf("primary_input").forGetter(TripleItemToItemRecipe::getFirstInput),
                ItemStackIngredient.CODEC.fieldOf("secondary_input").forGetter(TripleItemToItemRecipe::getSecondInput),
                ItemStackIngredient.CODEC.fieldOf("tertiary_input").forGetter(TripleItemToItemRecipe::getThirdInput),
                ItemStackTemplate.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicPresserRecipe::getOutputRaw)).apply(instance, factory)), StreamCodec.composite(
                        ItemStackIngredient.STREAM_CODEC, BasicPresserRecipe::getFirstInput,
                        ItemStackIngredient.STREAM_CODEC, BasicPresserRecipe::getSecondInput,
                        ItemStackIngredient.STREAM_CODEC, BasicPresserRecipe::getThirdInput,
                        ItemStackTemplate.STREAM_CODEC, BasicPresserRecipe::getOutputRaw,
                        factory));
    }
}
