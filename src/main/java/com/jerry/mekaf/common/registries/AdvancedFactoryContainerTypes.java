package com.jerry.mekaf.common.registries;

import com.jerry.mekaf.common.inventory.container.tile.AdvancedFactoryContainer;
import com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.Mekmm;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

public class AdvancedFactoryContainerTypes {

    private AdvancedFactoryContainerTypes() {}

    public static final ContainerTypeDeferredRegister AF_CONTAINER_TYPES = new ContainerTypeDeferredRegister(Mekmm.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityAdvancedFactoryBase<?>>> ADVANCED_FACTORY = AF_CONTAINER_TYPES.register("advanced_factory", factoryClass(), AdvancedFactoryContainer::new);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Class<TileEntityAdvancedFactoryBase<?>> factoryClass() {
        return (Class) TileEntityAdvancedFactoryBase.class;
    }
}
