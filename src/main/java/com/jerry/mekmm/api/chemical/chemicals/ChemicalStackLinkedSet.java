package com.jerry.mekmm.api.chemical.chemicals;

import mekanism.api.chemical.ChemicalStack;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ChemicalStackLinkedSet {

    public static final Hash.Strategy<? super ChemicalStack> TYPE_AND_COMPONENTS = new Hash.Strategy<>() {

        public int hashCode(@Nullable ChemicalStack stack) {
            // Keep this strategy consistent with equals: compare chemical type only, ignore amount.
            return stack == null || stack.isEmpty() ? 0 : stack.getChemical().hashCode();
        }

        public boolean equals(@Nullable ChemicalStack first, @Nullable ChemicalStack second) {
            return first == second || first != null && second != null && first.isEmpty() == second.isEmpty() && ChemicalStack.isSameChemical(first, second);
        }
    };

    public ChemicalStackLinkedSet() {}

    public static Set<ChemicalStack> createTypeAndComponentsSet() {
        return new ObjectLinkedOpenCustomHashSet(TYPE_AND_COMPONENTS);
    }
}
