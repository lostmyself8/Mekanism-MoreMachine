package com.jerry.mekmm.common.recipe.serializer;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.basic.BasicPlantingRecipe;

import mekanism.api.SerializationConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

@NothingNullByDefault
public class PlantingRecipeSerializer implements RecipeSerializer<BasicPlantingRecipe> {

    private final StreamCodec<RegistryFriendlyByteBuf, BasicPlantingRecipe> streamCodec;
    private final MapCodec<BasicPlantingRecipe> codec;

    public PlantingRecipeSerializer(Function6<ItemStackIngredient, ChemicalStackIngredient, ItemStack, ItemStack, Double, Boolean, BasicPlantingRecipe> factory) {
        Codec<Double> chanceCodec = Codec.DOUBLE.validate(d -> d > 0 && d <= 1 ? DataResult.success(d) : DataResult.error(() -> "Expected secondaryChance to be greater than zero, and less than or equal to one. Found " + d));
        MapCodec<Optional<Double>> secondaryChanceFieldBase = chanceCodec.optionalFieldOf(SerializationConstants.SECONDARY_CHANCE);
        MapCodec<Optional<ItemStack>> mainOutputFieldBase = ItemStack.CODEC.optionalFieldOf(SerializationConstants.MAIN_OUTPUT);
        RecordCodecBuilder<BasicPlantingRecipe, Optional<ItemStack>> secondaryOutputField = ItemStack.CODEC.optionalFieldOf(SerializationConstants.SECONDARY_OUTPUT).forGetter(BasicPlantingRecipe::getSecondaryOutputRaw);

        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStackIngredient.CODEC.fieldOf(SerializationConstants.ITEM_INPUT).forGetter(PlantingRecipe::getItemInput),
                IngredientCreatorAccess.chemicalStack().codec().fieldOf(SerializationConstants.CHEMICAL_INPUT).forGetter(PlantingRecipe::getChemicalInput),
                SerializerHelper.oneRequired(secondaryOutputField, mainOutputFieldBase, BasicPlantingRecipe::getMainOutputRaw),
                secondaryOutputField,
                SerializerHelper.dependentOptionality(secondaryOutputField, secondaryChanceFieldBase, plantingRecipe -> {
                    double secondaryChance = plantingRecipe.getSecondaryChance();
                    return secondaryChance == 0 ? Optional.empty() : Optional.of(secondaryChance);
                }),
                Codec.BOOL.fieldOf(SerializationConstants.PER_TICK_USAGE).forGetter(BasicPlantingRecipe::perTickUsage)).apply(instance, (itemInput, chemicalInput, mainOutput, secondaryOutput, secondChance, perTickUsage) -> factory.apply(itemInput, chemicalInput, mainOutput.orElse(ItemStack.EMPTY), secondaryOutput.orElse(ItemStack.EMPTY), secondChance.orElse(0D), perTickUsage)));

        this.streamCodec = StreamCodec.composite(
                ItemStackIngredient.STREAM_CODEC, PlantingRecipe::getItemInput,
                IngredientCreatorAccess.chemicalStack().streamCodec(), PlantingRecipe::getChemicalInput,
                ItemStack.OPTIONAL_STREAM_CODEC, (BasicPlantingRecipe recipe) -> recipe.getMainOutputRaw().orElse(ItemStack.EMPTY),
                ItemStack.OPTIONAL_STREAM_CODEC, (BasicPlantingRecipe recipe) -> recipe.getSecondaryOutputRaw().orElse(ItemStack.EMPTY),
                ByteBufCodecs.DOUBLE, PlantingRecipe::getSecondaryChance,
                ByteBufCodecs.BOOL, BasicPlantingRecipe::perTickUsage,
                factory);
    }

    @Override
    public MapCodec<BasicPlantingRecipe> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BasicPlantingRecipe> streamCodec() {
        return streamCodec;
    }
}
