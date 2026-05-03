package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.tile.factory.base.TileEntityItemToItemAdvancedFactory;

import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToObjectCachedRecipe;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToObjectCachedRecipe.ChemicalUsageMultiplier;
import mekanism.api.recipes.cache.TwoInputCachedRecipe;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.vanilla_input.SingleItemChemicalRecipeInput;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.Mekanism;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.IRecipeLookupHandler.ConstantUsageRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.DoubleInputRecipeCache.CheckRecipeType;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.AdvancedMachineUpgradeData;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.InventoryUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TileEntityPaintingFactory extends TileEntityItemToItemAdvancedFactory<ItemStackChemicalToItemStackRecipe> implements ConstantUsageRecipeLookupHandler,
                                       ItemChemicalRecipeLookupHandler<ItemStackChemicalToItemStackRecipe>, IHasDumpButton {

    private static final CheckRecipeType<ItemStack, ChemicalStack, ItemStackChemicalToItemStackRecipe, ItemStack> OUTPUT_CHECK = (recipe, input, extra, output) -> InventoryUtils.areItemsStackable(recipe.getOutput(input, extra), output);
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT);

    // Pigment Tank
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getChemicalInput", "getChemicalInputCapacity", "getChemicalInputNeeded",
                                    "getChemicalInputFilledPercentage" },
                            docPlaceholder = "chemical tank")
    public IChemicalTank chemicalTank;

    private final ChemicalUsageMultiplier chemicalUsageMultiplier = ChemicalUsageMultiplier.constantUse(this::getTicksRequired, this::getTicksRequired);
    private final long[] usedSoFar;

    private final ILongInputHandler<Chemical, @NotNull ChemicalStack> chemicalInputHandler;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputChemicalItem", docPlaceholder = "chemical slot")
    ChemicalInventorySlot chemicalInputSlot;

    public TileEntityPaintingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);

        configComponent.setupInputConfig(TransmissionType.CHEMICAL, chemicalTank);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL);
        usedSoFar = new long[tier.processes];

        chemicalInputHandler = InputHelper.getInputHandler(chemicalTank, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
    }

    @Override
    protected void addTanks(ChemicalTankHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        builder.addTank(chemicalTank = BasicChemicalTank.input(MAX_CHEMICAL * tier.processes, this::containsRecipeB, markAllMonitorsChanged(listener)));
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addSlots(builder, listener, updateSortingListener);
        builder.addSlot(chemicalInputSlot = ChemicalInventorySlot.fillOrConvert(chemicalTank, this::getLevel, listener, 7, 57));
        chemicalInputSlot.setSlotOverlay(SlotOverlay.MINUS);
    }

    @Override
    protected @Nullable IInventorySlot getExtraSlot() {
        return chemicalInputSlot;
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ItemStackChemicalToItemStackRecipe> cached, @NotNull ItemStack stack) {
        if (cached != null) {
            ItemStackChemicalToItemStackRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getItemInput().testType(stack) && (chemicalTank.isEmpty() || cachedRecipe.getChemicalInput().testType(chemicalTank.getType()));
        }
        return false;
    }

    @Override
    protected @Nullable ItemStackChemicalToItemStackRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot) {
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, chemicalTank.getStack(), outputSlot.getStack(), OUTPUT_CHECK);
    }

    @Override
    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return containsRecipeBA(stack, chemicalTank.getStack());
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipeA(stack);
    }

    @Override
    protected int getNeededInput(ItemStackChemicalToItemStackRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getItemInput().getNeededAmount(inputStack));
    }

    public IChemicalTank getChemicalTankBar() {
        return chemicalTank;
    }

    @Override
    public boolean hasExtraResourceBar() {
        return true;
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<SingleItemChemicalRecipeInput, ItemStackChemicalToItemStackRecipe, InputRecipeCache.ItemChemical<ItemStackChemicalToItemStackRecipe>> getRecipeType() {
        return MekanismRecipeType.PAINTING;
    }

    @Override
    public IRecipeViewerRecipeType<ItemStackChemicalToItemStackRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.PAINTING;
    }

    @Nullable
    @Override
    public ItemStackChemicalToItemStackRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandlers[cacheIndex], chemicalInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<ItemStackChemicalToItemStackRecipe> createNewCachedRecipe(@NotNull ItemStackChemicalToItemStackRecipe recipe, int cacheIndex) {
        CachedRecipe<ItemStackChemicalToItemStackRecipe> cachedRecipe;
        if (recipe.perTickUsage()) {
            cachedRecipe = ItemStackConstantChemicalToObjectCachedRecipe.toItem(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], chemicalInputHandler,
                    chemicalUsageMultiplier, used -> usedSoFar[cacheIndex] = used, itemOutputHandlers[cacheIndex]);
        } else {
            cachedRecipe = TwoInputCachedRecipe.itemChemicalToItem(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], chemicalInputHandler, itemOutputHandlers[cacheIndex]);
        }
        return cachedRecipe
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
    public long getSavedUsedSoFar(int cacheIndex) {
        return usedSoFar[cacheIndex];
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        input.read(SerializationConstants.USED_SO_FAR, Codec.LONG_STREAM).ifPresentOrElse(savedUsedStream -> {
            long[] savedUsed = savedUsedStream.toArray();
            if (tier.processes != savedUsed.length) {
                Arrays.fill(usedSoFar, 0);
            }
            for (int i = 0; i < tier.processes && i < savedUsed.length; i++) {
                usedSoFar[i] = savedUsed[i];
            }
        }, () -> Arrays.fill(usedSoFar, 0));
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.store(SerializationConstants.USED_SO_FAR, Codec.LONG_STREAM, Arrays.stream(usedSoFar));
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider) {
        if (upgradeData instanceof AdvancedMachineUpgradeData data) {
            super.parseUpgradeData(upgradeData, provider);
            ContainerType.CHEMICAL.copy(data.stored, chemicalTank);
            ContainerType.ITEM.copy(data.chemicalSlot, chemicalInputSlot);
            System.arraycopy(data.usedSoFar, 0, usedSoFar, 0, data.usedSoFar.length);
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @NotNull
    @Override
    public AdvancedMachineUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new AdvancedMachineUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), progress, usedSoFar, chemicalTank, chemicalInputSlot, energySlot,
                inputItemSlots, outputItemSlots, isSorting(), getComponents(), problemPath());
    }

    @Override
    public void dump() {
        chemicalTank.setEmpty();
    }
}
