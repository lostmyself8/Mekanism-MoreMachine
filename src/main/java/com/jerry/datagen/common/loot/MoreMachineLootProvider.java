package com.jerry.datagen.common.loot;

import com.jerry.datagen.common.loot.table.MoreMachineBlockLootTables;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;

public class MoreMachineLootProvider extends BaseLootProvider {

    public MoreMachineLootProvider(PackOutput output) {
        super(output, List.of(
                new SubProviderEntry(MoreMachineBlockLootTables::new, LootContextParamSets.BLOCK)));
    }
}
