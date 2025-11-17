package com.jerry.mekaf.common.upgrade;

import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;

import java.util.Collections;
import java.util.List;

public class ItemGasToMergedUpgradeData extends ItemToMergedUpgradeData {

    public final GasInventorySlot gasSlot;
    public final IGasTank inputTank;

    public ItemGasToMergedUpgradeData(boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                      IEnergyContainer energyContainer, int operatingTicks, EnergyInventorySlot energySlot, GasInventorySlot gasSlot,
                                      InputInventorySlot inputSlot, IGasTank inputTank, MergedChemicalTank outputTank, List<ITileComponent> components) {
        this(redstone, controlType, energyContainer, new int[] { operatingTicks }, energySlot, gasSlot, Collections.singletonList(inputSlot), inputTank,
                Collections.singletonList(outputTank), false, components);
    }

    public ItemGasToMergedUpgradeData(boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                      IEnergyContainer energyContainer, int[] progress, EnergyInventorySlot energySlot, GasInventorySlot gasSlot,
                                      List<IInventorySlot> inputSlots, IGasTank inputTank, List<MergedChemicalTank> outputTanks, boolean sorting, List<ITileComponent> components) {
        super(redstone, controlType, energyContainer, progress, energySlot, inputSlots, outputTanks, sorting, components);
        this.gasSlot = gasSlot;
        this.inputTank = inputTank;
    }
}
