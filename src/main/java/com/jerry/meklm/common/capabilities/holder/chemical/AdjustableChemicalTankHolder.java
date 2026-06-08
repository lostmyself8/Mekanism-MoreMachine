package com.jerry.meklm.common.capabilities.holder.chemical;

import mekanism.api.RelativeSide;
import mekanism.api.chemical.IChemicalTank;
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

public class AdjustableChemicalTankHolder extends BasicHolder implements IContainerHolder<IChemicalTank> {

    private Map<RelativeSide, List<IChemicalTank>> directionalTanks = Collections.emptyMap();
    private final List<IChemicalTank> tanks = new ArrayList<>();

    AdjustableChemicalTankHolder(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate, @Nullable Predicate<RelativeSide> extractPredicate) {
        super(facingSupplier, insertPredicate, extractPredicate);
    }

    void addContainer(IChemicalTank tank) {
        addContainer(tank, new RelativeSide[0]);
    }

    void addContainer(IChemicalTank tank, RelativeSide... sides) {
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

    @NotNull
    @Override
    public List<IChemicalTank> getContainers(@Nullable Direction direction) {
        if (direction == null || directionalTanks.isEmpty()) {
            return tanks;
        }
        List<IChemicalTank> containers = directionalTanks.get(RelativeSide.fromDirections(facingSupplier.get(), direction));
        return containers == null ? Collections.emptyList() : containers;
    }
}
