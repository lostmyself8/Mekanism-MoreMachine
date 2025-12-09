package com.jerry.meklm.common.capabilities.holder.chemical;

import mekanism.api.RelativeSide;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.common.capabilities.holder.BasicHolder;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CanAdjustChemicalTankHolder<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>>
                                        extends BasicHolder<TANK> implements IChemicalTankHolder<CHEMICAL, STACK, TANK> {

    @Nullable
    private final Predicate<RelativeSide> insertPredicate;
    @Nullable
    private final Predicate<RelativeSide> extractPredicate;

    CanAdjustChemicalTankHolder(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate, @Nullable Predicate<RelativeSide> extractPredicate) {
        super(facingSupplier);
        this.insertPredicate = insertPredicate;
        this.extractPredicate = extractPredicate;
    }

    void addTank(@NotNull TANK tank, RelativeSide... sides) {
        addSlotInternal(tank, sides);
    }

    @NotNull
    @Override
    public List<TANK> getTanks(@Nullable Direction direction) {
        return getSlots(direction);
    }

    @Override
    public boolean canInsert(@Nullable Direction direction) {
        // If the insert predicate is null then we can insert from any side, don't bother looking up our facing
        return direction != null && (insertPredicate == null || insertPredicate.test(RelativeSide.fromDirections(facingSupplier.get(), direction)));
    }

    @Override
    public boolean canExtract(@Nullable Direction direction) {
        // If the extract predicate is null then we can extract from any side, don't bother looking up our facing
        return direction != null && (extractPredicate == null || extractPredicate.test(RelativeSide.fromDirections(facingSupplier.get(), direction)));
    }
}
