package com.jerry.meklm.common.tile.machine;

import com.jerry.mekmm.api.ITileEntityMekanismAccessor;
import com.jerry.mekmm.common.capabilities.holder.chemical.AdjustableChemicalTankHelper;
import com.jerry.mekmm.common.util.WorldUtil.SolarCheck;

import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.RelativeSide;
import mekanism.api.Upgrade;
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
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleChemical;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;

import com.jerry.meklm.api.INotNeedConfig;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TileEntityLargeSolarNeutronActivator extends TileEntityRecipeMachine<GasToGasRecipe> implements IBoundingBlock, ChemicalRecipeLookupHandler<Gas, GasStack, GasToGasRecipe>, INotNeedConfig {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    public static final long MAX_GAS = 10L * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;
    protected LargeSNA solarCheck;
    private final LargeSNA[] solarChecks = new LargeSNA[8];

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = { "getInput", "getInputCapacity", "getInputNeeded", "getInputFilledPercentage" }, docPlaceholder = "input tank")
    public IGasTank inputTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = { "getOutput", "getOutputCapacity", "getOutputNeeded", "getOutputFilledPercentage" }, docPlaceholder = "output tank")
    public IGasTank outputTank;

    @SyntheticComputerMethod(getter = "getProductionRate")
    private float productionRate;
    private int baselineMaxOperations = 1;
    private int numPowering;
    private byte seeSunCount = 0;

    private final IOutputHandler<@NotNull GasStack> outputHandler;
    private final IInputHandler<@NotNull GasStack> inputHandler;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "input slot")
    GasInventorySlot inputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "output slot")
    GasInventorySlot outputSlot;

    public TileEntityLargeSolarNeutronActivator(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_SOLAR_NEUTRON_ACTIVATOR, pos, state, TRACKED_ERROR_TYPES);
        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.GAS);
        configComponent.setupIOConfig(TransmissionType.ITEM, inputSlot, outputSlot, RelativeSide.BACK);
        configComponent.setupIOConfig(TransmissionType.GAS, inputTank, outputTank, RelativeSide.BACK, false, true).setEjecting(true);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.GAS)
                .setCanTankEject(tank -> tank != inputTank);
        inputHandler = InputHelper.getInputHandler(inputTank, RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener, IContentsListener recipeCacheListener) {
        AdjustableChemicalTankHelper<Gas, GasStack, IGasTank> builder = AdjustableChemicalTankHelper.forSideGas(this::getDirection, side -> side == RelativeSide.RIGHT || side == RelativeSide.LEFT, side -> side == RelativeSide.BACK);
        // Allow extracting out of the input gas tank if it isn't external OR the output tank is empty AND the input is
        // radioactive
        builder.addTank(inputTank = ChemicalTankBuilder.GAS.create(MAX_GAS, ChemicalTankHelper.radioactiveInputTankPredicate(() -> outputTank),
                ChemicalTankBuilder.GAS.alwaysTrueBi, this::containsRecipe, ChemicalAttributeValidator.ALWAYS_ALLOW, recipeCacheListener), RelativeSide.RIGHT, RelativeSide.LEFT, RelativeSide.BACK);
        builder.addTank(outputTank = ChemicalTankBuilder.GAS.output(MAX_GAS, listener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection, side -> side == RelativeSide.RIGHT || side == RelativeSide.LEFT, side -> side == RelativeSide.BACK);
        builder.addSlot(inputSlot = GasInventorySlot.fill(inputTank, listener, 5, 56), RelativeSide.RIGHT, RelativeSide.LEFT);
        builder.addSlot(outputSlot = GasInventorySlot.drain(outputTank, listener, 155, 56), RelativeSide.BACK);
        inputSlot.setSlotType(ContainerSlotType.INPUT);
        inputSlot.setSlotOverlay(SlotOverlay.MINUS);
        outputSlot.setSlotType(ContainerSlotType.OUTPUT);
        outputSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    private void recheckSettings() {
        if (level == null) {
            return;
        }
        BlockPos topPos = worldPosition.above(2);
        solarCheck = new LargeSNA(level, topPos);
        for (int i = 0; i < solarChecks.length; i++) {
            if (i < 3) {
                solarChecks[i] = new LargeSNA(level, topPos.offset(-1, 0, i - 1));
            } else if (i == 3) {
                solarChecks[i] = new LargeSNA(level, topPos.offset(0, 0, -1));
            } else if (i == 4) {
                solarChecks[i] = new LargeSNA(level, topPos.offset(0, 0, 1));
            } else {
                solarChecks[i] = new LargeSNA(level, topPos.offset(1, 0, i - 6));
            }
        }
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (solarCheck == null) {
            recheckSettings();
        }
        updateSeeSunCount();
        inputSlot.fillTank();
        outputSlot.drainTank();
        productionRate = recalculateProductionRate();
        recipeCacheLookupMonitor.updateAndProcess();
        handleEject();
    }

    private void handleEject() {
        if (MekanismUtils.canFunction(this)) {
            Direction side = getOppositeDirection();
            for (BlockEntity ejectTile : getEjectEntity(side)) {
                if (ejectTile != null) {
                    ChemicalUtil.emit(Collections.singleton(side), outputTank, ejectTile, outputTank.getCapacity());
                }
            }
        }
    }

    private BlockEntity[] getEjectEntity(Direction side) {
        return new BlockEntity[] {
                WorldUtils.getTileEntity(getLevel(), worldPosition.offset(side.getNormal()).offset(getLeftSide().getNormal())),
                WorldUtils.getTileEntity(getLevel(), worldPosition.offset(side.getNormal()).offset(getRightSide().getNormal()))
        };
    }

    /**
     * 更新能看到太阳的太阳能板数量(每tick调用一次)
     */
    private void updateSeeSunCount() {
        solarCheck.recheckCanSeeSun();
        byte count = solarCheck.canSeeSun() ? (byte) 1 : 0;
        for (LargeSNA check : solarChecks) {
            check.recheckCanSeeSun();
            if (check.canSeeSun()) {
                count++;
            }
        }
        seeSunCount = count;
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<GasToGasRecipe, SingleChemical<Gas, GasStack, GasToGasRecipe>> getRecipeType() {
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

    /**
     * 根据可以看见太阳的太阳能板数获取减少效率的乘数
     *
     * @return 效率减小倍数
     */
    private float reduceMultiplier() {
        int panelCount = solarChecks.length + 1;
        byte notSeeSunCount = (byte) (panelCount - seeSunCount);
        float reduction = 0f;
        // 无遮挡或意外情况
        if (notSeeSunCount <= 0) {
            return reduction;
        }
        // 阻挡一块降低40%，阻挡二至五块每块降低10%，阻挡六至九块每块降低5%
        if (notSeeSunCount <= 5) {
            reduction += (notSeeSunCount - 1) * 0.1f + 0.4f;
        } else {
            reduction += (notSeeSunCount - 5) * 0.05f + 0.8f;
        }
        // 全遮挡或意外情况
        return Math.min(reduction, 1f);
    }

    private boolean canFunction() {
        // Sort out if the solar neutron activator can see the sun; we no longer check if it's raining here,
        // since under the new rules, we can still function when it's raining, albeit at a significant penalty.
        return MekanismUtils.canFunction(this) && canSeeSun();
    }

    private float recalculateProductionRate() {
        if (level == null || !canFunction() || solarCheck == null) {
            return 0;
        }
        // Get the brightness of the sun; note that there are some implementations that depend on the base
        // brightness function which doesn't take into account the fact that rain can't occur in some biomes.
        // 这里会计算对应的峰值，因此不需要在之前计算
        float brightness = WorldUtils.getSunBrightness(level, 1.0F);
        float generationMultiplier = solarCheck.getProductionMultiplier();
        for (LargeSNA check : solarChecks) {
            generationMultiplier += check.getProductionMultiplier();
        }
        generationMultiplier /= solarChecks.length + 1;
        return MekanismConfig.general.maxSolarNeutronActivatorRate.get() * generationMultiplier * brightness * (1 - reduceMultiplier());
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
                .setBaselineMaxOperations(() -> baselineMaxOperations * (productionRate > 0 && productionRate < 1 ? 1 : (int) productionRate));
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            baselineMaxOperations = (int) Math.pow(2, upgradeComponent.getUpgrades(Upgrade.SPEED));
        }
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
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = left.getOpposite();
        switch (getDirection()) {
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

    protected static class LargeSNA extends SolarCheck {

        private final int recheckFrequency;
        private long lastCheckedSun;

        public LargeSNA(Level world, BlockPos pos) {
            super(world, pos);
            // Recheck between every 10-30 ticks, to not end up checking each position each tick
            recheckFrequency = Mth.nextInt(world.random, 10, 10 + SharedConstants.TICKS_PER_SECOND);
        }

        @Override
        public void recheckCanSeeSun() {
            if (!world.dimensionType().hasSkyLight() || world.getSkyDarken() >= 4) {
                // Inline of most of WorldUtils#canSeeSun so that we can exit early if it is not day or there is no
                // skylight
                // We start with the basic dimension checks and always run those, as they are simple and quick checks,
                // and
                // we want to be able to stop quickly when it gets too dark
                canSeeSun = false;
                return;
            }
            long time = world.getGameTime();
            if (time < lastCheckedSun + recheckFrequency) {
                // If we have checked for blocks above the solar panel in the past recheckFrequency
                // number of ticks, skip checking for now for performance reasons
                return;
            }
            // otherwise, mark that we checked and actually check
            lastCheckedSun = time;
            if (world.getFluidState(pos).isEmpty()) {
                // If the top isn't fluid logged we can just quickly check if the top can see the sun
                canSeeSun = world.canSeeSky(pos);
            } else {
                BlockPos above = pos.above();
                if (world.canSeeSky(above)) {
                    // If the spot above can see the sun, check to make sure we can see through the block there
                    BlockState state = world.getBlockState(above);
                    canSeeSun = !state.liquid() && state.getLightBlock(world, above) <= 0;
                } else {
                    canSeeSun = false;
                }
            }
        }
    }
}
