package com.jerry.mekmm.common.tile.factory;

import com.jerry.mekmm.api.recipes.RecyclerRecipe;
import com.jerry.mekmm.api.recipes.cache.MoreMachineOneInputCachedRecipe;
import com.jerry.mekmm.api.recipes.outputs.MoreMachineOutputHelper;
import com.jerry.mekmm.common.inventory.slot.MoreMachineFactoryInputInventorySlot;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;

import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tier.FactoryTier;
import mekanism.common.upgrade.MachineUpgradeData;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityRecyclingFactory extends TileEntityMoreMachineFactory<RecyclerRecipe> implements ISingleRecipeLookupHandler.ItemRecipeLookupHandler<RecyclerRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);

    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(RecipeError.NOT_ENOUGH_ENERGY);

    protected IInputHandler<@NotNull ItemStack>[] inputHandlers;
    protected IOutputHandler<RecyclerRecipe.ChanceOutput>[] outputHandlers;

    public TileEntityRecyclingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        inputHandlers = new IInputHandler[tier.processes];
        outputHandlers = new IOutputHandler[tier.processes];
        processInfoSlots = new ProcessInfo[tier.processes];
        int baseX = tier == FactoryTier.BASIC ? 55 : tier == FactoryTier.ADVANCED ? 35 : tier == FactoryTier.ELITE ? 29 : 27;
        int baseXMult = tier == FactoryTier.BASIC ? 38 : tier == FactoryTier.ADVANCED ? 26 : 19;
        for (int i = 0; i < tier.processes; i++) {
            int xPos = baseX + (i * baseXMult);
            OutputInventorySlot outputSlot = OutputInventorySlot.at(updateSortingListener, xPos, 57);
            // Note: As we are an item factory that has comparator's based on items we can just use the monitor as a
            // listener directly
            MoreMachineFactoryInputInventorySlot inputSlot = MoreMachineFactoryInputInventorySlot.create(this, i, outputSlot, recipeCacheLookupMonitors[i], xPos, 13);
            int index = i;
            builder.addSlot(inputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT, index)));
            builder.addSlot(outputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index)));

            inputHandlers[i] = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT);
            outputHandlers[i] = MoreMachineOutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
            processInfoSlots[i] = new ProcessInfo(i, inputSlot, outputSlot, null);
        }
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<RecyclerRecipe> cached, @NotNull ItemStack stack) {
        return cached != null && cached.getRecipe().getInput().testType(stack);
    }

    @Override
    protected @Nullable RecyclerRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot chanceOutputSlot, @Nullable IInventorySlot outputSlot) {
        ItemStack output = chanceOutputSlot.getStack();
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, recipe -> InventoryUtils.areItemsStackable(recipe.getOutput(fallbackInput).getMaxChanceOutput(), output));
    }

    @Override
    protected int getNeededInput(RecyclerRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    @Override
    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipe(stack);
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<RecyclerRecipe, InputRecipeCache.SingleItem<RecyclerRecipe>> getRecipeType() {
        return MoreMachineRecipeType.RECYCLING;
    }

    @Override
    public @Nullable RecyclerRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandlers[cacheIndex]);
    }

    @Override
    public @NotNull CachedRecipe<RecyclerRecipe> createNewCachedRecipe(@NotNull RecyclerRecipe recipe, int cacheIndex) {
        return MoreMachineOneInputCachedRecipe.recycler(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], outputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks);
    }

    @NotNull
    @Override
    public MachineUpgradeData getUpgradeData() {
        return new MachineUpgradeData(redstone, getControlType(), getEnergyContainer(), progress, energySlot, inputSlots, outputSlots, isSorting(), getComponents());
    }
}
