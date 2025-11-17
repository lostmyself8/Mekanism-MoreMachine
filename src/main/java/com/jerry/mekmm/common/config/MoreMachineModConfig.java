package com.jerry.mekmm.common.config;

import com.jerry.mekmm.Mekmm;

import mekanism.common.config.IMekanismConfig;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.nio.file.Path;
import java.util.function.Function;

public class MoreMachineModConfig extends ModConfig {

    private static final MoreMachineConfigFileTypeHandler MORE_MACHINE_TOML = new MoreMachineConfigFileTypeHandler();

    private final IMekanismConfig moreMachineConfig;

    public MoreMachineModConfig(ModContainer container, IMekanismConfig config) {
        super(config.getConfigType(), config.getConfigSpec(), container, Mekmm.MOD_NAME + "/" + config.getFileName() + ".toml");
        this.moreMachineConfig = config;
    }

    @Override
    public ConfigFileTypeHandler getHandler() {
        return MORE_MACHINE_TOML;
    }

    public void clearCache(ModConfigEvent event) {
        moreMachineConfig.clearCache(event instanceof ModConfigEvent.Unloading);
    }

    private static class MoreMachineConfigFileTypeHandler extends ConfigFileTypeHandler {

        private static Path getPath(Path configBasePath) {
            // Intercept server config path reading for Mekanism configs and reroute it to the normal config directory
            if (configBasePath.endsWith("serverconfig")) {
                return FMLPaths.CONFIGDIR.get();
            }
            return configBasePath;
        }

        @Override
        public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
            return super.reader(getPath(configBasePath));
        }

        @Override
        public void unload(Path configBasePath, ModConfig config) {
            super.unload(getPath(configBasePath), config);
        }
    }
}
