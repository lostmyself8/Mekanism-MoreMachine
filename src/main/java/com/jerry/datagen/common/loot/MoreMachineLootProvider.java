package com.jerry.datagen.common.loot;

import com.jerry.datagen.common.loot.table.MoreMachineLootTables;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MoreMachineLootProvider extends BaseLootProvider {

    public MoreMachineLootProvider(PackOutput output, CompletableFuture<Provider> provider) {
        super(output, List.of(
                new SubProviderEntry(MoreMachineLootTables::new, LootContextParamSets.BLOCK)), provider);
    }
}
