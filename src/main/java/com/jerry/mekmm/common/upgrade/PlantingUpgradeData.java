package com.jerry.mekmm.common.upgrade;

import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl.RedstoneControl;
import mekanism.common.upgrade.MachineUpgradeData;

import java.util.Collections;
import java.util.List;

public class PlantingUpgradeData extends MachineUpgradeData {

    public final IGasTank stored;
    public final GasInventorySlot gasSlot;
    public final long[] usedSoFar;

    // Planting Station Constructor
    public PlantingUpgradeData(boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, int operatingTicks, long usedSoFar, IGasTank stored,
                               EnergyInventorySlot energySlot, GasInventorySlot gasSlot, InputInventorySlot inputSlot, OutputInventorySlot outputSlot, OutputInventorySlot secondaryOutputSlot,
                               List<ITileComponent> components) {
        this(redstone, controlType, energyContainer, new int[] { operatingTicks }, new long[] { usedSoFar }, stored, energySlot, gasSlot,
                Collections.singletonList(inputSlot), List.of(outputSlot, secondaryOutputSlot), false, components);
    }

    // Planting Factory Constructor
    public PlantingUpgradeData(boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, int[] progress, long[] usedSoFar, IGasTank stored,
                               EnergyInventorySlot energySlot, GasInventorySlot gasSlot, List<IInventorySlot> inputSlots, List<IInventorySlot> outputSlots, boolean sorting, List<ITileComponent> components) {
        super(redstone, controlType, energyContainer, progress, energySlot, inputSlots, outputSlots, sorting, components);
        this.stored = stored;
        this.gasSlot = gasSlot;
        this.usedSoFar = usedSoFar;
    }
}
