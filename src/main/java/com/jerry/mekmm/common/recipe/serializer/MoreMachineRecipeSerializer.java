package com.jerry.mekmm.common.recipe.serializer;

import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicStamperRecipe;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.SerializationConstants;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MoreMachineRecipeSerializer {

    private MoreMachineRecipeSerializer() {
    }

    public static RecipeSerializer<BasicStamperRecipe> stamping(Function3<ItemStackIngredient, ItemStackIngredient, ItemStack, BasicStamperRecipe> factory) {
        return new RecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
              ItemStackIngredient.CODEC.fieldOf(SerializationConstants.INPUT).forGetter(StamperRecipe::getInput),
              ItemStackIngredient.CODEC.fieldOf(MoreMachineSerializationConstants.MOLD).forGetter(StamperRecipe::getMold),
              ItemStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicStamperRecipe::getOutputRaw)
        ).apply(instance, factory)), StreamCodec.composite(
              ItemStackIngredient.STREAM_CODEC, BasicStamperRecipe::getInput,
              ItemStackIngredient.STREAM_CODEC, BasicStamperRecipe::getMold,
              ItemStack.STREAM_CODEC, BasicStamperRecipe::getOutputRaw,
              factory
        ));
    }
}
