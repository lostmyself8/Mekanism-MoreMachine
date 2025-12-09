package com.jerry.meklm.common.capabilities.chemical.item;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
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
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.chemical.variable.RateLimitChemicalTank;

import com.jerry.meklm.common.tier.ILargeTankTier;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public abstract class LargeChemicalTankRateLimitChemicalTank<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>>
                                                            extends RateLimitChemicalTank<CHEMICAL, STACK> {

    private LargeChemicalTankRateLimitChemicalTank(ILargeTankTier tier, ChemicalTankBuilder<CHEMICAL, STACK, ?> tankBuilder, @Nullable IContentsListener listener) {
        super(tier::getOutput, tier::getStorage, tankBuilder.alwaysTrueBi, tankBuilder.alwaysTrueBi, tankBuilder.alwaysTrue, null, listener);
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

    public static class GasTankRateLimitChemicalTank extends LargeChemicalTankRateLimitChemicalTank<Gas, GasStack> implements IGasHandler, IGasTank {

        public GasTankRateLimitChemicalTank(ILargeTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.GAS, listener);
        }
    }

    public static class InfusionTankRateLimitChemicalTank extends LargeChemicalTankRateLimitChemicalTank<InfuseType, InfusionStack> implements IInfusionHandler, IInfusionTank {

        public InfusionTankRateLimitChemicalTank(ILargeTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.INFUSION, listener);
        }
    }

    public static class PigmentTankRateLimitChemicalTank extends LargeChemicalTankRateLimitChemicalTank<Pigment, PigmentStack> implements IPigmentHandler, IPigmentTank {

        public PigmentTankRateLimitChemicalTank(ILargeTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.PIGMENT, listener);
        }
    }

    public static class SlurryTankRateLimitChemicalTank extends LargeChemicalTankRateLimitChemicalTank<Slurry, SlurryStack> implements ISlurryHandler, ISlurryTank {

        public SlurryTankRateLimitChemicalTank(ILargeTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.SLURRY, listener);
        }
    }
}
