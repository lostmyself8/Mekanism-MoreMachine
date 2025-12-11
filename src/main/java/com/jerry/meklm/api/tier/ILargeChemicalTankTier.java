package com.jerry.meklm.api.tier;

import mekanism.api.tier.ITier;

public interface ILargeChemicalTankTier extends ITier {

    long getStorage();

    long getOutput();

    String getType();
}
