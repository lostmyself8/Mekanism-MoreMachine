package com.jerry.meklg.common.tile.generator;

import com.jerry.meklm.common.capabilities.holder.chemical.AdjustableChemicalTankHelper;
import com.jerry.meklm.common.capabilities.holder.fluid.AdjustableFluidTankHelper;
import com.jerry.meklm.common.capabilities.holder.heat.AdjustableHeatCapacitorHelper;

import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.api.datamaps.SolarHeatFluid;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.item.ItemReflector;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;
import com.jerry.mekmm.common.util.WorldUtil;
import com.jerry.mekmm.common.util.WorldUtil.CachedSolarCheck;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.datamaps.IMekanismDataMapTypes;
import mekanism.api.datamaps.chemical.attribute.CooledCoolant;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.heat.HeatAPI.HeatTransfer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class TileEntitySolarHeatGenerator extends TileEntityMoreMachineGenerator implements IBoundingBlock {

    public static final Predicate<ChemicalStack> IS_HEATED_COOLANT = chemical -> chemical.getData(IMekanismDataMapTypes.INSTANCE.heatedChemicalCoolant()) != null;
    public static final Predicate<ChemicalStack> IS_COOLED_COOLANT = chemical -> chemical.getData(IMekanismDataMapTypes.INSTANCE.cooledChemicalCoolant()) != null;

    /**
     * The maximum amount of gas this block can store.
     */
    public static final long MAX_GAS = 24 * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;
    public static final int MAX_FLUID = 24 * FluidType.BUCKET_VOLUME * FluidType.BUCKET_VOLUME;

    public static final double HEAT_CAPACITY = 10_000;
    public static final double INVERSE_CONDUCTION_COEFFICIENT = 5;
    public static final double INVERSE_INSULATION_COEFFICIENT = 100;
    /**
     * 放置反射镜的槽位数量
     */
    private static final int SLOT_COUNT = 4;
    private static final int SOLAR_CLEAR_RADIUS = 2;
    private static final int SOLAR_CHECK_INTERVAL = 20;
    private static final double NORTH_SOUTH_HEAT_GAIN_MULTIPLIER = 0.12;
    private static final double GENERATION_CONVERSION_MULTIPLIER = 5D / 3D;
    /**
     * 反射锅能旋转的角度，垂直向上为0度
     */
    private static final float MIN_ANGLE = -30F;
    private static final float MAX_ANGLE = 30F;

    /**
     * 决定槽位是否能渲染反射镜
     */
    private final boolean[] renderPanels = new boolean[SLOT_COUNT];

    private static final String RENDER_PANEL_PREFIX = "renderPanel";

    /**
     * 当前旋转了的角度
     */
    @Getter
    private float angle;
    /**
     * 太阳到中心方块的射线与地面的夹角
     */
    @Getter
    private float sunRayGroundAngle;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getHeatedCoolant", "getHeatedCoolantCapacity", "getHeatedCoolantNeeded",
                                    "getHeatedCoolantFilledPercentage" },
                            docPlaceholder = "heated coolant tank")
    public IChemicalTank superheatedCoolantTank;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getCooledCoolant", "getCooledCoolantCapacity", "getCooledCoolantNeeded",
                                    "getCooledCoolantFilledPercentage" },
                            docPlaceholder = "cooled coolant tank")
    public IChemicalTank cooledCoolantTank;

    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class,
                            methodNames = { "getFluid", "getFluidCapacity", "getFluidNeeded", "getFluidFilledPercentage" },
                            docPlaceholder = "work fluid tank")
    public IExtendedFluidTank fluidTank;

    @WrappingComputerMethod(wrapper = ComputerHeatCapacitorWrapper.class, methodNames = "getTemperature", docPlaceholder = "transmission")
    public BasicHeatCapacitor heatCapacitor;

    private List<IInventorySlot> slots;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;
    private long productionRate;
    @Getter
    private long lastCoolantConverted;
    @Getter
    private double coolantConversionEfficiency;
    @Getter
    private double fluidEfficiency = 1;
    @Getter
    private double lastTransferLoss;
    @Getter
    private double lastEnvironmentLoss;
    @Getter
    private double solarIntensity;
    @Getter
    private double solarVisibility;
    private boolean hasSolarExposure;
    private long lastSolarAreaCheck;
    private CachedSolarCheck[] solarChecks;
    private double clientTemperature;
    private double reflectorDamageProgress;

    @Nullable
    private List<BlockCapabilityCache<IChemicalHandler, @Nullable Direction>> chemicalOutputCaches;

    public TileEntitySolarHeatGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorBlocks.SOLAR_HEAT_GENERATOR, pos, state, () -> 0L);
    }

    @Override
    public @Nullable IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        AdjustableChemicalTankHelper builder = AdjustableChemicalTankHelper.forSide(facingSupplier, side -> side == RelativeSide.BACK, side -> side == RelativeSide.BACK);
        builder.addTank(cooledCoolantTank = BasicChemicalTank.inputModern(MAX_GAS, IS_COOLED_COOLANT, listener), RelativeSide.BACK);
        builder.addTank(superheatedCoolantTank = BasicChemicalTank.createModern(MAX_GAS, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(), IS_HEATED_COOLANT, ChemicalAttributeValidator.ALWAYS_ALLOW, listener), RelativeSide.BACK);
        return builder.build();
    }

    @Override
    protected @Nullable IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        AdjustableFluidTankHelper builder = AdjustableFluidTankHelper.forSide(facingSupplier, side -> side == RelativeSide.BACK, side -> side == RelativeSide.BACK);
        builder.addTank(fluidTank = BasicFluidTank.input(MAX_FLUID, fluidStack -> IMoreMachineDataMapTypes.INSTANCE.getSolarHeatFluid(fluidStack.getFluidHolder()) != null, listener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        AdjustableHeatCapacitorHelper builder = AdjustableHeatCapacitorHelper.forSide(facingSupplier, side -> side == RelativeSide.LEFT || side == RelativeSide.RIGHT, side -> side == RelativeSide.LEFT || side == RelativeSide.RIGHT);
        builder.addCapacitor(heatCapacitor = BasicHeatCapacitor.create(HEAT_CAPACITY, INVERSE_CONDUCTION_COEFFICIENT, INVERSE_INSULATION_COEFFICIENT, ambientTemperature, listener), RelativeSide.LEFT, RelativeSide.RIGHT);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        slots = new ArrayList<>();
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier);
        for (int slotX = 0; slotX < SLOT_COUNT; slotX++) {
            BasicInventorySlot slot = BasicInventorySlot.at(ConstantPredicates.alwaysTrueBi(), (stack, automationType) -> automationType != AutomationType.EXTERNAL && canInsert(stack), listener, 67 + slotX * 18, 57);
            builder.addSlot(slot, RelativeSide.BACK, RelativeSide.TOP);
            slots.add(slot);
        }
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 180, 57));
        return builder.build();
    }

    private boolean canInsert(ItemStack stack) {
        return isValidReflector(stack);
    }

    public static boolean isValidReflector(ItemStack stack) {
        return stack.getItem() instanceof ItemReflector;
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[] { RelativeSide.FRONT, RelativeSide.BACK };
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.drainContainer();

        long previousEnergy = getEnergyContainer().getEnergy();
        lastCoolantConverted = 0;
        productionRate = 0;

        // 消耗反射镜耐久
        int reflectorCount = getReflectorCount();
        updateSolarAreaCheck();
        HeatTransfer loss = updateTemperature(reflectorCount);
        lastTransferLoss = loss.adjacentTransfer();
        lastEnvironmentLoss = loss.environmentTransfer();

        if (canFunction() && hasEnergyCapacity()) {
            GenerationPlan generationPlan = createGenerationPlan();
            if (generationPlan != null) {
                executeGenerationPlan(generationPlan);
            }
        }
        if (level instanceof ServerLevel serverLevel) {
            if (!superheatedCoolantTank.isEmpty()) {
                handleChemicalEject(serverLevel);
            }
            damageReflectors(serverLevel);
        }

        productionRate = getEnergyContainer().getEnergy() - previousEnergy;
        updateMaxOutputRaw(Math.max(productionRate, getEnergyContainer().getEnergy()));

        setActive(isGatheringHeat(reflectorCount) || productionRate > 0);
        if (updateRenderPanels()) {
            sendUpdatePacket = true;
        }
        return sendUpdatePacket;
    }

    private void handleChemicalEject(ServerLevel serverLevel) {
        if (chemicalOutputCaches == null) {
            chemicalOutputCaches = new ArrayList<>(1);
            Direction back = RelativeSide.BACK.getDirection(getDirection());
            chemicalOutputCaches.add(Capabilities.CHEMICAL.createCache(serverLevel, worldPosition.relative(back, 4).relative(getLeftSide()).above(), back.getOpposite()));
        }
        ChemicalUtil.emit(chemicalOutputCaches, superheatedCoolantTank, superheatedCoolantTank.getCapacity());
    }

    @Override
    protected void invalidateDirectionCaches(Direction newDirection) {
        super.invalidateDirectionCaches(newDirection);
        chemicalOutputCaches = null;
    }

    @Override
    protected int portCount(int input) {
        return 4;
    }

    @Override
    protected BlockPos[] offsetOutput(BlockPos from, Direction side) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = getRightSide();
        return new BlockPos[] {
                // 前
                from.offset(new Vec3i(left.getStepX(), 0, left.getStepZ())).offset(new Vec3i(front.getStepX() * 3, 0, front.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(right.getStepX(), 0, right.getStepZ())).offset(new Vec3i(front.getStepX() * 3, 0, front.getStepZ() * 3)).relative(side),
                // 后
                from.offset(new Vec3i(back.getStepX() * 3, 0, back.getStepZ() * 3)).relative(side),
                from.offset(new Vec3i(back.getStepX() * 3, 1, back.getStepZ() * 3)).relative(side),
        };
    }

    private HeatTransfer updateTemperature(int reflectorCount) {
        if (isGatheringHeat(reflectorCount)) {
            double heatGain = MoreMachineConfig.generators.solarHeatHeatGainPerReflector.get() * reflectorCount * calculateSolarIntensity() * heatCapacitor.getHeatCapacity();
            heatCapacitor.handleHeat(heatGain);
        }
        return simulate();
    }

    @Override
    public double getAmbientTemperature(@NotNull Direction side) {
        return getAmbientTemperature();
    }

    private double getAmbientTemperature() {
        return ambientTemperature.getAsDouble();
    }

    private boolean isGatheringHeat(int reflectorCount) {
        return canFunction() && hasEnergyCapacity() && reflectorCount > 0 && hasSolarExposure;
    }

    private boolean hasEnergyCapacity() {
        return getEnergyContainer().getNeeded() > 0L;
    }

    private boolean isEastWestFacing() {
        Direction direction = getDirection();
        return direction == Direction.EAST || direction == Direction.WEST;
    }

    private void updateSolarAreaCheck() {
        if (level == null) {
            hasSolarExposure = false;
            solarVisibility = 0;
            solarIntensity = 0;
            return;
        }
        long gameTime = level.getGameTime();
        if (gameTime < lastSolarAreaCheck + SOLAR_CHECK_INTERVAL) {
            return;
        }
        lastSolarAreaCheck = gameTime;
        if (solarChecks == null) {
            recheckSolarSettings();
        }
        updateSolarStats();
    }

    private void recheckSolarSettings() {
        if (level == null) {
            solarChecks = null;
            return;
        }
        BlockPos center = getBlockPos().above(3);
        solarChecks = new CachedSolarCheck[(SOLAR_CLEAR_RADIUS * 2 + 1) * (SOLAR_CLEAR_RADIUS * 2 + 1)];
        int index = 0;
        for (int x = -SOLAR_CLEAR_RADIUS; x <= SOLAR_CLEAR_RADIUS; x++) {
            for (int z = -SOLAR_CLEAR_RADIUS; z <= SOLAR_CLEAR_RADIUS; z++) {
                solarChecks[index++] = new CachedSolarCheck(level, center.offset(x, 0, z));
            }
        }
    }

    private void updateSolarStats() {
        if (level == null || solarChecks == null || !level.dimensionType().hasSkyLight() || level.getSkyDarken() >= 4) {
            hasSolarExposure = false;
            solarVisibility = 0;
            solarIntensity = 0;
            return;
        }
        int visible = 0;
        for (CachedSolarCheck check : solarChecks) {
            check.recheckCanSeeSun();
            if (check.canSeeSun()) {
                visible++;
            }
        }
        solarVisibility = solarChecks.length == 0 ? 0 : visible / (double) solarChecks.length;
        hasSolarExposure = visible > 0;
        if (!hasSolarExposure) {
            solarIntensity = 0;
        }
    }

    private double calculateSolarIntensity() {
        if (level == null || !hasSolarExposure || solarChecks == null) {
            solarIntensity = 0;
            return 0;
        }
        double productionMultiplier = 0;
        for (CachedSolarCheck check : solarChecks) {
            productionMultiplier += check.getProductionMultiplier();
        }
        productionMultiplier /= solarChecks.length;
        double sunBrightness = WorldUtils.getSunBrightness(level, 1.0F);
        double directionMultiplier;
        if (isEastWestFacing()) {
            double angle = WorldUtil.calculateSunRayGroundAngle(level, getBlockPos().above(3));
            directionMultiplier = angle <= 0 ? 0 : Math.sin(Math.toRadians(angle));
        } else {
            directionMultiplier = NORTH_SOUTH_HEAT_GAIN_MULTIPLIER;
        }
        double multiplier = directionMultiplier * sunBrightness * productionMultiplier;
        solarIntensity = Mth.clamp(multiplier, 0, 1);
        return solarIntensity;
    }

    @Nullable
    private CooledCoolant getCooledCoolant(ChemicalStack stack) {
        return stack.getData(IMekanismDataMapTypes.INSTANCE.cooledChemicalCoolant());
    }

    @Nullable
    private SolarHeatFluid getSolarHeatFluid() {
        if (fluidTank.isEmpty()) {
            return null;
        }
        FluidStack fluid = fluidTank.getFluid();
        return IMoreMachineDataMapTypes.INSTANCE.getSolarHeatFluid(fluid.getFluidHolder());
    }

    @Nullable
    private GenerationPlan createGenerationPlan() {
        SolarHeatFluid workFluid = getSolarHeatFluid();
        if (workFluid == null || fluidTank.getFluidAmount() < workFluid.consumption() || cooledCoolantTank.isEmpty()) {
            coolantConversionEfficiency = 0;
            fluidEfficiency = workFluid == null ? 0 : workFluid.generationModifier();
            return null;
        }
        ChemicalStack cooledCoolant = cooledCoolantTank.getStack();
        CooledCoolant coolant = getCooledCoolant(cooledCoolant);
        if (coolant == null) {
            coolantConversionEfficiency = 0;
            fluidEfficiency = workFluid.generationModifier();
            return null;
        }
        double availableHeat = Math.max(0, getSolarHeatTemperature() - MoreMachineConfig.generators.solarHeatTargetConversionTemperature.get()) * heatCapacitor.getHeatCapacity() * coolant.conductivity();
        double thermalEnthalpy = coolant.thermalEnthalpy();
        if (availableHeat <= 0 || thermalEnthalpy <= 0) {
            coolantConversionEfficiency = 0;
            fluidEfficiency = workFluid.generationModifier();
            return null;
        }
        double energyPerHeat = getEnergyPerHeat(workFluid);
        if (energyPerHeat <= 0) {
            coolantConversionEfficiency = 0;
            fluidEfficiency = workFluid.generationModifier();
            return null;
        }
        long maxCoolantFromHeat = MathUtils.clampToLong(availableHeat / thermalEnthalpy);
        long maxCoolantFromEnergy = MathUtils.clampToLong(getEnergyContainer().getNeeded() / (thermalEnthalpy * energyPerHeat));
        long coolantAmount = Math.min(Math.min(maxCoolantFromHeat, maxCoolantFromEnergy), cooledCoolantTank.getStored());
        if (coolantAmount <= 0) {
            coolantConversionEfficiency = 0;
            fluidEfficiency = workFluid.generationModifier();
            return null;
        }
        ChemicalStack heatedCoolant = coolant.heat(coolantAmount);
        coolantAmount -= superheatedCoolantTank.insert(heatedCoolant, Action.SIMULATE, AutomationType.INTERNAL).getAmount();
        if (coolantAmount <= 0) {
            coolantConversionEfficiency = 0;
            fluidEfficiency = workFluid.generationModifier();
            return null;
        }
        double heatRemoved = coolantAmount * thermalEnthalpy;
        long generatedEnergy = calculateGeneratedEnergy(heatRemoved, energyPerHeat);
        if (generatedEnergy <= 0 || generatedEnergy > getEnergyContainer().getNeeded()) {
            coolantConversionEfficiency = 0;
            fluidEfficiency = workFluid.generationModifier();
            return null;
        }
        if (getEnergyContainer().insert(generatedEnergy, Action.SIMULATE, AutomationType.INTERNAL) != 0) {
            throw new IllegalStateException("Solar heat generation plan exceeded available energy storage");
        }
        return new GenerationPlan(coolantAmount, coolant.heat(coolantAmount), heatRemoved, workFluid.consumption(), generatedEnergy,
                availableHeat, workFluid.generationModifier());
    }

    private double getEnergyPerHeat(SolarHeatFluid workFluid) {
        double conversionTemperature = MoreMachineConfig.generators.solarHeatTargetConversionTemperature.get();
        double ratedTemperature = MoreMachineConfig.generators.solarHeatRatedGenerationTemperature.get();
        double thermalHeadFactor = Math.max(0, getSolarHeatTemperature() - conversionTemperature) / Math.max(1, ratedTemperature - conversionTemperature);
        return workFluid.consumption() * workFluid.generationModifier() * GENERATION_CONVERSION_MULTIPLIER * thermalHeadFactor;
    }

    private long calculateGeneratedEnergy(double heatRemoved, double energyPerHeat) {
        return MathUtils.clampToLong(heatRemoved * energyPerHeat);
    }

    private void executeGenerationPlan(GenerationPlan generationPlan) {
        ChemicalStack remainder = superheatedCoolantTank.insert(generationPlan.heatedCoolant(), Action.EXECUTE, AutomationType.INTERNAL);
        if (!remainder.isEmpty()) {
            throw new IllegalStateException("Solar heat generation plan could not insert superheated coolant");
        }
        cooledCoolantTank.shrinkStack(generationPlan.coolantAmount(), Action.EXECUTE);
        fluidTank.shrinkStack(generationPlan.workFluidConsumption(), Action.EXECUTE);
        heatCapacitor.handleHeat(-generationPlan.heatRemoved());
        if (getEnergyContainer().insert(generationPlan.generatedEnergy(), Action.EXECUTE, AutomationType.INTERNAL) != 0) {
            throw new IllegalStateException("Solar heat generation plan could not insert generated energy");
        }
        lastCoolantConverted = generationPlan.coolantAmount();
        coolantConversionEfficiency = Mth.clamp(generationPlan.heatRemoved() / generationPlan.availableHeat(), 0, 1);
        fluidEfficiency = generationPlan.generationModifier();
    }

    private void damageReflectors(ServerLevel level) {
        if (getReflectorCount() == 0) {
            return;
        }
        double damage = getReflectorDamagePerTick();
        if (damage <= 0) {
            return;
        }
        reflectorDamageProgress += damage;
        int wholeDamage = MathUtils.clampToInt(Math.floor(reflectorDamageProgress));
        if (wholeDamage <= 0) {
            return;
        }
        reflectorDamageProgress -= wholeDamage;
        for (IInventorySlot slot : slots) {
            ItemStack stack = slot.getStack();
            if (stack.getItem() instanceof ItemReflector) {
                ItemStack damagedStack = stack.copy();
                damagedStack.hurtAndBreak(wholeDamage, level, null, item -> {});
                slot.setStack(damagedStack);
            }
        }
    }

    public double getReflectorDamagePerTick() {
        if (getReflectorCount() == 0) {
            return 0;
        }
        return Math.max(0, getSolarHeatTemperature() - MoreMachineConfig.generators.solarHeatReflectorDamageThreshold.get()) * MoreMachineConfig.generators.solarHeatReflectorDamageRate.get();
    }

    private record GenerationPlan(long coolantAmount, ChemicalStack heatedCoolant, double heatRemoved,
                                  int workFluidConsumption,
                                  long generatedEnergy, double availableHeat, double generationModifier) {}

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        updateAngle();
    }

    private void updateAngle() {
        if (level == null) {
            sunRayGroundAngle = 0F;
            return;
        }
        sunRayGroundAngle = WorldUtil.calculateSunRayGroundAngle(level, getBlockPos().above(3));
        if (sunRayGroundAngle > 0F) {
            angle = calculateSunTrackingReflectorAngle();
        }
    }

    private float clampAngle(float angle) {
        return Mth.clamp(angle, MIN_ANGLE, MAX_ANGLE);
    }

    // 让反射镜对着太阳
    private float calculateSunTrackingReflectorAngle() {
        if (level == null) {
            return angle;
        }
        double dayProgress = Math.floorMod(level.getDayTime(), 24_000L) / 24_000D;
        float eastTrackingAngle = clampAngle((float) (MAX_ANGLE - (MAX_ANGLE - MIN_ANGLE) * dayProgress * 2D));
        return switch (getDirection()) {
            case EAST -> eastTrackingAngle;
            case WEST -> -eastTrackingAngle;
            default -> 0F;
        };
    }

    private boolean updateRenderPanels() {
        boolean changed = false;
        for (int slot = 0; slot < renderPanels.length; slot++) {
            boolean hasPanel = hasReflectorInSlot(slot);
            if (renderPanels[slot] != hasPanel) {
                renderPanels[slot] = hasPanel;
                changed = true;
            }
        }
        return changed;
    }

    // 槽位中是否有物品
    private boolean hasReflectorInSlot(int slot) {
        return slots != null && slot >= 0 && slot < slots.size() && slots.get(slot).getStack().getItem() instanceof ItemReflector;
    }

    public int getReflectorCount() {
        if (slots == null) {
            return 0;
        }
        int count = 0;
        for (int slot = 0; slot < slots.size(); slot++) {
            if (hasReflectorInSlot(slot)) {
                count++;
            }
        }
        return count;
    }

    public double getSolarHeatTemperature() {
        if (level != null && level.isClientSide()) {
            return clientTemperature;
        }
        return heatCapacitor == null ? getAmbientTemperature() : heatCapacitor.getTemperature();
    }

    private void setClientTemperature(double temperature) {
        clientTemperature = temperature;
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag updateTag = super.getReducedUpdateTag(provider);
        for (int slot = 0; slot < renderPanels.length; slot++) {
            updateTag.putBoolean(RENDER_PANEL_PREFIX + slot, hasReflectorInSlot(slot));
        }
        return updateTag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.handleUpdateTag(tag, provider);
        for (int slot = 0; slot < renderPanels.length; slot++) {
            String key = RENDER_PANEL_PREFIX + slot;
            if (tag.contains(key)) {
                renderPanels[slot] = tag.getBoolean(key);
            }
        }
    }

    @Override
    public void readSustainedData(HolderLookup.Provider provider, CompoundTag data) {
        super.readSustainedData(provider, data);
        reflectorDamageProgress = data.getDouble(MoreMachineSerializationConstants.REFLECTOR_DAMAGE_PROGRESS);
    }

    @Override
    public void writeSustainedData(HolderLookup.Provider provider, CompoundTag data) {
        super.writeSustainedData(provider, data);
        data.putDouble(MoreMachineSerializationConstants.REFLECTOR_DAMAGE_PROGRESS, reflectorDamageProgress);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(nbt, provider);
        reflectorDamageProgress = nbt.getDouble(MoreMachineSerializationConstants.REFLECTOR_DAMAGE_PROGRESS);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(nbt, provider);
        nbt.putDouble(MoreMachineSerializationConstants.REFLECTOR_DAMAGE_PROGRESS, reflectorDamageProgress);
    }

    public boolean shouldRenderPanel(int slot) {
        return slot >= 0 && slot < renderPanels.length && renderPanels[slot];
    }

    @Override
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.CHEMICAL.block()) {
            return Objects.requireNonNull(chemicalHandlerManager, "Expected to have chemical handler").resolve(capability, side);
        } else if (capability == Capabilities.FLUID.block()) {
            return Objects.requireNonNull(fluidHandlerManager, "Expected to have fluid handler").resolve(capability, side);
        } else if (capability == Capabilities.HEAT) {
            return Objects.requireNonNull(heatHandlerManager, "Expected to have heat handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return notEnergyPort(side, offset);
        } else if (capability == Capabilities.CHEMICAL.block()) {
            return notChemicalPort(side, offset);
        } else if (capability == Capabilities.FLUID.block()) {
            return notFluidPort(side, offset);
        } else if (capability == Capabilities.HEAT) {
            return notHeatPort(side, offset);
        } else if (capability == Capabilities.ITEM.block()) {
            return notItemPort(side, offset);
        }
        return true;
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = getRightSide();
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, front.getStepZ() * 3))) {
                    return side != front;
                }
                if (offset.equals(new Vec3i(right.getStepX(), 0, front.getStepZ() * 3))) {
                    return side != front;
                }
                if (offset.equals(new Vec3i(0, 0, back.getStepZ() * 3)) || offset.equals(new Vec3i(0, 1, back.getStepZ() * 3))) {
                    return side != back;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(front.getStepX() * 3, 0, left.getStepZ()))) {
                    return side != front;
                }
                if (offset.equals(new Vec3i(front.getStepX() * 3, 0, right.getStepZ()))) {
                    return side != front;
                }
                if (offset.equals(new Vec3i(back.getStepX() * 3, 0, 0)) || offset.equals(new Vec3i(back.getStepX() * 3, 1, 0))) {
                    return side != back;
                }
            }
        }
        return true;
    }

    private boolean notChemicalPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = getRightSide();
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 1, back.getStepZ() * 3)) || offset.equals(new Vec3i(right.getStepX(), 1, back.getStepZ() * 3))) {
                    return side != back;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX() * 3, 1, left.getStepZ())) || offset.equals(new Vec3i(back.getStepX() * 3, 1, right.getStepZ()))) {
                    return side != back;
                }
            }
        }
        return true;
    }

    private boolean notFluidPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = getRightSide();
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX(), 0, back.getStepZ() * 3)) || offset.equals(new Vec3i(right.getStepX(), 0, back.getStepZ() * 3))) {
                    return side != back;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX() * 3, 0, left.getStepZ())) || offset.equals(new Vec3i(back.getStepX() * 3, 0, right.getStepZ()))) {
                    return side != back;
                }
            }
        }
        return true;
    }

    private boolean notHeatPort(Direction side, Vec3i offset) {
        Direction front = getDirection();
        Direction back = getOppositeDirection();
        Direction left = getLeftSide();
        Direction right = getRightSide();
        switch (front) {
            case NORTH, SOUTH -> {
                if (offset.equals(new Vec3i(left.getStepX() * 3, 0, back.getStepZ())) || offset.equals(new Vec3i(left.getStepX() * 3, 0, front.getStepZ()))) {
                    return side != left;
                }
                if (offset.equals(new Vec3i(right.getStepX() * 3, 0, back.getStepZ())) || offset.equals(new Vec3i(right.getStepX() * 3, 0, front.getStepZ()))) {
                    return side != right;
                }
            }
            case WEST, EAST -> {
                if (offset.equals(new Vec3i(back.getStepX(), 0, left.getStepZ() * 3)) || offset.equals(new Vec3i(front.getStepX(), 0, left.getStepZ() * 3))) {
                    return side != left;
                }
                if (offset.equals(new Vec3i(back.getStepX(), 0, right.getStepZ() * 3)) || offset.equals(new Vec3i(front.getStepX(), 0, right.getStepZ() * 3))) {
                    return side != right;
                }
            }
        }
        return true;
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        return notEnergyPort(side, offset) && notChemicalPort(side, offset) && notFluidPort(side, offset);
    }

    @Override
    public long getProductionRate() {
        return productionRate;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableLong.create(this::getProductionRate, value -> productionRate = value));
        container.track(SyncableLong.create(this::getLastCoolantConverted, value -> lastCoolantConverted = value));
        container.track(SyncableDouble.create(this::getCoolantConversionEfficiency, value -> coolantConversionEfficiency = value));
        container.track(SyncableDouble.create(this::getFluidEfficiency, value -> fluidEfficiency = value));
        container.track(SyncableDouble.create(this::getLastTransferLoss, value -> lastTransferLoss = value));
        container.track(SyncableDouble.create(this::getLastEnvironmentLoss, value -> lastEnvironmentLoss = value));
        container.track(SyncableDouble.create(this::getSolarIntensity, value -> solarIntensity = value));
        container.track(SyncableDouble.create(this::getSolarVisibility, value -> solarVisibility = value));
        container.track(SyncableDouble.create(this::getSolarHeatTemperature, this::setClientTemperature));
        container.track(SyncableInt.create(this::getReflectorCount, value -> {}));
    }
}
