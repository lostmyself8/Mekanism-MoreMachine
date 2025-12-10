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

    private final OutputType outputType;
    private final ItemStackIngredient itemInput;
    private final GasStackIngredient gasInput;
    private final ItemStack mainOutput;
    private final ItemStack secondaryOutput;
    private final double secondaryChance;

    protected PlantingStationRecipeBuilder(ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack mainOutput, ItemStack secondaryOutput, double secondaryChance, OutputType outputType) {
        super(ResourceLocation.fromNamespaceAndPath(Mekmm.MOD_ID, "planting"));
        this.outputType = outputType;
        this.itemInput = itemInput;
        this.gasInput = gasInput;
        this.mainOutput = mainOutput;
        this.secondaryOutput = secondaryOutput;
        this.secondaryChance = secondaryChance;
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
        return new PlantingStationRecipeBuilder(itemInput, gasInput, mainOutput, ItemStack.EMPTY, 0, OutputType.PRIMARY);
    }

    /**
     * Creates a Sawing recipe builder.
     * 创建一个种植站的配方生成器。
     *
     * @param input           Input.
     * @param secondaryOutput Secondary Output.
     * @param secondaryChance Chance of the secondary output being produced. This must be a number greater than zero and
     *                        less than one.
     */
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient input, GasStackIngredient gasInput, ItemStack secondaryOutput, double secondaryChance) {
        if (secondaryOutput.isEmpty()) {
            throw new IllegalArgumentException("This planting recipe requires a non empty secondary output.");
        }
        if (secondaryChance <= 0 || secondaryChance > 1) {
            throw new IllegalArgumentException("This planting recipe requires a secondary output chance greater than zero and at most one.");
        } else if (secondaryChance == 1) {
            throw new IllegalArgumentException("planting recipes with a single 100% change output should specify their output as the main output.");
        }
        return new PlantingStationRecipeBuilder(input, gasInput, ItemStack.EMPTY, secondaryOutput, secondaryChance, OutputType.SECONDARY);
    }

    /**
     * Creates a planting recipe builder.
     * 创建一个种植站的配方生成器。
     *
     * @param itemInput       ItemInput
     * @param gasInput        GasInput
     * @param mainOutput      MainOutput
     * @param secondaryOutput SecondaryOutput
     * @param secondaryChance Chance of the secondary output being produced. This must be a number greater than zero and
     *                        at most one.
     */
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack mainOutput, ItemStack secondaryOutput, double secondaryChance) {
        if (mainOutput.isEmpty() || secondaryOutput.isEmpty()) {
            throw new IllegalArgumentException("This planting recipe requires a non empty primary, and secondary output.");
        }
        if (secondaryChance <= 0 || secondaryChance > 1) {
            throw new IllegalArgumentException("This planting recipe requires a secondary output chance greater than zero and at most one.");
        }
        return new PlantingStationRecipeBuilder(itemInput, gasInput, mainOutput, secondaryOutput, secondaryChance, OutputType.BOTH);
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
            if (outputType.hasPrimary) {
                json.add(JsonConstants.MAIN_OUTPUT, SerializerHelper.serializeItemStack(mainOutput));
            }
            if (outputType.hasSecondary) {
                json.add(JsonConstants.SECONDARY_OUTPUT, SerializerHelper.serializeItemStack(secondaryOutput));
                json.addProperty(JsonConstants.SECONDARY_CHANCE, secondaryChance);
            }
        }
    }

    private enum OutputType {

        PRIMARY(true, false),
        SECONDARY(false, true),
        BOTH(true, true);

        private final boolean hasPrimary;
        private final boolean hasSecondary;

        OutputType(boolean hasPrimary, boolean hasSecondary) {
            this.hasPrimary = hasPrimary;
            this.hasSecondary = hasSecondary;
        }
    }
}
