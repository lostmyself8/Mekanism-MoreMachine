package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.MoreMachineChemicalConstants;

import mekanism.api.chemical.gas.Gas;
import mekanism.common.registration.impl.GasDeferredRegister;
import mekanism.common.registration.impl.GasRegistryObject;

public class MoreMachineGas {

    private MoreMachineGas() {}

    public static final GasDeferredRegister MM_GASES = new GasDeferredRegister(Mekmm.MOD_ID);

    public static final GasRegistryObject<Gas> NUTRITIONAL_PASTE = MM_GASES.register(MoreMachineChemicalConstants.NUTRITIONAL_PASTE);
    public static final GasRegistryObject<Gas> NUTRIENT_SOLUTION = MM_GASES.register(MoreMachineChemicalConstants.NUTRIENT_SOLUTION);
    public static final GasRegistryObject<Gas> UU_MATTER = MM_GASES.register(MoreMachineChemicalConstants.UU_MATTER);
    public static final GasRegistryObject<Gas> UNSTABLE_DIMENSIONAL_GAS = MM_GASES.register(MoreMachineChemicalConstants.UNSTABLE_DIMENSIONAL_GAS);
}
