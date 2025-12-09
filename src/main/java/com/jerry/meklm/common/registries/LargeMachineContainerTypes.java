package com.jerry.meklm.common.registries;

import com.jerry.mekmm.Mekmm;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;

public class LargeMachineContainerTypes {

    private LargeMachineContainerTypes() {}

    public static final ContainerTypeDeferredRegister LM_CONTAINER_TYPES = new ContainerTypeDeferredRegister(Mekmm.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLargeChemicalTank<?>>> LARGE_CHEMICAL_TANK = LM_CONTAINER_TYPES.custom("chemical_tank", largeTankClass()).armorSideBar().build();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Class<TileEntityLargeChemicalTank<?>> largeTankClass() {
        return (Class) TileEntityLargeChemicalTank.class;
    }
}
