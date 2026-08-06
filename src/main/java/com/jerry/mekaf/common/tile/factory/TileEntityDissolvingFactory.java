package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.tile.factory.base.TileEntityItemToChemicalFactory;
import com.jerry.mekaf.common.upgrade.ItemChemicalToChemicalUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.Upgrade;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ChemicalDissolutionRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToObjectCachedRecipe;
import mekanism.api.recipes.cache.ItemStackConstantChemicalToObjectCachedRecipe.ChemicalUsageMultiplier;
import mekanism.api.recipes.cache.TwoInputCachedRecipe;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.Mekanism;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
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
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.IUpgradeData;
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TileEntityDissolvingFactory extends TileEntityItemToChemicalFactory<ChemicalDissolutionRecipe, AdvancedFactoryType> implements IHasDumpButton, ConstantUsageRecipeLookupHandler,
                                         ItemChemicalRecipeLookupHandler<ChemicalDissolutionRecipe> {

    private static final CheckRecipeType<ItemStack, ChemicalStack, ChemicalDissolutionRecipe, ChemicalStack> OUTPUT_CHECK = (recipe, input, extra, output) -> output.isEmpty() || ChemicalStack.isSameChemical(recipe.getOutput(input, extra), output);
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT);

    private final ILongInputHandler<@NotNull ChemicalStack> chemicalInputHandler;

    private final ChemicalUsageMultiplier injectUsageMultiplier;
    private double injectUsage = 1;
    private final long[] usedSoFar;

    // Gas Tank
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class,
                            methodNames = { "getChemicalInput", "getChemicalInputCapacity", "getChemicalInputNeeded",
                                    "getChemicalInputFilledPercentage" },
                            docPlaceholder = "chemical input tank")
    public IChemicalTank chemicalTank;

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getInputChemicalItem", docPlaceholder = "chemical input item slot")
    ChemicalInventorySlot chemicalInputSlot;

    public TileEntityDissolvingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES, Attribute.getOrThrow(blockProvider, AttributeAdvancedFactoryType.class).getAdvancedFactoryType());

        ConfigInfo chemicalConfig = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (chemicalConfig != null) {
            chemicalConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo(true, false, chemicalTank));
            List<IChemicalTank> ioTank = new ArrayList<>(List.of(chemicalTank));
            ioTank.addAll(outputChemicalTanks);
            // 这个只能设定一个
            chemicalConfig.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo(true, true, ioTank));
        }

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                // 有多个储罐时可以使用该方法指定某个罐是否可以弹出
                .setCanTankEject(tank -> tank != chemicalTank);
        usedSoFar = new long[tier.processes];

        chemicalInputHandler = InputHelper.getConstantInputHandler(chemicalTank);

        injectUsageMultiplier = (usedSoFar, operatingTicks) -> StatUtils.inversePoisson(injectUsage);
    }

    @Override
    protected void addTanks(ChemicalTankHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addTanks(builder, listener, updateSortingListener);
        builder.addTank(chemicalTank = BasicChemicalTank.inputModern(MAX_CHEMICAL * tier.processes, this::containsRecipeB, markAllMonitorsChanged(listener)));
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addSlots(builder, listener, updateSortingListener);
        builder.addSlot(chemicalInputSlot = ChemicalInventorySlot.fillOrConvert(chemicalTank, this::getLevel, listener, 7, 70));
        chemicalInputSlot.setSlotOverlay(SlotOverlay.MINUS);
    }

    public IChemicalTank getChemicalTankBar() {
        return chemicalTank;
    }

    @Override
    protected void handleSecondaryFuel() {
        chemicalInputSlot.fillTankOrConvert();
    }

    @Override
    public boolean hasExtraResourceBar() {
        return true;
    }

    @Override
    protected @Nullable IInventorySlot getExtraSlot() {
        return chemicalInputSlot;
    }

    @Override
    @Contract("null, _ -> false")
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ChemicalDissolutionRecipe> cached, @NotNull ItemStack stack) {
        if (cached != null) {
            ChemicalDissolutionRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getItemInput().testType(stack) && (chemicalTank.isEmpty() || cachedRecipe.getChemicalInput().testType(chemicalTank.getTypeHolder()));
        }
        return false;
    }

    @Override
    protected @Nullable ChemicalDissolutionRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IChemicalTank outputSlot) {
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, chemicalTank.getStack(), outputSlot.getStack(), OUTPUT_CHECK);
    }

    @Override
    protected int getNeededInput(ChemicalDissolutionRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getItemInput().getNeededAmount(inputStack));
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
    public @NotNull IMekanismRecipeTypeProvider<?, ChemicalDissolutionRecipe, InputRecipeCache.ItemChemical<ChemicalDissolutionRecipe>> getRecipeType() {
        return MekanismRecipeType.DISSOLUTION;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<ChemicalDissolutionRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.DISSOLUTION;
    }

    @Override
    public @Nullable ChemicalDissolutionRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandlers[cacheIndex], chemicalInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<ChemicalDissolutionRecipe> createNewCachedRecipe(@NotNull ChemicalDissolutionRecipe recipe, int cacheIndex) {
        CachedRecipe<ChemicalDissolutionRecipe> cachedRecipe;
        if (recipe.perTickUsage()) {
            cachedRecipe = ItemStackConstantChemicalToObjectCachedRecipe.dissolution(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], chemicalInputHandler,
                    injectUsageMultiplier, used -> usedSoFar[cacheIndex] = used, chemicalOutputHandlers[cacheIndex]);
        } else {
            cachedRecipe = TwoInputCachedRecipe.itemChemicalToChemical(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], chemicalInputHandler, chemicalOutputHandlers[cacheIndex]);
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
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.CHEMICAL || upgrade == Upgrade.SPEED) {
            injectUsage = MekanismUtils.getGasPerTickMeanMultiplier(this);
        }
    }

    @Override
    public long getSavedUsedSoFar(int cacheIndex) {
        return usedSoFar[cacheIndex];
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
    public void parseUpgradeData(HolderLookup.Provider provider, @NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof ItemChemicalToChemicalUpgradeData data) {
            super.parseUpgradeData(provider, upgradeData);
            chemicalTank.deserializeNBT(provider, data.inputTank.serializeNBT(provider));
            chemicalInputSlot.deserializeNBT(provider, data.chemicalSlot.serializeNBT(provider));
            System.arraycopy(data.usedSoFar, 0, usedSoFar, 0, data.usedSoFar.length);
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @Override
    public @Nullable ItemChemicalToChemicalUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new ItemChemicalToChemicalUpgradeData(provider, redstone, getControlType(), getEnergyContainer(),
                progress, usedSoFar, energySlot, chemicalInputSlot, inputItemSlots, chemicalTank, outputChemicalTanks, isSorting(), getComponents());
    }

    @Override
    public void dump() {
        chemicalTank.setEmpty();
    }
}
