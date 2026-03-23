package com.jerry.meklg.common.registries;

import com.jerry.mekmm.Mekmm;

import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.item.block.machine.ItemBlockMachine;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.generators.common.content.blocktype.Generator;

import net.minecraft.world.level.material.MapColor;

import com.jerry.meklg.common.item.ItemBlockLargeWindGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeGasBurningGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeHeatGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeWindGenerator;

public class LargeGeneratorsBlocks {

    private LargeGeneratorsBlocks() {}

    public static final BlockDeferredRegister LG_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeHeatGenerator, Generator<TileEntityLargeHeatGenerator>>, ItemBlockMachine> LARGE_HEAT_GENERATOR = LG_BLOCKS.register("large_heat_generator", () -> new BlockTileModel<>(LargeGeneratorsBlockTypes.LARGE_HEAT_GENERATOR, properties -> properties.mapColor(MapColor.METAL)), ItemBlockMachine::new);
    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeGasBurningGenerator, Generator<TileEntityLargeGasBurningGenerator>>, ItemBlockMachine> LARGE_GAS_BURNING_GENERATOR = LG_BLOCKS.register("large_gas_burning_generator", () -> new BlockTileModel<>(LargeGeneratorsBlockTypes.LARGE_GAS_BURNING_GENERATOR, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockMachine::new);
    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeWindGenerator, Generator<TileEntityLargeWindGenerator>>, ItemBlockLargeWindGenerator> LARGE_WIND_GENERATOR = LG_BLOCKS.register("large_wind_generator", () -> new BlockTileModel<>(LargeGeneratorsBlockTypes.LARGE_WIND_GENERATOR, properties -> properties.mapColor(MapColor.METAL)), ItemBlockLargeWindGenerator::new);
}
