package com.jerry.mekaf.common.attachments.containers.chemical;

import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.resource.LargeResourceStack;
import mekanism.common.component.containers.ContainsRecipe;
import mekanism.common.component.containers.chemical.ComponentBackedChemicalTank;
import mekanism.common.component.containers.creator.BaseContainerCreator;
import mekanism.common.component.containers.creator.IBasicContainerCreator;
import mekanism.common.component.containers.resource.AttachedResources;
import mekanism.common.component.containers.resource.ResourceContainersBuilder.BaseContainerBuilder;
import mekanism.common.config.MekanismConfig;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;

import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

public class AFChemicalTanksBuilder {

    public static AFChemicalTanksBuilder builder() {
        return new AFChemicalTanksBuilder();
    }

    protected final List<IBasicContainerCreator<IChemicalTank>> tankCreators = new ArrayList<>();

    protected AFChemicalTanksBuilder() {}

    public BaseContainerCreator<AttachedResources<ChemicalResource>, IChemicalTank> build() {
        return new BaseContainerBuilder<>(tankCreators, LargeResourceStack.CHEMICAL_HELPER);
    }

    public <VANILLA_INPUT extends RecipeInput, RECIPE extends MekanismRecipe<VANILLA_INPUT>, INPUT_CACHE extends IInputRecipeCache> AFChemicalTanksBuilder addBasic(long capacity,
                                                                                                                                                                    IMekanismRecipeTypeProvider<VANILLA_INPUT, RECIPE, INPUT_CACHE> recipeType, ContainsRecipe<INPUT_CACHE, ChemicalResource> containsRecipe) {
        return addBasic(capacity, chemical -> containsRecipe.check(recipeType.getInputCache(), null, chemical));
    }

    public AFChemicalTanksBuilder addBasic(long capacity, Predicate<ChemicalResource> isValid) {
        return addBasic(() -> capacity, isValid);
    }

    public AFChemicalTanksBuilder addBasic(LongSupplier capacity, Predicate<ChemicalResource> isValid) {
        return addTank((attachedTo, containerIndex) -> new ComponentBackedChemicalTank(attachedTo,
                containerIndex, ConstantPredicates.notExternal(), ConstantPredicates.alwaysTrueBi(), isValid, capacity, MekanismConfig.general.chemicalItemFillRate));
    }

    public AFChemicalTanksBuilder addBasic(long capacity) {
        return addBasic(() -> capacity);
    }

    public AFChemicalTanksBuilder addBasic(LongSupplier capacity) {
        return addTank((attachedTo, containerIndex) -> new ComponentBackedChemicalTank(attachedTo,
                containerIndex, ConstantPredicates.manualOnly(), ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrue(),
                capacity, MekanismConfig.general.chemicalItemFillRate));
    }

    public AFChemicalTanksBuilder addOutputFactoryTank(int process, long capacity) {
        for (int i = 0; i < process; i++) {
            addBasic(capacity);
        }
        return this;
    }

    public AFChemicalTanksBuilder addInputFactoryTank(int process, long capacity, Predicate<ChemicalResource> recipeInputPredicate) {
        IBasicContainerCreator<IChemicalTank> inputTankCreator = (attachedTo, containerIndex) -> new ComponentBackedChemicalTank(attachedTo,
                containerIndex, ConstantPredicates.notExternal(), ConstantPredicates.alwaysTrueBi(), recipeInputPredicate, () -> capacity, MekanismConfig.general.chemicalItemFillRate);
        for (int i = 0; i < process; i++) {
            addTank(inputTankCreator);
        }
        return this;
    }

    public AFChemicalTanksBuilder addInternalStorage(LongSupplier rate, LongSupplier capacity, Predicate<ChemicalResource> isValid) {
        return addTank((attachedTo, containerIndex) -> new ComponentBackedChemicalTank(attachedTo,
                containerIndex, ConstantPredicates.notExternal(), ConstantPredicates.alwaysTrueBi(), isValid, capacity, () -> Math.toIntExact(rate.getAsLong())));
    }

    public AFChemicalTanksBuilder addTank(IBasicContainerCreator<IChemicalTank> tank) {
        tankCreators.add(tank);
        return this;
    }
}
