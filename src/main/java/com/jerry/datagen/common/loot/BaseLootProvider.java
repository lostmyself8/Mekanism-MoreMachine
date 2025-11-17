package com.jerry.datagen.common.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class BaseLootProvider extends LootTableProvider {

    protected BaseLootProvider(PackOutput output, List<SubProviderEntry> subProviders) {
        this(output, Collections.emptySet(), subProviders);
    }

    protected BaseLootProvider(PackOutput output, Set<ResourceLocation> requiredTables, List<SubProviderEntry> subProviders) {
        super(output, requiredTables, subProviders);
    }
}
