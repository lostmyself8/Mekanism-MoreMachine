package com.jerry.meklm.common.tile.machine;

import com.jerry.mekmm.api.ITileEntityMekanismAccessor;

import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.recipes.GasToGasRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import com.jerry.meklm.api.INotNeedConfig;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileEntityLargeSolarNeutronActivator extends TileEntityRecipeMachine<GasToGasRecipe> implements IBoundingBlock, ChemicalRecipeLookupHandler<Gas, GasStack, GasToGasRecipe>, INotNeedConfig {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    public static final long MAX_GAS = 10_000;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = { "getInput", "getInputCapacity", "getInputNeeded", "getInputFilledPercentage" }, docPlaceholder = "input tank")
    public IGasTank inputTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = { "getOutput", "getOutputCapacity", "getOutputNeeded", "getOutputFilledPercentage" }, docPlaceholder = "output tank")
    public IGasTank outputTank;

    @SyntheticComputerMethod(getter = "getPeakProductionRate")
    private float peakProductionRate;
    @SyntheticComputerMethod(getter = "getProductionRate")
    private float productionRate;
    private int numPowering;
    private boolean settingsChecked;
    private boolean needsRainCheck;

    private final IOutputHandler<@NotNull GasStack> outputHandler;
    private final IInputHandler<@NotNull GasStack> inputHandler;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "input slot")
    GasInventorySlot inputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "output slot")
    GasInventorySlot outputSlot;

    public TileEntityLargeSolarNeutronActivator(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_SOLAR_NEUTRON_ACTIVATOR, pos, state, TRACKED_ERROR_TYPES);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.GAS);
        configComponent.setupIOConfig(TransmissionType.ITEM, inputSlot, outputSlot, RelativeSide.FRONT);
        configComponent.setupIOConfig(TransmissionType.GAS, inputTank, outputTank, RelativeSide.FRONT, false, true).setEjecting(true);
        configComponent.addDisabledSides(RelativeSide.TOP);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.GAS)
                .setCanTankEject(tank -> tank != inputTank);
        inputHandler = InputHelper.getInputHandler(inputTank, RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSideGasWithConfig(this::getDirection, this::getConfig);
        // Allow extracting out of the input gas tank if it isn't external OR the output tank is empty AND the input is
        // radioactive
        builder.addTank(inputTank = ChemicalTankBuilder.GAS.create(MAX_GAS, ChemicalTankHelper.radioactiveInputTankPredicate(() -> outputTank),
                ChemicalTankBuilder.GAS.alwaysTrueBi, this::containsRecipe, ChemicalAttributeValidator.ALWAYS_ALLOW, recipeCacheListener));
        builder.addTank(outputTank = ChemicalTankBuilder.GAS.output(MAX_GAS, listener));
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addSlot(inputSlot = GasInventorySlot.fill(inputTank, listener, 5, 56));
        builder.addSlot(outputSlot = GasInventorySlot.drain(outputTank, listener, 155, 56));
        inputSlot.setSlotType(ContainerSlotType.INPUT);
        inputSlot.setSlotOverlay(SlotOverlay.MINUS);
        outputSlot.setSlotType(ContainerSlotType.OUTPUT);
        outputSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    private void recheckSettings() {
        Level world = getLevel();
        if (world == null) {
            return;
        }
        BlockPos pos = getBlockPos();
        Biome b = world.getBiomeManager().getBiome(pos).value();
        needsRainCheck = b.getPrecipitationAt(pos) != Biome.Precipitation.NONE;
        // Consider the best temperature to be 0.8; biomes that are higher than that
        // will suffer an efficiency loss (semiconductors don't like heat); biomes that are cooler
        // get a boost. We scale the efficiency to around 30% so that it doesn't totally dominate
        float tempEff = 0.3F * (0.8F - b.getTemperature(pos));

        // Treat rainfall as a proxy for humidity; any humidity works as a drag on overall efficiency.
        // As with temperature, we scale it so that it doesn't overwhelm production. Note the signedness
        // on the scaling factor. Also note that we only use rainfall as a proxy if it CAN rain; some dimensions
        // (like the End) have rainfall set, but can't actually support rain.
        float humidityEff = needsRainCheck ? -0.3F * b.getModifiedClimateSettings().downfall() : 0.0F;
        peakProductionRate = MekanismConfig.general.maxSolarNeutronActivatorRate.get() * (1.0F + tempEff + humidityEff);
        settingsChecked = true;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (!settingsChecked) {
            recheckSettings();
        }
        inputSlot.fillTank();
        outputSlot.drainTank();
        productionRate = recalculateProductionRate();
        recipeCacheLookupMonitor.updateAndProcess();
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<GasToGasRecipe, InputRecipeCache.SingleChemical<Gas, GasStack, GasToGasRecipe>> getRecipeType() {
        return MekanismRecipeType.ACTIVATING;
    }

    @Nullable
    @Override
    public GasToGasRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandler);
    }

    @ComputerMethod
    boolean canSeeSun() {
        return WorldUtils.canSeeSun(level, worldPosition.above());
    }

    private boolean canFunction() {
        // Sort out if the solar neutron activator can see the sun; we no longer check if it's raining here,
        // since under the new rules, we can still function when it's raining, albeit at a significant penalty.
        return MekanismUtils.canFunction(this) && canSeeSun();
    }

    private float recalculateProductionRate() {
        Level world = getLevel();
        if (world == null || !canFunction()) {
            return 0;
        }
        // Get the brightness of the sun; note that there are some implementations that depend on the base
        // brightness function which doesn't take into account the fact that rain can't occur in some biomes.
        float brightness = WorldUtils.getSunBrightness(world, 1.0F);
        // Production is a function of the peak possible output in this biome and sun's current brightness
        float production = peakProductionRate * brightness;
        // If the solar neutron activator is in a biome where it can rain, and it's raining penalize production by 80%
        if (needsRainCheck && (world.isRaining() || world.isThundering())) {
            production *= 0.2F;
        }
        return production;
    }

    @NotNull
    @Override
    public CachedRecipe<GasToGasRecipe> createNewCachedRecipe(@NotNull GasToGasRecipe recipe, int cacheIndex) {
        return OneInputCachedRecipe.chemicalToChemical(recipe, recheckAllRecipeErrors, inputHandler, outputHandler)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(this::canFunction)
                .setActive(this::setActive)
                .setOnFinish(this::markForSave)
                // Edge case handling, this should almost always end up being 1
                .setRequiredTicks(() -> productionRate > 0 && productionRate < 1 ? (int) Math.ceil(1 / productionRate) : 1)
                .setBaselineMaxOperations(() -> productionRate > 0 && productionRate < 1 ? 1 : (int) productionRate);
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(inputTank.getStored(), inputTank.getCapacity());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        numPowering = nbt.getInt(NBTConstants.NUM_POWERING);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        nbtTags.putInt(NBTConstants.NUM_POWERING, numPowering);
    }

    @Override
    protected boolean makesComparatorDirty(@Nullable SubstanceType type) {
        return type == SubstanceType.GAS;
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
        Direction direction = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (direction) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ())) || offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ()))) {
                    return getCurrentRedstoneLevel();
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ())) || offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ()))) {
                    return getCurrentRedstoneLevel();
                }
            }
        }
        return 0;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getOffsetCapabilityIfEnabled(@NotNull Capability<T> capability, Direction side, @NotNull Vec3i offset) {
        if (this instanceof ITileEntityMekanismAccessor accessor) {
            if (capability == Capabilities.GAS_HANDLER) {
                return accessor.getGasHandlerManager().resolve(capability, side);
            }
        }
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerManager.resolve(capability, side);
        }
        return getCapability(capability, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull Capability<?> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.GAS_HANDLER) {
            return notGasPort(side, offset);
        } else if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return notItemPort(side, offset);
        } else if (canEverResolve(capability) && IBoundingBlock.super.isOffsetCapabilityDisabled(capability, side, offset)) {
            // If we are not an item handler or energy capability, and it is a capability that we can support,
            // but it is one that normally should be disabled for offset capabilities, then expose it but only do so
            // via our ports for things like computer integration capabilities, then we treat the capability as
            // disabled if it is not against one of our ports
            return notGasPort(side, offset);
        }
        return false;
    }

    private boolean notGasPort(Direction side, Vec3i offset) {
        Direction direction = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (direction) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ()))) {
                    return side != back && side != left;
                }
                if (offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ()))) {
                    return side != back && side != right;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ()))) {
                    return side != back && side != left;
                }
                if (offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ()))) {
                    return side != back && side != right;
                }
            }
        }
        return true;
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        // 所有端口都可以与物品管道交互
        return notGasPort(side, offset);
    }
}
