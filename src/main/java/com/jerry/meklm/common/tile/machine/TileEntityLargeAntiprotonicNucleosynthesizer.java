package com.jerry.meklm.common.tile.machine;

import com.jerry.meklm.common.capabilities.holder.chemical.AdjustableChemicalTankHelper;
import com.jerry.meklm.common.inventory.slot.BigStackInputInventorySlot;
import com.jerry.meklm.common.inventory.slot.BigStackOutputInventorySlot;
import com.jerry.meklm.common.registries.LargeMachineBlocks;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.SerializationConstants;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.NucleosynthesizingRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToObjectCachedRecipe;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToObjectCachedRecipe.ChemicalUsageMultiplier;
import mekanism.api.recipes.cache.TwoInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.IRecipeLookupHandler.ConstantUsageRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.ItemChemical;
import mekanism.common.recipe.lookup.monitor.NucleosynthesizerRecipeCacheLookupMonitor;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.util.WorldUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidType;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TileEntityLargeAntiprotonicNucleosynthesizer extends TileEntityProgressMachine<NucleosynthesizingRecipe> implements ConstantUsageRecipeLookupHandler,
                                                          ItemChemicalRecipeLookupHandler<NucleosynthesizingRecipe>, IBoundingBlock {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    public static final int BASE_TICKS_REQUIRED = 20 * SharedConstants.TICKS_PER_SECOND;
    public static final long MAX_GAS = 10L * FluidType.BUCKET_VOLUME;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getInputChemical", "getInputChemicalCapacity", "getInputChemicalNeeded",
                                    "getInputChemicalFilledPercentage" },
                            docPlaceholder = "input gas tank")
    public IChemicalTank gasTank;

    private final ChemicalUsageMultiplier chemicalUsageMultiplier = ChemicalUsageMultiplier.constantUse(() -> ticksRequired, this::getTicksRequired);
    private final int baselineMaxOperations = 64;
    private long usedSoFar;
    private int numPowering;

    protected final IOutputHandler<@NotNull ItemStack> outputHandler;
    protected final IInputHandler<@NotNull ItemStack> itemInputHandler;
    protected final ILongInputHandler<@NotNull ChemicalStack> gasInputHandler;

    @Getter
    private MachineEnergyContainer<TileEntityLargeAntiprotonicNucleosynthesizer> energyContainer;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputChemicalItem", docPlaceholder = "input gas item slot")
    ChemicalInventorySlot gasInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "input item slot")
    BigStackInputInventorySlot inputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "output slot")
    BigStackOutputInventorySlot outputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    private long clientEnergyUsed = 0L;

    public TileEntityLargeAntiprotonicNucleosynthesizer(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER, pos, state, TRACKED_ERROR_TYPES, BASE_TICKS_REQUIRED);
        configComponent.setupItemIOExtraConfig(inputSlot, outputSlot, gasInputSlot, energySlot);
        configComponent.setupInputConfig(TransmissionType.CHEMICAL, gasTank);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        itemInputHandler = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT);
        gasInputHandler = InputHelper.getInputHandler(gasTank, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @Override
    protected RecipeCacheLookupMonitor<NucleosynthesizingRecipe> createNewCacheMonitor() {
        return new NucleosynthesizerRecipeCacheLookupMonitor(this);
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        AdjustableChemicalTankHelper builder = AdjustableChemicalTankHelper.forSide(facingSupplier, side -> side == RelativeSide.BACK, side -> false);
        builder.addTank(gasTank = BasicChemicalTank.inputModern(MAX_GAS, gas -> containsRecipeBA(inputSlot.getStack(), gas), this::containsRecipeB, recipeCacheListener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(facingSupplier);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier, side -> side == RelativeSide.BACK, side -> side == RelativeSide.BACK);
        builder.addSlot(gasInputSlot = ChemicalInventorySlot.fillOrConvert(gasTank, this::getLevel, listener, 6, 69), RelativeSide.BACK);
        builder.addSlot(inputSlot = BigStackInputInventorySlot.at(item -> containsRecipeAB(item, gasTank.getStack()), this::containsRecipeA, recipeCacheListener, 26, 40), RelativeSide.BACK)
                .tracksWarnings(slot -> slot.warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT)));
        builder.addSlot(outputSlot = BigStackOutputInventorySlot.at(recipeCacheUnpauseListener, 152, 40), RelativeSide.BACK)
                .tracksWarnings(slot -> slot.warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE)));
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 173, 69), RelativeSide.BACK);
        gasInputSlot.setSlotOverlay(SlotOverlay.MINUS);
        return builder.build();
    }

    public double getProcessRate() {
        return (double) clientEnergyUsed / energyContainer.getEnergyPerTick();
    }

    @ComputerMethod(nameOverride = "getEnergyUsage", methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    public long getEnergyUsed() {
        return clientEnergyUsed;
    }

    @Override
    public void onCachedRecipeChanged(@Nullable CachedRecipe<NucleosynthesizingRecipe> cachedRecipe, int cacheIndex) {
        super.onCachedRecipeChanged(cachedRecipe, cacheIndex);
        // Note: Because we don't support speed upgrades we can do this in a much cleaner way than how we have to do it
        // for the PRC
        ticksRequired = cachedRecipe == null ? BASE_TICKS_REQUIRED : cachedRecipe.getRecipe().getDuration();
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        gasInputSlot.fillTankOrConvert();
        clientEnergyUsed = recipeCacheLookupMonitor.updateAndProcess(energyContainer);
        return sendUpdatePacket;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<SingleItemChemicalRecipeInput, NucleosynthesizingRecipe, ItemChemical<NucleosynthesizingRecipe>> getRecipeType() {
        return MekanismRecipeType.NUCLEOSYNTHESIZING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<NucleosynthesizingRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.NUCLEOSYNTHESIZING;
    }

    @Override
    public @Nullable NucleosynthesizingRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandler, gasInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<NucleosynthesizingRecipe> createNewCachedRecipe(@NotNull NucleosynthesizingRecipe recipe, int cacheIndex) {
        CachedRecipe<NucleosynthesizingRecipe> cachedRecipe;
        if (recipe.perTickUsage()) {
            cachedRecipe = ItemStackConstantChemicalToObjectCachedRecipe.toItem(recipe, recheckAllRecipeErrors, itemInputHandler, gasInputHandler,
                    chemicalUsageMultiplier, used -> usedSoFar = used, outputHandler);
        } else {
            cachedRecipe = TwoInputCachedRecipe.itemChemicalToItem(recipe, recheckAllRecipeErrors, itemInputHandler, gasInputHandler, outputHandler);
        }
        return cachedRecipe
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(this::canFunction)
                .setActive(this::setActive)
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(this::setOperatingTicks)
                .setBaselineMaxOperations(() -> baselineMaxOperations);
    }

    @Override
    public long getSavedUsedSoFar(int cacheIndex) {
        return usedSoFar;
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        usedSoFar = nbt.getLong(SerializationConstants.USED_SO_FAR);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(nbtTags, provider);
        nbtTags.putLong(SerializationConstants.USED_SO_FAR, usedSoFar);
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableLong.create(this::getEnergyUsed, value -> clientEnergyUsed = value));
    }

    @Override
    public boolean isPowered() {
        return redstone || numPowering > 0;
    }

    @Override
    public void onBoundingBlockPowerChange(BlockPos boundingPos, int oldLevel, int newLevel) {
        if (oldLevel > 0) {
            if (newLevel == 0) {
                numPowering--;
            }
        } else if (newLevel > 0) {
            numPowering++;
        }
    }

    @Override
    public int getBoundingComparatorSignal(Vec3i offset) {
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 2, back.getStepZ()))) {
            return getCurrentRedstoneLevel();
        }
        switch (getDirection()) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(left.getStepX(), 2, back.getStepZ()))) {
                    return getCurrentRedstoneLevel();
                }
                if (offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(left.getStepX(), 2, back.getStepZ()))) {
                    return getCurrentRedstoneLevel();
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ())) || offset.equals(new Vec3i(left.getStepX(), 2, back.getStepZ()))) {
                    return getCurrentRedstoneLevel();
                }
                if (offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ())) || offset.equals(new Vec3i(left.getStepX(), 2, back.getStepZ()))) {
                    return getCurrentRedstoneLevel();
                }
            }
        }
        return Redstone.SIGNAL_NONE;
    }

    @Override
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.CHEMICAL.block()) {
            return Objects.requireNonNull(chemicalHandlerManager, "Expected to have chemical handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.CHEMICAL.block()) {
            return notChemicalPort(side, offset);
        } else if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == Capabilities.ITEM.block()) {
            return notItemPort(side, offset);
        }
        return notChemicalPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notChemicalPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (getDirection()) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 2, back.getStepZ()))) {
                    return side != back;
                }
                if (offset.equals(new Vec3i(right.getStepX(), 2, back.getStepZ()))) {
                    return side != back;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 2, left.getStepZ()))) {
                    return side != back;
                }
                if (offset.equals(new Vec3i(back.getStepX(), 2, right.getStepZ()))) {
                    return side != back;
                }
            }
        }
        return true;
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 2, back.getStepZ()))) {
            return side != back;
        }
        return notChemicalPort(side, offset) && notEnergyPort(side, offset);
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (getDirection()) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ()))) {
                    return side != back;
                }
                if (offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ()))) {
                    return side != back;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ()))) {
                    return side != back;
                }
                if (offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ()))) {
                    return side != back;
                }
            }
        }
        return true;
    }
}
