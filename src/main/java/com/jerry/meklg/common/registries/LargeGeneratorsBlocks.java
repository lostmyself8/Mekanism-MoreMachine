package com.jerry.meklg.common.registries;

import com.jerry.mekmm.Mekmm;

import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.generators.common.content.blocktype.Generator;

import net.minecraft.world.level.material.MapColor;

import com.jerry.meklg.common.item.ItemBlockLargeWindGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeWindGenerator;

public class LargeGeneratorsBlocks {

    private LargeGeneratorsBlocks() {}

    public static final BlockDeferredRegister LG_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeWindGenerator, Generator<TileEntityLargeWindGenerator>>, ItemBlockLargeWindGenerator> LARGE_WIND_GENERATOR = LG_BLOCKS.register("large_wind_generator", () -> new BlockTileModel<>(LargeGeneratorsBlockTypes.LARGE_WIND_GENERATOR, properties -> properties.mapColor(MapColor.METAL)), ItemBlockLargeWindGenerator::new);
}
