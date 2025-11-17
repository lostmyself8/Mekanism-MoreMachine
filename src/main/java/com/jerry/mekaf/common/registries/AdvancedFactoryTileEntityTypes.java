package com.jerry.mekaf.common.registries;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.tile.*;
import com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.Mekmm;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.EnumUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class AdvancedFactoryTileEntityTypes {

    private AdvancedFactoryTileEntityTypes() {}

    public static final TileEntityTypeDeferredRegister AF_TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Mekmm.MOD_ID);

    private static final Table<FactoryTier, AdvancedFactoryType, TileEntityTypeRegistryObject<? extends TileEntityAdvancedFactoryBase<?>>> FACTORIES = HashBasedTable.create();

    static {
        for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
            FACTORIES.put(tier, AdvancedFactoryType.OXIDIZING, AF_TILE_ENTITY_TYPES.register(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.OXIDIZING), (pos, state) -> new TileEntityOxidizingFactory(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.OXIDIZING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, AdvancedFactoryType.CHEMICAL_INFUSING, AF_TILE_ENTITY_TYPES.register(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CHEMICAL_INFUSING), (pos, state) -> new TileEntityChemicalInfusingFactory(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CHEMICAL_INFUSING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, AdvancedFactoryType.DISSOLVING, AF_TILE_ENTITY_TYPES.register(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.DISSOLVING), (pos, state) -> new TileEntityDissolvingFactory(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.DISSOLVING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, AdvancedFactoryType.WASHING, AF_TILE_ENTITY_TYPES.register(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.WASHING), (pos, state) -> new TileEntityWashingFactory(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.WASHING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, AdvancedFactoryType.CRYSTALLIZING, AF_TILE_ENTITY_TYPES.register(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CRYSTALLIZING), (pos, state) -> new TileEntityCrystallizingFactory(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CRYSTALLIZING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, AdvancedFactoryType.PRESSURISED_REACTING, AF_TILE_ENTITY_TYPES.register(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PRESSURISED_REACTING), (pos, state) -> new TileEntityPressurizedReactingFactory(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PRESSURISED_REACTING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, AdvancedFactoryType.CENTRIFUGING, AF_TILE_ENTITY_TYPES.register(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CENTRIFUGING), (pos, state) -> new TileEntityCentrifugingFactory(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CENTRIFUGING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
            FACTORIES.put(tier, AdvancedFactoryType.LIQUIFYING, AF_TILE_ENTITY_TYPES.register(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.LIQUIFYING), (pos, state) -> new TileEntityLiquifyingFactory(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.LIQUIFYING), pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
        }
    }

    public static TileEntityTypeRegistryObject<? extends TileEntityAdvancedFactoryBase<?>> getAdvancedFactoryTile(FactoryTier tier, AdvancedFactoryType type) {
        return FACTORIES.get(tier, type);
    }

    @SuppressWarnings("unchecked")
    public static TileEntityTypeRegistryObject<? extends TileEntityAdvancedFactoryBase<?>>[] getAdvancedFactoryTiles() {
        return FACTORIES.values().toArray(new TileEntityTypeRegistryObject[0]);
    }
}
