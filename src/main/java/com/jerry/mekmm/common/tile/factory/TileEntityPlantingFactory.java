package com.jerry.mekmm.common.tile.factory;

import com.jerry.mekmm.api.recipes.PlantingRecipe;
import com.jerry.mekmm.api.recipes.PlantingRecipe.PlantingStationRecipeOutput;
import com.jerry.mekmm.api.recipes.cache.PlantingCacheRecipe;
import com.jerry.mekmm.api.recipes.outputs.MoreMachineOutputHelper;
import com.jerry.mekmm.common.inventory.slot.MoreMachineFactoryInputInventorySlot;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.tile.machine.TileEntityPlantingStation;
import com.jerry.mekmm.common.upgrade.PlantingUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToItemStackCachedRecipe.ChemicalUsageMultiplier;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.IRecipeLookupHandler.ConstantUsageRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TileEntityPlantingFactory extends TileEntityMoreMachineFactory<PlantingRecipe> implements IHasDumpButton, ConstantUsageRecipeLookupHandler,
                                       ItemChemicalRecipeLookupHandler<Gas, GasStack, PlantingRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            TileEntityPlantingStation.NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT);

    private IInputHandler<@NotNull ItemStack>[] inputHandlers;
    private final ILongInputHandler<@NotNull GasStack> gasInputHandler;
    private IOutputHandler<PlantingStationRecipeOutput>[] outputHandlers;

    GasInventorySlot gasSlot;

    @Getter
    IGasTank gasTank;

    private final ChemicalUsageMultiplier chemicalUsageMultiplier;
    private long baseTotalUsage;
    private final long[] usedSoFar;

    public TileEntityPlantingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        configComponent.addSupported(TransmissionType.GAS);
        configComponent.setupInputConfig(TransmissionType.GAS, gasTank);
        configComponent.setupIOConfig(TransmissionType.GAS, gasTank, RelativeSide.RIGHT).setCanEject(false);

        baseTotalUsage = BASE_TICKS_REQUIRED;
        usedSoFar = new long[tier.processes];
        chemicalUsageMultiplier = (usedSoFar, operatingTicks) -> {
            long baseRemaining = baseTotalUsage - usedSoFar;
            int remainingTicks = getTicksRequired() - operatingTicks;
            if (baseRemaining < remainingTicks) {
                // If we already used more than we would need to use (due to removing speed upgrades or adding gas
                // upgrades)
                // then just don't use any gas this tick
                return 0;
            } else if (baseRemaining == remainingTicks) {
                return 1;
            }
            return Math.max(MathUtils.clampToLong(baseRemaining / (double) remainingTicks), 0);
        };

        gasInputHandler = InputHelper.getConstantInputHandler(gasTank);
    }

    @Override
    public @Nullable IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSideGasWithConfig(this::getDirection, this::getConfig);
        gasTank = ChemicalTankBuilder.GAS.create(TileEntityAdvancedElectricMachine.MAX_GAS * tier.processes, this::containsRecipeB,
                markAllMonitorsChanged(listener));
        builder.addTank(gasTank);
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
            OutputInventorySlot outputSlot = OutputInventorySlot.at(updateSortingListener, xPos, 57);
            OutputInventorySlot secondaryOutputSlot = OutputInventorySlot.at(updateSortingListener, xPos, 77);
            // Note: As we are an item factory that has comparator's based on items we can just use the monitor as a
            // listener directly
            MoreMachineFactoryInputInventorySlot inputSlot = MoreMachineFactoryInputInventorySlot.create(this, i, outputSlot, secondaryOutputSlot, recipeCacheLookupMonitors[i], xPos, 13);
            int index = i;
            builder.addSlot(inputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT, index)));
            builder.addSlot(outputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index)));
            builder.addSlot(secondaryOutputSlot).tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT,
                    getWarningCheck(TileEntityPlantingStation.NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR, index)));
            inputHandlers[i] = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT);
            outputHandlers[i] = MoreMachineOutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE, secondaryOutputSlot, TileEntityPlantingStation.NOT_ENOUGH_SPACE_SECONDARY_OUTPUT_ERROR);
            processInfoSlots[i] = new ProcessInfo(i, inputSlot, outputSlot, secondaryOutputSlot);
        }
        builder.addSlot(gasSlot = GasInventorySlot.fillOrConvert(gasTank, this::getLevel, listener, 7, 77));
    }

    @Nullable
    @Override
    public GasInventorySlot getExtraSlot() {
        return gasSlot;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<PlantingRecipe, InputRecipeCache.ItemChemical<Gas, GasStack, PlantingRecipe>> getRecipeType() {
        return MoreMachineRecipeType.PLANTING;
    }

    @Override
    public @Nullable PlantingRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandlers[cacheIndex], gasInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<PlantingRecipe> createNewCachedRecipe(@NotNull PlantingRecipe recipe, int cacheIndex) {
        return PlantingCacheRecipe.create(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], gasInputHandler,
                chemicalUsageMultiplier, used -> usedSoFar[cacheIndex] = used, outputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
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
            return cachedRecipe.getItemInput().testType(stack) && (gasTank.isEmpty() || cachedRecipe.getGasInput().testType(gasTank.getStack()));
        }
        return false;
    }

    @Override
    protected @Nullable PlantingRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot) {
        GasStack gasInput = gasTank.getStack();
        ItemStack output = outputSlot.getStack();
        ItemStack extra = secondaryOutputSlot == null ? ItemStack.EMPTY : secondaryOutputSlot.getStack();
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, gasInput, recipe -> {
            PlantingStationRecipeOutput chanceOutput = recipe.getOutput(fallbackInput, gasInput);
            if (InventoryUtils.areItemsStackable(chanceOutput.getMainOutput(), output)) {
                if (extra.isEmpty()) {
                    return true;
                }
                ItemStack secondaryOutput = chanceOutput.getMaxSecondaryOutput();
                return secondaryOutput.isEmpty() || ItemHandlerHelper.canItemStacksStack(secondaryOutput, extra);
            }
            return false;
        });
    }

    @Override
    protected void handleSecondaryFuel() {
        gasSlot.fillTankOrConvert();
    }

    @Override
    protected int getNeededInput(PlantingRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getItemInput().getNeededAmount(inputStack));
    }

    @Override
    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return containsRecipeAB(stack, gasTank.getStack());
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
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(NBTConstants.USED_SO_FAR, Tag.TAG_LONG_ARRAY)) {
            long[] savedUsed = nbt.getLongArray(NBTConstants.USED_SO_FAR);
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
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        nbtTags.putLongArray(NBTConstants.USED_SO_FAR, Arrays.copyOf(usedSoFar, usedSoFar.length));
    }

    @Override
    public long getSavedUsedSoFar(int cacheIndex) {
        return usedSoFar[cacheIndex];
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof PlantingUpgradeData data) {
            // Generic factory upgrade data handling
            super.parseUpgradeData(upgradeData);
            // Copy the contents using NBT so that if it is not actually valid due to a reload we don't crash
            gasTank.deserializeNBT(data.stored.serializeNBT());
            gasSlot.deserializeNBT(data.gasSlot.serializeNBT());
            System.arraycopy(data.usedSoFar, 0, usedSoFar, 0, data.usedSoFar.length);
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @NotNull
    @Override
    public PlantingUpgradeData getUpgradeData() {
        return new PlantingUpgradeData(redstone, getControlType(), getEnergyContainer(), progress, usedSoFar, gasTank, energySlot, gasSlot,
                inputSlots, outputSlots, isSorting(), getComponents());
    }

    @Override
    public void dump() {
        gasTank.setEmpty();
    }
}
