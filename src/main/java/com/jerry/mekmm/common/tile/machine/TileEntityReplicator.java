package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.recipes.cache.ReplicatorCachedRecipe;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.recipe.impl.ReplicatorIRecipe;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineGas;
import com.jerry.mekmm.common.util.MoreMachineUtils;
import com.jerry.mekmm.common.util.ValidatorUtils;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.ILongInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.upgrade.AdvancedMachineUpgradeData;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.RegistryUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TileEntityReplicator extends TileEntityProgressMachine<ItemStackGasToItemStackRecipe> {

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
    public IGasTank gasTank;

    private MachineEnergyContainer<TileEntityReplicator> energyContainer;

    protected final IInputHandler<@NotNull ItemStack> itemInputHandler;
    private final IOutputHandler<ItemStack> outputHandler;
    private final ILongInputHandler<GasStack> gasInputHandler;

    InputInventorySlot inputSlot;
    OutputInventorySlot outputSlot;
    // 气罐槽
    GasInventorySlot gasSlot;
    EnergyInventorySlot energySlot;

    public TileEntityReplicator(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.REPLICATOR, pos, state, TRACKED_ERROR_TYPES, BASE_TICKS_REQUIRED);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.GAS, TransmissionType.ENERGY);
        configComponent.setupItemIOExtraConfig(inputSlot, outputSlot, gasSlot, energySlot);
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);
        configComponent.setupInputConfig(TransmissionType.GAS, gasTank);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        gasInputHandler = InputHelper.getConstantInputHandler(gasTank);
        itemInputHandler = InputHelper.getInputHandler(inputSlot, RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputSlot, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @Override
    protected @Nullable IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSideGasWithConfig(this::getDirection, this::getConfig);
        builder.addTank(gasTank = ChemicalTankBuilder.GAS.create(MAX_GAS, TileEntityReplicator::isValidGasInput, recipeCacheListener));
        return builder.build();
    }

    @Override
    protected @Nullable IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener, IContentsListener recipeCacheListener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener));
        return builder.build();
    }

    @Override
    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addSlot(inputSlot = InputInventorySlot.at(TileEntityReplicator::isValidItemInput, recipeCacheListener, 29, 32))
                .tracksWarnings(slot -> slot.warning(WarningTracker.WarningType.NO_MATCHING_RECIPE, getWarningCheck(RecipeError.NOT_ENOUGH_INPUT)));
        // 输出槽位置
        // recipeCacheUnpauseListener，输出检测需要使用recipeCacheUnpauseListener，不然满了之后拿走物品不会更新状态
        builder.addSlot(outputSlot = OutputInventorySlot.at(listener, 131, 32))
                .tracksWarnings(slot -> slot.warning(WarningTracker.WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE)));
        // 化学品罐槽位置
        builder.addSlot(gasSlot = GasInventorySlot.fillOrConvert(gasTank, this::getLevel, listener, 8, 65));
        // 能量槽位置
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 152, 65));
        // 化学品罐槽减号图标
        gasSlot.setSlotOverlay(SlotOverlay.MINUS);
        return builder.build();
    }

    public static boolean isValidGasInput(Gas gas) {
        return gas.equals(MoreMachineGas.UU_MATTER.getChemical());
    }

    public static boolean isValidItemInput(ItemStack stack) {
        if (customRecipeMap != null) {
            return customRecipeMap.containsKey(Objects.requireNonNull(RegistryUtils.getName(stack.getItem())).toString());
        }
        return false;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        gasSlot.fillTankOrConvert();
        recipeCacheLookupMonitor.updateAndProcess();
    }

    public @Nullable MachineEnergyContainer<TileEntityReplicator> getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<ItemStackGasToItemStackRecipe, ?> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable ItemStackGasToItemStackRecipe getRecipe(int cacheIndex) {
        return getRecipe(itemInputHandler.getInput(), gasInputHandler.getInput());
    }

    public static ItemStackGasToItemStackRecipe getRecipe(ItemStack itemStack, GasStack gasStack) {
        if (gasStack.isEmpty() || itemStack.isEmpty()) {
            return null;
        }
        if (customRecipeMap != null) {
            Item item = itemStack.getItem();
            // 如果为空则赋值为0
            int amount = customRecipeMap.getOrDefault(RegistryUtils.getName(item).toString(), 0);
            // 防止null和配置文件中出现0
            if (amount == 0) return null;
            return new ReplicatorIRecipe(item, IngredientCreatorAccess.item().from(item, 1),
                    IngredientCreatorAccess.gas().from(MoreMachineGas.UU_MATTER, amount),
                    new ItemStack(item, 1));
        }
        return null;
    }

    @Override
    public @NotNull CachedRecipe<ItemStackGasToItemStackRecipe> createNewCachedRecipe(@NotNull ItemStackGasToItemStackRecipe recipe, int cacheIndex) {
        return ReplicatorCachedRecipe.itemReplicator(recipe, recheckAllRecipeErrors, itemInputHandler, gasInputHandler, outputHandler)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(this::setActive)
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(this::setOperatingTicks);
    }

    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameMMTypeFactory(getBlockType(), type);
    }

    @Override
    public @Nullable AdvancedMachineUpgradeData getUpgradeData() {
        return new AdvancedMachineUpgradeData(redstone, getControlType(), getEnergyContainer(), getOperatingTicks(), 0, gasTank, gasSlot, energySlot,
                inputSlot, outputSlot, getComponents());
    }
}
