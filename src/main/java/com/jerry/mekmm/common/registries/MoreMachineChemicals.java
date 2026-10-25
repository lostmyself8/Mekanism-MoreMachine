package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.resource.MoreMachineResource;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.MekanismRegistries;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.CleanDirtySlurryId;

import net.minecraft.resources.ResourceKey;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MoreMachineChemicals {

    private MoreMachineChemicals() {}

    private static ResourceKey<Chemical> key(String name) {
        return ResourceKey.create(MekanismRegistries.Keys.CHEMICAL, Mekmm.rl(name));
    }

    public static final ResourceKey<Chemical> NUTRITIONAL_PASTE = key("nutritional_paste");
    public static final ResourceKey<Chemical> NUTRIENT_SOLUTION = key("nutrient_solution");
    public static final ResourceKey<Chemical> UU_MATTER = key("uu_matter");
    public static final ResourceKey<Chemical> UNSTABLE_DIMENSIONAL_GAS = key("unstable_dimensional_gas");
    public static final Map<MoreMachineResource, CleanDirtySlurryId> PROCESSED_RESOURCES = new LinkedHashMap<>();
    static {
        for (MoreMachineResource resource : MoreMachineEnumUtils.MM_RESOURCES) {
            PROCESSED_RESOURCES.put(resource, new CleanDirtySlurryId(key("clean_" + resource.getRegistrySuffix()), key("dirty_" + resource.getRegistrySuffix())));
        }
    }
}
