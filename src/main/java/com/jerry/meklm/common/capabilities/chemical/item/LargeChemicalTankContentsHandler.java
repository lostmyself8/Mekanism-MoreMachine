package com.jerry.meklm.common.capabilities.chemical.item;

import mekanism.api.NBTConstants;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.common.capabilities.DynamicHandler;
import mekanism.common.capabilities.chemical.dynamic.DynamicChemicalHandler.DynamicGasHandler;
import mekanism.common.capabilities.chemical.dynamic.DynamicChemicalHandler.DynamicInfusionHandler;
import mekanism.common.capabilities.chemical.dynamic.DynamicChemicalHandler.DynamicPigmentHandler;
import mekanism.common.capabilities.chemical.dynamic.DynamicChemicalHandler.DynamicSlurryHandler;
import mekanism.common.capabilities.merged.MergedTankContentsHandler;

import com.jerry.meklm.common.capabilities.chemical.item.LargeChemicalTankRateLimitChemicalTank.GasTankRateLimitChemicalTank;
import com.jerry.meklm.common.capabilities.chemical.item.LargeChemicalTankRateLimitChemicalTank.InfusionTankRateLimitChemicalTank;
import com.jerry.meklm.common.capabilities.chemical.item.LargeChemicalTankRateLimitChemicalTank.PigmentTankRateLimitChemicalTank;
import com.jerry.meklm.common.capabilities.chemical.item.LargeChemicalTankRateLimitChemicalTank.SlurryTankRateLimitChemicalTank;
import com.jerry.meklm.common.tier.ILargeTankTier;

import java.util.Objects;

public class LargeChemicalTankContentsHandler extends MergedTankContentsHandler<MergedChemicalTank> {

    public static LargeChemicalTankContentsHandler create(ILargeTankTier tier) {
        Objects.requireNonNull(tier, "Large Chemical tank tier cannot be null");
        return new LargeChemicalTankContentsHandler(tier);
    }

    private LargeChemicalTankContentsHandler(ILargeTankTier tier) {
        mergedTank = MergedChemicalTank.create(
                new GasTankRateLimitChemicalTank(tier, gasHandler = new DynamicGasHandler(side -> gasTanks, DynamicHandler.InteractPredicate.ALWAYS_TRUE, DynamicHandler.InteractPredicate.ALWAYS_TRUE,
                        () -> onContentsChanged(NBTConstants.GAS_TANKS, gasTanks))),
                new InfusionTankRateLimitChemicalTank(tier, infusionHandler = new DynamicInfusionHandler(side -> infusionTanks, DynamicHandler.InteractPredicate.ALWAYS_TRUE,
                        DynamicHandler.InteractPredicate.ALWAYS_TRUE, () -> onContentsChanged(NBTConstants.INFUSION_TANKS, infusionTanks))),
                new PigmentTankRateLimitChemicalTank(tier, pigmentHandler = new DynamicPigmentHandler(side -> pigmentTanks, DynamicHandler.InteractPredicate.ALWAYS_TRUE,
                        DynamicHandler.InteractPredicate.ALWAYS_TRUE, () -> onContentsChanged(NBTConstants.PIGMENT_TANKS, pigmentTanks))),
                new SlurryTankRateLimitChemicalTank(tier, slurryHandler = new DynamicSlurryHandler(side -> slurryTanks, DynamicHandler.InteractPredicate.ALWAYS_TRUE,
                        DynamicHandler.InteractPredicate.ALWAYS_TRUE, () -> onContentsChanged(NBTConstants.SLURRY_TANKS, slurryTanks))));
    }
}
