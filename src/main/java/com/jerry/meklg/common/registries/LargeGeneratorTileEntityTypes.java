package com.jerry.meklg.common.registries;

import com.jerry.mekmm.Mekmm;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;

import com.jerry.meklg.common.tile.generator.TileEntityLargeGasGenerator;
import com.jerry.meklg.common.tile.generator.TileEntityLargeHeatGenerator;

public class LargeGeneratorTileEntityTypes {

    private LargeGeneratorTileEntityTypes() {}

    public static final TileEntityTypeDeferredRegister LG_TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Mekmm.MOD_ID);

    public static final TileEntityTypeRegistryObject<TileEntityLargeHeatGenerator> LARGE_HEAT_GENERATOR = LG_TILE_ENTITY_TYPES
            .mekBuilder(LargeGeneratorBlocks.LARGE_HEAT_GENERATOR, TileEntityLargeHeatGenerator::new)
            .clientTicker(TileEntityMekanism::tickClient)
            .serverTicker(TileEntityMekanism::tickServer)
            .withSimple(Capabilities.CONFIG_CARD)
            .build();

    public static final TileEntityTypeRegistryObject<TileEntityLargeGasGenerator> LARGE_GAS_BURNING_GENERATOR = LG_TILE_ENTITY_TYPES
            .mekBuilder(LargeGeneratorBlocks.LARGE_GAS_BURNING_GENERATOR, TileEntityLargeGasGenerator::new)
            .clientTicker(TileEntityMekanism::tickClient)
            .serverTicker(TileEntityMekanism::tickServer)
            .withSimple(Capabilities.CONFIG_CARD)
            .build();
}
