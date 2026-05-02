package com.jerry.mekmm.common.upgrade;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl.RedstoneControl;
import mekanism.common.upgrade.MachineUpgradeData;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.ProblemReporter;

import java.util.List;

public class StamperUpgradeData extends MachineUpgradeData {

    public final InputInventorySlot extraSlot;

    // Stamper Constructor
    public StamperUpgradeData(HolderLookup.Provider provider, boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, int operatingTicks,
                              EnergyInventorySlot energySlot, InputInventorySlot extraSlot, InputInventorySlot inputSlot, OutputInventorySlot outputSlot,
                              List<ITileComponent> components, ProblemReporter.PathElement problemPath) {
        super(provider, redstone, controlType, energyContainer, operatingTicks, energySlot, inputSlot, outputSlot, components, problemPath);
        this.extraSlot = extraSlot;
    }

    // Stamper Factory Constructor
    public StamperUpgradeData(HolderLookup.Provider provider, boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, int[] progress,
                              EnergyInventorySlot energySlot, InputInventorySlot extraSlot, List<IInventorySlot> inputSlots, List<IInventorySlot> outputSlots, boolean sorting,
                              List<ITileComponent> components, ProblemReporter.PathElement problemPath) {
        super(provider, redstone, controlType, energyContainer, progress, energySlot, inputSlots, outputSlots, sorting, components, problemPath);
        this.extraSlot = extraSlot;
    }
}
