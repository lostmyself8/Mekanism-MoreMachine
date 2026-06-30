package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.MoreMachineChemicalConstants;
import com.jerry.mekmm.common.resource.MoreMachineResource;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.chemical.Chemical;
import mekanism.common.registration.impl.ChemicalDeferredRegister;
import mekanism.common.registration.impl.DeferredChemical;
import mekanism.common.registration.impl.SlurryRegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MoreMachineChemicals {

    private MoreMachineChemicals() {}

    public static final ChemicalDeferredRegister MM_CHEMICALS = new ChemicalDeferredRegister(Mekmm.MOD_ID);

    public static final DeferredChemical<Chemical> NUTRITIONAL_PASTE = MM_CHEMICALS.register(MoreMachineChemicalConstants.NUTRITIONAL_PASTE);
    public static final DeferredChemical<Chemical> NUTRIENT_SOLUTION = MM_CHEMICALS.register(MoreMachineChemicalConstants.NUTRIENT_SOLUTION);
    public static final DeferredChemical<Chemical> UU_MATTER = MM_CHEMICALS.register(MoreMachineChemicalConstants.UU_MATTER);
    public static final DeferredChemical<Chemical> UNSTABLE_DIMENSIONAL_GAS = MM_CHEMICALS.register(MoreMachineChemicalConstants.UNSTABLE_DIMENSIONAL_GAS);
    public static final Map<MoreMachineResource, SlurryRegistryObject<Chemical, Chemical>> PROCESSED_RESOURCES = new LinkedHashMap<>();

    static {
        for (MoreMachineResource resource : MoreMachineEnumUtils.MM_RESOURCES) {
            PROCESSED_RESOURCES.put(resource, MM_CHEMICALS.registerSlurry(resource.getRegistrySuffix(), builder -> builder.tint(resource.getTint())));
        }
    }
}
