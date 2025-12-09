package com.jerry.mekmm.common.util;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;

import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;

import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tier.MidChemicalTankTier;

public class MoreMachineEnumUtils {

    private MoreMachineEnumUtils() {}

    /**
     * Cached value of {@link MoreMachineFactoryType#values()}. DO NOT MODIFY THIS LIST.
     */
    public static final MoreMachineFactoryType[] MM_FACTORY_TYPES = MoreMachineFactoryType.values();

    /**
     * Cached value of {@link AdvancedFactoryType#values()}. DO NOT MODIFY THIS LIST.
     */
    public static final AdvancedFactoryType[] ADVANCED_FACTORY_TYPES = AdvancedFactoryType.values();

    /**
     * Cached value of {@link MidChemicalTankTier#values()}. DO NOT MODIFY THIS LIST.
     */
    public static final MidChemicalTankTier[] MID_CHEMICAL_TANK_TIER = MidChemicalTankTier.values();

    /**
     * Cached value of {@link MaxChemicalTankTier#values()}. DO NOT MODIFY THIS LIST.
     */
    public static final MaxChemicalTankTier[] MAX_CHEMICAL_TANK_TIER = MaxChemicalTankTier.values();
}
