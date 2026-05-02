package com.jerry.mekaf.common.upgrade;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter.PathElement;

import java.util.Collections;
import java.util.List;

public class ChemicalToItemUpgradeData implements IUpgradeData {

    public final boolean redstone;
    public final IRedstoneControl.RedstoneControl controlType;
    public final IEnergyContainer energyContainer;
    public final int[] progress;
    public final boolean sorting;
    public final EnergyInventorySlot energySlot;
    public final List<IChemicalTank> inputTanks;
    public final List<IInventorySlot> outputSlots;
    public final CompoundTag components;

    public ChemicalToItemUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                     IEnergyContainer energyContainer, int operatingTicks, EnergyInventorySlot energySlot,
                                     IChemicalTank inputTank, IInventorySlot outputSlot, List<ITileComponent> components, PathElement problemPath) {
        this(provider, redstone, controlType, energyContainer, new int[] { operatingTicks }, energySlot, Collections.singletonList(inputTank),
                Collections.singletonList(outputSlot), false, components, problemPath);
    }

    public ChemicalToItemUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                     IEnergyContainer energyContainer, int[] progress, EnergyInventorySlot energySlot,
                                     List<IChemicalTank> inputTanks, List<IInventorySlot> outputSlots, boolean sorting, List<ITileComponent> components, PathElement problemPath) {
        this.redstone = redstone;
        this.controlType = controlType;
        this.energyContainer = energyContainer;
        this.progress = progress;
        this.energySlot = energySlot;
        this.inputTanks = inputTanks;
        this.outputSlots = outputSlots;
        this.sorting = sorting;
        this.components = IUpgradeData.readComponents(provider, components, problemPath);
    }
}
