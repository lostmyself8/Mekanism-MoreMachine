package com.jerry.mekmm.common.config;

import net.minecraftforge.fml.ModContainer;

public class MoreMachineConfig {

    private MoreMachineConfig() {}

    public static final MoreMachineGeneralConfig general = new MoreMachineGeneralConfig();
    public static final MoreMachineStorageConfig storage = new MoreMachineStorageConfig();
    public static final MoreMachineTierConfig tier = new MoreMachineTierConfig();
    public static final MoreMachineUsageConfig usage = new MoreMachineUsageConfig();

    public static void registerConfigs(ModContainer modContainer) {
        MoreMachineConfigHelper.registerConfig(modContainer, general);
        MoreMachineConfigHelper.registerConfig(modContainer, storage);
        MoreMachineConfigHelper.registerConfig(modContainer, tier);
        MoreMachineConfigHelper.registerConfig(modContainer, usage);
    }
}
