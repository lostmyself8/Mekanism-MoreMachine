package com.jerry.mekmm.common.integration;

import net.minecraftforge.fml.ModList;

public final class MoreMachineHooks {

    private static final String EVOLVED_MEKANISM_MOD_ID = "evolvedmekanism";
    private static final String MEKANISM_GENERATOR_MOD_ID = "mekanismgenerators";

    public boolean EMLoaded;
    public boolean MGLoaded;

    public void hookCommonSetup() {
        ModList modList = ModList.get();
        EMLoaded = modList.isLoaded(EVOLVED_MEKANISM_MOD_ID);
        MGLoaded = modList.isLoaded(MEKANISM_GENERATOR_MOD_ID);
    }
}
