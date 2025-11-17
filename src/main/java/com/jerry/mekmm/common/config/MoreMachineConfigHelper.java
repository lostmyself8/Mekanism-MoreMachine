package com.jerry.mekmm.common.config;

import com.jerry.mekmm.Mekmm;

import mekanism.common.config.IMekanismConfig;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class MoreMachineConfigHelper {

    private MoreMachineConfigHelper() {}

    public static final Path CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(Mekmm.MOD_NAME));

    /**
     * Creates a mod config so that {@link net.minecraftforge.fml.config.ConfigTracker} will track it and sync server
     * configs from server to client.
     */
    public static void registerConfig(ModContainer modContainer, IMekanismConfig config) {
        MoreMachineModConfig modConfig = new MoreMachineModConfig(modContainer, config);
        if (config.addToContainer()) {
            modContainer.addConfig(modConfig);
        }
    }
}
