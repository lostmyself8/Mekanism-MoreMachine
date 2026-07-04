package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.recipes.TripleItemToItemRecipe;
import com.jerry.mekmm.api.recipes.cache.ThreeInputCachedRecipe;
import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.recipe.lookup.cache.MoreMachineInputRecipeCache;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.IContentsListener;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.capabilities.holder.single.SingleConfigHolder;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.ITripleRecipeLookupHandler;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileEntityPresser extends TileEntityProgressMachine<TripleItemToItemRecipe>
                               implements ITripleRecipeLookupHandler<Item, ItemStack, Item, ItemStack, Item, ItemStack, TripleItemToItemRecipe, MoreMachineInputRecipeCache.TripleItem<TripleItemToItemRecipe>> {

    public static final RecipeError NOT_ENOUGH_PRIMARY_INPUT_ERROR = RecipeError.create();
    public static final RecipeError NOT_ENOUGH_TERTIARY_INPUT_ERROR = RecipeError.create();

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            NOT_ENOUGH_PRIMARY_INPUT_ERROR,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            NOT_ENOUGH_TERTIARY_INPUT_ERROR,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    public static final int BASE_TICKS_REQUIRED = 10 * SharedConstants.TICKS_PER_SECOND;

    private final IOutputHandler<@NotNull ItemStackTemplate> outputHandler;
    private final IInputHandler<Item, @NotNull ItemStack> primaryInputHandler;
    private final IInputHandler<Item, @NotNull ItemStack> secondaryInputHandler;
    private final IInputHandler<Item, @NotNull ItemStack> tertiaryInputHandler;

    private MachineEnergyContainer<TileEntityPresser> energyContainer;

    public MachineEnergyContainer<TileEntityPresser> getEnergyContainerTyped() {
        return energyContainer;
    }

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getPrimaryInput", docPlaceholder = "primary input slot")
    InputInventorySlot primaryItemInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getSecondaryInput", docPlaceholder = "secondary input slot")
    InputInventorySlot secondaryItemInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getTertiaryInput", docPlaceholder = "tertiary input slot")
    InputInventorySlot tertiaryItemInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutput", docPlaceholder = "output slot")
    OutputInventorySlot outputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityPresser(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.PRESSER, pos, state, TRACKED_ERROR_TYPES, BASE_TICKS_REQUIRED);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.INPUT_1, new InventorySlotInfo(true, false, primaryItemInputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_2, new InventorySlotInfo(true, false, secondaryItemInputSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(false, true, outputSlot));
            itemConfig.addSlotInfo(DataType.INPUT_OUTPUT, new InventorySlotInfo(true, true, primaryItemInputSlot, secondaryItemInputSlot, outputSlot));
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, tertiaryItemInputSlot));
            itemConfig.addSlotInfo(DataType.ENERGY, new InventorySlotInfo(true, true, energySlot));
        }
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        primaryInputHandler = InputHelper.getInputHandler(primaryItemInputSlot, NOT_ENOUGH_PRIMARY_INPUT_ERROR);
        secondaryInputHandler = InputHelper.getInputHandler(secondaryItemInputSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        tertiaryInputHandler = InputHelper.getInputHandler(tertiaryItemInputSlot, NOT_ENOUGH_TERTIARY_INPUT_ERROR);
        outputHandler = OutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @NotNull
    @Override
    protected ISingleContainerHolder<IEnergyContainer> getInitialEnergyContainer(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener);
        return SingleConfigHolder.energy(energyContainer, this);
    }

    @NotNull
    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(primaryItemInputSlot = InputInventorySlot.at((item, _) -> containsRecipeABC(item.toStack(), secondaryItemInputSlot.resource().toStack(secondaryItemInputSlot.amountAsInt()), tertiaryItemInputSlot.resource().toStack(tertiaryItemInputSlot.amountAsInt())),
                item -> containsRecipeA(item.toStack(1)), recipeCacheListener, 64, 16))
                .tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(NOT_ENOUGH_PRIMARY_INPUT_ERROR)));
        builder.addContainer(secondaryItemInputSlot = InputInventorySlot.at((item, _) -> containsRecipeBAC(primaryItemInputSlot.resource().toStack(primaryItemInputSlot.amountAsInt()), item.toStack(), tertiaryItemInputSlot.resource().toStack(tertiaryItemInputSlot.amountAsInt())),
                item -> containsRecipeB(item.toStack(1)), recipeCacheListener, 64, 35))
                .tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT)));
        builder.addContainer(tertiaryItemInputSlot = InputInventorySlot.at((item, _) -> containsRecipeCAB(primaryItemInputSlot.resource().toStack(primaryItemInputSlot.amountAsInt()), secondaryItemInputSlot.resource().toStack(secondaryItemInputSlot.amountAsInt()), item.toStack()),
                item -> containsRecipeC(item.toStack(1)), recipeCacheListener, 64, 54))
                .tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(NOT_ENOUGH_TERTIARY_INPUT_ERROR)));
        builder.addContainer(outputSlot = OutputInventorySlot.at(recipeCacheUnpauseListener, 116, 35))
                .tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE)));
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 41, 35));
        tertiaryItemInputSlot.setSlotType(ContainerSlotType.EXTRA);
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer(net.minecraft.server.level.ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        energySlot.fillContainerOrConvert(null);
        recipeCacheLookupMonitor.updateAndProcess();
        return sendUpdatePacket;
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
        return findFirstRecipe(primaryInputHandler, secondaryInputHandler, tertiaryInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<TripleItemToItemRecipe> createNewCachedRecipe(@NotNull TripleItemToItemRecipe recipe, int cacheIndex) {
        return ThreeInputCachedRecipe.tripleItemToItem(recipe, recheckAllRecipeErrors, primaryInputHandler, secondaryInputHandler, tertiaryInputHandler, outputHandler)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(this::canFunction)
                .setActive(this::setActive)
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(this::setOperatingTicks)
                .setBaselineMaxOperations(this::getOperationsPerTick);
    }

    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameMMTypeFactory(getBlockHolder(), type);
    }

    @ComputerMethod(methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    long getEnergyUsage() {
        return getActive() ? energyContainer.getEnergyPerTick() : 0L;
    }
}
