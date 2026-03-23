package com.jerry.meklg.common.registries;

import com.jerry.mekmm.Mekmm;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;

import com.jerry.meklg.common.tile.TileEntityLargeGasBurningGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeHeatGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeWindGenerator;

public class LargeGeneratorsTileEntityTypes {

    private LargeGeneratorsTileEntityTypes() {}

    public static final TileEntityTypeDeferredRegister LG_TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Mekmm.MOD_ID);

    public static final TileEntityTypeRegistryObject<TileEntityLargeHeatGenerator> LARGE_HEAT_GENERATOR = LG_TILE_ENTITY_TYPES.register(LargeGeneratorsBlocks.LARGE_HEAT_GENERATOR, TileEntityLargeHeatGenerator::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityLargeGasBurningGenerator> LARGE_GAS_BURNING_GENERATOR = LG_TILE_ENTITY_TYPES.register(LargeGeneratorsBlocks.LARGE_GAS_BURNING_GENERATOR, TileEntityLargeGasBurningGenerator::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityLargeWindGenerator> LARGE_WIND_GENERATOR = LG_TILE_ENTITY_TYPES.register(LargeGeneratorsBlocks.LARGE_WIND_GENERATOR, TileEntityLargeWindGenerator::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
}
