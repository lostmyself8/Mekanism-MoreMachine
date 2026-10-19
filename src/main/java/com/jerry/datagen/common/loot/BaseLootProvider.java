package com.jerry.datagen.common.loot;

import mekanism.api.annotations.NothingNullByDefault;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@NothingNullByDefault
public abstract class BaseLootProvider extends LootTableProvider {

    private static final Codec<Optional<WithConditions<JsonElement>>> CONDITIONAL_JSON = ConditionalOps.createConditionalCodecWithConditions(ExtraCodecs.JSON);
    private final PackOutput.PathProvider pathProvider;
    private final Map<Path, List<ICondition>> conditions = new HashMap<>();

    protected BaseLootProvider(PackOutput output, List<SubProviderEntry> subProviders, CompletableFuture<HolderLookup.Provider> provider) {
        this(output, Collections.emptySet(), subProviders, provider);
    }

    protected BaseLootProvider(PackOutput output, Set<ResourceKey<LootTable>> requiredTables, List<SubProviderEntry> subProviders,
                               CompletableFuture<HolderLookup.Provider> provider) {
        super(output, requiredTables, subProviders, provider);
        pathProvider = output.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
    }

    /** Registers loading conditions without changing the loot table's drop predicates. */
    protected void addConditions(ResourceKey<LootTable> table, ICondition... tableConditions) {
        conditions.put(pathProvider.json(table.location()), List.of(tableConditions));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        // LootTableProvider validates tables but always writes DIRECT_CODEC. Decorate only its output so
        // NeoForge's loading conditions are retained without replacing vanilla's generation/validation.
        var writes = new ConcurrentLinkedQueue<CompletableFuture<?>>();
        return super.run((path, bytes, hash) -> {
            List<ICondition> tableConditions = conditions.get(path);
            if (tableConditions == null) {
                output.writeIfNeeded(path, bytes, hash);
            } else {
                JsonElement table = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8));
                JsonElement conditionalTable = CONDITIONAL_JSON.encodeStart(JsonOps.INSTANCE,
                        Optional.of(new WithConditions<>(tableConditions, table))).getOrThrow();
                // saveStable handles formatting and hashes the final bytes. Await all delegated writes below.
                writes.add(DataProvider.saveStable(output, conditionalTable, path));
            }
        }).thenCompose(ignored -> CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new)));
    }
}
