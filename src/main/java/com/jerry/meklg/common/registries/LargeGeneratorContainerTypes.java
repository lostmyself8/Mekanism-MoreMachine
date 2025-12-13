package com.jerry.meklg.common.registries;

import com.jerry.mekmm.Mekmm;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

import com.jerry.meklg.common.tile.generator.TileEntityLargeGasGenerator;
import com.jerry.meklg.common.tile.generator.TileEntityLargeHeatGenerator;

public class LargeGeneratorContainerTypes {

    private LargeGeneratorContainerTypes() {}

    public static final ContainerTypeDeferredRegister LG_CONTAINER_TYPES = new ContainerTypeDeferredRegister(Mekmm.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLargeHeatGenerator>> LARGE_HEAT_GENERATOR = LG_CONTAINER_TYPES.custom(LargeGeneratorBlocks.LARGE_HEAT_GENERATOR, TileEntityLargeHeatGenerator.class).armorSideBar(-20, 11, 0).build();
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLargeGasGenerator>> LARGE_GAS_BURNING_GENERATOR = LG_CONTAINER_TYPES.custom(LargeGeneratorBlocks.LARGE_GAS_BURNING_GENERATOR, TileEntityLargeGasGenerator.class).armorSideBar(-20, 11, 0).build();
}
