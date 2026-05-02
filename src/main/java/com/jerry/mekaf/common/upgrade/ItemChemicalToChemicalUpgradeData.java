package com.jerry.mekaf.common.upgrade;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.Util.ProblemReporter.PathElement;

import java.util.Collections;
import java.util.List;

public class ItemChemicalToChemicalUpgradeData extends ItemToChemicalUpgradeData {

    public final ChemicalInventorySlot chemicalSlot;
    public final IChemicalTank inputTank;
    public final long[] usedSoFar;

    public ItemChemicalToChemicalUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                             IEnergyContainer energyContainer, int operatingTicks, long usedSoFar, EnergyInventorySlot energySlot,
                                             ChemicalInventorySlot chemicalSlot, InputInventorySlot inputSlot, IChemicalTank inputTank, IChemicalTank outputTank,
                                             List<ITileComponent> components, PathElement problemPath) {
        this(provider, redstone, controlType, energyContainer, new int[] { operatingTicks }, new long[] { usedSoFar }, energySlot, chemicalSlot, Collections.singletonList(inputSlot), inputTank, Collections.singletonList(outputTank), false, components, problemPath);
    }

    public ItemChemicalToChemicalUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                             IEnergyContainer energyContainer, int[] progress, long[] usedSoFar, EnergyInventorySlot energySlot, ChemicalInventorySlot chemicalSlot,
                                             List<IInventorySlot> inputSlots, IChemicalTank inputTank, List<IChemicalTank> outputTanks, boolean sorting, List<ITileComponent> components, PathElement problemPath) {
        super(provider, redstone, controlType, energyContainer, progress, energySlot, inputSlots, outputTanks, sorting, components, problemPath);
        this.chemicalSlot = chemicalSlot;
        this.inputTank = inputTank;
        this.usedSoFar = usedSoFar;
    }
}
