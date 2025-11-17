package com.jerry.mekaf.common.inventory.slot;

import com.jerry.mekaf.common.tile.TileEntityPressurizedReactingFactory;
import com.jerry.mekaf.common.tile.base.TileEntityItemToFluidFactory;
import com.jerry.mekaf.common.tile.base.TileEntityItemToGasFactory;
import com.jerry.mekaf.common.tile.base.TileEntityItemToMergedFactory;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AdvancedFactoryInputInventorySlot extends InputInventorySlot {

    public static AdvancedFactoryInputInventorySlot create(TileEntityItemToGasFactory<?> factory, int process, IGasTank outputTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(factory, "Factory cannot be null");
        Objects.requireNonNull(outputTank, "Chemical output tank cannot be null");
        return new AdvancedFactoryInputInventorySlot(factory, process, outputTank, listener, x, y);
    }

    private AdvancedFactoryInputInventorySlot(TileEntityItemToGasFactory<?> factory, int process, IGasTank outputTank, @Nullable IContentsListener listener, int x, int y) {
        super(stack -> factory.inputProducesOutput(process, stack, outputTank, false),
                factory::isValidInputItem, listener, x, y);
    }

    public static AdvancedFactoryInputInventorySlot create(TileEntityItemToMergedFactory<?> factory, int process, MergedChemicalTank outputTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(factory, "Factory cannot be null");
        Objects.requireNonNull(outputTank, "Chemical output tank cannot be null");
        return new AdvancedFactoryInputInventorySlot(factory, process, outputTank, listener, x, y);
    }

    private AdvancedFactoryInputInventorySlot(TileEntityItemToMergedFactory<?> factory, int process, MergedChemicalTank outputTank, @Nullable IContentsListener listener, int x, int y) {
        super(stack -> factory.inputProducesOutput(process, stack, outputTank, false),
                factory::isValidInputItem, listener, x, y);
    }

    public static AdvancedFactoryInputInventorySlot create(TileEntityItemToFluidFactory<?> factory, int process, IExtendedFluidTank outputTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(factory, "Factory cannot be null");
        Objects.requireNonNull(outputTank, "Fluid output tank cannot be null");
        return new AdvancedFactoryInputInventorySlot(factory, process, outputTank, listener, x, y);
    }

    private AdvancedFactoryInputInventorySlot(TileEntityItemToFluidFactory<?> factory, int process, IExtendedFluidTank outputTank, @Nullable IContentsListener listener, int x, int y) {
        super(stack -> factory.inputProducesOutput(process, stack, outputTank, false),
                factory::isValidInputItem, listener, x, y);
    }

    public static AdvancedFactoryInputInventorySlot create(TileEntityPressurizedReactingFactory factory, int process, IInventorySlot outputSlot, IGasTank outputTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(factory, "Factory cannot be null");
        Objects.requireNonNull(outputTank, "Fluid output tank cannot be null");
        return new AdvancedFactoryInputInventorySlot(factory, process, outputSlot, outputTank, listener, x, y);
    }

    private AdvancedFactoryInputInventorySlot(TileEntityPressurizedReactingFactory factory, int process, IInventorySlot outputSlot, IGasTank outputTank, @Nullable IContentsListener listener, int x, int y) {
        super(stack -> factory.isItemValidForSlot(stack) && factory.inputProducesOutput(process, stack, outputSlot, outputTank, false),
                factory::isValidInputItem, listener, x, y);
    }

    @Override
    public void setStackUnchecked(@NotNull ItemStack stack) {
        super.setStackUnchecked(stack);
    }
}
