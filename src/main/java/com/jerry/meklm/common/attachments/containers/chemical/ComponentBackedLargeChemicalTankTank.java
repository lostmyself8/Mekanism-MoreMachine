package com.jerry.meklm.common.attachments.containers.chemical;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;
import com.jerry.meklm.common.item.block.ItemBlockLargeChemicalTank;

import mekanism.api.functions.ConstantPredicates;
import mekanism.api.math.MathUtils;
import mekanism.common.component.containers.chemical.ComponentBackedChemicalTank;

import net.neoforged.neoforge.transfer.access.ItemAccess;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class ComponentBackedLargeChemicalTankTank extends ComponentBackedChemicalTank {

    public static ComponentBackedLargeChemicalTankTank create(ItemAccess attachedAccess, int tankIndex) {
        if (!(attachedAccess.getResource().getItem() instanceof ItemBlockLargeChemicalTank<?> item)) {
            throw new IllegalStateException("Attached to should always be a large chemical tank item");
        }
        return new ComponentBackedLargeChemicalTankTank(attachedAccess, tankIndex, (ILargeChemicalTankTier) item.getTier());
    }

    private ComponentBackedLargeChemicalTankTank(ItemAccess attachedAccess, int tankIndex, ILargeChemicalTankTier tier) {
        super(attachedAccess, tankIndex, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrue(),
                tier::getStorage, () -> MathUtils.clampToInt(tier.getOutput()));
    }
}
