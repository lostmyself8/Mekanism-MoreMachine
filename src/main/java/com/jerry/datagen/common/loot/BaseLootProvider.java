package com.jerry.datagen.common.loot;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class BaseLootProvider extends LootTableProvider {

    protected BaseLootProvider(PackOutput output, List<SubProviderEntry> subProviders, CompletableFuture<Provider> provider) {
        this(output, Collections.emptySet(), subProviders, provider);
    }

    protected BaseLootProvider(PackOutput output, Set<ResourceKey<LootTable>> requiredTables, List<SubProviderEntry> subProviders,
                               CompletableFuture<Provider> provider) {
        super(output, requiredTables, subProviders, provider);
    }
}
