package com.jerry.mekmm.common.integration;

import net.minecraftforge.fml.ModList;

public final class MoreMachineHooks {

    public static final String EVOLVED_MEKANISM_MOD_ID = "evolvedmekanism";

    public boolean EMLoaded;

    public void hookCommonSetup() {
        ModList modList = ModList.get();
        EMLoaded = modList.isLoaded(EVOLVED_MEKANISM_MOD_ID);
    }
}
