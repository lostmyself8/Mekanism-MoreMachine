package com.jerry.meklm.common.capabilities.holder.fluid;

import mekanism.api.RelativeSide;
import mekanism.api.fluid.IFluidTank;
import mekanism.common.capabilities.holder.BasicHolder;
import mekanism.common.capabilities.holder.container.IContainerHolder;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CanAdjustFluidTankHolder extends BasicHolder implements IContainerHolder<IFluidTank> {

    private Map<RelativeSide, List<IFluidTank>> directionalTanks = Collections.emptyMap();
    private final List<IFluidTank> tanks = new ArrayList<>();

    public CanAdjustFluidTankHolder(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate, @Nullable Predicate<RelativeSide> extractPredicate) {
        super(facingSupplier, insertPredicate, extractPredicate);
    }

    void addContainer(@NotNull IFluidTank tank) {
        addContainer(tank, new RelativeSide[0]);
    }

    void addContainer(@NotNull IFluidTank tank, RelativeSide... sides) {
        tanks.add(tank);
        if (sides.length > 0) {
            if (directionalTanks.isEmpty()) {
                directionalTanks = new EnumMap<>(RelativeSide.class);
            }
            for (RelativeSide side : sides) {
                directionalTanks.computeIfAbsent(side, ignoredSide -> new ArrayList<>()).add(tank);
            }
        }
    }

    @Override
    public @NotNull List<IFluidTank> getContainers(@Nullable Direction direction) {
        if (direction == null || directionalTanks.isEmpty()) {
            return tanks;
        }
        List<IFluidTank> containers = directionalTanks.get(RelativeSide.fromDirections(facingSupplier.get(), direction));
        return containers == null ? Collections.emptyList() : containers;
    }
}
