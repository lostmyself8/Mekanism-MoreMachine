package com.jerry.mekmm.api.datagen.recipe.builder;

import com.jerry.mekmm.api.recipes.basic.BasicPlantingRecipe;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

@NothingNullByDefault
public class PlantingStationRecipeBuilder extends MekanismRecipeBuilder<PlantingStationRecipeBuilder> {

    private final ItemStackIngredient itemInput;
    private final ChemicalStackIngredient chemicalInput;
    private final ItemStack mainOutput;
    private final ItemStack secondaryOutput;
    private final double secondaryChance;
    private final boolean perTickUsage;

    protected PlantingStationRecipeBuilder(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStack mainOutput, ItemStack secondaryOutput, double secondaryChance, boolean perTickUsage) {
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
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStack mainOutput, boolean perTickUsage) {
        if (mainOutput.isEmpty()) {
            throw new IllegalArgumentException("This planting recipe requires a non empty output.");
        }
        return new PlantingStationRecipeBuilder(itemInput, chemicalInput, mainOutput, ItemStack.EMPTY, 0, perTickUsage);
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
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStack secondaryOutput, double secondaryChance, boolean perTickUsage) {
        if (secondaryOutput.isEmpty()) {
            throw new IllegalArgumentException("This planting recipe requires a non empty secondary output.");
        }
        if (secondaryChance <= 0 || secondaryChance > 1) {
            throw new IllegalArgumentException("This planting recipe requires a secondary output chance greater than zero and at most one.");
        } else if (secondaryChance == 1) {
            throw new IllegalArgumentException("Planting recipes with a single 100% change output should specify their output as the main output.");
        }
        return new PlantingStationRecipeBuilder(itemInput, chemicalInput, ItemStack.EMPTY, secondaryOutput, secondaryChance, perTickUsage);
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
    public static PlantingStationRecipeBuilder planting(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStack mainOutput, ItemStack secondaryOutput, double secondaryChance, boolean perTickUsage) {
        if (mainOutput.isEmpty() || secondaryOutput.isEmpty()) {
            throw new IllegalArgumentException("This planting recipe requires a non empty primary, and secondary output.");
        }
        if (secondaryChance <= 0 || secondaryChance > 1) {
            throw new IllegalArgumentException("This planting recipe requires a secondary output chance greater than zero and at most one.");
        }
        return new PlantingStationRecipeBuilder(itemInput, chemicalInput, mainOutput, secondaryOutput, secondaryChance, perTickUsage);
    }

    @Override
    protected Recipe<?> asRecipe() {
        return new BasicPlantingRecipe(itemInput, chemicalInput, mainOutput, secondaryOutput, secondaryChance, perTickUsage);
    }
}
