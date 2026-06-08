package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.recipes.StamperRecipe;
import com.jerry.mekmm.api.recipes.cache.StamperCachedRecipe;
import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.upgrade.StamperUpgradeData;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.IContentsListener;
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
import mekanism.common.capabilities.holder.energy.EnergyConfigHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
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
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.DoubleItem;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileEntityStamper extends TileEntityProgressMachine<StamperRecipe> implements IDoubleRecipeLookupHandler.DoubleItemRecipeLookupHandler<StamperRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    public static final int BASE_TICKS_REQUIRED = 10 * SharedConstants.TICKS_PER_SECOND;

    private final IOutputHandler<@NotNull ItemStackTemplate> outputHandler;
    private final IInputHandler<Item, @NotNull ItemStack> itemInputHandler;
    private final IInputHandler<Item, @NotNull ItemStack> moldInputHandler;

    private MachineEnergyContainer<TileEntityStamper> energyContainer;

    public MachineEnergyContainer<TileEntityStamper> getEnergyContainerTyped() {
        return energyContainer;
    }

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getItemInput", docPlaceholder = "item input slot")
    InputInventorySlot itemInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getMoldInput", docPlaceholder = "mold input slot")
    InputInventorySlot moldInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutput", docPlaceholder = "output slot")
    OutputInventorySlot outputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityStamper(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.CNC_STAMPER, pos, state, TRACKED_ERROR_TYPES, BASE_TICKS_REQUIRED);
        configComponent.setupItemIOExtraConfig(itemInputSlot, outputSlot, moldInputSlot, energySlot);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        itemInputHandler = InputHelper.getInputHandler(itemInputSlot, RecipeError.NOT_ENOUGH_INPUT);
        moldInputHandler = InputHelper.getInputHandler(moldInputSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainer(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener);
        return new EnergyConfigHolder(energyContainer, this);
    }

    @NotNull
    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(itemInputSlot = InputInventorySlot.at((item, automationType) -> containsRecipeAB(item.toStack(), moldInputSlot.resource().toStack(moldInputSlot.amountAsInt())),
                item -> containsRecipeA(item.toStack(1)), recipeCacheListener,
                64, 17)).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT)));
        builder.addContainer(moldInputSlot = InputInventorySlot.at((item, automationType) -> containsRecipeBA(itemInputSlot.resource().toStack(itemInputSlot.amountAsInt()), item.toStack()),
                item -> containsRecipeB(item.toStack(1)), recipeCacheListener,
                64, 53)).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_SECONDARY_INPUT)));
        builder.addContainer(outputSlot = OutputInventorySlot.at(recipeCacheUnpauseListener, 116, 35))
                .tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE)));
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 39, 35));
        moldInputSlot.setSlotType(ContainerSlotType.EXTRA);
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.fillContainerOrConvert(null);
        recipeCacheLookupMonitor.updateAndProcess();
        return sendUpdatePacket;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, StamperRecipe, DoubleItem<StamperRecipe>> getRecipeType() {
        return MoreMachineRecipeType.STAMPING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<StamperRecipe> recipeViewerType() {
        return MMRecipeViewerRecipeType.STAMPING;
    }

    @Nullable
    @Override
    public StamperRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandler, moldInputHandler);
    }

    @NotNull
    @Override
    public CachedRecipe<StamperRecipe> createNewCachedRecipe(@NotNull StamperRecipe recipe, int cacheIndex) {
        return StamperCachedRecipe.createCache(recipe, recheckAllRecipeErrors, itemInputHandler, moldInputHandler, outputHandler)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(this::canFunction)
                .setActive(this::setActive)
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(this::setOperatingTicks)
                .setBaselineMaxOperations(this::getOperationsPerTick);
    }

    @NotNull
    @Override
    public StamperUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new StamperUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), getOperatingTicks(), energySlot, moldInputSlot, itemInputSlot, outputSlot, getComponents(), problemPath());
    }

    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameMMTypeFactory(getBlockHolder(), type);
    }

    // Methods relating to IComputerTile
    @ComputerMethod(methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    long getEnergyUsage() {
        return getActive() ? energyContainer.getEnergyPerTick() : 0L;
    }
    // End methods IComputerTile
}
