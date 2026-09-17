package com.jerry.mekaf.common.upgrade;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.Collections;
import java.util.List;

public class NutritionLiquifyingUpgradeData implements IUpgradeData {

    public final boolean redstone;
    public final IRedstoneControl.RedstoneControl controlType;
    public final IEnergyContainer energyContainer;
    public final int[] progress;
    public final boolean sorting;
    public final EnergyInventorySlot energySlot;
    public final FluidInventorySlot fluidInputSlot;
    public final OutputInventorySlot fluidOutputSlot;
    public final List<IInventorySlot> inputSlots;
    public final List<IInventorySlot> outputSlots;
    public final IExtendedFluidTank fluidTank;
    public final CompoundTag components;

    public NutritionLiquifyingUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                          IEnergyContainer energyContainer, int operatingTicks, EnergyInventorySlot energySlot, FluidInventorySlot fluidInputSlot,
                                          OutputInventorySlot fluidOutputSlot, InputInventorySlot inputSlot, IInventorySlot outputSlot, IExtendedFluidTank fluidTank,
                                          List<ITileComponent> components) {
        this(provider, redstone, controlType, energyContainer, new int[] { operatingTicks }, energySlot, fluidInputSlot, fluidOutputSlot, Collections.singletonList(inputSlot),
                Collections.singletonList(outputSlot), fluidTank, false, components);
    }

    public NutritionLiquifyingUpgradeData(HolderLookup.Provider provider, boolean redstone, IRedstoneControl.RedstoneControl controlType,
                                          IEnergyContainer energyContainer, int[] progress, EnergyInventorySlot energySlot, FluidInventorySlot fluidInputSlot,
                                          OutputInventorySlot fluidOutputSlot, List<IInventorySlot> inputSlots, List<IInventorySlot> outputSlots,
                                          IExtendedFluidTank fluidTank, boolean sorting, List<ITileComponent> components) {
        this.redstone = redstone;
        this.controlType = controlType;
        this.energyContainer = energyContainer;
        this.progress = progress;
        this.energySlot = energySlot;
        this.fluidInputSlot = fluidInputSlot;
        this.fluidOutputSlot = fluidOutputSlot;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.fluidTank = fluidTank;
        this.sorting = sorting;
        this.components = new CompoundTag();
        for (ITileComponent component : components) {
            component.write(this.components, provider);
        }
    }
}
