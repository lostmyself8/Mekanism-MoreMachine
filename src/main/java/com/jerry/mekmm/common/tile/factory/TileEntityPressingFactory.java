package com.jerry.mekmm.common.tile.factory;

import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;
import com.jerry.mekmm.api.recipes.cache.ThreeInputCachedRecipe;
import com.jerry.mekmm.client.recipe_viewer.MoreMachineRecipeViewerRecipeType;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.lookup.TripleItemRecipeLookupHandler;
import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache.TripleItem;
import com.jerry.mekmm.common.tile.machine.TileEntityPresser;

import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityPressingFactory extends TileEntityMoreMachineItemToItemFactory<TripleItemToItemRecipe> implements TripleItemRecipeLookupHandler<TripleItemToItemRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            TileEntityPresser.NOT_ENOUGH_PRIMARY_INPUT_ERROR,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            TileEntityPresser.NOT_ENOUGH_TERTIARY_INPUT_ERROR,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            TileEntityPresser.NOT_ENOUGH_TERTIARY_INPUT_ERROR);

    private final IInputHandler<@NotNull ItemStack> secondaryInputHandler;
    private final IInputHandler<@NotNull ItemStack> tertiaryInputHandler;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getSecondaryInput", docPlaceholder = "secondary input slot")
    InputInventorySlot secondarySlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getTertiaryInput", docPlaceholder = "tertiary input slot")
    InputInventorySlot tertiarySlot;

    public TileEntityPressingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT_1, new InventorySlotInfo(true, false, inputSlots));
            itemConfig.addSlotInfo(DataType.INPUT_2, new InventorySlotInfo(true, false, secondarySlot));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(false, true, outputSlots));
            List<IInventorySlot> ioSlots = inputSlots;
            ioSlots.addAll(outputSlots);
            ioSlots.add(secondarySlot);
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, ioSlots));
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, tertiarySlot));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }

        secondaryInputHandler = InputHelper.getInputHandler(secondarySlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        tertiaryInputHandler = InputHelper.getInputHandler(tertiarySlot, TileEntityPresser.NOT_ENOUGH_TERTIARY_INPUT_ERROR);
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addSlots(builder, listener, updateSortingListener);
        builder.addSlot(secondarySlot = InputInventorySlot.at(this::containsRecipeB, markAllMonitorsChanged(listener), 7, 57));
        builder.addSlot(tertiarySlot = InputInventorySlot.at(this::containsRecipeC, markAllMonitorsChanged(listener), 7, 37));
        tertiarySlot.setSlotType(ContainerSlotType.EXTRA);
    }

    @Nullable
    @Override
    protected InputInventorySlot getExtraSlot() {
        return tertiarySlot;
    }

    @Override
    protected boolean defaultsIOConfig() {
        return false;
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<TripleItemToItemRecipe> cached, @NotNull ItemStack stack) {
        if (cached != null) {
            TripleItemToItemRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getFirstInput().testType(stack) && (secondarySlot.isEmpty() || cachedRecipe.getSecondInput().testType(secondarySlot.getStack())) && (tertiarySlot.isEmpty() || cachedRecipe.getThirdInput().testType(tertiarySlot.getStack()));
        }
        return false;
    }

    @Override
    protected @Nullable TripleItemToItemRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot) {
        return getRecipeType().getInputCache().findFirstRecipe(level, fallbackInput, secondarySlot.getStack(), tertiarySlot.getStack());
    }

    @Override
    protected int getNeededInput(TripleItemToItemRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getFirstInput().getNeededAmount(inputStack));
    }

    @Override
    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return containsRecipeABC(stack, secondarySlot.getStack(), tertiarySlot.getStack());
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipeA(stack);
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<RecipeInput, TripleItemToItemRecipe, TripleItem<TripleItemToItemRecipe>> getRecipeType() {
        return MoreMachineRecipeType.PRESSING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<TripleItemToItemRecipe> recipeViewerType() {
        return MoreMachineRecipeViewerRecipeType.PRESSING;
    }

    @Override
    public @Nullable TripleItemToItemRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandlers[cacheIndex], secondaryInputHandler, tertiaryInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<TripleItemToItemRecipe> createNewCachedRecipe(@NotNull TripleItemToItemRecipe recipe, int cacheIndex) {
        return ThreeInputCachedRecipe.TripleItemToItem(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], secondaryInputHandler, tertiaryInputHandler, outputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(this::canFunction)
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks)
                .setBaselineMaxOperations(this::getOperationsPerTick);
    }
}
