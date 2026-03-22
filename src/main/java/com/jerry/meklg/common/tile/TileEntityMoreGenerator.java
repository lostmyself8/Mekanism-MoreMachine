package com.jerry.meklg.common.tile;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.resolver.BasicCapabilityResolver;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.sync.ISyncableData;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.CableUtils;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class TileEntityMoreGenerator extends TileEntityMekanism {

    /**
     * Output per tick this generator can transfer.
     */
    private FloatingLong maxOutput;
    @Getter
    private BasicEnergyContainer energyContainer;

    /**
     * Generator -- a block that produces energy. It has a certain amount of fuel it can store as well as an output
     * rate.
     */
    public TileEntityMoreGenerator(IBlockProvider blockProvider, BlockPos pos, BlockState state, @NotNull FloatingLongSupplier maxOutput) {
        super(blockProvider, pos, state);
        updateMaxOutputRaw(maxOutput.get());
        addCapabilityResolver(BasicCapabilityResolver.constant(Capabilities.CONFIG_CARD, this));
    }

    protected RelativeSide[] getEnergySides() {
        return new RelativeSide[] { RelativeSide.FRONT };
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = BasicEnergyContainer.output(MachineEnergyContainer.validateBlock(this).getStorage(), listener), getEnergySides());
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        handleEject();
    }

    protected void handleEject() {
        if (MekanismUtils.canFunction(this)) {
            for (RelativeSide side : getEnergySides()) {
                Direction direction = side.getDirection(getDirection());
                for (BlockEntity ejectEntity : getEjectEntity(side, direction)) {
                    if (ejectEntity != null)
                        CableUtils.emit(Collections.singleton(direction), energyContainer, ejectEntity, getMaxOutput());
                }
            }
        }
    }

    protected abstract List<BlockEntity> getEjectEntity(RelativeSide side, Direction direction);

    @ComputerMethod
    public FloatingLong getMaxOutput() {
        return maxOutput;
    }

    protected void updateMaxOutputRaw(FloatingLong maxOutput) {
        this.maxOutput = maxOutput.multiply(2);
    }

    protected ISyncableData syncableMaxOutput() {
        return SyncableFloatingLong.create(this::getMaxOutput, value -> maxOutput = value);
    }

    @ComputerMethod(methodDescription = "Get the amount of energy produced by this generator in the last tick.")
    public abstract FloatingLong getProductionRate();
}
