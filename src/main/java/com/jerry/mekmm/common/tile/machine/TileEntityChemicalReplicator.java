package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.datamaps.ChemicalReplicatorRecipe;
import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.api.recipes.basic.MMBasicChemicalChemicalToChemicalRecipe;
import com.jerry.mekmm.api.recipes.cache.ReplicatorCachedRecipe;
import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.recipe.impl.ChemicalReplicatorIRecipeSingle;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineChemicals;
import com.jerry.mekmm.common.util.MoreMachineUtils;
import com.jerry.mekmm.common.util.ValidatorUtils;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalStackTemplate;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
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
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class TileEntityChemicalReplicator extends TileEntityProgressMachine<MMBasicChemicalChemicalToChemicalRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);

    public static final long MAX_GAS = 10 * FluidType.BUCKET_VOLUME;
    private static final int BASE_TICKS_REQUIRED = 10 * SharedConstants.TICKS_PER_SECOND;

    public static HashMap<String, Integer> customRecipeMap = ValidatorUtils.getRecipeFromConfig(MoreMachineConfig.general.chemicalReplicatorRecipe.get());

    // 要复制的化学品
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getInput", "getInputCapacity", "getInputNeeded",
                                    "getInputFilledPercentage" },
                            docPlaceholder = "input tank")
    public IChemicalTank inputTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getOutput", "getOutputCapacity", "getOutputNeeded",
                                    "getOutputFilledPercentage" },
                            docPlaceholder = "output tank")
    public IChemicalTank outputTank;
    // UU
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getUU", "getUUCapacity", "getUUNeeded",
                                    "getUUFilledPercentage" },
                            docPlaceholder = "uu tank")
    public IChemicalTank uuTank;

    private MachineEnergyContainer<TileEntityChemicalReplicator> energyContainer;

    public MachineEnergyContainer<TileEntityChemicalReplicator> getEnergyContainerTyped() {
        return energyContainer;
    }

    private final IInputHandler<Chemical, @NotNull ChemicalStack> firstInputHandler;
    private final IInputHandler<Chemical, @NotNull ChemicalStack> secondaryInputHandler;
    private final IOutputHandler<@NotNull ChemicalStackTemplate> outputHandler;
    // 气罐槽
    // UU物质
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getUUSlot", docPlaceholder = "uu slot")
    ChemicalInventorySlot uuSlot;
    // 要复制的化学品
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputSlot", docPlaceholder = "input slot")
    ChemicalInventorySlot inputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputSlot", docPlaceholder = "output slot")
    ChemicalInventorySlot outputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityChemicalReplicator(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.CHEMICAL_REPLICATOR, pos, state, TRACKED_ERROR_TYPES, BASE_TICKS_REQUIRED);
        configComponent.setupItemIOExtraConfig(uuSlot, outputSlot, inputSlot, energySlot);
        ConfigInfo fluidConfig = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (fluidConfig != null) {
            fluidConfig.addSlotInfo(DataType.INPUT_1, new ChemicalSlotInfo(true, false, inputTank));
            fluidConfig.addSlotInfo(DataType.INPUT_2, new ChemicalSlotInfo(true, false, uuTank));
            fluidConfig.addSlotInfo(DataType.OUTPUT, new ChemicalSlotInfo(false, true, outputTank));
        }
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer);

        ejectorComponent.setOutputData(configComponent, TransmissionType.CHEMICAL, TransmissionType.ITEM)
                .setCanTankEject(tank -> tank == outputTank);

        firstInputHandler = InputHelper.getInputHandler(inputTank, RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        secondaryInputHandler = InputHelper.getConstantInputHandler(uuTank);
    }

    @NotNull
    @Override
    public IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IChemicalTank> builder = MekContainerHelper.forSideWithChemicalConfig(this);
        builder.addContainer(inputTank = BasicChemicalTank.input(MAX_GAS, TileEntityChemicalReplicator::isValidInputChemical, recipeCacheListener));
        builder.addContainer(uuTank = BasicChemicalTank.input(MAX_GAS, TileEntityChemicalReplicator::isValidChemicalInput, recipeCacheListener));
        builder.addContainer(outputTank = BasicChemicalTank.output(MAX_GAS, recipeCacheUnpauseListener));
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainer(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        energyContainer = MachineEnergyContainer.input(this, recipeCacheUnpauseListener);
        return new EnergyConfigHolder(energyContainer, this);
    }

    @Override
    protected @Nullable IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        // 化学品罐槽位置
        builder.addContainer(uuSlot = ChemicalInventorySlot.fillOrConvert(inputTank, this::getLevel, listener, 29, 65));
        builder.addContainer(inputSlot = ChemicalInventorySlot.fillOrConvert(uuTank, this::getLevel, listener, 8, 65));
        builder.addContainer(outputSlot = ChemicalInventorySlot.drain(outputTank, listener, 132, 65));
        // 能量槽位置
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 152, 65));
        // 化学品罐槽减号图标
        uuSlot.setSlotOverlay(SlotOverlay.MINUS);
        inputSlot.setSlotOverlay(SlotOverlay.MINUS);
        outputSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    // 需要复制的化学品
    public static boolean isValidInputChemical(ChemicalResource resource) {
        return !resource.isEmpty() && IMoreMachineDataMapTypes.INSTANCE.getChemicalReplicatorRecipe(resource.typeHolder()) != null;
    }

    // uu物质
    public static boolean isValidChemicalInput(ChemicalResource resource) {
        return resource.is(MoreMachineChemicals.UU_MATTER);
    }

    @Override
    protected boolean onUpdateServer(net.minecraft.server.level.ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        energySlot.fillContainerOrConvert(null);
        uuSlot.fillTankOrConvert(null);
        inputSlot.fillTankOrConvert(null);
        recipeCacheLookupMonitor.updateAndProcess();
        return sendUpdatePacket;
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, MMBasicChemicalChemicalToChemicalRecipe, ?> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable MMBasicChemicalChemicalToChemicalRecipe getRecipe(int cacheIndex) {
        return getRecipe(firstInputHandler.getInput(), secondaryInputHandler.getInput());
    }

    @Override
    public @NotNull CachedRecipe<MMBasicChemicalChemicalToChemicalRecipe> createNewCachedRecipe(@NotNull MMBasicChemicalChemicalToChemicalRecipe recipe, int cacheIndex) {
        return ReplicatorCachedRecipe.createChemicalReplicator(recipe, recheckAllRecipeErrors, firstInputHandler, secondaryInputHandler, outputHandler)
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
    public @Nullable IRecipeViewerRecipeType<MMBasicChemicalChemicalToChemicalRecipe> recipeViewerType() {
        return MMRecipeViewerRecipeType.CHEMICAL_REPLICATOR;
    }

    public static MMBasicChemicalChemicalToChemicalRecipe getRecipe(ChemicalStack chemicalStack, ChemicalStack UUStack) {
        if (chemicalStack.isEmpty() || UUStack.isEmpty()) {
            return null;
        }

        // if (customRecipeMap != null) {
        // Holder<Chemical> chemicalHolder = chemicalStack.getChemicalHolder();
        // // 如果为空则赋值为0
        // int amount = customRecipeMap.getOrDefault(RegistryUtils.getName(chemicalHolder).toString(), 0);
        // // 防止null和配置文件中出现0
        // if (amount == 0) return null;
        // return new ChemicalReplicatorIRecipeSingle(
        // IngredientCreatorAccess.chemicalStack().fromHolder(chemicalHolder, 1000),
        // IngredientCreatorAccess.chemicalStack().fromHolder(MoreMachineChemicals.UU_MATTER, amount),
        // new ChemicalStack(chemicalHolder, 1000));
        // }

        Holder<Chemical> chemicalHolder = chemicalStack.typeHolder();
        ChemicalReplicatorRecipe recipe = IMoreMachineDataMapTypes.INSTANCE.getChemicalReplicatorRecipe(chemicalHolder);
        if (recipe != null) {
            return new ChemicalReplicatorIRecipeSingle(
                    IngredientCreatorAccess.chemicalStack().fromHolder(chemicalHolder, Math.toIntExact(recipe.inputAmount())),
                    IngredientCreatorAccess.chemicalStack().fromHolder(MoreMachineChemicals.UU_MATTER, Math.toIntExact(recipe.UUAmount())),
                    new ChemicalStackTemplate(chemicalHolder, Math.toIntExact(recipe.outputAmount())));
        }
        return null;
    }

    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameMMTypeFactory(getBlockHolder(), type);
    }

    // Methods relating to IComputerTile
    @ComputerMethod(methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    long getEnergyUsage() {
        return getActive() ? energyContainer.getEnergyPerTick() : 0;
    }
    // End methods IComputerTile
}
