package com.jerry.meklm.common.capabilities.holder.chemical;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.transaction.ITransactionHelper;
import mekanism.api.transaction.RateLimitTracker;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.function.LongSupplier;

@NothingNullByDefault
public class LargeChemicalTankChemicalTank<TIER extends ILargeChemicalTankTier> extends BasicChemicalTank {

    public static <TIER extends ILargeChemicalTankTier> LargeChemicalTankChemicalTank<TIER> create(TIER tier, LongSupplier gameTimeSupplier, @Nullable IContentsListener listener) {
        Objects.requireNonNull(tier, "Lagre chemical tank tier cannot be null");
        Objects.requireNonNull(gameTimeSupplier, "Game time supplier cannot be null");
        return new LargeChemicalTankChemicalTank<>(tier, gameTimeSupplier, listener);
    }

    private LargeChemicalTankChemicalTank(TIER tier, LongSupplier gameTimeSupplier, @Nullable IContentsListener listener) {
        this(tier, ITransactionHelper.INSTANCE.createInternalOnlyRateLimit(gameTimeSupplier, () -> Math.toIntExact(Math.min(Integer.MAX_VALUE, tier.getOutput()))),
                ITransactionHelper.INSTANCE.createInternalOnlyRateLimit(gameTimeSupplier, () -> Math.toIntExact(Math.min(Integer.MAX_VALUE, tier.getOutput()))), listener);
    }

    private LargeChemicalTankChemicalTank(TIER tier, @Nullable RateLimitTracker insertionRateLimiter, @Nullable RateLimitTracker extractionRateLimiter,
                                          @Nullable IContentsListener listener) {
        super(tier.getStorage(), ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrue(), insertionRateLimiter,
                extractionRateLimiter, null, listener);
    }

    @Override
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int insert(ChemicalResource resource, @Range(from = 0, to = Integer.MAX_VALUE) int amount, TransactionContext transaction, AutomationType automationType) {
        return super.insert(resource, amount, transaction, automationType);
    }

    @Override
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int extract(ChemicalResource resource, @Range(from = 0, to = Integer.MAX_VALUE) int amount, TransactionContext transaction, AutomationType automationType) {
        return super.extract(resource, amount, transaction, automationType);
    }
}
