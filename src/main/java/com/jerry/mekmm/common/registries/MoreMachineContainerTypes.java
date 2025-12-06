package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.inventory.container.tile.MoreMachineFactoryContainer;
import com.jerry.mekmm.common.tile.TileEntityWirelessChargingStation;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;
import com.jerry.mekmm.common.tile.machine.*;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;

public class MoreMachineContainerTypes {

    private MoreMachineContainerTypes() {}

    public static final ContainerTypeDeferredRegister MM_CONTAINER_TYPES = new ContainerTypeDeferredRegister(Mekmm.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityRecycler>> RECYCLER = MM_CONTAINER_TYPES.register(MoreMachineBlocks.RECYCLER, TileEntityRecycler.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityPlantingStation>> PLANTING_STATION = MM_CONTAINER_TYPES.register(MoreMachineBlocks.PLANTING_STATION, TileEntityPlantingStation.class);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityStamper>> CNC_STAMPER = MM_CONTAINER_TYPES.register(MoreMachineBlocks.CNC_STAMPER, TileEntityStamper.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLathe>> CNC_LATHE = MM_CONTAINER_TYPES.register(MoreMachineBlocks.CNC_LATHE, TileEntityLathe.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityRollingMill>> CNC_ROLLING_MILL = MM_CONTAINER_TYPES.register(MoreMachineBlocks.CNC_ROLLING_MILL, TileEntityRollingMill.class);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityReplicator>> REPLICATOR = MM_CONTAINER_TYPES.register(MoreMachineBlocks.REPLICATOR, TileEntityReplicator.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityFluidReplicator>> FLUID_REPLICATOR = MM_CONTAINER_TYPES.register(MoreMachineBlocks.FLUID_REPLICATOR, TileEntityFluidReplicator.class);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityAmbientGasCollector>> AMBIENT_GAS_COLLECTOR = MM_CONTAINER_TYPES.register(MoreMachineBlocks.AMBIENT_GAS_COLLECTOR, TileEntityAmbientGasCollector.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityWirelessChargingStation>> WIRELESS_CHARGING_STATION = MM_CONTAINER_TYPES.register(MoreMachineBlocks.WIRELESS_CHARGING_STATION, TileEntityWirelessChargingStation.class);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityMoreMachineFactory<?>>> MM_FACTORY = MM_CONTAINER_TYPES.register("factory", factoryClass(), MoreMachineFactoryContainer::new);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Class<TileEntityMoreMachineFactory<?>> factoryClass() {
        return (Class) TileEntityMoreMachineFactory.class;
    }
}
