package com.jerry.mekmm.api.datagen.recipe.builder;

import com.jerry.mekmm.Mekmm;

import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

@NothingNullByDefault
public class PlantingStationRecipeBuilder extends MekanismRecipeBuilder<PlantingStationRecipeBuilder> {

    private final ItemStackIngredient itemInput;
    private final GasStackIngredient gasInput;
    private final ItemStack mainOutput;
    private final ItemStack secondaryOutput;

    protected PlantingStationRecipeBuilder(ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack mainOutput, ItemStack secondaryOutput) {
        super(ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "planting"));
        this.itemInput = itemInput;
        this.gasInput = gasInput;
        this.mainOutput = mainOutput;
        this.secondaryOutput = secondaryOutput;
    }

    /**
     * Creates a planting recipe builder.
     * 创建一个种植站的配方生成器。
     *
     * @param itemInput  ItemInput
     * @param gasInput   GasInput
     * @param mainOutput MainOutput
     */
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack mainOutput) {
        if (mainOutput.isEmpty()) {
            throw new IllegalArgumentException("This planting recipe requires a non empty output.");
        }
        return new PlantingStationRecipeBuilder(itemInput, gasInput, mainOutput, ItemStack.EMPTY);
    }

    /**
     * Creates a planting recipe builder.
     * 创建一个种植站的配方生成器。
     *
     * @param itemInput       ItemInput
     * @param gasInput        GasInput
     * @param mainOutput      MainOutput
     * @param secondaryOutput SecondaryOutput
     */
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack mainOutput, ItemStack secondaryOutput) {
        if (mainOutput.isEmpty() || secondaryOutput.isEmpty()) {
            throw new IllegalArgumentException("This planting recipe requires a non empty primary, and secondary output.");
        }
        return new PlantingStationRecipeBuilder(itemInput, gasInput, mainOutput, secondaryOutput);
    }

    @Override
    protected PlantingStationRecipeResult getResult(ResourceLocation id) {
        return new PlantingStationRecipeResult(id);
    }

    public class PlantingStationRecipeResult extends RecipeResult {

        public PlantingStationRecipeResult(ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            json.add(JsonConstants.ITEM_INPUT, itemInput.serialize());
            json.add(JsonConstants.GAS_INPUT, gasInput.serialize());
            json.add(JsonConstants.MAIN_OUTPUT, SerializerHelper.serializeItemStack(mainOutput));
            if (!secondaryOutput.isEmpty()) {
                json.add(JsonConstants.SECONDARY_OUTPUT, SerializerHelper.serializeItemStack(secondaryOutput));
            }
        }
    }
}
