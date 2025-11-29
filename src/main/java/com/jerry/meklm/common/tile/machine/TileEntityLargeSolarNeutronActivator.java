package com.jerry.meklm.common.tile.machine;

import com.jerry.meklm.common.capabilities.holder.chemical.CanAdjustChemicalTankHelper;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import com.jerry.meklm.common.tile.INeedConfig;

import com.jerry.mekmm.common.util.WorldUtil;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.Upgrade;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.recipes.ChemicalToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.api.recipes.vanilla_input.SingleChemicalRecipeInput;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TileEntityLargeSolarNeutronActivator extends TileEntityRecipeMachine<ChemicalToChemicalRecipe> implements IBoundingBlock, ChemicalRecipeLookupHandler<ChemicalToChemicalRecipe>, INeedConfig {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    public static final long MAX_GAS = 10L * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;
    protected LargeSNA solarCheck;
    private final LargeSNA[] solarChecks = new LargeSNA[8];

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class,
                            methodNames = { "getInput", "getInputCapacity", "getInputNeeded",
                                    "getInputFilledPercentage" },
                            docPlaceholder = "input tank")
    public IChemicalTank inputTank;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class,
                            methodNames = { "getOutput", "getOutputCapacity", "getOutputNeeded",
                                    "getOutputFilledPercentage" },
                            docPlaceholder = "output tank")
    public IChemicalTank outputTank;

    @SyntheticComputerMethod(getter = "getProductionRate")
    private float productionRate;
    private int baselineMaxOperations = 1;
    private int numPowering;
    private byte seeSunCount = 0;

    private final IOutputHandler<@NotNull ChemicalStack> outputHandler;
    private final IInputHandler<@NotNull ChemicalStack> inputHandler;

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "input slot")
    ChemicalInventorySlot inputSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "output slot")
    ChemicalInventorySlot outputSlot;

    public TileEntityLargeSolarNeutronActivator(BlockPos pos, BlockState state) {
        super(LargeMachineBlocks.LARGE_SOLAR_NEUTRON_ACTIVATOR, pos, state, TRACKED_ERROR_TYPES);
        configComponent.setupIOConfig(TransmissionType.ITEM, inputSlot, outputSlot, RelativeSide.FRONT);
        configComponent.setupIOConfig(TransmissionType.CHEMICAL, inputTank, outputTank, RelativeSide.FRONT, false, true);
        configComponent.addDisabledSides(RelativeSide.TOP);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                .setCanTankEject(tank -> tank != inputTank);
        inputHandler = InputHelper.getInputHandler(inputTank, RecipeError.NOT_ENOUGH_INPUT);
        outputHandler = OutputHelper.getOutputHandler(outputTank, RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        // TODO:计划换成左侧专职输入右侧专职输出
        CanAdjustChemicalTankHelper builder = CanAdjustChemicalTankHelper.forSide(facingSupplier, side -> side == RelativeSide.RIGHT || side == RelativeSide.LEFT, side -> side == RelativeSide.BACK);
        // Allow extracting out of the input gas tank if it isn't external OR the output tank is empty AND the input is
        // radioactive
        builder.addTank(inputTank = BasicChemicalTank.createModern(MAX_GAS, ChemicalTankHelper.radioactiveInputTankPredicate(() -> outputTank),
                ConstantPredicates.alwaysTrueBi(), this::containsRecipe, ChemicalAttributeValidator.ALWAYS_ALLOW, recipeCacheListener), RelativeSide.RIGHT, RelativeSide.LEFT);
        builder.addTank(outputTank = BasicChemicalTank.output(MAX_GAS, recipeCacheUnpauseListener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener, IContentsListener recipeCacheListener, IContentsListener recipeCacheUnpauseListener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier, side -> side == RelativeSide.RIGHT || side == RelativeSide.LEFT, side -> side == RelativeSide.BACK);
        builder.addSlot(inputSlot = ChemicalInventorySlot.fill(inputTank, listener, 5, 56), RelativeSide.RIGHT, RelativeSide.LEFT);
        builder.addSlot(outputSlot = ChemicalInventorySlot.drain(outputTank, listener, 155, 56), RelativeSide.BACK);
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
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        if (solarCheck == null) {
            recheckSettings();
        }
        updateSeeSunCount();
        inputSlot.fillTank();
        outputSlot.drainTank();
        productionRate = recalculateProductionRate();
        recipeCacheLookupMonitor.updateAndProcess();
        return sendUpdatePacket;
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

    @Override
    public boolean needConfig() {
        return false;
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<SingleChemicalRecipeInput, ChemicalToChemicalRecipe, InputRecipeCache.SingleChemical<ChemicalToChemicalRecipe>> getRecipeType() {
        return MekanismRecipeType.ACTIVATING;
    }

    @Override
    public IRecipeViewerRecipeType<ChemicalToChemicalRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.ACTIVATING;
    }

    @Nullable
    @Override
    public ChemicalToChemicalRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandler);
    }

    @ComputerMethod
    boolean canSeeSun() {
        if (solarCheck == null) {
            // Note: We assume if solarCheck is null then solarChecks will be filled with null, and if it isn't
            // then it won't be as they get initialized at the same time
            return false;
        }
        // 只要有太阳能板能看到太阳那就工作
        return seeSunCount > 0;
    }

    /**
     * 根据可以看见太阳的太阳能板数获取减少效率的乘数
     *
     * @return 效率减小倍数
     */
    private float reduceMultiplier() {
        // TODO:这里的倍率有点问题，会导致在遮住第六个板子时将为0
        int panelCount = solarChecks.length + 1;
        byte notSeeSunCount = (byte) (panelCount - seeSunCount);
        // 无遮挡或意外情况
        if (notSeeSunCount <= 0) {
            return 0f;
        }
        // 全遮挡或意外情况
        if (notSeeSunCount >= panelCount) {
            return 1f;
        }
        float reduction;
        if (notSeeSunCount <= 3) {
            reduction = 0.05f * notSeeSunCount + 0.8f;
        } else if (notSeeSunCount <= 7) {
            reduction = 0.1f * notSeeSunCount + 0.4f;
        } else {
            reduction = 0.4f;
        }
        return reduction;
    }

    @Override
    public boolean canFunction() {
        // Sort out if the solar neutron activator can see the sun; we no longer check if it's raining here,
        // since under the new rules, we can still function when it's raining, albeit at a significant penalty.
        return super.canFunction() && canSeeSun();
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
    public CachedRecipe<ChemicalToChemicalRecipe> createNewCachedRecipe(@NotNull ChemicalToChemicalRecipe recipe, int cacheIndex) {
        return OneInputCachedRecipe.chemicalToChemical(recipe, recheckAllRecipeErrors, inputHandler, outputHandler)
                .setErrorsChanged(this::onErrorsChanged)
                .setCanHolderFunction(this::canFunction)
                .setActive(this::setActive)
                .setOnFinish(this::markForSave)
                // Edge case handling, this should almost always end up being 1
                .setRequiredTicks(() -> productionRate > 0 && productionRate < 1 ? Mth.ceil(1 / productionRate) : 1)
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
    protected boolean makesComparatorDirty(ContainerType<?, ?, ?> type) {
        return type == ContainerType.CHEMICAL;
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
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, @Nullable Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.CHEMICAL.block()) {
            return Objects.requireNonNull(chemicalHandlerManager, "Expected to have chemical handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.CHEMICAL.block()) {
            return notChemicalPort(side, offset);
        } else if (capability == Capabilities.ITEM.block()) {
            return notItemPort(side, offset);
        }
        return notChemicalPort(side, offset) && notItemPort(side, offset);
    }

    private boolean notChemicalPort(Direction side, Vec3i offset) {
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
        return notChemicalPort(side, offset);
    }

    protected static class LargeSNA extends WorldUtil.SolarCheck {

        private final int recheckFrequency;
        private long lastCheckedSun;

        public LargeSNA(Level world, BlockPos pos) {
            super(world, pos);
            // Recheck between every 10-30 ticks, to not end up checking each position each tick
            recheckFrequency = Mth.nextInt(world.random, MekanismUtils.TICKS_PER_HALF_SECOND, MekanismUtils.TICKS_PER_HALF_SECOND + SharedConstants.TICKS_PER_SECOND);
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
