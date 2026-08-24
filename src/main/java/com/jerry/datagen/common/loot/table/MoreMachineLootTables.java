package com.jerry.datagen.common.loot.table;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.meklm.common.registries.LargeMachineBlocks;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import net.minecraft.core.HolderLookup;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;

public class MoreMachineLootTables extends BaseBlockLootTables {

    public MoreMachineLootTables(HolderLookup.Provider provider) {
        super(provider);
    }

    @Override
    protected void generate() {
        dropSelfWithContents(MoreMachineBlocks.MM_BLOCKS.getPrimaryEntries());
        dropSelfWithContents(AdvancedFactoryBlocks.AF_BLOCKS.getPrimaryEntries());
        dropSelfWithContents(LargeMachineBlocks.LM_BLOCKS.getPrimaryEntries());
        if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
            dropSelfWithContents(LargeGeneratorBlocks.LG_BLOCKS.getPrimaryEntries());
        }
    }
}
