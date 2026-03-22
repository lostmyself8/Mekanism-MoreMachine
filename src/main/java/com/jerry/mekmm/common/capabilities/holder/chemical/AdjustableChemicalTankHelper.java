package com.jerry.mekmm.common.capabilities.holder.chemical;

import com.jerry.mekmm.common.capabilities.holder.chemical.AdjustableChemicalTankHolder.AdjustableGasTankHolder;
import com.jerry.mekmm.common.capabilities.holder.chemical.AdjustableChemicalTankHolder.AdjustableInfusionTankHolder;
import com.jerry.mekmm.common.capabilities.holder.chemical.AdjustableChemicalTankHolder.AdjustablePigmentTankHolder;
import com.jerry.mekmm.common.capabilities.holder.chemical.AdjustableChemicalTankHolder.AdjustableSlurryTankHolder;

import mekanism.api.RelativeSide;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class AdjustableChemicalTankHelper<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>> {

    private final IChemicalTankHolder<CHEMICAL, STACK, TANK> slotHolder;
    private boolean built;

    private AdjustableChemicalTankHelper(IChemicalTankHolder<CHEMICAL, STACK, TANK> slotHolder) {
        this.slotHolder = slotHolder;
    }

    public static AdjustableChemicalTankHelper<Gas, GasStack, IGasTank> forSideGas(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate, @Nullable Predicate<RelativeSide> extractPredicate) {
        return new AdjustableChemicalTankHelper<>(new AdjustableGasTankHolder(facingSupplier, insertPredicate, extractPredicate));
    }

    public static AdjustableChemicalTankHelper<InfuseType, InfusionStack, IInfusionTank> forSideInfusion(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate, @Nullable Predicate<RelativeSide> extractPredicate) {
        return new AdjustableChemicalTankHelper<>(new AdjustableInfusionTankHolder(facingSupplier, insertPredicate, extractPredicate));
    }

    public static AdjustableChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> forSidePigment(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate, @Nullable Predicate<RelativeSide> extractPredicate) {
        return new AdjustableChemicalTankHelper<>(new AdjustablePigmentTankHolder(facingSupplier, insertPredicate, extractPredicate));
    }

    public static AdjustableChemicalTankHelper<Slurry, SlurryStack, ISlurryTank> forSideSlurry(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate, @Nullable Predicate<RelativeSide> extractPredicate) {
        return new AdjustableChemicalTankHelper<>(new AdjustableSlurryTankHolder(facingSupplier, insertPredicate, extractPredicate));
    }

    public TANK addTank(@NotNull TANK tank) {
        if (built) {
            throw new IllegalStateException("Builder has already built.");
        }
        if (slotHolder instanceof AdjustableChemicalTankHolder<CHEMICAL, STACK, TANK> slotHolder) {
            slotHolder.addTank(tank);
        } else {
            throw new IllegalArgumentException("Holder does not know how to add tanks");
        }
        return tank;
    }

    public TANK addTank(@NotNull TANK tank, RelativeSide... sides) {
        if (built) {
            throw new IllegalStateException("Builder has already built.");
        }
        if (slotHolder instanceof AdjustableChemicalTankHolder<CHEMICAL, STACK, TANK> slotHolder) {
            slotHolder.addTank(tank, sides);
        } else {
            throw new IllegalArgumentException("Holder does not know how to add tanks on specific sides");
        }
        return tank;
    }

    public IChemicalTankHolder<CHEMICAL, STACK, TANK> build() {
        built = true;
        return slotHolder;
    }
}
