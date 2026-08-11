package com.jerry.meklm.common.capabilities.holder.heat;

import mekanism.api.RelativeSide;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.tile.interfaces.ISideConfiguration;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class AdjustableHeatCapacitorHelper {

    private final IHeatCapacitorHolder capacitorHolder;
    private boolean built;

    private AdjustableHeatCapacitorHelper(IHeatCapacitorHolder capacitorHolder) {
        this.capacitorHolder = capacitorHolder;
    }

    public static AdjustableHeatCapacitorHelper forSide(Supplier<Direction> facingSupplier) {
        return forSide(facingSupplier, null, null);
    }

    public static AdjustableHeatCapacitorHelper forSide(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate,
                                                        @Nullable Predicate<RelativeSide> extractPredicate) {
        return new AdjustableHeatCapacitorHelper(new AdjustableHeatCapacitorHolder(facingSupplier, insertPredicate, extractPredicate));
    }

    public static AdjustableHeatCapacitorHelper forSideWithConfig(ISideConfiguration sideConfiguration) {
        return new AdjustableHeatCapacitorHelper(new AdjustableConfigHeatCapacitorHolder(sideConfiguration));
    }

    public <CAPACITOR extends IHeatCapacitor> CAPACITOR addCapacitor(@NotNull CAPACITOR capacitor) {
        if (built) {
            throw new IllegalStateException("Builder has already built.");
        }
        if (capacitorHolder instanceof AdjustableHeatCapacitorHolder capacitorHolder) {
            capacitorHolder.addCapacitor(capacitor);
        } else if (capacitorHolder instanceof AdjustableConfigHeatCapacitorHolder capacitorHolder) {
            capacitorHolder.addCapacitor(capacitor);
        } else {
            throw new IllegalArgumentException("Holder does not know how to add capacitors");
        }
        return capacitor;
    }

    public <CAPACITOR extends IHeatCapacitor> CAPACITOR addCapacitor(@NotNull CAPACITOR capacitor, RelativeSide... sides) {
        if (built) {
            throw new IllegalStateException("Builder has already built.");
        }
        if (capacitorHolder instanceof AdjustableHeatCapacitorHolder capacitorHolder) {
            capacitorHolder.addCapacitor(capacitor, sides);
        } else {
            throw new IllegalArgumentException("Holder does not know how to add capacitors on specific sides");
        }
        return capacitor;
    }

    public IHeatCapacitorHolder build() {
        built = true;
        return capacitorHolder;
    }
}
