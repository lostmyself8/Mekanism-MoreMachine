package com.jerry.meklm.common.capabilities.holder.chemical;

import mekanism.api.chemical.IChemicalTank;
import mekanism.common.capabilities.holder.ConfigHolder;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.ISlotInfo;
import mekanism.common.tile.interfaces.ISideConfiguration;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CanAdjustConfigChemicalTankHolder extends ConfigHolder<List<IChemicalTank>> implements IContainerHolder<IChemicalTank> {

    private static final Function<ISlotInfo, List<IChemicalTank>> SLOT_PARSER = slotInfo -> slotInfo instanceof ChemicalSlotInfo info ? info.getTanks() : Collections.emptyList();
    private final List<IChemicalTank> tanks = new java.util.ArrayList<>();

    protected CanAdjustConfigChemicalTankHolder(ISideConfiguration sideConfiguration) {
        super(sideConfiguration, TransmissionType.CHEMICAL, SLOT_PARSER);
    }

    void addContainer(IChemicalTank tank) {
        tanks.add(tank);
    }

    @NotNull
    @Override
    public List<IChemicalTank> getContainers(@Nullable Direction direction) {
        return getData(direction);
    }

    @Override
    protected List<IChemicalTank> defaultValue() {
        return Collections.emptyList();
    }

    @Override
    protected List<IChemicalTank> allData() {
        return tanks;
    }
}
