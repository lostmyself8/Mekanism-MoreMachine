package com.jerry.meklg.common.tile.generator;

import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.item.ItemReflector;
import com.jerry.mekmm.common.tile.prefab.TileEntityMoreMachineGenerator;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TileEntitySolarHeatGenerator extends TileEntityMoreMachineGenerator implements IBoundingBlock {

    private static final float MIN_ANGLE = -30F;
    private static final float MAX_ANGLE = 30F;

    @Getter
    private float angle;
    @Getter
    private float sunRayGroundAngle;

    private List<IInventorySlot> slots;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;

    public TileEntitySolarHeatGenerator(BlockPos pos, BlockState state) {
        super(LargeGeneratorBlocks.SOLAR_HEAT_GENERATOR, pos, state, MoreMachineConfig.generators.solarHeatGeneration);
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        slots = new ArrayList<>();
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier);
        for (int slotX = 0; slotX < 4; slotX++) {
            BasicInventorySlot slot = BasicInventorySlot.at(ConstantPredicates.alwaysTrueBi(), (stack, automationType) -> automationType != AutomationType.EXTERNAL && canInsert(stack), listener, 8 + slotX * 18, 35);
            builder.addSlot(slot, RelativeSide.BACK, RelativeSide.TOP);
            slots.add(slot);
        }
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35));
        return builder.build();
    }

    private boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof ItemReflector;
    }

    @Override
    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[] { RelativeSide.FRONT, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK };
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.drainContainer();
        if (canFunction() && level instanceof ServerLevel serverLevel) {
            sendUpdatePacket |= damageReflectors(serverLevel);
        }
        updateReflectorAngle();
        setActive(false);
        return sendUpdatePacket;
    }

    private boolean damageReflectors(ServerLevel level) {
        boolean damaged = false;
        for (IInventorySlot slot : slots) {
            ItemStack stack = slot.getStack();
            if (stack.getItem() instanceof ItemReflector) {
                ItemStack damagedStack = stack.copy();
                damagedStack.hurtAndBreak(1, level, null, item -> {});
                slot.setStack(damagedStack);
                damaged = true;
            }
        }
        return damaged;
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        updateReflectorAngle();
    }

    private void updateReflectorAngle() {
        sunRayGroundAngle = calculateSunRayGroundAngle(getBlockPos().above(3));
        if (sunRayGroundAngle > 0F) {
            angle = calculateSunTrackingReflectorAngle();
        }
    }

    private float calculateSunRayGroundAngle(BlockPos targetPos) {
        if (level == null || !level.dimensionType().hasSkyLight()) {
            return 0F;
        }
        double dayProgress = Math.floorMod(level.getDayTime(), 24_000L) / 24_000D;
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

    @Override
    public <T> @Nullable T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.ITEM.block()) {
            return Objects.requireNonNull(itemHandlerManager, "Expected to have item handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (EnergyCompatUtils.isEnergyCapability(capability)) {
            return false;
        } else if (capability == Capabilities.ITEM.block()) {
            return false;
        }
        return true;
    }

    @Override
    protected long getProductionRate() {
        return 0L;
    }

    public ItemStack getItemStack(int slot) {
        return slots.get(slot).getStack();
    }
}
