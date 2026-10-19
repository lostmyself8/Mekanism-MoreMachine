package com.jerry.datagen.common.loot;

import com.jerry.datagen.common.loot.table.MoreMachineLootTables;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.common.tier.FactoryTier;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MoreMachineLootProvider extends BaseLootProvider {

    public MoreMachineLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, List.of(
                new SubProviderEntry(MoreMachineLootTables::new, LootContextParamSets.BLOCK)), provider);
        if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
            ModLoadedCondition condition = new ModLoadedCondition("mekanismgenerators");
            for (DeferredHolder<Block, ? extends Block> block : LargeGeneratorBlocks.LG_BLOCKS.getPrimaryEntries()) {
                addConditions(block.value().getLootTable(), condition);
            }
        }
        if (Mekmm.hooks.evolvedMekanism.isLoaded()) {
            ModLoadedCondition condition = new ModLoadedCondition("evolvedmekanism");
            // Use the same tiers and factory types as registration so only optional factories get conditions.
            for (FactoryTier tier : MoreMachineEnumUtils.EM_TIERS) {
                for (MoreMachineFactoryType type : MoreMachineEnumUtils.MM_FACTORY_TYPES) {
                    addConditions(MoreMachineBlocks.getMoreMachineFactory(tier, type).value().getLootTable(), condition);
                }
                for (AdvancedFactoryType type : MoreMachineEnumUtils.ADVANCED_FACTORY_TYPES) {
                    addConditions(AdvancedFactoryBlocks.getAdvancedFactory(tier, type).value().getLootTable(), condition);
                }
            }
        }
    }
}
