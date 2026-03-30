package com.jerry.mekmm.common.capabilities.holder.energy;

import mekanism.api.RelativeSide;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class AdjustableEnergyContainerHelper {

    private final IEnergyContainerHolder slotHolder;
    private boolean built;

    private AdjustableEnergyContainerHelper(IEnergyContainerHolder slotHolder) {
        this.slotHolder = slotHolder;
    }

    public static AdjustableEnergyContainerHelper forSide(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate, @Nullable Predicate<RelativeSide> extractPredicate) {
        return new AdjustableEnergyContainerHelper(new AdjustableEnergyContainerHolder(facingSupplier, insertPredicate, extractPredicate));
    }

    public <CONTAINER extends IEnergyContainer> CONTAINER addContainer(@NotNull CONTAINER container) {
        if (built) {
            throw new IllegalStateException("Builder has already built.");
        }
        if (slotHolder instanceof AdjustableEnergyContainerHolder slotHolder) {
            slotHolder.addContainer(container);
        } else {
            throw new IllegalArgumentException("Holder does not know how to add containers");
        }
        return container;
    }

    public <CONTAINER extends IEnergyContainer> CONTAINER addContainer(@NotNull CONTAINER container, RelativeSide... sides) {
        if (built) {
            throw new IllegalStateException("Builder has already built.");
        }
        if (slotHolder instanceof AdjustableEnergyContainerHolder slotHolder) {
            slotHolder.addContainer(container, sides);
        } else {
            throw new IllegalArgumentException("Holder does not know how to add containers on specific sides");
        }
        return container;
    }

    public IEnergyContainerHolder build() {
        built = true;
        return slotHolder;
    }
}
