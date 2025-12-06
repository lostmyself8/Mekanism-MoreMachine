package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;
import com.jerry.mekmm.common.tile.TileEntityAuthorDoll;
import com.jerry.mekmm.common.tile.TileEntityModelerDoll;
import com.jerry.mekmm.common.tile.TileEntityWirelessChargingStation;
import com.jerry.mekmm.common.tile.factory.*;
import com.jerry.mekmm.common.tile.machine.*;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.EnumUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class MoreMachineTileEntityTypes {

    private MoreMachineTileEntityTypes() {}

    public static final TileEntityTypeDeferredRegister MM_TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Mekmm.MOD_ID);

    private static final Table<FactoryTier, MoreMachineFactoryType, TileEntityTypeRegistryObject<? extends TileEntityMoreMachineFactory<?>>> FACTORIES = HashBasedTable.create();

    static {
        for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
            FACTORIES.put(tier, MoreMachineFactoryType.RECYCLING, MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.RECYCLING), (pos, state) -> new TileEntityRecyclingFactory(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.RECYCLING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, MoreMachineFactoryType.PLANTING, MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.PLANTING), (pos, state) -> new TileEntityPlantingFactory(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.PLANTING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, MoreMachineFactoryType.CNC_STAMPING, MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.CNC_STAMPING), (pos, state) -> new TileEntityStampingFactory(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.CNC_STAMPING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, MoreMachineFactoryType.CNC_LATHING, MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.CNC_LATHING), (pos, state) -> new TileEntityItemStackToItemStackMoreMachineFactory(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.CNC_LATHING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, MoreMachineFactoryType.CNC_ROLLING_MILL, MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.CNC_ROLLING_MILL), (pos, state) -> new TileEntityItemStackToItemStackMoreMachineFactory(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.CNC_ROLLING_MILL), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, MoreMachineFactoryType.REPLICATING, MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.REPLICATING), (pos, state) -> new TileEntityReplicatingFactory(MoreMachineBlocks.getMoreMachineFactory(tier, MoreMachineFactoryType.REPLICATING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
        }
    }

    public static final TileEntityTypeRegistryObject<TileEntityRecycler> RECYCLER = MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.RECYCLER, TileEntityRecycler::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityPlantingStation> PLANTING_STATION = MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.PLANTING_STATION, TileEntityPlantingStation::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);

    public static final TileEntityTypeRegistryObject<TileEntityStamper> CNC_STAMPER = MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.CNC_STAMPER, TileEntityStamper::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityLathe> CNC_LATHE = MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.CNC_LATHE, TileEntityLathe::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityRollingMill> CNC_ROLLING_MILL = MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.CNC_ROLLING_MILL, TileEntityRollingMill::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);

    public static final TileEntityTypeRegistryObject<TileEntityReplicator> REPLICATOR = MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.REPLICATOR, TileEntityReplicator::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityFluidReplicator> FLUID_REPLICATOR = MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.FLUID_REPLICATOR, TileEntityFluidReplicator::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);

    public static final TileEntityTypeRegistryObject<TileEntityAmbientGasCollector> AMBIENT_GAS_COLLECTOR = MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.AMBIENT_GAS_COLLECTOR, TileEntityAmbientGasCollector::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityWirelessChargingStation> WIRELESS_CHARGING_STATION = MM_TILE_ENTITY_TYPES.register(MoreMachineBlocks.WIRELESS_CHARGING_STATION, TileEntityWirelessChargingStation::new, TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);

    public static final TileEntityTypeRegistryObject<TileEntityAuthorDoll> AUTHOR_DOLL = MM_TILE_ENTITY_TYPES
            .builder(MoreMachineBlocks.AUTHOR_DOLL, TileEntityAuthorDoll::new)
            .build();

    public static final TileEntityTypeRegistryObject<TileEntityModelerDoll> MODELER_DOLL = MM_TILE_ENTITY_TYPES
            .builder(MoreMachineBlocks.MODELER_DOLL, TileEntityModelerDoll::new)
            .build();

    public static TileEntityTypeRegistryObject<? extends TileEntityMoreMachineFactory<?>> getMoreMachineFactoryTile(FactoryTier tier, MoreMachineFactoryType type) {
        return FACTORIES.get(tier, type);
    }

    @SuppressWarnings("unchecked")
    public static TileEntityTypeRegistryObject<? extends TileEntityMoreMachineFactory<?>>[] getMoreMachineFactoryTiles() {
        return FACTORIES.values().toArray(new TileEntityTypeRegistryObject[0]);
    }
}
