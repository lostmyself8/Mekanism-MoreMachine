package com.jerry.mekmm.common.capabilities.holder.energy;

import mekanism.api.RelativeSide;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.holder.BasicHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AdjustableEnergyContainerHolder extends BasicHolder<IEnergyContainer> implements IEnergyContainerHolder {

    @Nullable
    private final Predicate<RelativeSide> insertPredicate;
    @Nullable
    private final Predicate<RelativeSide> extractPredicate;

    protected AdjustableEnergyContainerHolder(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate, @Nullable Predicate<RelativeSide> extractPredicate) {
        super(facingSupplier);
        this.insertPredicate = insertPredicate;
        this.extractPredicate = extractPredicate;
    }

    void addContainer(@NotNull IEnergyContainer container, RelativeSide... sides) {
        addSlotInternal(container, sides);
    }

    @NotNull
    @Override
    public List<IEnergyContainer> getEnergyContainers(@Nullable Direction direction) {
        return getSlots(direction);
    }

    @Override
    public boolean canInsert(@Nullable Direction direction) {
        return direction != null && (insertPredicate == null || insertPredicate.test(RelativeSide.fromDirections(facingSupplier.get(), direction)));
    }

    @Override
    public boolean canExtract(@Nullable Direction direction) {
        return direction != null && (extractPredicate == null || extractPredicate.test(RelativeSide.fromDirections(facingSupplier.get(), direction)));
    }
}
