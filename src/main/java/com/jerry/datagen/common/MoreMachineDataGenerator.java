package com.jerry.datagen.common;

import com.jerry.datagen.common.loot.MoreMachineLootProvider;
import com.jerry.datagen.common.recipe.impl.MoreMachineRecipeProvider;

import com.jerry.mekmm.Mekmm;

import mekanism.common.Mekanism;

import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Mekmm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MoreMachineDataGenerator {

    private MoreMachineDataGenerator() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        bootstrapConfigs(Mekanism.MODID);
        bootstrapConfigs(Mekmm.MOD_ID);
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        // Client side data generators

        // Server side data generators
        addProvider(gen, event.includeServer(), MoreMachineLootProvider::new);
        MoreMachineRecipeProvider recipeProvider = new MoreMachineRecipeProvider(output, existingFileHelper);
        gen.addProvider(event.includeServer(), recipeProvider);
    }

    public static <PROVIDER extends DataProvider> void addProvider(DataGenerator gen, boolean run, DataProvider.Factory<PROVIDER> factory) {
        gen.addProvider(run, factory);
    }

    /**
     * Used to bootstrap configs to their default values so that if we are querying if things exist we don't have issues
     * with it happening to early or in cases we have
     * fake tiles.
     */
    public static void bootstrapConfigs(String modId) {
        ConfigTracker.INSTANCE.configSets().forEach((type, configs) -> {
            for (ModConfig config : configs) {
                if (config.getModId().equals(modId)) {
                    // Similar to how ConfigTracker#loadDefaultServerConfigs works for loading default server configs on
                    // the client
                    // except we don't bother firing an event as it is private, and we are already at defaults if we had
                    // called earlier,
                    // and we also don't fully initialize the mod config as the spec is what we care about, and we can
                    // do so without having
                    // to reflect into package private methods
                    CommentedConfig commentedConfig = CommentedConfig.inMemory();
                    config.getSpec().correct(commentedConfig);
                    config.getSpec().acceptConfig(commentedConfig);
                }
            }
        });
    }

    /**
     * Basically a copy of {@link DataProvider#saveStable(CachedOutput, JsonElement, Path)} but it takes a consumer of
     * the output stream instead of serializes json using GSON.
     * Use it to write arbitrary files.
     */
    @SuppressWarnings({ "UnstableApiUsage", "deprecation" })
    public static CompletableFuture<?> save(CachedOutput cache, IOConsumer<OutputStream> osConsumer, Path path) {
        return CompletableFuture.runAsync(() -> {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), outputStream)) {
                osConsumer.accept(hashingOutputStream);
                cache.writeIfNeeded(path, outputStream.toByteArray(), hashingOutputStream.hash());
            } catch (IOException ioexception) {
                DataProvider.LOGGER.error("Failed to save file to {}", path, ioexception);
            }
        }, Util.backgroundExecutor());
    }

    @FunctionalInterface
    public interface IOConsumer<T> {

        void accept(T value) throws IOException;
    }
}
