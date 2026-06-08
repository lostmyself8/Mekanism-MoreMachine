package com.jerry.mekmm.common.inventory.tank;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

public class FactoryVariableCapacityChemicalTank extends VariableCapacityChemicalTank {

    public FactoryVariableCapacityChemicalTank(LongSupplier capacity, BiPredicate<ChemicalResource, @NotNull AutomationType> canExtract, BiPredicate<ChemicalResource, @NotNull AutomationType> canInsert, Predicate<ChemicalResource> validator, @Nullable ChemicalAttributeValidator attributeValidator, @Nullable IContentsListener listener) {
        super(capacity, canExtract, canInsert, validator, null, null, attributeValidator, listener);
    }
}
