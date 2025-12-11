package com.jerry.meklm.common.attachments.containers.chemical;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;
import com.jerry.meklm.common.item.block.ItemBlockLargeChemicalTank;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.attachments.containers.chemical.AttachedChemicals;
import mekanism.common.attachments.containers.chemical.ComponentBackedChemicalTank;

import net.minecraft.world.item.ItemStack;

@NothingNullByDefault
public class ComponentBackedLargeChemicalTankTank extends ComponentBackedChemicalTank {

    public static ComponentBackedLargeChemicalTankTank create(ContainerType<?, ?, ?> ignored, ItemStack attachedTo, int tankIndex) {
        if (!(attachedTo.getItem() instanceof ItemBlockLargeChemicalTank<?> item)) {
            throw new IllegalStateException("Attached to should always be a large chemical tank item");
        }
        return new ComponentBackedLargeChemicalTankTank(attachedTo, tankIndex, (ILargeChemicalTankTier) item.getTier());
    }

    private ComponentBackedLargeChemicalTankTank(ItemStack attachedTo, int tankIndex, ILargeChemicalTankTier tier) {
        super(attachedTo, tankIndex, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrue(),
                tier::getOutput, tier::getStorage, null);
    }

    @Override
    public ChemicalStack insert(ChemicalStack stack, Action action, AutomationType automationType) {
        return super.insert(stack, action.combine(true), automationType);
    }

    @Override
    public ChemicalStack extract(AttachedChemicals attachedChemicals, ChemicalStack stored, long amount, Action action, AutomationType automationType) {
        return super.extract(attachedChemicals, stored, amount, action.combine(true), automationType);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: We are only patching {@link #setStackSize(AttachedChemicals, ChemicalStack, long, Action)}, as both
     * {@link #growStack(long, Action)} and
     * {@link #shrinkStack(long, Action)} are wrapped through this method.
     */
    @Override
    public long setStackSize(AttachedChemicals attachedChemicals, ChemicalStack stored, long amount, Action action) {
        return super.setStackSize(attachedChemicals, stored, amount, action.combine(true));
    }
}
