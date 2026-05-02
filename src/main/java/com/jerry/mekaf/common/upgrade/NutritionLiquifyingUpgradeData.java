package com.jerry.mekaf.common.upgrade;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter.PathElement;

import java.util.Collections;
import java.util.List;

public class NutritionLiquifyingUpgradeData implements IUpgradeData {

    public final boolean redstone;
    public final IRedstoneControl.RedstoneControl controlType;
    public final IEnergyContainer energyContainer;
    public final int[] progress;
    public final boolean sorting;
    public final EnergyInventorySlot energySlot;
    public final List<IInventorySlot> inputSlots;
    public final List<IInventorySlot> outputSlots;
    public final IExtendedFluidTank fluidTank;
    public final CompoundTag components;

    public NutritionLiquifyingUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                          IEnergyContainer energyContainer, int operatingTicks, EnergyInventorySlot energySlot, InputInventorySlot inputSlot,
                                          IInventorySlot outputSlot, IExtendedFluidTank fluidTank, List<ITileComponent> components, PathElement problemPath) {
        this(provider, redstone, controlType, energyContainer, new int[] { operatingTicks }, energySlot, Collections.singletonList(inputSlot),
                Collections.singletonList(outputSlot), fluidTank, false, components, problemPath);
    }

    public NutritionLiquifyingUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                          IEnergyContainer energyContainer, int[] progress, EnergyInventorySlot energySlot, List<IInventorySlot> inputSlots,
                                          List<IInventorySlot> outputSlots, IExtendedFluidTank fluidTank, boolean sorting, List<ITileComponent> components, PathElement problemPath) {
        this.redstone = redstone;
        this.controlType = controlType;
        this.energyContainer = energyContainer;
        this.progress = progress;
        this.energySlot = energySlot;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.fluidTank = fluidTank;
        this.sorting = sorting;
        this.components = IUpgradeData.readComponents(provider, components, problemPath);
    }
}
