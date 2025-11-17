package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineGas;

import mekanism.api.*;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.FloatingLong;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.chemical.SyncableGasStack;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TileEntityAmbientGasCollector extends TileEntityMekanism implements IConfigurable {

    /**
     * How many ticks it takes to run an operation.
     */
    private static final int BASE_TICKS_REQUIRED = 19;
    public static final int MAX_CHEMICAL = 10 * FluidType.BUCKET_VOLUME;
    private static final int BASE_OUTPUT_RATE = 256;

    // 化学品存储槽
    public IGasTank chemicalTank;
    /**
     * The type of chemical this collector is collecting
     */
    @NotNull
    private GasStack activeType = GasStack.EMPTY;
    public int ticksRequired = BASE_TICKS_REQUIRED;
    /**
     * How many ticks this machine has been operating for.
     */
    public int operatingTicks;
    private boolean usedEnergy = false;
    private int outputRate = BASE_OUTPUT_RATE;

    private boolean noBlocking = true;

    private MachineEnergyContainer<TileEntityAmbientGasCollector> energyContainer;
    GasInventorySlot chemicalSlot;
    EnergyInventorySlot energySlot;

    public TileEntityAmbientGasCollector(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.AMBIENT_GAS_COLLECTOR, pos, state);
    }

    @Override
    public @Nullable IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        ChemicalTankHelper<Gas, GasStack, IGasTank> builder = ChemicalTankHelper.forSide(this::getDirection);
        builder.addTank(chemicalTank = ChemicalTankBuilder.GAS.output(MAX_CHEMICAL, listener), RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener), RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.BACK);
        return builder.build();
    }

    @Override
    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(chemicalSlot = GasInventorySlot.drain(chemicalTank, listener, 28, 35), RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.BACK);
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 143, 35), RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.BACK);
        chemicalSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        chemicalSlot.drainTank();
        FloatingLong clientEnergyUsed = FloatingLong.ZERO;
        if (MekanismUtils.canFunction(this) && (chemicalTank.isEmpty() || estimateIncrementAmount() <= chemicalTank.getNeeded())) {
            FloatingLong energyPerTick = energyContainer.getEnergyPerTick();
            if (energyContainer.extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL).equals(energyPerTick)) {
                if (!activeType.isEmpty()) {
                    // If we have an active type of fluid, use energy. This can cause there to be ticks where there
                    // isn't actually
                    // anything to suck that use energy, but those will balance out with the first set of ticks where it
                    // doesn't
                    // use any energy until it actually picks up the first block
                    clientEnergyUsed = energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
                }
                operatingTicks++;
                if (operatingTicks >= ticksRequired) {
                    operatingTicks = 0;
                    // 判断收集器上方是否是空气
                    if (suck(worldPosition.relative(Direction.UP))) {
                        if (clientEnergyUsed.isZero()) {
                            // If it didn't already have an active type (hasn't used energy this tick), then extract
                            // energy
                            clientEnergyUsed = energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
                        }
                    } else {
                        reset();
                    }
                }
            }
        }
        usedEnergy = !clientEnergyUsed.isZero();
        if (!chemicalTank.isEmpty()) {
            ChemicalUtil.emit(Collections.singleton(Direction.UP), chemicalTank, this, outputRate);
        }
    }

    public int estimateIncrementAmount() {
        return 1;
    }

    private boolean suck(BlockPos pos) {
        Optional<BlockState> state = WorldUtils.getBlockState(level, pos);
        if (state.isPresent()) {
            BlockState blockState = state.get();
            Block block = blockState.getBlock();
            if (isAir(block)) {
                GasStack gasStack = new GasStack(MoreMachineGas.UNSTABLE_DIMENSIONAL_GAS, MoreMachineConfig.general.gasCollectAmount.get());
                activeType = gasStack;
                chemicalTank.insert(gasStack, Action.EXECUTE, AutomationType.INTERNAL);
                return true;
            }
        }
        return false;
    }

    public boolean isAir(Block block) {
        return noBlocking = block == Blocks.AIR;
    }

    public boolean getNotBlocking() {
        return noBlocking;
    }

    public void reset() {
        activeType = GasStack.EMPTY;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        if (!activeType.isEmpty()) {
            nbtTags.put(NBTConstants.GAS_STORED, activeType.write(new CompoundTag()));
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        operatingTicks = nbt.getInt(NBTConstants.PROGRESS);
        NBTUtils.setGasStackIfPresent(nbt, NBTConstants.GAS_STORED, gas -> activeType = gas);
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        reset();
        player.displayClientMessage(MekanismLang.PUMP_RESET.translate(), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onRightClick(Player player) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean canPulse() {
        return true;
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            ticksRequired = MekanismUtils.getTicks(this, BASE_TICKS_REQUIRED);
            outputRate = BASE_OUTPUT_RATE * (1 + upgradeComponent.getUpgrades(Upgrade.SPEED));
        }
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(chemicalTank.getStored(), chemicalTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(SubstanceType type) {
        return type == SubstanceType.GAS;
    }

    @NotNull
    @Override
    public List<Component> getInfo(@NotNull Upgrade upgrade) {
        return UpgradeUtils.getMultScaledInfo(this, upgrade);
    }

    public MachineEnergyContainer<TileEntityAmbientGasCollector> getEnergyContainer() {
        return energyContainer;
    }

    public boolean usedEnergy() {
        return usedEnergy;
    }

    @NotNull
    public GasStack getActiveType() {
        return this.activeType;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::usedEnergy, value -> usedEnergy = value));
        container.track(SyncableBoolean.create(this::getNotBlocking, value -> noBlocking = value));
        container.track(SyncableGasStack.create(this::getActiveType, value -> activeType = value));
    }
}
