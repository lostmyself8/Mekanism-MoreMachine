package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.api.datamaps.ItemReplicatorRecipe;
import com.jerry.mekmm.api.recipes.basic.MMBasicItemStackChemicalToItemStackRecipe;
import com.jerry.mekmm.api.recipes.cache.ReplicatorCachedRecipe;
import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.recipe.impl.ReplicatorIRecipeSingle;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineChemicals;
import com.jerry.mekmm.common.util.MoreMachineUtils;
import com.jerry.mekmm.common.util.ValidatorUtils;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
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
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.upgrade.AdvancedMachineUpgradeData;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class TileEntityReplicator extends TileEntityProgressMachine<MMBasicItemStackChemicalToItemStackRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);

    public static final int BASE_TICKS_REQUIRED = 10 * SharedConstants.TICKS_PER_SECOND;
    public static final long MAX_GAS = 10 * FluidType.BUCKET_VOLUME;

    public static HashMap<String, Integer> customRecipeMap = ValidatorUtils.getRecipeFromConfig(MoreMachineConfig.general.itemReplicatorRecipe.get());

    // 化学品存储槽
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getChemical", "getChemicalCapacity", "getChemicalNeeded",
                                    "getChemicalFilledPercentage" },
                            docPlaceholder = "chemical tank")
    public IChemicalTank chemicalTank;

    private MachineEnergyContainer<TileEntityReplicator> energyContainer;

    protected final IInputHandler<Item, @NotNull ItemStack> itemInputHandler;
    private final IOutputHandler<ItemStackTemplate> outputHandler;
    private final ILongInputHandler<Chemical, @NotNull ChemicalStack> chemicalInputHandler;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getItemInput", docPlaceholder = "item input slot")
    InputInventorySlot inputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutput", docPlaceholder = "output slot")
    OutputInventorySlot outputSlot;
    // 气罐槽
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getChemicalInput", docPlaceholder = "chemical input slot")
    ChemicalInventorySlot chemicalSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityReplicator(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.REPLICATOR, pos, state, TRACKED_ERROR_TYPES, BASE_TICKS_REQUIRED);
        configComponent.setupItemIOExtraConfig(inputSlot, outputSlot, chemicalSlot, energySlot);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);
        configComponent.setupInputConfig(TransmissionType.CHEMICAL, chemicalTank);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        chemicalInputHandler = InputHelper.getConstantInputHandler(chemicalTank);
        itemInputHandler = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        ChemicalTankHelper builder = ChemicalTankHelper.forSideWithConfig(this);
        builder.addTank(chemicalTank = BasicChemicalTank.input(MAX_GAS, TileEntityReplicator::isValidChemicalInput, recipeCacheListener));
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener));
        return builder.build();
    }

    @Override
    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this);
        builder.addSlot(inputSlot = InputInventorySlot.at(TileEntityReplicator::isValidItemInput, recipeCacheListener, 29, 32))
                .tracksWarnings(slot -> slot.warning(WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT)));
        // 输出槽位置
        // recipeCacheUnpauseListener，输出检测需要使用recipeCacheUnpauseListener，不然满了之后拿走物品不会更新状态
        builder.addSlot(outputSlot = OutputInventorySlot.at(recipeCacheUnpauseListener, 131, 32))
                .tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE)));
        // 化学品罐槽位置
        builder.addSlot(chemicalSlot = ChemicalInventorySlot.fillOrConvert(chemicalTank, this::getLevel, listener, 8, 65));
        // 能量槽位置
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 152, 65));
        // 化学品罐槽减号图标
        chemicalSlot.setSlotOverlay(SlotOverlay.MINUS);
        return builder.build();
    }

    public static boolean isValidChemicalInput(ChemicalStack stack) {
        return stack.is(MoreMachineChemicals.UU_MATTER);
    }

    public static boolean isValidItemInput(ItemStack stack) {
        return IMoreMachineDataMapTypes.INSTANCE.getItemReplicatorRecipe(stack.typeHolder()) != null;
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        chemicalSlot.fillTankOrConvert();
        recipeCacheLookupMonitor.updateAndProcess();
        return sendUpdatePacket;
    }

    public @Nullable MachineEnergyContainer<TileEntityReplicator> getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, MMBasicItemStackChemicalToItemStackRecipe, ?> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable MMBasicItemStackChemicalToItemStackRecipe getRecipe(int cacheIndex) {
        return getRecipe(itemInputHandler.getInput(), chemicalInputHandler.getInput());
    }

    @Override
    public @NotNull CachedRecipe<MMBasicItemStackChemicalToItemStackRecipe> createNewCachedRecipe(@NotNull MMBasicItemStackChemicalToItemStackRecipe recipe, int cacheIndex) {
        return ReplicatorCachedRecipe.createItemReplicator(recipe, recheckAllRecipeErrors, itemInputHandler, chemicalInputHandler, outputHandler)
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
    public @Nullable IRecipeViewerRecipeType<MMBasicItemStackChemicalToItemStackRecipe> recipeViewerType() {
        return MMRecipeViewerRecipeType.REPLICATOR;
    }

    // public static MMBasicItemStackChemicalToItemStackRecipe getRecipe(ItemStack itemStack, ChemicalStack
    // chemicalStack) {
    // if (chemicalStack.isEmpty() || itemStack.isEmpty()) {
    // return null;
    // }
    // if (customRecipeMap != null) {
    // Holder<Item> itemHolder = itemStack.getItemHolder();
    // // 如果为空则赋值为0
    // int amount = customRecipeMap.getOrDefault(RegistryUtils.getName(itemHolder).toString(), 0);
    // // 防止null和配置文件中出现0
    // if (amount == 0) return null;
    // return new ReplicatorIRecipeSingle(
    // IngredientCreatorAccess.item().fromHolder(itemHolder, 1),
    // IngredientCreatorAccess.chemicalStack().fromHolder(MoreMachineChemicals.UU_MATTER, amount),
    // new ItemStackTemplate(itemHolder, 1));
    // }
    // return null;
    // }

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
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameMMTypeFactory(getBlockHolder(), type);
    }

    @Override
    public @Nullable AdvancedMachineUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new AdvancedMachineUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), getOperatingTicks(), 0, chemicalTank, chemicalSlot, energySlot,
                inputSlot, outputSlot, getComponents(), problemPath());
    }

    // Methods relating to IComputerTile
    @ComputerMethod(methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    long getEnergyUsage() {
        return getActive() ? energyContainer.getEnergyPerTick() : 0;
    }
    // End methods IComputerTile
}

