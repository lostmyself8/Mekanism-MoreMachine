package com.jerry.mekaf.common.tile;

import com.jerry.mekaf.common.tile.base.TileEntityItemToFluidFactory;
import com.jerry.mekaf.common.upgrade.ItemToFluidUpgradeData;

import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ItemStackToFluidRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.impl.NutritionalLiquifierIRecipe;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityLiquifyingFactory extends TileEntityItemToFluidFactory<ItemStackToFluidRecipe> {

    // 单个槽位报错，例如输入槽和输出槽
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    // GLOBAL对应要统一处理的错误例如这里的输出储罐，在监听时应该用GLOBAL声明的Error才能正常报错
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE);

    public TileEntityLiquifyingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.FLUID);
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ItemStackToFluidRecipe> cached, @NotNull ItemStack stack) {
        return cached != null && isValidInputItem(stack);
    }

    @Override
    protected @Nullable ItemStackToFluidRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IExtendedFluidTank outputTanks) {
        return null;
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        FoodProperties food = stack.getFoodProperties(null);
        return food != null && food.getNutrition() > 0;
    }

    @Override
    protected int getNeededInput(ItemStackToFluidRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<ItemStackToFluidRecipe, ?> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable ItemStackToFluidRecipe getRecipe(int cacheIndex) {
        ItemStack stack = itemInputHandlers[cacheIndex].getInput();
        if (stack.isEmpty() || !stack.getItem().isEdible()) {
            return null;
        }
        FoodProperties food = stack.getFoodProperties(null);
        if (food == null || food.getNutrition() == 0) {
            // If the food provides no healing don't allow consuming it as it won't provide any paste
            return null;
        }
        return new NutritionalLiquifierIRecipe(stack.getItem(), IngredientCreatorAccess.item().from(stack, 1),
                MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(food.getNutrition() * 50));
    }

    @Override
    public @NotNull CachedRecipe<ItemStackToFluidRecipe> createNewCachedRecipe(@NotNull ItemStackToFluidRecipe recipe, int cacheIndex) {
        return OneInputCachedRecipe.itemToFluid(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], fluidOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks);
    }

    @Override
    public @Nullable ItemToFluidUpgradeData getUpgradeData() {
        return new ItemToFluidUpgradeData(redstone, getControlType(), getEnergyContainer(), progress,
                energySlot, inputItemSlots, outputFluidTanks, isSorting(), getComponents());
    }
}
