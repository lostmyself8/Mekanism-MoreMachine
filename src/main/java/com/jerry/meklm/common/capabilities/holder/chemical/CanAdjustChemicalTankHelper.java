package com.jerry.meklm.common.capabilities.holder.chemical;

import mekanism.api.AutomationType;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.radiation.IRadiationManager;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.tile.component.TileComponentConfig;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.NonNullSupplier;

import com.jerry.meklm.common.capabilities.holder.chemical.CanAdjustConfigChemicalTankHolder.CanAdjustConfigGasTankHolder;
import com.jerry.meklm.common.capabilities.holder.chemical.CanAdjustConfigChemicalTankHolder.CanAdjustConfigInfusionTankHolder;
import com.jerry.meklm.common.capabilities.holder.chemical.CanAdjustConfigChemicalTankHolder.CanAdjustConfigPigmentTankHolder;
import com.jerry.meklm.common.capabilities.holder.chemical.CanAdjustConfigChemicalTankHolder.CanAdjustConfigSlurryTankHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CanAdjustChemicalTankHelper<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>> {

    private final IChemicalTankHolder<CHEMICAL, STACK, TANK> slotHolder;
    private boolean built;

    private CanAdjustChemicalTankHelper(IChemicalTankHolder<CHEMICAL, STACK, TANK> slotHolder) {
        this.slotHolder = slotHolder;
    }

    public static BiPredicate<@NotNull Gas, @NotNull AutomationType> radioactiveInputTankPredicate(NonNullSupplier<IGasTank> outputTank) {
        // Allow extracting out of the input gas tank if it isn't external OR the output tank is empty AND the input is
        // radioactive
        // Note: This only is the case if radiation is enabled as otherwise things like gauge droppers can work as the
        // way to remove radioactive contents
        return (type, automationType) -> automationType != AutomationType.EXTERNAL || (outputTank.get().isEmpty() && type.has(GasAttributes.Radiation.class) &&
                IRadiationManager.INSTANCE.isRadiationEnabled());
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>> CanAdjustChemicalTankHelper<CHEMICAL, STACK, TANK> forSide(Supplier<Direction> facingSupplier, @Nullable Predicate<RelativeSide> insertPredicate,
                                                                                                                                                                                                       @Nullable Predicate<RelativeSide> extractPredicate) {
        return new CanAdjustChemicalTankHelper<CHEMICAL, STACK, TANK>(new CanAdjustChemicalTankHolder<>(facingSupplier, insertPredicate, extractPredicate));
    }

    public static CanAdjustChemicalTankHelper<Gas, GasStack, IGasTank> forSideGasWithConfig(Supplier<Direction> facingSupplier, Supplier<TileComponentConfig> configSupplier) {
        return new CanAdjustChemicalTankHelper<>(new CanAdjustConfigGasTankHolder(facingSupplier, configSupplier));
    }

    public static CanAdjustChemicalTankHelper<InfuseType, InfusionStack, IInfusionTank> forSideInfusionWithConfig(Supplier<Direction> facingSupplier, Supplier<TileComponentConfig> configSupplier) {
        return new CanAdjustChemicalTankHelper<>(new CanAdjustConfigInfusionTankHolder(facingSupplier, configSupplier));
    }

    public static CanAdjustChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> forSidePigmentWithConfig(Supplier<Direction> facingSupplier, Supplier<TileComponentConfig> configSupplier) {
        return new CanAdjustChemicalTankHelper<>(new CanAdjustConfigPigmentTankHolder(facingSupplier, configSupplier));
    }

    public static CanAdjustChemicalTankHelper<Slurry, SlurryStack, ISlurryTank> forSideSlurryWithConfig(Supplier<Direction> facingSupplier, Supplier<TileComponentConfig> configSupplier) {
        return new CanAdjustChemicalTankHelper<>(new CanAdjustConfigSlurryTankHolder(facingSupplier, configSupplier));
    }

    public TANK addTank(@NotNull TANK tank) {
        if (built) {
            throw new IllegalStateException("Builder has already built.");
        }
        if (slotHolder instanceof CanAdjustChemicalTankHolder<CHEMICAL, STACK, TANK> slotHolder) {
            slotHolder.addTank(tank);
        } else if (slotHolder instanceof CanAdjustConfigChemicalTankHolder<CHEMICAL, STACK, TANK> slotHolder) {
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
        if (slotHolder instanceof CanAdjustChemicalTankHolder<CHEMICAL, STACK, TANK> slotHolder) {
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
