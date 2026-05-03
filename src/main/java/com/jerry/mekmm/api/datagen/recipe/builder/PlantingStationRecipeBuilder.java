package com.jerry.mekmm.api.datagen.recipe.builder;

import com.jerry.mekmm.api.recipes.basic.BasicPlantingRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Objects;

@NothingNullByDefault
public class PlantingStationRecipeBuilder extends MekanismRecipeBuilder<PlantingStationRecipeBuilder> {

    private final ItemStackIngredient itemInput;
    private final ChemicalStackIngredient chemicalInput;
    private final ItemStackTemplate mainOutput;
    private final ItemStackTemplate secondaryOutput;
    private final double secondaryChance;
    private final boolean perTickUsage;

    protected PlantingStationRecipeBuilder(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStackTemplate mainOutput, ItemStackTemplate secondaryOutput, double secondaryChance, boolean perTickUsage) {
        this.itemInput = itemInput;
        this.chemicalInput = chemicalInput;
        this.mainOutput = mainOutput;
        this.secondaryOutput = secondaryOutput;
        this.secondaryChance = secondaryChance;
        this.perTickUsage = perTickUsage;
    }

    /**
     * Creates a Planting recipe builder.
     * 创建一个种植站的配方生成器。
     *
     * @param itemInput     ItemInput
     * @param chemicalInput ChemicalInput
     * @param mainOutput    MainOutput
     * @param perTickUsage  PerTickUsage
     */
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStackTemplate mainOutput, boolean perTickUsage) {
        Objects.requireNonNull(mainOutput, "This planting recipe requires a non empty output.");
        return new PlantingStationRecipeBuilder(itemInput, chemicalInput, mainOutput, null, 0, perTickUsage);
    }

    /**
     * Creates a Planting recipe builder.
     *
     * @param itemInput       itemInput.
     * @param chemicalInput   chemicalInput.
     * @param secondaryOutput Secondary Output.
     * @param secondaryChance Chance of the secondary output being produced. This must be a number greater than zero and
     *                        less than one.
     * @param perTickUsage    PerTickUsage
     */
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStackTemplate secondaryOutput, double secondaryChance, boolean perTickUsage) {
        Objects.requireNonNull(secondaryOutput, "This planting recipe requires a non empty secondary output.");
        if (secondaryChance <= 0 || secondaryChance > 1) {
            throw new IllegalArgumentException("This planting recipe requires a secondary output chance greater than zero and at most one.");
        } else if (secondaryChance == 1) {
            throw new IllegalArgumentException("Planting recipes with a single 100% change output should specify their output as the main output.");
        }
        return new PlantingStationRecipeBuilder(itemInput, chemicalInput, null, secondaryOutput, secondaryChance, perTickUsage);
    }

    /**
     * Creates a planting recipe builder.
     * 创建一个种植站的配方生成器。
     *
     * @param itemInput       ItemInput
     * @param chemicalInput   ChemicalInput
     * @param mainOutput      MainOutput
     * @param secondaryOutput SecondaryOutput
     * @param secondaryChance Chance of the secondary output being produced. This must be a number greater than zero and
     *                        less than one.
     * @param perTickUsage    PerTickUsage
     */
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStackTemplate mainOutput, ItemStackTemplate secondaryOutput, double secondaryChance, boolean perTickUsage) {
        Objects.requireNonNull(mainOutput, "This planting recipe requires a non empty output.");
        Objects.requireNonNull(secondaryOutput, "This planting recipe requires a non empty secondary output.");
        if (secondaryChance <= 0 || secondaryChance > 1) {
            throw new IllegalArgumentException("This planting recipe requires a secondary output chance greater than zero and at most one.");
        }
        return new PlantingStationRecipeBuilder(itemInput, chemicalInput, mainOutput, secondaryOutput, secondaryChance, perTickUsage);
    }

    @Override
    protected Recipe<?> asRecipe() {
        return new BasicPlantingRecipe(itemInput, chemicalInput, mainOutput, secondaryOutput, secondaryChance, perTickUsage);
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        ItemStackTemplate template = Objects.requireNonNull(mainOutput != null ? mainOutput : secondaryOutput, "Illegal config");
        return RecipeBuilder.getDefaultRecipeId(template);
    }
}
