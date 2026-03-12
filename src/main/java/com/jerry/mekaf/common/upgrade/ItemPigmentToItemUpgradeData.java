package com.jerry.mekaf.common.upgrade;

import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.slot.chemical.PigmentInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl.RedstoneControl;
import mekanism.common.upgrade.MachineUpgradeData;

import java.util.List;

public class ItemPigmentToItemUpgradeData extends MachineUpgradeData {

    public final IPigmentTank stored;
    public final PigmentInventorySlot pigmentSlot;

    public ItemPigmentToItemUpgradeData(boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, int operatingTicks, IPigmentTank stored,
                                        PigmentInventorySlot pigmentSlot, EnergyInventorySlot energySlot, InputInventorySlot inputSlot, OutputInventorySlot outputSlot, List<ITileComponent> components) {
        super(redstone, controlType, energyContainer, operatingTicks, energySlot, inputSlot, outputSlot, components);
        this.stored = stored;
        this.pigmentSlot = pigmentSlot;
    }

    public ItemPigmentToItemUpgradeData(boolean redstone, RedstoneControl controlType, IEnergyContainer energyContainer, int[] progress, IPigmentTank stored,
                                        PigmentInventorySlot pigmentSlot, EnergyInventorySlot energySlot, List<IInventorySlot> inputSlots, List<IInventorySlot> outputSlots, boolean sorting,
                                        List<ITileComponent> components) {
        super(redstone, controlType, energyContainer, progress, energySlot, inputSlots, outputSlots, sorting, components);
        this.stored = stored;
        this.pigmentSlot = pigmentSlot;
    }
}
