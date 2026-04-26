package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineChemicals;

import mekanism.api.*;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.fluids.FluidType;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TileEntityAmbientGasCollector extends TileEntityMekanism {

    // 会按这个顺序进行自动弹出（顺时针）
    private static final RelativeSide[] OUTPUT_SIDES = { RelativeSide.FRONT, RelativeSide.LEFT, RelativeSide.BACK, RelativeSide.RIGHT };
    /**
     * How many ticks it takes to run an operation.
     */
    private static final int BASE_TICKS_REQUIRED = 19;
    public static final int MAX_CHEMICAL = 10 * FluidType.BUCKET_VOLUME;
    private static final int BASE_OUTPUT_RATE = 256;

    // 化学品存储槽
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class,
                            methodNames = { "getChemical", "getChemicalCapacity", "getChemicalNeeded",
                                    "getChemicalFilledPercentage" },
                            docPlaceholder = "chemical tank")
    public IChemicalTank chemicalTank;

    public int ticksRequired = BASE_TICKS_REQUIRED;
    /**
     * How many ticks this machine has been operating for.
     */
    public int operatingTicks;
    private boolean usedEnergy = false;
    private int outputRate = BASE_OUTPUT_RATE;

    private boolean noBlocking = true;
    private List<BlockCapabilityCache<IChemicalHandler, @Nullable Direction>> chemicalHandler;

    @Getter
    private MachineEnergyContainer<TileEntityAmbientGasCollector> energyContainer;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getChemicalItem", docPlaceholder = "chemical slot")
    ChemicalInventorySlot chemicalSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityAmbientGasCollector(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.AMBIENT_GAS_COLLECTOR, pos, state);
    }

    @Override
    public @Nullable IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        ChemicalTankHelper builder = ChemicalTankHelper.forSide(facingSupplier);
        builder.addTank(chemicalTank = BasicChemicalTank.output(MAX_CHEMICAL, listener), RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(facingSupplier);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener), RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.BACK);
        return builder.build();
    }

    @Override
    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier);
        builder.addSlot(chemicalSlot = ChemicalInventorySlot.drain(chemicalTank, listener, 28, 35),
                RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.BACK);
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 143, 35), RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.BACK);
        chemicalSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        chemicalSlot.drainTank();
        long clientEnergyUsed = 0L;
        if (canFunction() && (chemicalTank.isEmpty() || estimateIncrementAmount() <= chemicalTank.getNeeded())) {
            long energyPerTick = energyContainer.getEnergyPerTick();
            if (energyContainer.extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL) == energyPerTick) {
                operatingTicks++;
                if (operatingTicks >= ticksRequired) {
                    operatingTicks = 0;
                    // 判断收集器上方是否是空气
                    if (suck(worldPosition.relative(Direction.UP))) {
                        // If it didn't already have an active type (hasn't used energy this tick), then extract
                        // energy
                        clientEnergyUsed = energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
                    }
                }
            }
        }
        usedEnergy = clientEnergyUsed > 0L;
        if (!chemicalTank.isEmpty()) {
            if (chemicalHandler == null) {
                RelativeSide[] outputSides = getOutputSides();
                chemicalHandler = new ArrayList<>(outputSides.length);
                for (RelativeSide outputSide : getOutputSides()) {
                    // 从相对位置获取绝对位置
                    Direction side = outputSide.getDirection(getDirection());
                    chemicalHandler.add(Capabilities.CHEMICAL.createCache((ServerLevel) level, worldPosition.relative(side), side.getOpposite()));
                }
            }
            ChemicalUtil.emit(chemicalHandler, chemicalTank, outputRate);
        }
        return sendUpdatePacket;
    }

    protected RelativeSide[] getOutputSides() {
        return OUTPUT_SIDES;
    }

    public int estimateIncrementAmount() {
        return MoreMachineConfig.general.gasCollectAmount.get();
    }

    private boolean suck(BlockPos pos) {
        Optional<BlockState> state = WorldUtils.getBlockState(level, pos);
        if (state.isPresent()) {
            BlockState blockState = state.get();
            Block block = blockState.getBlock();
            if (isAir(block)) {
                ChemicalStack chemicalStack = new ChemicalStack(MoreMachineChemicals.UNSTABLE_DIMENSIONAL_GAS, estimateIncrementAmount());
                chemicalTank.insert(chemicalStack, Action.EXECUTE, AutomationType.INTERNAL);
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

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(nbt, provider);
        operatingTicks = nbt.getInt(SerializationConstants.PROGRESS);
    }

    @Override
    public boolean supportsMode(RedstoneControl mode) {
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
    protected boolean makesComparatorDirty(ContainerType<?, ?, ?> type) {
        return type == ContainerType.CHEMICAL;
    }

    @NotNull
    @Override
    public List<Component> getInfo(@NotNull Upgrade upgrade) {
        return UpgradeUtils.getMultScaledInfo(this, upgrade);
    }

    public boolean usedEnergy() {
        return usedEnergy;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::usedEnergy, value -> usedEnergy = value));
        container.track(SyncableBoolean.create(this::getNotBlocking, value -> noBlocking = value));
    }
}
