package com.jerry.mekaf.common.upgrade;

import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;

import java.util.Collections;
import java.util.List;

public class GasGasToGasUpgradeData extends GasToGasUpgradeData {

    public final GasInventorySlot chemicalSlot;
    public final IGasTank inputTank;
    public final long[] usedSoFar;

    public GasGasToGasUpgradeData(boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                  IEnergyContainer energyContainer, int operatingTicks, EnergyInventorySlot energySlot,
                                  GasInventorySlot chemicalSlot, IGasTank inputSlot, IGasTank inputTank, IGasTank outputTank,
                                  List<ITileComponent> components) {
        this(redstone, controlType, energyContainer, new int[] { operatingTicks }, null, energySlot, chemicalSlot, Collections.singletonList(inputSlot), inputTank, Collections.singletonList(outputTank), false, components);
    }

    public GasGasToGasUpgradeData(boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                  IEnergyContainer energyContainer, int operatingTicks, long usedSoFar, EnergyInventorySlot energySlot,
                                  GasInventorySlot chemicalSlot, IGasTank inputSlot, IGasTank inputTank, IGasTank outputTank,
                                  List<ITileComponent> components) {
        this(redstone, controlType, energyContainer, new int[] { operatingTicks }, new long[] { usedSoFar }, energySlot, chemicalSlot, Collections.singletonList(inputSlot), inputTank, Collections.singletonList(outputTank), false, components);
    }

    public GasGasToGasUpgradeData(boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                  IEnergyContainer energyContainer, int[] progress, long[] usedSoFar, EnergyInventorySlot energySlot,
                                  GasInventorySlot chemicalSlot, List<IGasTank> inputSlots, IGasTank inputTank,
                                  List<IGasTank> outputTanks, boolean sorting, List<ITileComponent> components) {
        super(redstone, controlType, energyContainer, progress, energySlot, inputSlots, outputTanks, sorting, components);
        this.chemicalSlot = chemicalSlot;
        this.inputTank = inputTank;
        this.usedSoFar = usedSoFar;
    }
}
