package com.jerry.mekmm.common.tile.factory;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.cache.MMItemStackConstantChemicalToObjectCachedRecipe;
import com.jerry.mekmm.api.recipes.cache.MMTwoInputCachedRecipe;
import com.jerry.mekmm.api.recipes.outputs.MMOutputHelper;
import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
import com.jerry.mekmm.common.inventory.slot.MMFactoryInputInventorySlot;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.tile.machine.TileEntityPlantingStation;
import com.jerry.mekmm.common.upgrade.PlantingUpgradeData;
import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.Upgrade;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToObjectCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.DoubleInputRecipeCache;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.recipe.lookup.monitor.FactoryRecipeCacheLookupMonitor;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TileEntityPlantingFactory extends MMTileEntityFactory<PlantingRecipe> implements IHasDumpButton, IRecipeLookupHandler.ConstantUsageRecipeLookupHandler,
        IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler<PlantingRecipe> {

    protected static final DoubleInputRecipeCache.CheckRecipeType<ItemStack, ChemicalStack, PlantingRecipe, ItemStack> OUTPUT_CHECK =
            (recipe, input, extra, output) -> InventoryUtils.areItemsStackable(recipe.getOutput(input, extra).first(), output);

    private static final List<CachedRecipe.OperationTracker.RecipeError> TRACKED_ERROR_TYPES = List.of(
            CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY,
            CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT,
            CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            TileEntityPlantingStation.NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR,
            CachedRecipe.OperationTracker.RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );
    private static final Set<CachedRecipe.OperationTracker.RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_ENERGY,
            CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_SECONDARY_INPUT
    );

    private IInputHandler<@NotNull ItemStack>[] inputHandlers;
    private final ILongInputHandler<@NotNull ChemicalStack> chemicalInputHandler;
    private IOutputHandler<PlantingRecipe.PlantingStationRecipeOutput>[] outputHandlers;

    ChemicalInventorySlot chemicalSlot;

    IChemicalTank chemicalTank;

    private final ItemStackConstantChemicalToObjectCachedRecipe.ChemicalUsageMultiplier chemicalUsageMultiplier;
    private double chemicalPerTickMeanMultiplier = 1;
    private long baseTotalUsage;
    private final long[] usedSoFar;

    public TileEntityPlantingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        configComponent.setupInputConfig(TransmissionType.CHEMICAL, chemicalTank);

        chemicalInputHandler = InputHelper.getConstantInputHandler(chemicalTank);

        baseTotalUsage = BASE_TICKS_REQUIRED;
        usedSoFar = new long[tier.processes];
        if (useStatisticalMechanics()) {
            chemicalUsageMultiplier = (usedSoFar, operatingTicks) -> StatUtils.inversePoisson(chemicalPerTickMeanMultiplier);
        } else {
            chemicalUsageMultiplier = ItemStackConstantChemicalToObjectCachedRecipe.ChemicalUsageMultiplier.constantUse(() -> baseTotalUsage, this::getTicksRequired);
        }
    }

    @Override
    public @Nullable IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        ChemicalTankHelper builder = ChemicalTankHelper.forSideWithConfig(this);
        chemicalTank = BasicChemicalTank.inputModern(TileEntityPlantingStation.MAX_GAS * tier.processes, this::containsRecipeB, markAllMonitorsChanged(listener));
        builder.addTank(chemicalTank);
        return builder.build();
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
            FactoryRecipeCacheLookupMonitor<PlantingRecipe> lookupMonitor = recipeCacheLookupMonitors[i];
            IContentsListener updateSortingAndUnpause = () -> {
                updateSortingListener.onContentsChanged();
                lookupMonitor.unpause();
            };
            OutputInventorySlot outputSlot = OutputInventorySlot.at(updateSortingAndUnpause, xPos, 57);
            OutputInventorySlot secondaryOutputSlot = OutputInventorySlot.at(updateSortingAndUnpause, xPos, 77);
            //Note: As we are an item factory that has comparator's based on items we can just use the monitor as a listener directly
            MMFactoryInputInventorySlot inputSlot = MMFactoryInputInventorySlot.create(this, i, outputSlot, secondaryOutputSlot, lookupMonitor, xPos, 13);
            int index = i;
            builder.addSlot(inputSlot).tracksWarnings(slot -> slot.warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT, index)));
            builder.addSlot(outputSlot).tracksWarnings(slot -> slot.warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index)));
            builder.addSlot(secondaryOutputSlot).tracksWarnings(slot -> slot.warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT,
                    getWarningCheck(TileEntityPlantingStation.NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR, index)));
            inputHandlers[i] = InputHelper.getInputHandler(inputSlot, CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_INPUT);
            outputHandlers[i] = MMOutputHelper.getOutputHandler(outputSlot, CachedRecipe.OperationTracker.RecipeError.NOT_ENOUGH_OUTPUT_SPACE, secondaryOutputSlot,
                    TileEntityPlantingStation.NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR);
            processInfoSlots[i] = new ProcessInfo(i, inputSlot, outputSlot, secondaryOutputSlot);
        }
        builder.addSlot(chemicalSlot = ChemicalInventorySlot.fillOrConvert(chemicalTank, this::getLevel, listener, 7, 77));
    }

    protected boolean useStatisticalMechanics() {
        return MekanismConfig.usage.randomizedConsumption.get();
    }

    public IChemicalTank getChemicalTank() {
        return chemicalTank;
    }

    @Nullable
    @Override
    public ChemicalInventorySlot getExtraSlot() {
        return chemicalSlot;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, PlantingRecipe, InputRecipeCache.ItemChemical<PlantingRecipe>> getRecipeType() {
        return MoreMachineRecipeType.PLANTING_STATION;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<PlantingRecipe> recipeViewerType() {
        return MMRecipeViewerRecipeType.PLANTING_STATION;
    }

    @Override
    public @Nullable PlantingRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandlers[cacheIndex], chemicalInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<PlantingRecipe> createNewCachedRecipe(@NotNull PlantingRecipe recipe, int cacheIndex) {
        CachedRecipe<PlantingRecipe> cachedRecipe;
        if (recipe.perTickUsage()) {
            cachedRecipe = MMItemStackConstantChemicalToObjectCachedRecipe.planting(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], chemicalInputHandler,
                    chemicalUsageMultiplier, used -> usedSoFar[cacheIndex] = used, outputHandlers[cacheIndex]);
        } else {
            cachedRecipe = MMTwoInputCachedRecipe.planting(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], chemicalInputHandler, outputHandlers[cacheIndex]);
        }
        return cachedRecipe
                //设置错误更改
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(this::canFunction)
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks);
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<PlantingRecipe> cached, @NotNull ItemStack stack) {
        if (cached != null) {
            PlantingRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getItemInput().testType(stack) && (chemicalTank.isEmpty() || cachedRecipe.getChemicalInput().testType(chemicalTank.getTypeHolder()));
        }
        return false;
    }

    @Override
    protected @Nullable PlantingRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot) {
        //TODO: Give it something that is not empty when we don't have a stored gas stack for getting the output?
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, chemicalTank.getStack(), outputSlot.getStack(), OUTPUT_CHECK);
    }

    @Override
    protected void handleSecondaryFuel() {
        chemicalSlot.fillTankOrConvert();
    }

    @Override
    protected int getNeededInput(PlantingRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getItemInput().getNeededAmount(inputStack));
    }

    @Override
    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return containsRecipeAB(stack, chemicalTank.getStack());
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipeA(stack);
    }

    @Override
    public boolean hasSecondaryResourceBar() {
        return true;
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        if (nbt.contains(SerializationConstants.USED_SO_FAR, Tag.TAG_LONG_ARRAY)) {
            long[] savedUsed = nbt.getLongArray(SerializationConstants.USED_SO_FAR);
            if (tier.processes != savedUsed.length) {
                Arrays.fill(usedSoFar, 0);
            }
            for (int i = 0; i < tier.processes && i < savedUsed.length; i++) {
                usedSoFar[i] = savedUsed[i];
            }
        } else {
            Arrays.fill(usedSoFar, 0);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(nbtTags, provider);
        nbtTags.putLongArray(SerializationConstants.USED_SO_FAR, Arrays.copyOf(usedSoFar, usedSoFar.length));
    }

    @Override
    public long getSavedUsedSoFar(int cacheIndex) {
        return usedSoFar[cacheIndex];
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED || upgrade == Upgrade.CHEMICAL && supportsUpgrade(Upgrade.CHEMICAL)) {
            if (useStatisticalMechanics()) {
                chemicalPerTickMeanMultiplier = MekanismUtils.getGasPerTickMeanMultiplier(this);
            } else {
                baseTotalUsage = MekanismUtils.getBaseUsage(this, BASE_TICKS_REQUIRED);
            }
        }
    }

    @Override
    public void parseUpgradeData(HolderLookup.Provider provider, @NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof PlantingUpgradeData data) {
            //Generic factory upgrade data handling
            super.parseUpgradeData(provider, upgradeData);
            //Copy the contents using NBT so that if it is not actually valid due to a reload we don't crash
            chemicalTank.deserializeNBT(provider, data.stored.serializeNBT(provider));
            chemicalSlot.deserializeNBT(provider, data.chemicalSlot.serializeNBT(provider));
            System.arraycopy(data.usedSoFar, 0, usedSoFar, 0, data.usedSoFar.length);
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @NotNull
    @Override
    public PlantingUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new PlantingUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), progress, usedSoFar, chemicalTank, energySlot, chemicalSlot,
                inputSlots, outputSlots, isSorting(), getComponents());
    }

    @Override
    public void dump() {
        chemicalTank.setEmpty();
    }
}
