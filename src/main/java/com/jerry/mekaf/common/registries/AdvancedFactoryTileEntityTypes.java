package com.jerry.mekaf.common.registries;

import com.jerry.mekaf.common.block.prefab.BlockAdvancedFactoryMachine;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.item.block.machine.ItemBlockAdvancedFactory;
import com.jerry.mekaf.common.tile.factory.*;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.base.TileEntityMekanism;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class AdvancedFactoryTileEntityTypes {

    private AdvancedFactoryTileEntityTypes() {}

    public static final TileEntityTypeDeferredRegister AF_TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Mekmm.MOD_ID);

    private static final Table<FactoryTier, AdvancedFactoryType, TileEntityTypeRegistryObject<? extends TileEntityAdvancedFactoryBase<?>>> AF_FACTORIES = HashBasedTable.create();

    static {
        for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
            registerFactory(tier, AdvancedFactoryType.OXIDIZING, TileEntityOxidizingFactory::new);
            registerFactory(tier, AdvancedFactoryType.DISSOLVING, TileEntityDissolvingFactory::new);
            registerFactory(tier, AdvancedFactoryType.CHEMICAL_INFUSING, TileEntityChemicalInfusingFactory::new);
            registerFactory(tier, AdvancedFactoryType.WASHING, TileEntityWashingFactory::new);
            registerFactory(tier, AdvancedFactoryType.PRESSURISED_REACTING, TileEntityPressurizedReactingFactory::new);
            registerFactory(tier, AdvancedFactoryType.CRYSTALLIZING, TileEntityCrystallizingFactory::new);
            registerFactory(tier, AdvancedFactoryType.CENTRIFUGING, TileEntityCentrifugingFactory::new);
            registerFactory(tier, AdvancedFactoryType.LIQUIFYING, TileEntityLiquifyingFactory::new);
        }
    }

    private static void registerFactory(FactoryTier tier, AdvancedFactoryType type, AdvancedBlockEntityFactory<? extends TileEntityAdvancedFactoryBase<?>> factoryConstructor) {
        BlockRegistryObject<BlockAdvancedFactoryMachine.BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> block = AdvancedFactoryBlocks.getAdvancedFactory(tier, type);
        TileEntityTypeRegistryObject<? extends TileEntityAdvancedFactoryBase<?>> tileRO = AF_TILE_ENTITY_TYPES.mekBuilder(block, (pos, state) -> factoryConstructor.create(block, pos, state))
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIG_CARD)
                .build();
        AF_FACTORIES.put(tier, type, tileRO);
    }

    public static TileEntityTypeRegistryObject<? extends TileEntityAdvancedFactoryBase<?>> getAdvancedFactoryTile(FactoryTier tier, AdvancedFactoryType type) {
        return AF_FACTORIES.get(tier, type);
    }

    @SuppressWarnings("unchecked")
    public static TileEntityTypeRegistryObject<? extends TileEntityAdvancedFactoryBase<?>>[] getAdvancedFactoryTiles() {
        return AF_FACTORIES.values().toArray(new TileEntityTypeRegistryObject[0]);
    }

    @FunctionalInterface
    private interface AdvancedBlockEntityFactory<BE extends BlockEntity> {

        BE create(Holder<Block> block, BlockPos pos, BlockState state);
    }
}
