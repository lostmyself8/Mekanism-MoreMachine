package com.jerry.mekmm.common.recipe.serializer;

import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicStamperRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicTripleItemToItemRecipe;

import mekanism.api.SerializationConstants;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.recipe.serializer.MekanismRecipeSerializer;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MoreMachineRecipeSerializer<RECIPE extends Recipe<?>>(MapCodec<RECIPE> codec, StreamCodec<RegistryFriendlyByteBuf, RECIPE> streamCodec)
        implements RecipeSerializer<RECIPE> {

    public static MekanismRecipeSerializer<BasicStamperRecipe> stamping(Function3<ItemStackIngredient, ItemStackIngredient, ItemStack, BasicStamperRecipe> factory) {
        return new MekanismRecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStackIngredient.CODEC.fieldOf(SerializationConstants.INPUT).forGetter(StamperRecipe::getInput),
                ItemStackIngredient.CODEC.fieldOf(MoreMachineSerializationConstants.MOLD).forGetter(StamperRecipe::getMold),
                ItemStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicStamperRecipe::getOutputRaw)).apply(instance, factory)), StreamCodec.composite(
                        ItemStackIngredient.STREAM_CODEC, BasicStamperRecipe::getInput,
                        ItemStackIngredient.STREAM_CODEC, BasicStamperRecipe::getMold,
                        ItemStack.STREAM_CODEC, BasicStamperRecipe::getOutputRaw,
                        factory));
    }

    public static <RECIPE extends BasicTripleItemToItemRecipe> MekanismRecipeSerializer<RECIPE> tripleItemToItem(Function4<ItemStackIngredient, ItemStackIngredient, ItemStackIngredient, ItemStack, RECIPE> factory) {
        return new MekanismRecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStackIngredient.CODEC.fieldOf(MoreMachineSerializationConstants.PRIMARY_INPUT).forGetter(BasicTripleItemToItemRecipe::getFirstInput),
                ItemStackIngredient.CODEC.fieldOf(MoreMachineSerializationConstants.SECONDARY_INPUT).forGetter(BasicTripleItemToItemRecipe::getSecondInput),
                ItemStackIngredient.CODEC.fieldOf(MoreMachineSerializationConstants.TERTIARY_INPUT).forGetter(BasicTripleItemToItemRecipe::getThirdInput),
                ItemStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicTripleItemToItemRecipe::getOutputRaw)).apply(instance, factory)), StreamCodec.composite(
                        ItemStackIngredient.STREAM_CODEC, BasicTripleItemToItemRecipe::getFirstInput,
                        ItemStackIngredient.STREAM_CODEC, BasicTripleItemToItemRecipe::getSecondInput,
                        ItemStackIngredient.STREAM_CODEC, BasicTripleItemToItemRecipe::getThirdInput,
                        ItemStack.STREAM_CODEC, BasicTripleItemToItemRecipe::getOutputRaw,
                        factory));
    }
}
