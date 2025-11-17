package com.jerry.mekmm.common.upgrade;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl.RedstoneControl;
import mekanism.common.upgrade.MachineUpgradeData;

import java.util.List;

public class StamperUpgradeData extends MachineUpgradeData {

    public final InputInventorySlot moldSlot;

    // Stamper Constructor
    public StamperUpgradeData(boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, int operatingTicks,
                              EnergyInventorySlot energySlot, InputInventorySlot moldSlot, InputInventorySlot inputSlot, OutputInventorySlot outputSlot, List<ITileComponent> components) {
        super(redstone, controlType, energyContainer, operatingTicks, energySlot, inputSlot, outputSlot, components);
        this.moldSlot = moldSlot;
    }

    // Stamper Factory Constructor
    public StamperUpgradeData(boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, int[] progress,
                              EnergyInventorySlot energySlot, InputInventorySlot moldSlot, List<IInventorySlot> inputSlots, List<IInventorySlot> outputSlots, boolean sorting,
                              List<ITileComponent> components) {
        super(redstone, controlType, energyContainer, progress, energySlot, inputSlots, outputSlots, sorting, components);
        this.moldSlot = moldSlot;
    }
}
