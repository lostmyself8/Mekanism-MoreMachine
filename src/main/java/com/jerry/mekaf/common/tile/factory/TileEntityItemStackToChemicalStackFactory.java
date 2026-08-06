package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.tile.factory.base.TileEntityItemToChemicalFactory;
import com.jerry.mekaf.common.upgrade.ItemToChemicalUpgradeData;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ItemStackToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ItemRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleItem;
import mekanism.common.tile.component.TileComponentEjector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriPredicate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityItemStackToChemicalStackFactory extends TileEntityItemToChemicalFactory<ItemStackToChemicalRecipe, AdvancedFactoryType> implements ItemRecipeLookupHandler<ItemStackToChemicalRecipe> {

    private static final TriPredicate<ItemStackToChemicalRecipe, ItemStack, ChemicalStack> OUTPUT_CHECK = (recipe, input, output) -> output.isEmpty() || ChemicalStack.isSameChemical(recipe.getOutput(input), output);
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(RecipeError.NOT_ENOUGH_ENERGY);

    public TileEntityItemStackToChemicalStackFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES, Attribute.getOrThrow(blockProvider, AttributeAdvancedFactoryType.class).getAdvancedFactoryType());

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL);
    }

    @Override
    @Contract("null, _ -> false")
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ItemStackToChemicalRecipe> cached, @NotNull ItemStack stack) {
        return cached != null && cached.getRecipe().getInput().testType(stack);
    }

    @Override
    @Nullable
    protected ItemStackToChemicalRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IChemicalTank outputSlot) {
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, outputSlot.getStack(), OUTPUT_CHECK);
    }

    @Override
    protected int getNeededInput(ItemStackToChemicalRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    // 物品能否放进槽位
    @Override
    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return containsRecipe(stack);
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipe(stack);
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<SingleRecipeInput, ItemStackToChemicalRecipe, SingleItem<ItemStackToChemicalRecipe>> getRecipeType() {
        return switch (getAdvancedFactoryType()) {
            case OXIDIZING -> MekanismRecipeType.OXIDIZING;
            case PIGMENT_EXTRACTING -> MekanismRecipeType.PIGMENT_EXTRACTING;
            default -> throw new IllegalStateException("Unhandled factory type");
        };
    }

    @Override
    public IRecipeViewerRecipeType<ItemStackToChemicalRecipe> recipeViewerType() {
        return switch (getAdvancedFactoryType()) {
            case OXIDIZING -> RecipeViewerRecipeType.OXIDIZING;
            case PIGMENT_EXTRACTING -> RecipeViewerRecipeType.PIGMENT_EXTRACTING;
            default -> throw new IllegalStateException("Unhandled factory type");
        };
    }

    @Override
    public @Nullable ItemStackToChemicalRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandlers[cacheIndex]);
    }

    @Override
    public @NotNull CachedRecipe<ItemStackToChemicalRecipe> createNewCachedRecipe(@NotNull ItemStackToChemicalRecipe recipe, int cacheIndex) {
        return OneInputCachedRecipe.itemToChemical(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], chemicalOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(this::canFunction)
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks)
                .setBaselineMaxOperations(this::getOperationsPerTick);
    }

    @Override
    public @Nullable ItemToChemicalUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new ItemToChemicalUpgradeData(provider, redstone, getControlType(), getEnergyContainer(),
                progress, energySlot, inputItemSlots, outputChemicalTanks, isSorting(), getComponents());
    }
}
