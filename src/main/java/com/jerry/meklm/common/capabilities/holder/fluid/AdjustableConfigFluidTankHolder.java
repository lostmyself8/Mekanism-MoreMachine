package com.jerry.meklm.common.capabilities.holder.fluid;

import mekanism.api.fluid.IFluidTank;
import mekanism.common.capabilities.holder.ConfigHolder;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import mekanism.common.tile.component.config.slot.ISlotInfo;
import mekanism.common.tile.interfaces.ISideConfiguration;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class AdjustableConfigFluidTankHolder extends ConfigHolder<List<IFluidTank>> implements IContainerHolder<IFluidTank> {

    private static final Function<ISlotInfo, List<IFluidTank>> SLOT_PARSER = slotInfo -> slotInfo instanceof FluidSlotInfo info ? info.getTanks() : Collections.emptyList();
    private final List<IFluidTank> tanks = new java.util.ArrayList<>();

    public AdjustableConfigFluidTankHolder(ISideConfiguration sideConfiguration) {
        super(sideConfiguration, TransmissionType.FLUID, SLOT_PARSER);
    }

    void addContainer(@NotNull IFluidTank tank) {
        tanks.add(tank);
    }

    @Override
    public @NotNull List<IFluidTank> getContainers(@Nullable Direction direction) {
        return getData(direction);
    }

    @Override
    protected List<IFluidTank> defaultValue() {
        return Collections.emptyList();
    }

    @Override
    protected List<IFluidTank> allData() {
        return tanks;
    }
}
