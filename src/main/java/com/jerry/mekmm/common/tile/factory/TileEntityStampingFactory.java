package com.jerry.mekmm.common.tile.factory;

import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.cache.StamperCachedRecipe;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.upgrade.StamperUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.DoubleItemRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.DoubleItem;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityStampingFactory extends TileEntityItemToItemMoreMachineFactory<StamperRecipe> implements DoubleItemRecipeLookupHandler<StamperRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT);

    private final IInputHandler<@NotNull ItemStack> extraInputHandler;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getSecondaryInput", docPlaceholder = "secondary input slot")
    InputInventorySlot moldSlot;

    public TileEntityStampingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        extraInputHandler = InputHelper.getInputHandler(moldSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addSlots(builder, listener, updateSortingListener);
        builder.addSlot(moldSlot = InputInventorySlot.at(this::containsRecipeB, markAllMonitorsChanged(listener), 7, 57));
        moldSlot.setSlotType(ContainerSlotType.EXTRA);
    }

    @Nullable
    @Override
    protected InputInventorySlot getExtraSlot() {
        return moldSlot;
    }

    @Override
    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return containsRecipeAB(stack, moldSlot.getStack());
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipeA(stack);
    }

    @Override
    protected int getNeededInput(StamperRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<StamperRecipe> cached, @NotNull ItemStack stack) {
        if (cached != null) {
            StamperRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getInput().testType(stack) && (moldSlot.isEmpty() || cachedRecipe.getMold().testType(moldSlot.getStack()));
        }
        return false;
    }

    @Override
    protected StamperRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot) {
        ItemStack extra = moldSlot.getStack();
        ItemStack output = outputSlot.getStack();
        // TODO: Give it something that is not empty when we don't have a stored secondary stack for getting the output?
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, moldSlot.getStack(),
                recipe -> InventoryUtils.areItemsStackable(recipe.getOutput(fallbackInput, extra), output));
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<StamperRecipe, DoubleItem<StamperRecipe>> getRecipeType() {
        return MoreMachineRecipeType.STAMPING;
    }

    @Nullable
    @Override
    public StamperRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandlers[cacheIndex], extraInputHandler);
    }

    @NotNull
    @Override
    public CachedRecipe<StamperRecipe> createNewCachedRecipe(@NotNull StamperRecipe recipe, int cacheIndex) {
        return StamperCachedRecipe.createCache(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], extraInputHandler, outputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks);
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof StamperUpgradeData data) {
            // Generic factory upgrade data handling
            super.parseUpgradeData(upgradeData);
            // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
            moldSlot.deserializeNBT(data.moldSlot.serializeNBT());
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @NotNull
    @Override
    public StamperUpgradeData getUpgradeData() {
        return new StamperUpgradeData(redstone, getControlType(), getEnergyContainer(), progress, energySlot, moldSlot, inputSlots, outputSlots, isSorting(), getComponents());
    }
}
