package com.jerry.datagen;

import com.jerry.datagen.client.lang.MoreMachineLangProvider;
import com.jerry.datagen.common.loot.MoreMachineLootProvider;
import com.jerry.datagen.common.recipe.imp.MoreMachineRecipeProvider;

import com.jerry.mekmm.Mekmm;

import mekanism.common.Mekanism;
import mekanism.common.lib.FieldReflectionHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.DeferredWorkQueue;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.concurrent.SynchronizedConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 来自Mekanism的仓库，删去了大部分这个模组不需要的模块，如果需要参考还请参考Mekanism的仓库
 */
@EventBusSubscriber(modid = Mekmm.MOD_ID)
public class MekmmDataGenerator {

    @SuppressWarnings("UnstableApiUsage")
    private static final FieldReflectionHelper<ConfigTracker, EnumMap<ModConfig.Type, Set<ModConfig>>> CONFIG_SETS = new FieldReflectionHelper<>(ConfigTracker.class, "configSets", () -> new EnumMap<>(ModConfig.Type.class));
    private static final Constructor<?> LOADED_CONFIG;
    private static final Method SET_CONFIG;

    static {
        Class<?> loadedConfig;
        try {
            loadedConfig = Class.forName("net.neoforged.fml.config.LoadedConfig");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        LOADED_CONFIG = ObfuscationReflectionHelper.findConstructor(loadedConfig, CommentedConfig.class, Path.class, ModConfig.class);
        SET_CONFIG = ObfuscationReflectionHelper.findMethod(ModConfig.class, "setConfig", loadedConfig, Function.class);
    }

    private MekmmDataGenerator() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        bootstrapConfigs(Mekanism.MODID);
        bootstrapConfigs(Mekmm.MOD_ID);
        bootstrapIMC();
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        // Client side data generators
        gen.addProvider(event.includeClient(), new MoreMachineLangProvider(output));
        // Server side data generators
        gen.addProvider(event.includeServer(), new MoreMachineLootProvider(output, lookupProvider));
        gen.addProvider(event.includeServer(), new MoreMachineRecipeProvider(output, lookupProvider, existingFileHelper));
        // Data generator to help with persisting data when porting across MC versions when optional deps aren't updated
        // yet
        // DO NOT ADD OTHERS AFTER THIS ONE
        // PersistingDisabledProvidersProvider.addDisableableProviders(event, lookupProvider,
        // recipeProvider.getDisabledCompats());
    }

    /**
     * Used to bootstrap configs to their default values so that if we are querying if things exist we don't have issues
     * with it happening to early or in cases we have
     * fake tiles.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void bootstrapConfigs(String modid) {
        for (Set<ModConfig> configs : CONFIG_SETS.getValue(ConfigTracker.INSTANCE).values()) {
            for (ModConfig config : configs) {
                if (config.getModId().equals(modid)) {
                    // Similar to how ConfigTracker#loadDefaultServerConfigs works for loading default server configs on
                    // the client
                    // except we don't bother firing an event as it is private, and we are already at defaults if we had
                    // called earlier,
                    // and we also don't fully initialize the mod config as the spec is what we care about, and we can
                    // do so without having
                    // to reflect into package private methods
                    CommentedConfig commentedConfig = new SynchronizedConfig(InMemoryCommentedFormat.defaultInstance(), LinkedHashMap::new);
                    config.getSpec().correct(commentedConfig);
                    try {
                        SET_CONFIG.invoke(config, LOADED_CONFIG.newInstance(commentedConfig, null, config),
                                (Function<ModConfig, ModConfigEvent>) ModConfigEvent.Loading::new);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static void bootstrapIMC() {
        List<ModContainer> mods = new ArrayList<>();
        DeferredWorkQueue enqueueIMC = new DeferredWorkQueue("IMC Bootstrap: Enqueue IMC");
        for (ModContainer mod : ModList.get().getSortedMods()) {
            // Handle all our modules
            if (mod.getModId().startsWith(Mekanism.MODID) || mod.getModId().startsWith(Mekmm.MOD_ID)) {
                mods.add(mod);
                Objects.requireNonNull(mod.getEventBus()).post(new InterModEnqueueEvent(mod, enqueueIMC));
            }
        }
        enqueueIMC.runTasks();
        DeferredWorkQueue processIMC = new DeferredWorkQueue("IMC Bootstrap: Process IMC");
        for (ModContainer mod : mods) {
            Objects.requireNonNull(mod.getEventBus()).post(new InterModProcessEvent(mod, processIMC));
        }
        processIMC.runTasks();
    }
}
