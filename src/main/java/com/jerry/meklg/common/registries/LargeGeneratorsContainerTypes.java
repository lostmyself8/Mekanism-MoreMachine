package com.jerry.meklg.common.registries;

import com.jerry.mekmm.Mekmm;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

import com.jerry.meklg.common.tile.TileEntityLargeWindGenerator;

public class LargeGeneratorsContainerTypes {

    private LargeGeneratorsContainerTypes() {}

    public static final ContainerTypeDeferredRegister LG_CONTAINER_TYPES = new ContainerTypeDeferredRegister(Mekmm.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLargeWindGenerator>> LARGE_WIND_GENERATOR = LG_CONTAINER_TYPES.register(LargeGeneratorsBlocks.LARGE_WIND_GENERATOR, TileEntityLargeWindGenerator.class);
}
