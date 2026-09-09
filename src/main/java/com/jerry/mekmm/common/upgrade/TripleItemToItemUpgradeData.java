package com.jerry.mekmm.common.upgrade;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.upgrade.MachineUpgradeData;

import net.minecraft.core.HolderLookup;

import java.util.List;

public class TripleItemToItemUpgradeData extends MachineUpgradeData {

    public final InputInventorySlot secondarySlot;
    public final InputInventorySlot tertiarySlot;

    public TripleItemToItemUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType, IEnergyContainer energyContainer, int operatingTicks,
                                       EnergyInventorySlot energySlot, InputInventorySlot inputSlot, InputInventorySlot secondarySlot, InputInventorySlot tertiarySlot,
                                       OutputInventorySlot outputSlot, List<ITileComponent> components) {
        super(provider, redstone, controlType, energyContainer, operatingTicks, energySlot, inputSlot, outputSlot, components);
        this.secondarySlot = secondarySlot;
        this.tertiarySlot = tertiarySlot;
    }

    public TripleItemToItemUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType, IEnergyContainer energyContainer, int[] progress,
                                       EnergyInventorySlot energySlot, InputInventorySlot secondarySlot, InputInventorySlot tertiarySlot, List<IInventorySlot> inputSlots,
                                       List<IInventorySlot> outputSlots, boolean sorting, List<ITileComponent> components) {
        super(provider, redstone, controlType, energyContainer, progress, energySlot, inputSlots, outputSlots, sorting, components);
        this.secondarySlot = secondarySlot;
        this.tertiarySlot = tertiarySlot;
    }
}
