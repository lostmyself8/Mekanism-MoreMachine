package com.jerry.mekmm.common.tile.factory;

import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;
import com.jerry.mekmm.api.recipes.cache.ThreeInputCachedRecipe;
import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache;
import com.jerry.mekmm.common.tile.machine.TileEntityPresser;

import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.ITripleRecipeLookupHandler;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TileEntityPressingFactory extends TileEntityMoreMachineItemToItemFactory<TripleItemToItemRecipe>
                                       implements ITripleRecipeLookupHandler<Item, ItemStack, Item, ItemStack, Item, ItemStack, TripleItemToItemRecipe, MoreMachineInputRecipeCache.TripleItem<TripleItemToItemRecipe>> {

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

    private final IInputHandler<Item, @NotNull ItemStack> secondaryInputHandler;
    private final IInputHandler<Item, @NotNull ItemStack> tertiaryInputHandler;

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
            List<IInventorySlot> ioSlots = new ArrayList<>(inputSlots);
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
    protected void addSlots(MekContainerHelper<IInventorySlot> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addSlots(builder, listener, updateSortingListener);
        builder.addContainer(secondarySlot = InputInventorySlot.at(this::containsRecipeB, markAllMonitorsChanged(listener), 7, 57));
        builder.addContainer(tertiarySlot = InputInventorySlot.at(this::containsRecipeC, markAllMonitorsChanged(listener), 7, 37));
        tertiarySlot.setSlotType(ContainerSlotType.EXTRA);
    }

    @Nullable
    @Override
    protected InputInventorySlot getExtraSlot() {
        return tertiarySlot;
    }

    @Override
    public boolean isItemValidForSlot(@NotNull ItemResource stack) {
        return containsRecipeABC(stack.toStack(), secondarySlot.resource().toStack(secondarySlot.amountAsInt()), tertiarySlot.resource().toStack(tertiarySlot.amountAsInt()));
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemResource stack) {
        return containsRecipeA(stack.toStack());
    }

    @Override
    protected int getNeededInput(TripleItemToItemRecipe recipe, ItemResource inputStack) {
        return MathUtils.clampToInt(recipe.getFirstInput().getNeededAmount(inputStack));
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<TripleItemToItemRecipe> cached, @NotNull ItemResource stack) {
        if (cached != null) {
            TripleItemToItemRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getFirstInput().testType(stack) && (secondarySlot.isEmpty() || cachedRecipe.getSecondInput().testType(secondarySlot.resource())) &&
                    (tertiarySlot.isEmpty() || cachedRecipe.getThirdInput().testType(tertiarySlot.resource()));
        }
        return false;
    }

    @Override
    protected @Nullable TripleItemToItemRecipe findRecipe(int process, @NotNull ItemResource fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot) {
        return getRecipeType().getInputCache().findFirstRecipe(level, fallbackInput.toStack(), secondarySlot.resource().toStack(secondarySlot.amountAsInt()), tertiarySlot.resource().toStack(tertiarySlot.amountAsInt()));
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<RecipeInput, TripleItemToItemRecipe, MoreMachineInputRecipeCache.TripleItem<TripleItemToItemRecipe>> getRecipeType() {
        return MoreMachineRecipeType.PRESSING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<TripleItemToItemRecipe> recipeViewerType() {
        return MMRecipeViewerRecipeType.PRESSING;
    }

    @Override
    public @Nullable TripleItemToItemRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandlers[cacheIndex], secondaryInputHandler, tertiaryInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<TripleItemToItemRecipe> createNewCachedRecipe(@NotNull TripleItemToItemRecipe recipe, int cacheIndex) {
        return ThreeInputCachedRecipe.tripleItemToItem(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], secondaryInputHandler, tertiaryInputHandler, outputHandlers[cacheIndex])
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
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider, TransactionContext transaction) {
        Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
    }
}
