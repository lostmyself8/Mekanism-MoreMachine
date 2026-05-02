package com.jerry.mekaf.common.upgrade;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util.ProblemReporter.PathElement;

import java.util.Collections;
import java.util.List;

public class PRCUpgradeData implements IUpgradeData {

    public final boolean redstone;
    public final IRedstoneControl.RedstoneControl controlType;
    public final IEnergyContainer energyContainer;
    public final int[] progress;
    public final boolean sorting;
    public final EnergyInventorySlot energySlot;
    public final IChemicalTank inputChemicalTank;
    public final BasicFluidTank inputFluidTank;
    public final List<IInventorySlot> inputSlots;
    public final List<IInventorySlot> outputSlots;
    public final IChemicalTank outputTank;
    public final CompoundTag components;

    public PRCUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                          IEnergyContainer energyContainer, int operatingTicks, EnergyInventorySlot energySlot,
                          IChemicalTank inputChemicalTank, BasicFluidTank inputFluidTank, InputInventorySlot inputSlot,
                          IInventorySlot outputSlot, IChemicalTank outputTank, List<ITileComponent> components, PathElement problemPath) {
        this(provider, redstone, controlType, energyContainer, new int[] { operatingTicks }, energySlot, inputChemicalTank, inputFluidTank, Collections.singletonList(inputSlot),
                Collections.singletonList(outputSlot), outputTank, false, components, problemPath);
    }

    public PRCUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                          IEnergyContainer energyContainer, int[] progress, EnergyInventorySlot energySlot,
                          IChemicalTank inputChemicalTank, BasicFluidTank inputFluidTank, List<IInventorySlot> inputSlots,
                          List<IInventorySlot> outputSlots, IChemicalTank outputTank, boolean sorting, List<ITileComponent> components, PathElement problemPath) {
        this.redstone = redstone;
        this.controlType = controlType;
        this.energyContainer = energyContainer;
        this.progress = progress;
        this.energySlot = energySlot;
        this.inputChemicalTank = inputChemicalTank;
        this.inputFluidTank = inputFluidTank;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.outputTank = outputTank;
        this.sorting = sorting;
        this.components = IUpgradeData.readComponents(provider, components, problemPath);
    }
}
