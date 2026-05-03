package com.jerry.datagen;

import com.jerry.mekmm.Mekmm;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class MekmmDatapackRegistryProvider extends BaseDatapackRegistryProvider{

    public MekmmDatapackRegistryProvider(PackOutput output, CompletableFuture<Provider> lookupProvider) {
        super(output, lookupProvider, BUILDER, Mekmm.MOD_ID);
    }

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();
}
