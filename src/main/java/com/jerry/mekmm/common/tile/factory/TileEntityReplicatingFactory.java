package com.jerry.mekmm.common.tile.factory;

import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.api.datamaps.ItemReplicatorRecipe;
import com.jerry.mekmm.api.recipes.basic.MMBasicItemStackChemicalToItemStackRecipe;
import com.jerry.mekmm.api.recipes.cache.ReplicatorCachedRecipe;
import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.recipe.impl.ReplicatorIRecipeSingle;
import com.jerry.mekmm.common.registries.MoreMachineChemicals;
import com.jerry.mekmm.common.util.ValidatorUtils;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.Mekanism;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.DoubleInputRecipeCache;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.ItemChemical;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.AdvancedMachineUpgradeData;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.InventoryUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TileEntityReplicatingFactory extends TileEntityMoreMachineItemToItemFactory<MMBasicItemStackChemicalToItemStackRecipe> implements IHasDumpButton,
                                          ItemChemicalRecipeLookupHandler<MMBasicItemStackChemicalToItemStackRecipe> {

    protected static final DoubleInputRecipeCache.CheckRecipeType<ItemStack, ChemicalStack, MMBasicItemStackChemicalToItemStackRecipe, ItemStack> OUTPUT_CHECK = (recipe, input, extra, output) -> InventoryUtils.areItemsStackable(recipe.getOutput(input, extra), output);
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT);

    public static final long MAX_GAS = 10 * FluidType.BUCKET_VOLUME;

    public static HashMap<String, Integer> customRecipeMap = ValidatorUtils.getRecipeFromConfig(MoreMachineConfig.general.itemReplicatorRecipe.get());

    private final ILongInputHandler<Chemical, ChemicalStack> chemicalInputHandler;
    // 化学品存储槽
    @Getter
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class, methodNames = { "getChemical", "getChemicalCapacity", "getChemicalNeeded", "getChemicalFilledPercentage" }, docPlaceholder = "chemical tank")
    public IChemicalTank chemicalTank;
    // 气罐槽
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getChemicalItem", docPlaceholder = "chemical item slot")
    ChemicalInventorySlot chemicalSlot;

    public TileEntityReplicatingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        configComponent.setupInputConfig(TransmissionType.CHEMICAL, chemicalTank);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        chemicalInputHandler = InputHelper.getConstantInputHandler(chemicalTank);
    }

    @Nullable
    @Override
    public ChemicalInventorySlot getExtraSlot() {
        return chemicalSlot;
    }

    @Override
    public @Nullable IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        ChemicalTankHelper builder = ChemicalTankHelper.forSideWithConfig(this);
        chemicalTank = BasicChemicalTank.input(MAX_GAS * tier.processes, TileEntityReplicatingFactory::isValidChemicalInput, markAllMonitorsChanged(listener));
        builder.addTank(chemicalTank);
        return builder.build();
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addSlots(builder, listener, updateSortingListener);
        builder.addSlot(chemicalSlot = ChemicalInventorySlot.fillOrConvert(chemicalTank, this::getLevel, listener, 7, 57));
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<MMBasicItemStackChemicalToItemStackRecipe> cached, @NotNull ItemStack stack) {
        return cached != null && isValidChemicalInput(chemicalTank.getStack());
    }

    @Override
    protected @Nullable MMBasicItemStackChemicalToItemStackRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot) {
        return null;
    }

    @Override
    protected void handleSecondaryFuel() {
        chemicalSlot.fillTankOrConvert();
    }

    @Override
    protected int getNeededInput(MMBasicItemStackChemicalToItemStackRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getItemInput().getNeededAmount(inputStack));
    }

    @Override
    public boolean isItemValidForSlot(@NotNull ItemStack stack) {
        return true;
    }

    public static boolean isValidChemicalInput(ChemicalStack stack) {
        return stack.is(MoreMachineChemicals.UU_MATTER);
    }

    @Override
    public boolean isValidInputItem(ItemStack stack) {
        return IMoreMachineDataMapTypes.INSTANCE.getItemReplicatorRecipe(stack.typeHolder()) != null;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, MMBasicItemStackChemicalToItemStackRecipe, ItemChemical<MMBasicItemStackChemicalToItemStackRecipe>> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable MMBasicItemStackChemicalToItemStackRecipe getRecipe(int cacheIndex) {
        return getRecipe(inputHandlers[cacheIndex].getInput(), chemicalInputHandler.getInput());
    }

    @Override
    public @NotNull CachedRecipe<MMBasicItemStackChemicalToItemStackRecipe> createNewCachedRecipe(@NotNull MMBasicItemStackChemicalToItemStackRecipe recipe, int cacheIndex) {
        return ReplicatorCachedRecipe.createItemReplicator(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], chemicalInputHandler, outputHandlers[cacheIndex])
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
    public @Nullable IRecipeViewerRecipeType<MMBasicItemStackChemicalToItemStackRecipe> recipeViewerType() {
        return MMRecipeViewerRecipeType.REPLICATOR;
    }

    public static MMBasicItemStackChemicalToItemStackRecipe getRecipe(ItemStack itemStack, ChemicalStack chemicalStack) {
        if (chemicalStack.isEmpty() || itemStack.isEmpty()) {
            return null;
        }
        Holder<Item> itemHolder = itemStack.typeHolder();
        ItemReplicatorRecipe recipe = IMoreMachineDataMapTypes.INSTANCE.getItemReplicatorRecipe(itemHolder);
        if (recipe != null) {
            return new ReplicatorIRecipeSingle(
                    IngredientCreatorAccess.item().fromHolder(itemHolder, 1),
                    IngredientCreatorAccess.chemicalStack().fromHolder(MoreMachineChemicals.UU_MATTER, recipe.UUAmount()),
                    new ItemStackTemplate(itemHolder, 1));
        }
        return null;
    }

    @Override
    public boolean hasSecondaryResourceBar() {
        return true;
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider) {
        if (upgradeData instanceof AdvancedMachineUpgradeData data) {
            // Generic factory upgrade data handling
            super.parseUpgradeData(upgradeData, provider);
            // Copy the contents using NBT so that if it is not actually valid due to a reload we don't crash
            ContainerType.CHEMICAL.copy(data.stored, chemicalTank);
            ContainerType.ITEM.copy(data.chemicalSlot, chemicalSlot);
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @Override
    public @Nullable AdvancedMachineUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new AdvancedMachineUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), progress, null, chemicalTank, chemicalSlot, energySlot,
                inputSlots, outputSlots, isSorting(), getComponents(), problemPath());
    }

    @Override
    public void dump() {
        chemicalTank.setEmpty();
    }

    // Methods relating to IComputerTile
    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Empty the contents of the chemical tank into the environment")
    void dumpChemical() throws ComputerException {
        validateSecurityIsPublic();
        dump();
    }
    // End methods IComputerTile
}

