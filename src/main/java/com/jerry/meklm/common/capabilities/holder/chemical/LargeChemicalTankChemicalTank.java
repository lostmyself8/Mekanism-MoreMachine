package com.jerry.meklm.common.capabilities.holder.chemical;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.functions.ConstantPredicates;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.LongSupplier;

@NothingNullByDefault
public class LargeChemicalTankChemicalTank<TIER extends ILargeChemicalTankTier> extends BasicChemicalTank {

    public static <TIER extends ILargeChemicalTankTier> LargeChemicalTankChemicalTank<TIER> create(TIER tier, @Nullable IContentsListener listener) {
        Objects.requireNonNull(tier, "Lagre chemical tank tier cannot be null");
        return new LargeChemicalTankChemicalTank<>(tier, listener);
    }

    private final LongSupplier rate;

    private LargeChemicalTankChemicalTank(TIER tier, @Nullable IContentsListener listener) {
        super(tier.getStorage(), ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrue(), null, listener, null);
        rate = tier::getOutput;
    }

    @Override
    protected long getInsertRate(@Nullable AutomationType automationType) {
        // Only limit the internal rate to change the speed at which this can be filled from an item
        return automationType == AutomationType.INTERNAL ? rate.getAsLong() : super.getInsertRate(automationType);
    }

    @Override
    protected long getExtractRate(@Nullable AutomationType automationType) {
        // Only limit the internal rate to change the speed at which this can be filled from an item
        return automationType == AutomationType.INTERNAL ? rate.getAsLong() : super.getExtractRate(automationType);
    }

    @Override
    public ChemicalStack insert(ChemicalStack stack, Action action, AutomationType automationType) {
        return super.insert(stack, action.combine(true), automationType);
    }

    @Override
    public ChemicalStack extract(long amount, Action action, AutomationType automationType) {
        return super.extract(amount, action.combine(true), automationType);
    }

    /**
     * {@inheritDoc}
     *
     * Note: We are only patching {@link #setStackSize(long, Action)}, as both {@link #growStack(long, Action)} and
     * {@link #shrinkStack(long, Action)} are wrapped through
     * this method.
     */
    @Override
    public long setStackSize(long amount, Action action) {
        return super.setStackSize(amount, action.combine(true));
    }
}
