package com.jerry.mekaf.common.upgrade;

import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.nbt.CompoundTag;

import java.util.Collections;
import java.util.List;

public class PRCUpgradeData implements IUpgradeData {

    public final boolean redstone;
    public final IRedstoneControl.RedstoneControl controlType;
    public final IEnergyContainer energyContainer;
    public final int[] progress;
    public final boolean sorting;
    public final EnergyInventorySlot energySlot;
    public final IGasTank inputChemicalTank;
    public final BasicFluidTank inputFluidTank;
    public final List<IInventorySlot> inputSlots;
    public final List<IInventorySlot> outputSlots;
    public final IGasTank outputTank;
    public final CompoundTag components;

    public PRCUpgradeData(boolean redstone, IRedstoneControl.RedstoneControl controlType,
                          IEnergyContainer energyContainer, int operatingTicks, EnergyInventorySlot energySlot,
                          IGasTank inputChemicalTank, BasicFluidTank inputFluidTank, InputInventorySlot inputSlot,
                          IInventorySlot outputSlot, IGasTank outputTank, List<ITileComponent> components) {
        this(redstone, controlType, energyContainer, new int[] { operatingTicks }, energySlot, inputChemicalTank, inputFluidTank, Collections.singletonList(inputSlot),
                Collections.singletonList(outputSlot), outputTank, false, components);
    }

    public PRCUpgradeData(boolean redstone, IRedstoneControl.RedstoneControl controlType,
                          IEnergyContainer energyContainer, int[] progress, EnergyInventorySlot energySlot,
                          IGasTank inputChemicalTank, BasicFluidTank inputFluidTank, List<IInventorySlot> inputSlots,
                          List<IInventorySlot> outputSlots, IGasTank outputTank, boolean sorting, List<ITileComponent> components) {
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
        this.components = new CompoundTag();
        for (ITileComponent component : components) {
            component.write(this.components);
        }
    }
}
