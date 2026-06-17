package com.jerry.mekmm.common.tile.prefab;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.single.BasicSingleHolder;
import mekanism.common.capabilities.holder.single.ISingleContainerHolder;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.EnergyUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class TileEntityMoreMachineGenerator extends TileEntityMekanism {

    private static final RelativeSide[] ENERGY_SIDES = { RelativeSide.FRONT };

    @Nullable
    private List<BlockCapabilityCache<EnergyHandler, @Nullable Direction>> outputCaches;
    /**
     * Output per tick this generator can transfer.
     */
    private long maxOutput;
    private BasicEnergyContainer energyContainer;

    /**
     * Generator -- a block that produces energy. It has a certain amount of fuel it can store as well as an output
     * rate.
     */
    public TileEntityMoreMachineGenerator(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    protected RelativeSide[] getEnergySides() {
        return ENERGY_SIDES;
    }

    @NotNull
    @Override
    protected ISingleContainerHolder<IEnergyContainer> getInitialEnergyContainer(IContentsListener listener) {
        energyContainer = BasicEnergyContainer.output(MachineEnergyContainer.validateBlock(this).getStorage(), listener);
        return new BasicSingleHolder<>(energyContainer, facingSupplier, getEnergySideSet());
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        if (canFunction()) {
            // TODO: Maybe even make some generators have a side config/ejector component and move this to the ejector
            // component?
            if (outputCaches == null) {
                Direction direction = getDirection();
                RelativeSide[] energySides = getEnergySides();
                outputCaches = new ArrayList<>(energySides.length);
                for (RelativeSide energySide : energySides) {
                    Direction side = energySide.getDirection(direction);
                    outputCaches.add(Capabilities.ENERGY.createCache((ServerLevel) level, offSetOutput(worldPosition, side), side.getOpposite()));
                }
            }
            EnergyUtils.emit(outputCaches, energyContainer, Math.toIntExact(Math.min(Integer.MAX_VALUE, getMaxOutput())), null);
        }
        return sendUpdatePacket;
    }

    private Set<RelativeSide> getEnergySideSet() {
        return Arrays.stream(getEnergySides()).collect(Collectors.toUnmodifiableSet());
    }

    protected BlockPos offSetOutput(BlockPos from, Direction side) {
        return from.relative(side);
    }

    @Override
    protected void invalidateDirectionCaches(Direction newDirection) {
        super.invalidateDirectionCaches(newDirection);
        outputCaches = null;
    }

    @ComputerMethod
    public long getMaxOutput() {
        return maxOutput;
    }

    protected void updateMaxOutputRaw(long maxOutput) {
        this.maxOutput = maxOutput;
    }

    @ComputerMethod(methodDescription = "Get the amount of energy produced by this generator in the last tick.")
    protected abstract long getProductionRate();
}
