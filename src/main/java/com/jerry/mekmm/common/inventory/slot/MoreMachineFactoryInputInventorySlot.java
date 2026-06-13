package com.jerry.mekmm.common.inventory.slot;

import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;

import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class MoreMachineFactoryInputInventorySlot extends InputInventorySlot {

    public static MoreMachineFactoryInputInventorySlot create(TileEntityMoreMachineFactory<?> factory, int process, IInventorySlot outputSlot, @Nullable IContentsListener listener,
                                                              int x, int y) {
        return create(factory, process, outputSlot, null, listener, x, y);
    }

    public static MoreMachineFactoryInputInventorySlot create(TileEntityMoreMachineFactory<?> factory, int process, IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot,
                                                              @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(factory, "Factory cannot be null");
        Objects.requireNonNull(outputSlot, "Primary output slot cannot be null");
        return new MoreMachineFactoryInputInventorySlot(factory, process, outputSlot, secondaryOutputSlot, listener, x, y);
    }

    private MoreMachineFactoryInputInventorySlot(TileEntityMoreMachineFactory<?> factory, int process, IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot,
                                                 @Nullable IContentsListener listener, int x, int y) {
        super(Item.ABSOLUTE_MAX_STACK_SIZE, (stack, automationType) -> factory.isItemValidForSlot(stack) && factory.inputProducesOutput(process, stack, outputSlot, secondaryOutputSlot, false),
                factory::isValidInputItem, null, null, listener, x, y);
    }
}
