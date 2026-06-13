package com.jerry.meklg.common.tile.generator;

import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.api.datamaps.SolarHeatFluid;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.item.ItemReflector;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;
import com.jerry.mekmm.common.util.WorldUtil.SolarCheck;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.datamaps.IMekanismDataMapTypes;
import mekanism.api.datamaps.chemical.attribute.CooledCoolant;
import mekanism.api.fluid.IFluidTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.heat.HeatAPI.HeatTransfer;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class TileEntitySolarHeatGenerator extends TileEntityMoreMachineGenerator implements IBoundingBlock {

    public static final Predicate<ChemicalResource> IS_HEATED_COOLANT = chemical -> chemical.getData(IMekanismDataMapTypes.INSTANCE.heatedChemicalCoolant()) != null;
    public static final Predicate<ChemicalResource> IS_COOLED_COOLANT = chemical -> chemical.getData(IMekanismDataMapTypes.INSTANCE.cooledChemicalCoolant()) != null;

    /**
     * The maximum amount of gas this block can store.
     */
    public static final long MAX_GAS = 24 * FluidType.BUCKET_VOLUME;
    public static final int MAX_FLUID = 24 * FluidType.BUCKET_VOLUME;

    public static final double HEAT_CAPACITY = 10_000;
    public static final double INVERSE_CONDUCTION_COEFFICIENT = 5;
    public static final double INVERSE_INSULATION_COEFFICIENT = 100;
    /**
     * 放置反射镜的槽位数量
     */
    public static final int SLOT_COUNT = 4;
    private static final int SOLAR_CLEAR_RADIUS = 2;
    private static final int SOLAR_CHECK_INTERVAL = 20;
    private static final double NORTH_SOUTH_HEAT_TARGET = 900;
    private static final double NORTH_SOUTH_HEAT_GAIN_MULTIPLIER = 0.12;
    private static final double GENERATION_HEAT_MULTIPLIER = 8;
    /**
     * 反射锅能旋转的角度，垂直向上为0度
     */
    private static final float MIN_ANGLE = -30F;
    private static final float MAX_ANGLE = 30F;

    /**
     * 决定槽位是否能渲染反射镜
     */
    private final boolean[] renderPanels = new boolean[SLOT_COUNT];

    /**
     * 当前旋转了的角度
     */
    private float angle;
    /**
     * 太阳到中心方块的射线与地面的夹角
     */
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

    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = { "", "", "", "" }, docPlaceholder = "")
    public BasicFluidTank fluidTank;

    @WrappingComputerMethod(wrapper = ComputerHeatCapacitorWrapper.class, methodNames = "getTemperature", docPlaceholder = "transmission")
    public BasicHeatCapacitor heatCapacitor;

    private List<IInventorySlot> slots;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;
    private long productionRate;
    private long lastCoolantConverted;
    private double coolantConversionEfficiency;
    private double fluidEfficiency = 1;
    private double lastTransferLoss;
    private double lastEnvironmentLoss;
    private double solarIntensity;
    private double solarVisibility;
    private boolean hasSolarExposure;
    private long lastSolarAreaCheck;
    private SolarHeatCheck[] solarChecks;
    private double clientTemperature;

    public TileEntitySolarHeatGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorBlocks.SOLAR_HEAT_GENERATOR, pos, state);
    }

    @Override
    public @NotNull IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener) {
        MekContainerHelper<IChemicalTank> builder = MekContainerHelper.forSide(facingSupplier);
        builder.addContainer(cooledCoolantTank = BasicChemicalTank.input(MAX_GAS, IS_COOLED_COOLANT, listener), RelativeSide.BACK);
        builder.addContainer(superheatedCoolantTank = BasicChemicalTank.create(MAX_GAS, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(), IS_HEATED_COOLANT, ChemicalAttributeValidator.ALWAYS_ALLOW, listener), RelativeSide.BACK);
        return builder.build();
    }

    @Override
    protected @NotNull IContainerHolder<IFluidTank> getInitialFluidTanks(IContentsListener listener) {
        MekContainerHelper<IFluidTank> builder = MekContainerHelper.forSide(facingSupplier);
        builder.addContainer(fluidTank = BasicFluidTank.input(MAX_FLUID, fluid -> IMoreMachineDataMapTypes.INSTANCE.getSolarHeatFluid(fluid.typeHolder()) != null, listener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IContainerHolder<IHeatCapacitor> getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        MekContainerHelper<IHeatCapacitor> builder = MekContainerHelper.forSide(facingSupplier);
        builder.addContainer(heatCapacitor = BasicHeatCapacitor.create(HEAT_CAPACITY, INVERSE_CONDUCTION_COEFFICIENT, INVERSE_INSULATION_COEFFICIENT, ambientTemperature, listener), RelativeSide.LEFT, RelativeSide.RIGHT);
        return builder.build();
    }

    @NotNull
    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        slots = new ArrayList<>();
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSide(facingSupplier);
        for (int slotX = 0; slotX < SLOT_COUNT; slotX++) {
            BasicInventorySlot slot = BasicInventorySlot.at(ConstantPredicates.alwaysTrueBi(), (stack, automationType) -> automationType != AutomationType.EXTERNAL && canInsert(stack.toStack()), listener, 67 + slotX * 18, 57);
            builder.addContainer(slot, RelativeSide.BACK, RelativeSide.TOP);
            slots.add(slot);
        }
        builder.addContainer(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 180, 57));
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
    protected boolean onUpdateServer(net.minecraft.server.level.ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        energySlot.drainContainerIntoSlot(null);

        long previousEnergy = getEnergyContainer().getAmountAsLong();
        lastCoolantConverted = 0;
        productionRate = 0;

        // 消耗反射镜耐久
        int reflectorCount = getReflectorCount();
        updateSolarAreaCheck();
        HeatTransfer loss = updateTemperature(reflectorCount);
        lastTransferLoss = loss.adjacentTransfer();
        lastEnvironmentLoss = loss.environmentTransfer();

        if (canFunction()) {
            CoolantConversion conversion = convertCoolant();
            lastCoolantConverted = conversion.converted();
            if (conversion.converted() > 0 && getEnergyContainer().getNeededAsLong() > 0L) {
                SolarHeatFluid solarHeatFluid = getSolarHeatFluid();
                long generation = calculateGeneration(conversion, solarHeatFluid);
                consumeSolarHeatFluid(conversion.converted(), solarHeatFluid, generation);
                try (Transaction transaction = Transaction.openRoot()) {
                    getEnergyContainer().insert(MathUtils.clampToInt(generation), transaction, AutomationType.INTERNAL);
                    transaction.commit();
                }
            }
        }
        if (isGatheringHeat(reflectorCount) && level instanceof ServerLevel serverLevel) {
            damageReflectors(serverLevel);
        }

        productionRate = getEnergyContainer().getAmountAsLong() - previousEnergy;
        updateMaxOutputRaw(MoreMachineConfig.generators.solarHeatGeneration.get());

        setActive(isGatheringHeat(reflectorCount) || productionRate > 0);
        if (updateRenderPanels()) {
            sendUpdatePacket = true;
        }
        return sendUpdatePacket;
    }

    @Override
    protected BlockPos offSetOutput(BlockPos from, Direction side) {
        Direction back = getOppositeDirection();
        return from.offset(new Vec3i(back.getStepX() * 3, 0, back.getStepZ() * 3)).relative(side);
        // 前
    }

    private HeatTransfer updateTemperature(int reflectorCount) {
        double ambientTemp = getAmbientTemperature();
        double temperature = getSolarHeatTemperature();
        double targetTemperature = getTargetTemperature(reflectorCount, ambientTemp);
        if (isGatheringHeat(reflectorCount) && temperature < targetTemperature) {
            double heatNeeded = (targetTemperature - temperature) * heatCapacitor.getHeatCapacity();
            double heatGain = MoreMachineConfig.generators.solarHeatHeatGainPerReflector.get() * reflectorCount * calculateSolarIntensity() * heatCapacitor.getHeatCapacity();
            heatCapacitor.handleHeat(Math.min(heatGain, heatNeeded));
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

    private double getTargetTemperature(int reflectorCount, double ambientTemp) {
        if (reflectorCount <= 0 || !hasSolarExposure) {
            return ambientTemp;
        }
        if (isEastWestFacing()) {
            double focusedTemperature = ambientTemp + 8_000 * reflectorCount * Math.max(0.2, solarVisibility);
            return Math.min(MoreMachineConfig.generators.solarHeatMaxTemperature.get(), focusedTemperature);
        }
        return Math.min(MoreMachineConfig.generators.solarHeatMaxTemperature.get(), ambientTemp + NORTH_SOUTH_HEAT_TARGET);
    }

    private double positiveTemperatureRange(double targetTemp, double ambientTemp) {
        return Math.max(1, targetTemp - ambientTemp);
    }

    private boolean isGatheringHeat(int reflectorCount) {
        return canFunction() && reflectorCount > 0 && hasSolarExposure;
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
        solarChecks = new SolarHeatCheck[(SOLAR_CLEAR_RADIUS * 2 + 1) * (SOLAR_CLEAR_RADIUS * 2 + 1)];
        int index = 0;
        for (int x = -SOLAR_CLEAR_RADIUS; x <= SOLAR_CLEAR_RADIUS; x++) {
            for (int z = -SOLAR_CLEAR_RADIUS; z <= SOLAR_CLEAR_RADIUS; z++) {
                solarChecks[index++] = new SolarHeatCheck(level, center.offset(x, 0, z));
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
        for (SolarHeatCheck check : solarChecks) {
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
        for (SolarHeatCheck check : solarChecks) {
            productionMultiplier += check.getProductionMultiplier();
        }
        productionMultiplier /= solarChecks.length;
        double sunBrightness = WorldUtils.getSunBrightness(level, getBlockPos().above(3));
        double directionMultiplier;
        if (isEastWestFacing()) {
            double angle = calculateSunRayGroundAngle(getBlockPos().above(3));
            directionMultiplier = angle <= 0 ? 0 : Math.sin(Math.toRadians(angle));
        } else {
            directionMultiplier = NORTH_SOUTH_HEAT_GAIN_MULTIPLIER;
        }
        double multiplier = directionMultiplier * sunBrightness * productionMultiplier;
        solarIntensity = Mth.clamp(multiplier, 0, 1);
        return solarIntensity;
    }

    @Nullable
    private CooledCoolant getCooledCoolant(ChemicalResource stack) {
        return stack.getData(IMekanismDataMapTypes.INSTANCE.cooledChemicalCoolant());
    }

    private CoolantConversion convertCoolant() {
        if (cooledCoolantTank.isEmpty()) {
            coolantConversionEfficiency = 0;
            fluidEfficiency = getSolarHeatFluidEfficiency();
            return CoolantConversion.NONE;
        }
        ChemicalResource cooledCoolant = cooledCoolantTank.resource();
        CooledCoolant coolantType = getCooledCoolant(cooledCoolant);
        if (coolantType == null) {
            coolantConversionEfficiency = 0;
            fluidEfficiency = getSolarHeatFluidEfficiency();
            return CoolantConversion.NONE;
        }
        fluidEfficiency = getSolarHeatFluidEfficiency();
        double availableHeat = Math.max(0, getSolarHeatTemperature() - MoreMachineConfig.generators.solarHeatTargetConversionTemperature.get()) * heatCapacitor.getHeatCapacity();
        availableHeat *= coolantType.conductivity();
        int toConvert = MathUtils.clampToInt(Math.min(MathUtils.clampToLong(availableHeat / coolantType.thermalEnthalpy()), cooledCoolantTank.amountAsLong()));
        if (toConvert <= 0) {
            coolantConversionEfficiency = 0;
            return CoolantConversion.NONE;
        }
        ChemicalResource heatedCoolant = coolantType.heat();
        if (!superheatedCoolantTank.isEmpty() && !superheatedCoolantTank.resource().equals(heatedCoolant)) {
            coolantConversionEfficiency = 0;
            return CoolantConversion.NONE;
        }
        int inserted;
        try (Transaction transaction = Transaction.openRoot()) {
            inserted = superheatedCoolantTank.insert(heatedCoolant, toConvert, transaction, AutomationType.INTERNAL);
            if (inserted <= 0 || cooledCoolantTank.extract(cooledCoolant, inserted, transaction, AutomationType.INTERNAL) != inserted) {
                coolantConversionEfficiency = 0;
                return CoolantConversion.NONE;
            }
            transaction.commit();
        }
        double heatRemoved = inserted * coolantType.thermalEnthalpy();
        heatCapacitor.handleHeat(-heatRemoved);
        coolantConversionEfficiency = Mth.clamp(heatRemoved / Math.max(1, availableHeat), 0, 1);
        return new CoolantConversion(inserted, heatRemoved);
    }

    @Nullable
    private SolarHeatFluid getSolarHeatFluid() {
        if (fluidTank.isEmpty()) {
            return null;
        }
        return IMoreMachineDataMapTypes.INSTANCE.getSolarHeatFluid(fluidTank.resource().typeHolder());
    }

    private double getSolarHeatFluidEfficiency() {
        SolarHeatFluid solarHeatFluid = getSolarHeatFluid();
        return solarHeatFluid == null ? 0 : solarHeatFluid.efficiency();
    }

    private void consumeSolarHeatFluid(long converted, @Nullable SolarHeatFluid solarHeatFluid, long generated) {
        if (generated > 0 && solarHeatFluid != null && solarHeatFluid.usage() > 0) {
            FluidResource fluid = fluidTank.resource();
            try (Transaction transaction = Transaction.openRoot()) {
                fluidTank.extract(fluid, getSolarHeatFluidUsage(converted, solarHeatFluid), transaction, AutomationType.INTERNAL);
                transaction.commit();
            }
        }
    }

    private int getSolarHeatFluidUsage(long converted, SolarHeatFluid solarHeatFluid) {
        return MathUtils.clampToInt(Math.ceil(converted * solarHeatFluid.usage()));
    }

    private long calculateGeneration(CoolantConversion conversion, @Nullable SolarHeatFluid solarHeatFluid) {
        if (solarHeatFluid == null || solarHeatFluid.efficiency() <= 0) {
            return 0;
        }
        if (solarHeatFluid.usage() > 0 && fluidTank.amountAsInt() < getSolarHeatFluidUsage(conversion.converted(), solarHeatFluid)) {
            return 0;
        }
        double ambientTemp = getAmbientTemperature();
        double temperature = getSolarHeatTemperature();
        double tempPowerFactor;
        double targetTemp = MoreMachineConfig.generators.solarHeatTargetConversionTemperature.get();
        double optimalTemp = MoreMachineConfig.generators.solarHeatOptimalGenerationTemperature.get();
        tempPowerFactor = Math.sqrt(Mth.clamp((temperature - targetTemp) / positiveTemperatureRange(optimalTemp, targetTemp), 0, 1));
        long generation = MathUtils.clampToLong(conversion.heatRemoved() * solarHeatFluid.efficiency() * GENERATION_HEAT_MULTIPLIER * tempPowerFactor);
        return Math.min(MoreMachineConfig.generators.solarHeatGeneration.get(), generation);
    }

    private void damageReflectors(ServerLevel level) {
        double ambientTemp = getAmbientTemperature();
        double temperature = getSolarHeatTemperature();
        double heatRatio = Mth.clamp((temperature - ambientTemp) / positiveTemperatureRange(MoreMachineConfig.generators.solarHeatMaxTemperature.get(), ambientTemp), 0, 1);
        double criticalTemp = MoreMachineConfig.generators.solarHeatCriticalGenerationTemperature.get();
        double overheatRatio = Mth.clamp((temperature - criticalTemp) / positiveTemperatureRange(MoreMachineConfig.generators.solarHeatMaxTemperature.get(), criticalTemp), 0, 1);
        double damageChance = 0.0005 + 0.004 * heatRatio * heatRatio + 0.04 * overheatRatio * overheatRatio;
        if (!isEastWestFacing()) {
            damageChance *= 0.35;
        }
        for (IInventorySlot slot : slots) {
            ItemStack stack = slot.resource().toStack(slot.amountAsInt());
            if (stack.getItem() instanceof ItemReflector && level.getRandom().nextDouble() < damageChance) {
                ItemStack damagedStack = stack.copy();
                damagedStack.hurtAndBreak(1, level, null, item -> {});
                slot.setContents(ItemResource.of(damagedStack), damagedStack.getCount(), null);
            }
        }
    }

    private record CoolantConversion(long converted, double heatRemoved) {

        private static final CoolantConversion NONE = new CoolantConversion(0, 0);
    }

    @Override
    protected void onUpdateClient(net.minecraft.world.level.Level level) {
        super.onUpdateClient(level);
        updateAngle();
    }

    private void updateAngle() {
        sunRayGroundAngle = calculateSunRayGroundAngle(getBlockPos().above(3));
        if (sunRayGroundAngle > 0F) {
            angle = calculateSunTrackingReflectorAngle();
        }
    }

    // 计算太阳到目标方块的直线与地面的夹角度数
    private float calculateSunRayGroundAngle(BlockPos targetPos) {
        if (level == null || !level.dimensionType().hasSkyLight()) {
            return 0F;
        }
        double dayProgress = Math.floorMod(level.getGameTime(), 24_000L) / 24_000D;
        double sunRadians = dayProgress * Math.PI * 2D;
        Vec3 target = Vec3.atCenterOf(targetPos);
        Vec3 sun = target.add(Math.cos(sunRadians) * 1024D, Math.sin(sunRadians) * 1024D, 0D);
        Vec3 ray = target.subtract(sun);
        double horizontalLength = Math.sqrt(ray.x * ray.x + ray.z * ray.z);
        return (float) Math.toDegrees(Math.atan2(-ray.y, horizontalLength));
    }

    private float clampAngle(float angle) {
        return Mth.clamp(angle, MIN_ANGLE, MAX_ANGLE);
    }

    // 让反射镜对着太阳
    private float calculateSunTrackingReflectorAngle() {
        if (level == null) {
            return angle;
        }
        double dayProgress = Math.floorMod(level.getGameTime(), 24_000L) / 24_000D;
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
        return slots != null && slot >= 0 && slot < slots.size() && slots.get(slot).resource().getItem() instanceof ItemReflector;
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

    public float getAngle() {
        return angle;
    }

    public float getSunRayGroundAngle() {
        return sunRayGroundAngle;
    }

    public long getLastCoolantConverted() {
        return lastCoolantConverted;
    }

    public double getCoolantConversionEfficiency() {
        return coolantConversionEfficiency;
    }

    public double getFluidEfficiency() {
        return fluidEfficiency;
    }

    public double getLastTransferLoss() {
        return lastTransferLoss;
    }

    public double getLastEnvironmentLoss() {
        return lastEnvironmentLoss;
    }

    public double getSolarIntensity() {
        return solarIntensity;
    }

    public double getSolarVisibility() {
        return solarVisibility;
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
        if (capability == Capabilities.ENERGY.block()) {
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

    private static class SolarHeatCheck extends SolarCheck {

        private final int recheckFrequency;
        private long lastCheckedSun;

        private SolarHeatCheck(Level world, BlockPos pos) {
            super(world, pos);
            recheckFrequency = Mth.nextInt(world.getRandom(), MekanismUtils.TICKS_PER_HALF_SECOND, MekanismUtils.TICKS_PER_HALF_SECOND + SharedConstants.TICKS_PER_SECOND);
        }

        @Override
        public void recheckCanSeeSun() {
            if (!world.dimensionType().hasSkyLight() || world.getSkyDarken() >= 4) {
                canSeeSun = false;
                return;
            }
            long time = world.getGameTime();
            if (time < lastCheckedSun + recheckFrequency) {
                return;
            }
            lastCheckedSun = time;
            if (world.getFluidState(pos).isEmpty()) {
                canSeeSun = world.canSeeSky(pos);
            } else {
                BlockPos above = pos.above();
                if (world.canSeeSky(above)) {
                    BlockState state = world.getBlockState(above);
                    canSeeSun = !state.liquid() && !state.canOcclude();
                } else {
                    canSeeSun = false;
                }
            }
        }
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
