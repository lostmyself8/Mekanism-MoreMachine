package com.jerry.meklm.common.registries;

import com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser;
import com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;

import com.jerry.mekmm.Mekmm;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

public class LargeMachineContainerTypes {

    private LargeMachineContainerTypes() {}

    public static final ContainerTypeDeferredRegister LM_CONTAINER_TYPES = new ContainerTypeDeferredRegister(Mekmm.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLargeChemicalTank<?>>> CHEMICAL_TANK = LM_CONTAINER_TYPES.custom("chemical_tank", tankClass()).armorSideBar().build();
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLargeRotaryCondensentrator>> LARGE_ROTARY_CONDENSENTRATOR = LM_CONTAINER_TYPES.register(LargeMachineBlocks.LARGE_ROTARY_CONDENSENTRATOR, TileEntityLargeRotaryCondensentrator.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLargeChemicalInfuser>> LARGE_CHEMICAL_INFUSER = LM_CONTAINER_TYPES.register(LargeMachineBlocks.LARGE_CHEMICAL_INFUSER, TileEntityLargeChemicalInfuser.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLargeElectrolyticSeparator>> LARGE_ELECTROLYTIC_SEPARATOR = LM_CONTAINER_TYPES.register(LargeMachineBlocks.LARGE_ELECTROLYTIC_SEPARATOR, TileEntityLargeElectrolyticSeparator.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLargeSolarNeutronActivator>> LARGE_SOLAR_NEUTRON_ACTIVATOR = LM_CONTAINER_TYPES.register(LargeMachineBlocks.LARGE_SOLAR_NEUTRON_ACTIVATOR, TileEntityLargeSolarNeutronActivator.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Class<TileEntityLargeChemicalTank<?>> tankClass() {
        return (Class) TileEntityLargeChemicalTank.class;
    }
}
