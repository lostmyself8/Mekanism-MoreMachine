package com.jerry.datagen.common.loot.table;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.mekmm.common.registries.MoreMachineBlocks;

public class MoreMachineBlockLootTables extends BaseBlockLootTables {

    @Override
    protected void generate() {
        dropSelfWithContents(MoreMachineBlocks.MM_BLOCKS.getAllBlocks());
        dropSelfWithContents(AdvancedFactoryBlocks.AF_BLOCKS.getAllBlocks());
    }
}
