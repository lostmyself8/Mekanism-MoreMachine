package com.jerry.meklm.common.capabilities.chemical;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;

import com.jerry.meklm.common.tier.ILargeTankTier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.LongSupplier;

@NothingNullByDefault
public abstract class LargeChemicalTankChemicalTank<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> extends BasicChemicalTank<CHEMICAL, STACK> {

    public static MergedChemicalTank create(ILargeTankTier tier, @Nullable IContentsListener listener) {
        Objects.requireNonNull(tier, "Chemical tank tier cannot be null");
        return MergedChemicalTank.create(
                new GasTankChemicalTank(tier, listener),
                new InfusionTankChemicalTank(tier, listener),
                new PigmentTankChemicalTank(tier, listener),
                new SlurryTankChemicalTank(tier, listener));
    }

    private final LongSupplier rate;

    private LargeChemicalTankChemicalTank(ILargeTankTier tier, ChemicalTankBuilder<CHEMICAL, STACK, ?> tankBuilder, @Nullable IContentsListener listener) {
        super(tier.getStorage(), tankBuilder.alwaysTrueBi, tankBuilder.alwaysTrueBi, tankBuilder.alwaysTrue, null, listener);
        rate = tier::getOutput;
    }

    @Override
    protected long getRate(@Nullable AutomationType automationType) {
        return automationType == AutomationType.INTERNAL ? rate.getAsLong() : super.getRate(automationType);
    }

    @Override
    public STACK insert(STACK stack, Action action, AutomationType automationType) {
        return super.insert(stack, action.combine(true), automationType);
    }

    @Override
    public STACK extract(long amount, Action action, AutomationType automationType) {
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

    private static class GasTankChemicalTank extends LargeChemicalTankChemicalTank<Gas, GasStack> implements IGasHandler, IGasTank {

        private GasTankChemicalTank(ILargeTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.GAS, listener);
        }
    }

    private static class InfusionTankChemicalTank extends LargeChemicalTankChemicalTank<InfuseType, InfusionStack> implements IInfusionHandler, IInfusionTank {

        private InfusionTankChemicalTank(ILargeTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.INFUSION, listener);
        }
    }

    private static class PigmentTankChemicalTank extends LargeChemicalTankChemicalTank<Pigment, PigmentStack> implements IPigmentHandler, IPigmentTank {

        private PigmentTankChemicalTank(ILargeTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.PIGMENT, listener);
        }
    }

    private static class SlurryTankChemicalTank extends LargeChemicalTankChemicalTank<Slurry, SlurryStack> implements ISlurryHandler, ISlurryTank {

        private SlurryTankChemicalTank(ILargeTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.SLURRY, listener);
        }
    }
}
